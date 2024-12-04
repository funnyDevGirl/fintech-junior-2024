package org.tbank.kafka.benchmarks;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class KafkaBenchmark {

    private KafkaProducerWrapper producerWrapper;
    private KafkaConsumerWrapper consumerWrapper;

    @Param({"small", "large"})
    private String messageSize;

    @Setup(Level.Trial)
    public void setup() {
        producerWrapper = new KafkaProducerWrapper();
        consumerWrapper = new KafkaConsumerWrapper();
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        producerWrapper.close();
        consumerWrapper.close();
    }

    @Benchmark
    public void testSimpleConfiguration() {
        runTest(1, 1);
    }

    @Benchmark
    public void testLoadBalancing() {
        runTest(1, 3);
    }

    @Benchmark
    public void testMultipleConsumers() {
        runTest(3, 1);
    }

    @Benchmark
    public void testLoadBalancingWithMultipleConsumers() {
        runTest(3, 3);
    }

    @Benchmark
    public void testStress() {
        runTest(10, 10);
    }

    private void runTest(int consumersCount, int producersCount) {
        consumerWrapper.setupConsumers(consumersCount);
        producerWrapper.sendMessages(producersCount, messageSize);
        consumerWrapper.consumeMessages();
    }
}
