package org.tbank.rabbitmq.benchmarks;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.openjdk.jmh.annotations.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class RabbitMQBenchmark {

    private static final String QUEUE_NAME = "test_queue";
    private Connection connection;
    private Channel channel;
    private byte[] smallMessage;
    private byte[] largeMessage;

    @Setup
    public void setup() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
        setupQueue(channel);

        smallMessage = new byte[100];
        largeMessage = new byte[10_000];
    }

    public static void setupQueue(Channel channel) throws IOException {
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    }

    public static void produceMessages(Channel channel, int count, byte[] message) throws IOException {
        for (int i = 0; i < count; i++) {
            channel.basicPublish("", QUEUE_NAME, null, message);
        }
    }

    public static void consumeMessages(Channel channel, int count) throws IOException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            // Здесь должна происходить обработка сообщения.
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }

    @Benchmark
    public void testSingleProducerSingleConsumerSmallMessage() throws Exception {
        produceMessages(channel, 100, smallMessage);
        consumeMessages(channel, 100);
    }

    @Benchmark
    public void testSingleProducerSingleConsumerLargeMessage() throws Exception {
        produceMessages(channel, 100, largeMessage);
        consumeMessages(channel, 100);
    }

    @Benchmark
    public void testMultipleProducersSingleConsumerSmallMessage() throws Exception {
        for (int i = 0; i < 3; i++) {
            produceMessages(channel, 100, smallMessage);
        }
        consumeMessages(channel, 300);
    }

    @Benchmark
    public void testMultipleProducersSingleConsumerLargeMessage() throws Exception {
        for (int i = 0; i < 3; i++) {
            produceMessages(channel, 100, largeMessage);
        }
        consumeMessages(channel, 300);
    }

    @Benchmark
    public void testSingleProducerMultipleConsumersSmallMessage() throws Exception {
        produceMessages(channel, 300, smallMessage);
        for (int i = 0; i < 3; i++) {
            consumeMessages(channel, 100);
        }
    }

    @Benchmark
    public void testSingleProducerMultipleConsumersLargeMessage() throws Exception {
        produceMessages(channel, 300, largeMessage);
        for (int i = 0; i < 3; i++) {
            consumeMessages(channel, 100);
        }
    }

    @Benchmark
    public void testMultipleProducerMultipleConsumersSmallMessage() throws Exception {
        for (int i = 0; i < 3; i++) {
            produceMessages(channel, 100, smallMessage);
        }

        for (int i = 0; i < 3; i++) {
            consumeMessages(channel, 100);
        }
    }

    @Benchmark
    public void testMultipleProducerMultipleConsumersLargeMessage() throws Exception {
        for (int i = 0; i < 3; i++) {
            produceMessages(channel, 100, largeMessage);
        }

        for (int i = 0; i < 3; i++) {
            consumeMessages(channel, 100);
        }
    }

    @Benchmark
    public void testStressTestWithSmallMessage() throws Exception {
        for (int i = 0; i < 10; i++) {
            produceMessages(channel, 1000, smallMessage);
        }
        for (int i = 0; i < 10; i++) {
            consumeMessages(channel, 1000);
        }
    }

    @Benchmark
    public void testStressTestWithLargeMessage() throws Exception {
        for (int i = 0; i < 10; i++) {
            produceMessages(channel, 1000, largeMessage);
        }
        for (int i = 0; i < 10; i++) {
            consumeMessages(channel, 1000);
        }
    }

    @TearDown
    public void tearDown() throws Exception {
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
        if (connection != null && connection.isOpen()) {
            connection.close();
        }
    }
}
