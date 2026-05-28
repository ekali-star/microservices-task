package com.example.gymcrm.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingMetricsTest {

    private MeterRegistry meterRegistry;
    private TrainingMetrics trainingMetrics;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        trainingMetrics = new TrainingMetrics(meterRegistry);
    }

    @Test
    void increment_shouldIncreaseCounter() {
        trainingMetrics.increment();
        trainingMetrics.increment();

        double count = meterRegistry.counter("training.created.count").count();
        assertThat(count).isEqualTo(2);
    }

    @Test
    void increment_shouldCreateCounterWithCorrectName() {
        trainingMetrics.increment();

        assertThat(meterRegistry.find("training.created.count").counter()).isNotNull();
    }
}