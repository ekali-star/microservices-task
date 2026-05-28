package com.example.gymcrm.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrainingTypeTest {

    @Test
    void testTrainingTypeGettersAndSetters() {
        TrainingType type = new TrainingType();

        type.setId(1L);
        type.setTrainingTypeName("Cardio");

        assertEquals(1L, type.getId());
        assertEquals("Cardio", type.getTrainingTypeName());
    }

    @Test
    void testTrainingTypeAllArgsConstructor() {
        TrainingType type = new TrainingType(1L, "Cardio");

        assertEquals(1L, type.getId());
        assertEquals("Cardio", type.getTrainingTypeName());
    }

    @Test
    void testTrainingTypeNoArgsConstructor() {
        TrainingType type = new TrainingType();
        assertNotNull(type);
    }
}