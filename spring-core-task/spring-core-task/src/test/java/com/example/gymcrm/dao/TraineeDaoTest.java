package com.example.gymcrm.dao;

import com.example.gymcrm.model.Trainee;
import com.example.gymcrm.storage.TraineeStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeDaoTest {

    @Mock
    private TraineeStorage traineeStorage;

    @InjectMocks
    private TraineeDao traineeDao;

    private Map<Long, Trainee> storageMap;
    private Trainee trainee;

    @BeforeEach
    void setUp() {
        storageMap = new HashMap<>();
        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        when(traineeStorage.getStorage()).thenReturn(storageMap);
    }

    @Test
    void save_ShouldAddTraineeToStorage() {
        traineeDao.save(1L, trainee);

        assertTrue(storageMap.containsKey(1L));
        assertEquals(trainee, storageMap.get(1L));
        verify(traineeStorage, times(1)).getStorage();
    }

    @Test
    void findById_ShouldReturnTrainee_WhenExists() {
        storageMap.put(1L, trainee);

        Trainee result = traineeDao.findById(1L);

        assertEquals(trainee, result);
    }

    @Test
    void findById_ShouldReturnNull_WhenNotExists() {
        Trainee result = traineeDao.findById(999L);

        assertNull(result);
    }

    @Test
    void delete_ShouldRemoveTraineeFromStorage() {
        storageMap.put(1L, trainee);

        traineeDao.delete(1L);

        assertFalse(storageMap.containsKey(1L));
    }

    @Test
    void findAll_ShouldReturnAllTrainees() {
        storageMap.put(1L, trainee);
        Trainee trainee2 = new Trainee();
        trainee2.setId(2L);
        trainee2.setFirstName("Jane");
        storageMap.put(2L, trainee2);

        Collection<Trainee> result = traineeDao.findAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(trainee));
        assertTrue(result.contains(trainee2));
    }
}