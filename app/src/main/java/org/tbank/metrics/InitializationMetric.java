package org.tbank.metrics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InitializationMetric {
    private int numberOfThreads;
    private long duration;
}
