package com.example.gymcrm.dao;

import com.example.gymcrm.model.Trainer;
import com.example.gymcrm.storage.TrainerStorage;
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
class TrainerDaoTest {

    @Mock
    private TrainerStorage trainerStorage;

    @InjectMocks
    private TrainerDao trainerDao;

    private Map<Long, Trainer> storageMap;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        storageMap = new HashMap<>();
        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");
        trainer.setSpecialization("Yoga");
        when(trainerStorage.getStorage()).thenReturn(storageMap);
    }

    @Test
    void save_ShouldAddTrainerToStorage() {
        trainerDao.save(1L, trainer);

        assertTrue(storageMap.containsKey(1L));
        assertEquals(trainer, storageMap.get(1L));
        verify(trainerStorage, times(1)).getStorage();
    }

    @Test
    void findById_ShouldReturnTrainer_WhenExists() {
        storageMap.put(1L, trainer);

        Trainer result = trainerDao.findById(1L);

        assertEquals(trainer, result);
    }

    @Test
    void findById_ShouldReturnNull_WhenNotExists() {
        Trainer result = trainerDao.findById(999L);

        assertNull(result);
    }

    @Test
    void delete_ShouldRemoveTrainerFromStorage() {
        storageMap.put(1L, trainer);

        trainerDao.delete(1L);

        assertFalse(storageMap.containsKey(1L));
    }

    @Test
    void findAll_ShouldReturnAllTrainers() {
        storageMap.put(1L, trainer);
        Trainer trainer2 = new Trainer();
        trainer2.setId(2L);
        trainer2.setFirstName("Mike");
        storageMap.put(2L, trainer2);

        Collection<Trainer> result = trainerDao.findAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(trainer));
        assertTrue(result.contains(trainer2));
    }
}