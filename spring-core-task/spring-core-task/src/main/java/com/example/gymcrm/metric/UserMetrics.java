package com.example.gymcrm.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class UserMetrics {

    private final Counter traineeCounter;
    private final Counter trainerCounter;

    public UserMetrics(MeterRegistry registry) {
        this.traineeCounter = Counter.builder("trainee.created.count")
                .description("Number of trainees created")
                .register(registry);

        this.trainerCounter = Counter.builder("trainer.created.count")
                .description("Number of trainers created")
                .register(registry);
    }

    public void incrementTrainee() {
        traineeCounter.increment();
    }

    public void incrementTrainer() {
        trainerCounter.increment();
    }
}