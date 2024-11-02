package org.tbank.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class ExecutorConfig {

    @Value("${app.threadPool.size}")
    private int poolSize;

    @Getter
    @Value("${app.dataInitialization.schedule}")
    private Duration schedule;


    @Bean(name = "customFixedThreadPool")
    public ExecutorService fixedThreadPool() {
        return Executors.newFixedThreadPool(poolSize);
    }

    @Bean(name = "customScheduledThreadPool")
    public ScheduledExecutorService scheduledThreadPool() {
        return Executors.newScheduledThreadPool(2);
    }
}
