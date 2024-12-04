package org.tbank.kafka.benchmarks;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;

public class KafkaProducerWrapper {
    private final KafkaProducer<String, String> producer;
    private static final String TOPIC = "benchmark_topic";

    public KafkaProducerWrapper() {
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.ACKS_CONFIG, "all");
        producerProps.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        this.producer = new KafkaProducer<>(producerProps);
    }

    public void sendMessages(int producersCount, String messageSize) {
        String messageContent = messageSize.equals("small") ? "small_message" : "large_message".repeat(1000);

        for (int i = 0; i < producersCount; i++) {
            producer.send(new ProducerRecord<>(TOPIC, "key-" + i, messageContent), (metadata, exception) -> {

                if (exception != null) {
                    exception.printStackTrace();
                }
            });
        }
    }

    public void close() {
        producer.close();
    }
}
