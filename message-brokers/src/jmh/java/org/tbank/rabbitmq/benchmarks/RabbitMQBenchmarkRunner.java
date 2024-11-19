package org.tbank.rabbitmq.benchmarks;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@Slf4j
public class RabbitMQBenchmarkRunner {
    public static void main(String[] args) {
        Options options = new OptionsBuilder()
                .include(RabbitMQBenchmark.class.getSimpleName())
                .forks(1)
                .output("rabbitmq_benchmark_report.txt")
                .build();

        try {
            Runner runner = new Runner(options);
            runner.run();

            log.info("Бенчмарки завершены и отчет сохранен.");

        } catch (RunnerException e) {
            e.printStackTrace();
        }
    }
}
