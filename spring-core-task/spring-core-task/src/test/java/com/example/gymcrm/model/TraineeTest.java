package com.example.gymcrm.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TraineeTest {

    @Test
    void testTraineeGettersAndSetters() {
        Trainee trainee = new Trainee();
        User user = new User(1L, "John", "Doe", "john.doe", "pass", true);
        LocalDate dob = LocalDate.of(1990, 1, 1);

        trainee.setId(1L);
        trainee.setDateOfBirth(dob);
        trainee.setAddress("123 Main St");
        trainee.setUser(user);
        trainee.setTrainers(new ArrayList<>());
        trainee.setTrainings(new ArrayList<>());

        assertEquals(1L, trainee.getId());
        assertEquals(dob, trainee.getDateOfBirth());
        assertEquals("123 Main St", trainee.getAddress());
        assertEquals(user, trainee.getUser());
        assertNotNull(trainee.getTrainers());
        assertNotNull(trainee.getTrainings());
    }

    @Test
    void testTraineeAllArgsConstructor() {
        User user = new User(1L, "John", "Doe", "john.doe", "pass", true);
        LocalDate dob = LocalDate.of(1990, 1, 1);
        List<Trainer> trainers = new ArrayList<>();
        List<Training> trainings = new ArrayList<>();

        Trainee trainee = new Trainee(1L, dob, "123 Main St", user, trainers, trainings);

        assertEquals(1L, trainee.getId());
        assertEquals(dob, trainee.getDateOfBirth());
        assertEquals("123 Main St", trainee.getAddress());
        assertEquals(user, trainee.getUser());
        assertEquals(trainers, trainee.getTrainers());
        assertEquals(trainings, trainee.getTrainings());
    }

    @Test
    void testTraineeNoArgsConstructor() {
        Trainee trainee = new Trainee();
        assertNotNull(trainee);
    }

    @Test
    void testTraineeGetUser() {
        User user = new User(1L, "John", "Doe", "john.doe", "pass", true);
        Trainee trainee = new Trainee(1L, null, null, user, null, null);

        assertEquals(user, trainee.getUser());
    }
}