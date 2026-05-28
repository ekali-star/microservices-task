package com.example.gymcrm.service;

import com.example.gymcrm.dto.Auth;
import com.example.gymcrm.model.*;
import com.example.gymcrm.repository.TrainingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    private TrainingService trainingService;

    private Trainee trainee;
    private Trainer trainer;
    private Training training;
    private Auth traineeAuth;
    private Auth trainerAuth;

    @BeforeEach
    void setUp() {
        trainingService = new TrainingService(trainingRepository, traineeService, trainerService);

        User traineeUser = new User(1L, "John", "Doe", "john.doe", "pass", true);
        trainee = new Trainee(1L, LocalDate.now(), "Address", traineeUser, List.of(), List.of());

        User trainerUser = new User(2L, "Mike", "Smith", "mike.smith", "pass", true);
        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(trainerUser);
        trainer.setSpecialization(new TrainingType(1L, "Cardio"));

        training = new Training(1L, trainee, trainer, "Training",
                new TrainingType(1L, "Cardio"), LocalDate.now(), 60);

        traineeAuth = new Auth("john.doe", "pass");
        trainerAuth = new Auth("mike.smith", "pass");
    }

    @Test
    void createTraining_ShouldSaveAndReturnTraining() {
        when(traineeService.findByUsername("john.doe")).thenReturn(trainee);
        when(trainerService.findByUsername("mike.smith")).thenReturn(trainer);
        when(trainingRepository.save(training)).thenReturn(training);

        Training result = trainingService.createTraining("john.doe", "mike.smith", training);

        assertEquals(training, result);
        verify(trainingRepository).save(training);
    }

    @Test
    void createTraining_ShouldThrowWhenTraineeNotFound() {
        when(traineeService.findByUsername("unknown")).thenReturn(null);
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.createTraining("unknown", "mike.smith", training));
    }

    @Test
    void createTraining_ShouldThrowWhenTrainerNotFound() {
        when(traineeService.findByUsername("john.doe")).thenReturn(trainee);
        when(trainerService.findByUsername("unknown")).thenReturn(null);
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.createTraining("john.doe", "unknown", training));
    }

    @Test
    void createTraining_ShouldThrowWhenTraineeInactive() {
        trainee.getUser().setIsActive(false);
        when(traineeService.findByUsername("john.doe")).thenReturn(trainee);
        when(trainerService.findByUsername("mike.smith")).thenReturn(trainer);
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.createTraining("john.doe", "mike.smith", training));
    }

    @Test
    void getTraineeTrainings_ShouldReturnList() {
        when(traineeService.authenticate(traineeAuth)).thenReturn(true);
        when(trainingRepository.findTraineeTrainings(eq("john.doe"), any(), any(), any(), any()))
                .thenReturn(List.of(training));

        List<Training> result = trainingService.getTraineeTrainings(traineeAuth, null, null, null, null);

        assertEquals(1, result.size());
    }

    @Test
    void getTraineeTrainings_ShouldThrowWhenAuthFails() {
        when(traineeService.authenticate(traineeAuth)).thenReturn(false);
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.getTraineeTrainings(traineeAuth, null, null, null, null));
    }

    @Test
    void getTrainerTrainings_ShouldReturnList() {
        when(trainerService.authenticate(trainerAuth)).thenReturn(true);
        when(trainingRepository.findTrainerTrainings(eq("mike.smith"), any(), any(), any()))
                .thenReturn(List.of(training));

        List<Training> result = trainingService.getTrainerTrainings(trainerAuth, null, null, null);

        assertEquals(1, result.size());
    }

    @Test
    void getTrainerTrainings_ShouldThrowWhenAuthFails() {
        when(trainerService.authenticate(trainerAuth)).thenReturn(false);
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.getTrainerTrainings(trainerAuth, null, null, null));
    }

    @Test
    void findById_ShouldReturnTraining() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        assertTrue(trainingService.findById(1L).isPresent());
    }

    @Test
    void findAll_ShouldReturnAllTrainings() {
        when(trainingRepository.findAll()).thenReturn(List.of(training));
        assertEquals(1, trainingService.findAll().size());
    }
}