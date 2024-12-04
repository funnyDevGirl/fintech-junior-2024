package org.tbank.rabbitmq.benchmarks;

import com.rabbitmq.client.Channel;
import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
public class RabbitMQBenchmark {

    private static final int MESSAGE_COUNT = 1000;
    private RabbitProducer producer;
    private RabbitConsumer consumer;
    private byte[] smallMessage;
    private byte[] largeMessage;

    @Setup
    public void setup() throws Exception {
        producer = new RabbitProducer();
        Channel channel = producer.getChannel();
        consumer = new RabbitConsumer(channel);
        smallMessage = new byte[100];
        largeMessage = new byte[10_000];
    }

    @Benchmark
    public void testSingleProducerSingleConsumerSmallMessage() throws Exception {
        producer.produceMessages(MESSAGE_COUNT, smallMessage);
        consumer.consumeMessages();
    }

    @Benchmark
    public void testSingleProducerSingleConsumerLargeMessage() throws Exception {
        producer.produceMessages(MESSAGE_COUNT, largeMessage);
        consumer.consumeMessages();
    }

    @Benchmark
    public void testMultipleProducersSingleConsumerSmallMessage() throws Exception {
        for (int i = 0; i < 3; i++) {
            producer.produceMessages(MESSAGE_COUNT / 3, smallMessage);
        }
        consumer.consumeMessages();
    }

    @Benchmark
    public void testMultipleProducersSingleConsumerLargeMessage() throws Exception {
        for (int i = 0; i < 3; i++) {
            producer.produceMessages(MESSAGE_COUNT / 3, largeMessage);
        }
        consumer.consumeMessages();
    }

    @Benchmark
    public void testSingleProducerMultipleConsumersSmallMessage() throws Exception {
        producer.produceMessages(MESSAGE_COUNT, smallMessage);
        for (int i = 0; i < 3; i++) {
            consumer.consumeMessages();
        }
    }

    @Benchmark
    public void testSingleProducerMultipleConsumersLargeMessage() throws Exception {
        producer.produceMessages(MESSAGE_COUNT, largeMessage);
        for (int i = 0; i < 3; i++) {
            consumer.consumeMessages();
        }
    }

    @Benchmark
    public void testMultipleProducerMultipleConsumersSmallMessage() throws Exception {
        for (int i = 0; i < 3; i++) {
            producer.produceMessages(MESSAGE_COUNT / 3, smallMessage);
        }
        for (int i = 0; i < 3; i++) {
            consumer.consumeMessages();
        }
    }

    @Benchmark
    public void testMultipleProducerMultipleConsumersLargeMessage() throws Exception {
        for (int i = 0; i < 3; i++) {
            producer.produceMessages(MESSAGE_COUNT / 3, largeMessage);
        }
        for (int i = 0; i < 3; i++) {
            consumer.consumeMessages();
        }
    }

    @Benchmark
    public void testStressTestWithSmallMessage() throws Exception {
        for (int i = 0; i < 10; i++) {
            producer.produceMessages(MESSAGE_COUNT / 10, smallMessage);
        }
        for (int i = 0; i < 10; i++) {
            consumer.consumeMessages();
        }
    }

    @Benchmark
    public void testStressTestWithLargeMessage() throws Exception {
        for (int i = 0; i < 10; i++) {
            producer.produceMessages(MESSAGE_COUNT / 10, largeMessage);
        }
        for (int i = 0; i < 10; i++) {
            consumer.consumeMessages();
        }
    }

    @TearDown
    public void tearDown() throws Exception {
        producer.close();
        consumer.close();
    }
}
