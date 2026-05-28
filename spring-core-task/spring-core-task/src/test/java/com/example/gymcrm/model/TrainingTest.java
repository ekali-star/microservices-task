package com.example.gymcrm.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TrainingTest {

    @Test
    void testTrainingGettersAndSetters() {
        Training training = new Training();
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        TrainingType type = new TrainingType(1L, "Cardio");
        LocalDate date = LocalDate.now();

        training.setId(1L);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName("Morning Cardio");
        training.setTrainingType(type);
        training.setTrainingDate(date);
        training.setTrainingDuration(60);

        assertEquals(1L, training.getId());
        assertEquals(trainee, training.getTrainee());
        assertEquals(trainer, training.getTrainer());
        assertEquals("Morning Cardio", training.getTrainingName());
        assertEquals(type, training.getTrainingType());
        assertEquals(date, training.getTrainingDate());
        assertEquals(60, training.getTrainingDuration());
    }

    @Test
    void testTrainingAllArgsConstructor() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        TrainingType type = new TrainingType(1L, "Cardio");
        LocalDate date = LocalDate.now();

        Training training = new Training(1L, trainee, trainer, "Morning Cardio", type, date, 60);

        assertEquals(1L, training.getId());
        assertEquals(trainee, training.getTrainee());
        assertEquals(trainer, training.getTrainer());
        assertEquals("Morning Cardio", training.getTrainingName());
        assertEquals(type, training.getTrainingType());
        assertEquals(date, training.getTrainingDate());
        assertEquals(60, training.getTrainingDuration());
    }

    @Test
    void testTrainingNoArgsConstructor() {
        Training training = new Training();
        assertNotNull(training);
    }
}