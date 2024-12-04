package org.tbank.kafka.benchmarks;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class KafkaConsumerWrapper {
    private final List<Consumer<String, String>> consumers = new ArrayList<>();
    private static final String TOPIC = "benchmark_topic";

    public void setupConsumers(int consumersCount) {
        consumers.clear();
        for (int i = 0; i < consumersCount; i++) {
            Consumer<String, String> consumer = new KafkaConsumer<>(getConsumerProperties("group-" + i));
            consumer.subscribe(Collections.singletonList(TOPIC));
            consumers.add(consumer);
        }
    }

    private Properties getConsumerProperties(String groupId) {
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return consumerProps;
    }

    public void consumeMessages() {
        consumers.forEach(consumer -> {
            consumer.poll(java.time.Duration.ofMillis(100)).forEach(this::processMessage);
            consumer.commitSync();
        });
    }

    private void processMessage(ConsumerRecord<String, String> record) {
        simulateProcessing();
    }

    private void simulateProcessing() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void close() {
        consumers.forEach(Consumer::close);
    }
}
