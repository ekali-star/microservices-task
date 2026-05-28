package com.example.gymcrm.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserMetricsTest {

    private MeterRegistry meterRegistry;
    private UserMetrics userMetrics;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        userMetrics = new UserMetrics(meterRegistry);
    }

    @Test
    void incrementTrainee_shouldIncreaseTraineeCounter() {
        userMetrics.incrementTrainee();
        userMetrics.incrementTrainee();

        double traineeCount = meterRegistry.counter("trainee.created.count").count();
        double trainerCount = meterRegistry.counter("trainer.created.count").count();

        assertThat(traineeCount).isEqualTo(2);
        assertThat(trainerCount).isEqualTo(0);
    }

    @Test
    void incrementTrainer_shouldIncreaseTrainerCounter() {
        userMetrics.incrementTrainer();
        userMetrics.incrementTrainer();

        double traineeCount = meterRegistry.counter("trainee.created.count").count();
        double trainerCount = meterRegistry.counter("trainer.created.count").count();

        assertThat(traineeCount).isEqualTo(0);
        assertThat(trainerCount).isEqualTo(2);
    }

    @Test
    void incrementTraineeAndTrainer_shouldIncreaseBothCounters() {
        userMetrics.incrementTrainee();
        userMetrics.incrementTrainer();

        double traineeCount = meterRegistry.counter("trainee.created.count").count();
        double trainerCount = meterRegistry.counter("trainer.created.count").count();

        assertThat(traineeCount).isEqualTo(1);
        assertThat(trainerCount).isEqualTo(1);
    }

    @Test
    void metrics_shouldHaveCorrectDescriptions() {
        userMetrics.incrementTrainee();

        Counter traineeCounter = meterRegistry.find("trainee.created.count").counter();
        Counter trainerCounter = meterRegistry.find("trainer.created.count").counter();

        assertThat(traineeCounter).isNotNull();
        assertThat(trainerCounter).isNotNull();
    }
}