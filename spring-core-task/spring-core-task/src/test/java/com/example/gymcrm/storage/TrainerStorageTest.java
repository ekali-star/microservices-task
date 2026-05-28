package com.example.gymcrm.storage;

import com.example.gymcrm.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TrainerStorageTest {

    private TrainerStorage trainerStorage;

    @BeforeEach
    void setUp() {
        trainerStorage = new TrainerStorage();
    }

    @Test
    void getStorage_ShouldReturnEmptyMap_WhenNoTrainersAdded() {
        Map<Long, Trainer> storage = trainerStorage.getStorage();

        assertNotNull(storage);
        assertTrue(storage.isEmpty());
    }

    @Test
    void getStorage_ShouldReturnMapWithTrainers_WhenTrainersAdded() {
        Map<Long, Trainer> storage = trainerStorage.getStorage();
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("Jane");

        storage.put(1L, trainer);

        assertEquals(1, storage.size());
        assertEquals(trainer, storage.get(1L));
    }

    @Test
    void getStorage_ShouldReturnSameInstance_WhenCalledMultipleTimes() {
        Map<Long, Trainer> storage1 = trainerStorage.getStorage();
        Map<Long, Trainer> storage2 = trainerStorage.getStorage();

        assertSame(storage1, storage2);
    }
}