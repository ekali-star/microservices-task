package com.example.gymcrm.storage;

import com.example.gymcrm.model.Training;
import com.example.gymcrm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TrainingStorageTest {

    private TrainingStorage trainingStorage;

    @BeforeEach
    void setUp() {
        trainingStorage = new TrainingStorage();
    }

    @Test
    void getStorage_ShouldReturnEmptyMap_WhenNoTrainingsAdded() {
        Map<Long, Training> storage = trainingStorage.getStorage();

        assertNotNull(storage);
        assertTrue(storage.isEmpty());
    }

    @Test
    void getStorage_ShouldReturnMapWithTrainings_WhenTrainingsAdded() {
        Map<Long, Training> storage = trainingStorage.getStorage();
        Training training = new Training();
        training.setId(1L);
        training.setTraineeId(1L);
        training.setTrainerId(1L);
        training.setTrainingName("Yoga Class");
        training.setTrainingType(TrainingType.YOGA);
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60);

        storage.put(1L, training);

        assertEquals(1, storage.size());
        assertEquals(training, storage.get(1L));
        assertEquals(TrainingType.YOGA, storage.get(1L).getTrainingType());
    }

    @Test
    void getStorage_ShouldReturnSameInstance_WhenCalledMultipleTimes() {
        Map<Long, Training> storage1 = trainingStorage.getStorage();
        Map<Long, Training> storage2 = trainingStorage.getStorage();

        assertSame(storage1, storage2);
    }

    @Test
    void getStorage_ShouldAllowMultipleTrainings() {
        Map<Long, Training> storage = trainingStorage.getStorage();

        Training training1 = new Training();
        training1.setId(1L);
        training1.setTrainingName("Yoga");
        training1.setTrainingType(TrainingType.YOGA);

        Training training2 = new Training();
        training2.setId(2L);
        training2.setTrainingName("Cardio");
        training2.setTrainingType(TrainingType.CARDIO);

        storage.put(1L, training1);
        storage.put(2L, training2);

        assertEquals(2, storage.size());
        assertEquals("Yoga", storage.get(1L).getTrainingName());
        assertEquals("Cardio", storage.get(2L).getTrainingName());
        assertEquals(TrainingType.YOGA, storage.get(1L).getTrainingType());
        assertEquals(TrainingType.CARDIO, storage.get(2L).getTrainingType());
    }
}