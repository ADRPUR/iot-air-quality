package com.example.iot.ingest.mqtt;

import com.example.iot.ingest.model.SensorDataEntity;
import com.example.iot.ingest.model.SensorRecord;
import com.example.iot.ingest.repository.SensorDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqttListener implements MqttCallback {

    private final MqttConfigProperties props;              // ✔ config bean
    private final SensorDataRepository repo;
    private final KafkaTemplate<String, String> kafka;

    private final ObjectMapper mapper;
    private IMqttClient client;                            // warning about “could be local” is harmless

    @PostConstruct
    void init() throws MqttException, InterruptedException {
        MqttConnectionOptions opts = new MqttConnectionOptions();
        opts.setUserName(props.username());
        opts.setPassword(props.password().getBytes(StandardCharsets.UTF_8));
        client = new MqttClient(props.broker(), props.clientId(), null);
        client.setCallback(this);
        for (int i=0; i<10; i++) {
            try {
                client.connect(opts);
                break;
            } catch (MqttException ex) {
                log.warn("MQTT connect failed, retry {}", i+1);
                Thread.sleep(2000);
            }
        }
        client.subscribe(props.topic(), 1);
        log.info("MQTT connected to {} and subscribed to {}", props.broker(), props.topic());
    }

    @Override
    public void messageArrived(String topic, MqttMessage raw) {
        try {
            SensorRecord rec = mapper.readValue(raw.getPayload(), SensorRecord.class);

            SensorDataEntity e = new SensorDataEntity();
            e.setTime(rec.ts().toInstant());
            e.setSensorId(rec.sensor());
            e.setField(rec.field());
            e.setValue(rec.value());
            repo.save(e);

            kafka.send("iot.raw", topic, new String(raw.getPayload()));
        } catch (Exception ex) {
            log.error("FAILED to parse {}", new String(raw.getPayload()), ex);
        }
    }

    @Override
    public void disconnected(MqttDisconnectResponse response) {
        log.warn("MQTT disconnected: {}", response.getReasonString());
    }

    @Override
    public void mqttErrorOccurred(MqttException exception) {
        log.error("MQTT error", exception);
    }

    @Override public void deliveryComplete(IMqttToken token) { }
    @Override public void connectComplete(boolean reconnect, String serverURI) { }
    @Override
    public void authPacketArrived(int reasonCode,
                                  org.eclipse.paho.mqttv5.common.packet.MqttProperties properties) { }
}

