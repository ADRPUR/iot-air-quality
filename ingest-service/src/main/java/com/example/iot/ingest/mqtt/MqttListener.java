package com.example.iot.ingest.mqtt;

import com.example.iot.ingest.config.KafkaProps;
import com.example.iot.ingest.dto.SensorValueDto;
import com.example.iot.ingest.events.SensorValuePublisher;
import com.example.iot.ingest.model.SensorDataEntity;
import com.example.iot.ingest.dto.SensorRecord;
import com.example.iot.ingest.repository.SensorDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Listens to the configured MQTT topic, persists every message and republishes it to Kafka
 * plus an in-process reactor stream (SensorValuePublisher).
 *
 * <p>• The ACK is always sent in the {@code finally} block – essential when
 * {@code setManualAcks(true)} is active.<br>
 * • Reconnection is left to the Paho client via
 * {@code opts.setAutomaticReconnect(true)}.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MqttListener implements MqttCallback {

    /* ------------------------------------------------------------------ */
    /*  Dependencies                                                      */
    /* ------------------------------------------------------------------ */
    private final MqttConfigProperties props;
    private final SensorDataRepository repo;
    private final KafkaTemplate<String, String> kafka;
    private final RetryTemplate kafkaRetryTemplate;
    private final KafkaProps kafkaProps;
    private final SensorValuePublisher publisher;
    private final ObjectMapper mapper;

    /* ------------------------------------------------------------------ */
    /*  Runtime state                                                     */
    /* ------------------------------------------------------------------ */
    private MqttClient client;

    /* ------------------------------------------------------------------ */
    /*  Lifecycle                                                         */
    /* ------------------------------------------------------------------ */

    @EventListener(ApplicationReadyEvent.class)
    public void init() throws Exception {

        client = new MqttClient(props.broker(), props.clientId(), null);

        MqttConnectionOptions opts = new MqttConnectionOptions();
        opts.setUserName(props.username());
        opts.setPassword(props.password().getBytes(StandardCharsets.UTF_8));
        opts.setAutomaticReconnect(true);
        // opts.setCleanStart(false);  // remove manual clean start override

        client.setManualAcks(false);  // disable manual acks so Paho auto-acknowledges
        client.setCallback(this);

        client.connect(opts);
        client.subscribe(props.topic(), 1);   // QoS 1 -> ACK mandatory

        log.info("MQTT connected - subscribed to '{}'", props.topic());
    }

    @PreDestroy
    public void close() throws MqttException {
        if (client != null && client.isConnected()) {
            client.disconnect();
        }
    }

    /* ------------------------------------------------------------------ */
    /*  MQTT callbacks                                                    */
    /* ------------------------------------------------------------------ */

    /**
     * Persists and propagates each MQTT message.
     */
    @Timed("mqtt_ingest_message")            // Simple metric micrometer
    @Override
    public void messageArrived(String topic, MqttMessage msg) {

        try {
            /* 1. Deserialize JSON --------------------------------------------------- */
            SensorRecord rec = mapper.readValue(msg.getPayload(), SensorRecord.class);

            /* 2. Persist to PostgreSQL --------------------------------------------- */
            SensorDataEntity e = new SensorDataEntity();
            e.setTime(rec.ts().toInstant());
            e.setSensorId(rec.sensorId());
            e.setField(rec.field());
            e.setValue(rec.value());

            repo.save(e);

            /* 3. Fan-out ------------------------------------------------------------ */
            sendToKafka(topic, new String(msg.getPayload(), StandardCharsets.UTF_8));

            publisher.publish(new SensorValueDto(
                    e.getSensorId(), e.getField(), e.getValue(), e.getTime()));

            log.debug("MQTT persisted {}", e);

        } catch (Exception ex) {
            log.error("FAILED to handle MQTT payload: {}",
                    new String(msg.getPayload(), StandardCharsets.UTF_8), ex);

            // here we can increment a Micrometer counter or send an alert
        } finally {
            // manual ack disabled, auto-ack handled by client
        }
    }

    private void sendToKafka(String key, String json) throws Exception {
        kafkaRetryTemplate.execute(ctx -> {
            kafka.send(kafkaProps.getTopicRaw(), key, json).get();
            return null;
        });
    }

    /**
     * Just log; reconnecting is done automatically by Paho.
     */
    @Override
    public void disconnected(MqttDisconnectResponse resp) {
        log.warn("MQTT disconnected ({}): {}", resp.getReturnCode(), resp.getReasonString());
    }

    @Override
    public void mqttErrorOccurred(MqttException e) {
        log.error("MQTT error", e);
    }

    @Override
    public void deliveryComplete(org.eclipse.paho.mqttv5.client.IMqttToken token) {
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        log.info("MQTT connectComplete (reconnect={}) to {}", reconnect, serverURI);
        if (reconnect) {
            try {
                client.subscribe(props.topic(), 1);
                log.info("Re-subscribed to MQTT topic '{}'", props.topic());
            } catch (MqttException e) {
                log.error("Failed to re-subscribe to MQTT topic '{}'", props.topic(), e);
            }
        }
    }

    @Override
    public void authPacketArrived(int reasonCode,
                                  org.eclipse.paho.mqttv5.common.packet.MqttProperties properties) {
    }
}
