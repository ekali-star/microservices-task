package com.example.gymcrm.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainerTest {

    @Test
    void testTrainerGettersAndSetters() {
        Trainer trainer = new Trainer();
        User user = new User(1L, "Mike", "Smith", "mike.smith", "pass", true);
        TrainingType specialization = new TrainingType(1L, "Cardio");

        trainer.setId(1L);
        trainer.setSpecialization(specialization);
        trainer.setUser(user);
        trainer.setTrainees(new ArrayList<>());
        trainer.setTrainings(new ArrayList<>());

        assertEquals(1L, trainer.getId());
        assertEquals(specialization, trainer.getSpecialization());
        assertEquals(user, trainer.getUser());
        assertNotNull(trainer.getTrainees());
        assertNotNull(trainer.getTrainings());
    }

    @Test
    void testTrainerAllArgsConstructor() {
        User user = new User(1L, "Mike", "Smith", "mike.smith", "pass", true);
        TrainingType specialization = new TrainingType(1L, "Cardio");
        List<Trainee> trainees = new ArrayList<>();
        List<Training> trainings = new ArrayList<>();

        Trainer trainer = new Trainer(1L, specialization, user, trainees, trainings);

        assertEquals(1L, trainer.getId());
        assertEquals(specialization, trainer.getSpecialization());
        assertEquals(user, trainer.getUser());
        assertEquals(trainees, trainer.getTrainees());
        assertEquals(trainings, trainer.getTrainings());
    }

    @Test
    void testTrainerNoArgsConstructor() {
        Trainer trainer = new Trainer();
        assertNotNull(trainer);
    }
}