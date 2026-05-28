package com.example.gymcrm.storage;

import com.example.gymcrm.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TraineeStorageTest {

    private TraineeStorage traineeStorage;

    @BeforeEach
    void setUp() {
        traineeStorage = new TraineeStorage();
    }

    @Test
    void getStorage_ShouldReturnEmptyMap_WhenNoTraineesAdded() {
        Map<Long, Trainee> storage = traineeStorage.getStorage();

        assertNotNull(storage);
        assertTrue(storage.isEmpty());
    }

    @Test
    void getStorage_ShouldReturnMapWithTrainees_WhenTraineesAdded() {
        Map<Long, Trainee> storage = traineeStorage.getStorage();
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");

        storage.put(1L, trainee);

        assertEquals(1, storage.size());
        assertEquals(trainee, storage.get(1L));
    }

    @Test
    void getStorage_ShouldReturnSameInstance_WhenCalledMultipleTimes() {
        Map<Long, Trainee> storage1 = traineeStorage.getStorage();
        Map<Long, Trainee> storage2 = traineeStorage.getStorage();

        assertSame(storage1, storage2);
    }
}