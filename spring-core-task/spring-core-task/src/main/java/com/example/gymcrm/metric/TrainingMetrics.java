package com.example.gymcrm.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TrainingMetrics {

    private final Counter trainingCounter;

    public TrainingMetrics(MeterRegistry registry) {
        this.trainingCounter = registry.counter("training.created.count");
    }

    public void increment() {
        trainingCounter.increment();
    }
}
