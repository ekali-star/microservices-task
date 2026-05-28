package com.example.gymcrm.dao;

import com.example.gymcrm.model.Training;
import com.example.gymcrm.model.TrainingType;
import com.example.gymcrm.storage.TrainingStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingDaoTest {

    @Mock
    private TrainingStorage trainingStorage;

    @InjectMocks
    private TrainingDao trainingDao;

    private Map<Long, Training> storageMap;
    private Training training;

    @BeforeEach
    void setUp() {
        storageMap = new HashMap<>();
        training = new Training();
        training.setId(1L);  // Add ID
        training.setTraineeId(1L);
        training.setTrainerId(1L);
        training.setTrainingName("Morning Yoga");
        training.setTrainingType(TrainingType.YOGA);
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60);

        when(trainingStorage.getStorage()).thenReturn(storageMap);
    }

    @Test
    void save_ShouldAddTrainingToStorage() {
        trainingDao.save(1L, training);

        assertTrue(storageMap.containsKey(1L));
        assertEquals(training, storageMap.get(1L));
        verify(trainingStorage, times(1)).getStorage();
    }

    @Test
    void findById_ShouldReturnTraining_WhenExists() {
        storageMap.put(1L, training);

        Training result = trainingDao.findById(1L);

        assertEquals(training, result);
        assertEquals(TrainingType.YOGA, result.getTrainingType());
    }

    @Test
    void findById_ShouldReturnNull_WhenNotExists() {
        Training result = trainingDao.findById(999L);

        assertNull(result);
    }

    @Test
    void delete_ShouldRemoveTrainingFromStorage() {
        storageMap.put(1L, training);

        trainingDao.delete(1L);

        assertFalse(storageMap.containsKey(1L));
    }

    @Test
    void findAll_ShouldReturnAllTrainings() {
        storageMap.put(1L, training);
        Training training2 = new Training();
        training2.setId(2L);
        training2.setTraineeId(2L);
        training2.setTrainerId(2L);
        training2.setTrainingName("Evening Cardio");
        training2.setTrainingType(TrainingType.CARDIO);
        training2.setTrainingDate(LocalDate.now());
        training2.setTrainingDuration(45);
        storageMap.put(2L, training2);

        Collection<Training> result = trainingDao.findAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(training));
        assertTrue(result.contains(training2));
    }
}