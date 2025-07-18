package com.example.iot.ingest.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Map;

@Configuration
@EnableConfigurationProperties(KafkaProps.class)
@RequiredArgsConstructor
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrap;

    private final KafkaProps props;   // custom topics

    /* ---------- ProducerFactory based on Boot properties -------------- */
    @Bean
    public ProducerFactory<String, String> producerFactory() {

        return new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,   StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
        ));
    }

    /* ---------- KafkaTemplate (without built-in retry) -------------------- */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(
            ProducerFactory<String, String> pf) {

        KafkaTemplate<String, String> kt = new KafkaTemplate<>(pf);
        kt.setDefaultTopic(props.getTopicRaw());          // „fallback” topic
        return kt;
    }

    /* ---------- RetryTemplate – 3 attempts, back-off expo. -------------- */
    @Bean
    public RetryTemplate kafkaRetryTemplate() {

        var rt = new RetryTemplate();
        rt.setRetryPolicy(new SimpleRetryPolicy(3));

        var bo = new ExponentialBackOffPolicy();
        bo.setInitialInterval(100);
        bo.setMultiplier(2);
        bo.setMaxInterval(800);
        rt.setBackOffPolicy(bo);

        return rt;
    }

    /* ---------- Dead-letter recoverer (message → *.DLT) -------------------- */
    @Bean
    public DeadLetterPublishingRecoverer deadLetterRecoverer(
            KafkaTemplate<String, String> template) {

        // topic   → topic+.DLT
        // partition kept (can change the mapping here)
        return new DeadLetterPublishingRecoverer(template,
                (rec, ex) -> new TopicPartition(rec.topic() + ".DLT", rec.partition()));
    }
}
