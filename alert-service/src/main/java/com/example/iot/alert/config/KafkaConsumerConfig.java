package com.example.iot.alert.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Map;

/**
 * Central Kafka **consumer** configuration (Alert-service).
 *
 * <ul>
 * <li>bootstrap-servers, group-id and topics are read from <code>application.yml</code></li>
 * <li>retry + DLQ: 3 attempts with 1 s back-off, then the message is sent to the <code>*.DLT</code></li> topic
 * </ul>
 */
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrap;

    /** <pre>alert.kafka.group-id=alert-service</pre> */
    @Value("${alert.kafka.group-id}")
    private String groupId;

    /* ------------------------------------------------------------------ *
     * PRODUCER _minimal_ — ONLY for Dead-Letter (DLT)                 *
     * ------------------------------------------------------------------ */
    @Bean
    public ProducerFactory<String, String> dlqProducerFactory() {
        return new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,   StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
        ));
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(dlqProducerFactory());
    }

    /* ---------- Consumer Factory (String-&-String deserialization) ---- */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,   StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.GROUP_ID_CONFIG,                 groupId,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,        "earliest"
        ));
    }

    /* ---------- ContainerFactory with retry + DLQ --------------------------- */
    @Bean(name = "stringKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            KafkaTemplate<String, String> kafkaTemplate) {

        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(2);

        var recoverer    = new DeadLetterPublishingRecoverer(kafkaTemplate);
        var errorHandler = new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(1_000L, 3));
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
