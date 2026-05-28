package com.example.gymcrm.facade;

import com.example.gymcrm.dto.Auth;
import com.example.gymcrm.dto.AuthCredentials;
import com.example.gymcrm.model.*;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GymFacadeTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    private GymFacade gymFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gymFacade = new GymFacade(traineeService, trainerService, trainingService);
    }

    @Test
    void createTrainee_shouldCallService() {
        Trainee trainee = mock(Trainee.class);
        AuthCredentials credentials = new AuthCredentials("john", "pass");
        when(traineeService.create(trainee)).thenReturn(credentials);

        assertEquals(credentials, gymFacade.createTrainee(trainee));
        verify(traineeService).create(trainee);
    }

    @Test
    void createTrainer_shouldCallService() {
        Trainer trainer = mock(Trainer.class);
        AuthCredentials credentials = new AuthCredentials("mike", "pass");
        when(trainerService.create(trainer)).thenReturn(credentials);

        assertEquals(credentials, gymFacade.createTrainer(trainer));
        verify(trainerService).create(trainer);
    }

    @Test
    void createTraining_shouldCallService() {
        Training training = mock(Training.class);
        when(trainingService.createTraining("trainee", "trainer", training)).thenReturn(training);

        assertEquals(training, gymFacade.createTraining("trainee", "trainer", training));
        verify(trainingService).createTraining("trainee", "trainer", training);
    }

    @Test
    void getTraineeById_shouldReturnTrainee() {
        Trainee trainee = mock(Trainee.class);
        when(traineeService.findById(1L)).thenReturn(trainee);

        assertEquals(trainee, gymFacade.getTraineeById(1L));
        verify(traineeService).findById(1L);
    }

    @Test
    void getTraineeById_shouldReturnNullWhenNotFound() {
        when(traineeService.findById(99L)).thenReturn(null);

        assertNull(gymFacade.getTraineeById(99L));
        verify(traineeService).findById(99L);
    }

    @Test
    void getTraineeByUsername_shouldReturnTrainee() {
        Trainee trainee = mock(Trainee.class);
        when(traineeService.findByUsername("john")).thenReturn(trainee);

        assertEquals(trainee, gymFacade.getTraineeByUsername("john"));
        verify(traineeService).findByUsername("john");
    }

    @Test
    void getTraineeByUsername_shouldReturnNullWhenNotFound() {
        when(traineeService.findByUsername("unknown")).thenReturn(null);

        assertNull(gymFacade.getTraineeByUsername("unknown"));
        verify(traineeService).findByUsername("unknown");
    }

    @Test
    void getAllTrainees_shouldReturnList() {
        Trainee t1 = mock(Trainee.class);
        Trainee t2 = mock(Trainee.class);
        when(traineeService.findAll()).thenReturn(List.of(t1, t2));

        Collection<Trainee> result = gymFacade.getAllTrainees();
        assertEquals(2, result.size());
        assertTrue(result.contains(t1));
        assertTrue(result.contains(t2));
        verify(traineeService).findAll();
    }

    @Test
    void getAllTrainees_shouldReturnEmptyList() {
        when(traineeService.findAll()).thenReturn(List.of());

        Collection<Trainee> result = gymFacade.getAllTrainees();
        assertTrue(result.isEmpty());
        verify(traineeService).findAll();
    }

    @Test
    void getTrainerById_shouldReturnTrainer() {
        Trainer trainer = mock(Trainer.class);
        when(trainerService.findById(1L)).thenReturn(trainer);

        assertEquals(trainer, gymFacade.getTrainerById(1L));
        verify(trainerService).findById(1L);
    }

    @Test
    void getTrainerById_shouldReturnNullWhenNotFound() {
        when(trainerService.findById(99L)).thenReturn(null);

        assertNull(gymFacade.getTrainerById(99L));
        verify(trainerService).findById(99L);
    }

    @Test
    void getTrainerByUsername_shouldReturnTrainer() {
        Trainer trainer = mock(Trainer.class);
        when(trainerService.findByUsername("mike")).thenReturn(trainer);

        assertEquals(trainer, gymFacade.getTrainerByUsername("mike"));
        verify(trainerService).findByUsername("mike");
    }

    @Test
    void getTrainerByUsername_shouldReturnNullWhenNotFound() {
        when(trainerService.findByUsername("unknown")).thenReturn(null);

        assertNull(gymFacade.getTrainerByUsername("unknown"));
        verify(trainerService).findByUsername("unknown");
    }

    @Test
    void getAllTrainers_shouldReturnList() {
        Trainer tr1 = mock(Trainer.class);
        Trainer tr2 = mock(Trainer.class);
        when(trainerService.findAll()).thenReturn(List.of(tr1, tr2));

        Collection<Trainer> result = gymFacade.getAllTrainers();
        assertEquals(2, result.size());
        assertTrue(result.contains(tr1));
        assertTrue(result.contains(tr2));
        verify(trainerService).findAll();
    }

    @Test
    void getAllTrainers_shouldReturnEmptyList() {
        when(trainerService.findAll()).thenReturn(List.of());

        Collection<Trainer> result = gymFacade.getAllTrainers();
        assertTrue(result.isEmpty());
        verify(trainerService).findAll();
    }

    @Test
    void getAllTrainings_shouldReturnList() {
        Training tr1 = mock(Training.class);
        Training tr2 = mock(Training.class);
        when(trainingService.findAll()).thenReturn(List.of(tr1, tr2));

        Collection<Training> result = gymFacade.getAllTrainings();
        assertEquals(2, result.size());
        assertTrue(result.contains(tr1));
        assertTrue(result.contains(tr2));
        verify(trainingService).findAll();
    }

    @Test
    void getAllTrainings_shouldReturnEmptyList() {
        when(trainingService.findAll()).thenReturn(List.of());

        Collection<Training> result = gymFacade.getAllTrainings();
        assertTrue(result.isEmpty());
        verify(trainingService).findAll();
    }

    @Test
    void updateTrainee_shouldCallService() {
        Trainee trainee = mock(Trainee.class);
        when(traineeService.updateTrainee(any(Auth.class), eq(trainee))).thenReturn(trainee);

        assertEquals(trainee, gymFacade.updateTrainee("john", "pass", trainee));
        verify(traineeService).updateTrainee(any(Auth.class), eq(trainee));
    }

    @Test
    void updateTrainer_shouldCallService() {
        Trainer trainer = mock(Trainer.class);
        when(trainerService.updateTrainer(any(Auth.class), eq(trainer))).thenReturn(trainer);

        assertEquals(trainer, gymFacade.updateTrainer("mike", "pass", trainer));
        verify(trainerService).updateTrainer(any(Auth.class), eq(trainer));
    }

    @Test
    void updateTraineeTrainers_shouldCallService() {
        List<String> trainerUsernames = List.of("trainer1", "trainer2");
        doNothing().when(traineeService).updateTrainers(any(Auth.class), eq(trainerUsernames));

        gymFacade.updateTraineeTrainers("john", "pass", trainerUsernames);
        verify(traineeService).updateTrainers(any(Auth.class), eq(trainerUsernames));
    }

    @Test
    void updateTraineeTrainers_shouldHandleEmptyList() {
        List<String> trainerUsernames = List.of();
        doNothing().when(traineeService).updateTrainers(any(Auth.class), eq(trainerUsernames));

        gymFacade.updateTraineeTrainers("john", "pass", trainerUsernames);
        verify(traineeService).updateTrainers(any(Auth.class), eq(trainerUsernames));
    }

    @Test
    void deleteTrainee_shouldCallService() {
        doNothing().when(traineeService).deleteTrainee(any(Auth.class));

        gymFacade.deleteTrainee("john", "pass");
        verify(traineeService).deleteTrainee(any(Auth.class));
    }

    @Test
    void changeTraineePassword_shouldCallService() {
        doNothing().when(traineeService).changePassword(any(Auth.class), eq("newPass"));

        gymFacade.changeTraineePassword("john", "pass", "newPass");
        verify(traineeService).changePassword(any(Auth.class), eq("newPass"));
    }

    @Test
    void changeTrainerPassword_shouldCallService() {
        doNothing().when(trainerService).changePassword(any(Auth.class), eq("newPass"));

        gymFacade.changeTrainerPassword("mike", "pass", "newPass");
        verify(trainerService).changePassword(any(Auth.class), eq("newPass"));
    }

    @Test
    void activateTrainee_shouldCallService() {
        doNothing().when(traineeService).setActiveStatus(any(Auth.class), eq(true));

        gymFacade.activateTrainee("john", "pass");
        verify(traineeService).setActiveStatus(any(Auth.class), eq(true));
    }

    @Test
    void deactivateTrainee_shouldCallService() {
        doNothing().when(traineeService).setActiveStatus(any(Auth.class), eq(false));

        gymFacade.deactivateTrainee("john", "pass");
        verify(traineeService).setActiveStatus(any(Auth.class), eq(false));
    }

    @Test
    void activateTrainer_shouldCallService() {
        doNothing().when(trainerService).setActiveStatus(any(Auth.class), eq(true));

        gymFacade.activateTrainer("mike", "pass");
        verify(trainerService).setActiveStatus(any(Auth.class), eq(true));
    }

    @Test
    void deactivateTrainer_shouldCallService() {
        doNothing().when(trainerService).setActiveStatus(any(Auth.class), eq(false));

        gymFacade.deactivateTrainer("mike", "pass");
        verify(trainerService).setActiveStatus(any(Auth.class), eq(false));
    }

    @Test
    void authenticateTrainee_shouldReturnTrue() {
        when(traineeService.authenticate("john", "pass")).thenReturn(true);

        assertTrue(gymFacade.authenticateTrainee("john", "pass"));
        verify(traineeService).authenticate("john", "pass");
    }

    @Test
    void authenticateTrainee_shouldReturnFalse() {
        when(traineeService.authenticate("john", "wrong")).thenReturn(false);

        assertFalse(gymFacade.authenticateTrainee("john", "wrong"));
        verify(traineeService).authenticate("john", "wrong");
    }

    @Test
    void authenticateTrainer_shouldReturnTrue() {
        when(trainerService.authenticate("mike", "pass")).thenReturn(true);

        assertTrue(gymFacade.authenticateTrainer("mike", "pass"));
        verify(trainerService).authenticate("mike", "pass");
    }

    @Test
    void authenticateTrainer_shouldReturnFalse() {
        when(trainerService.authenticate("mike", "wrong")).thenReturn(false);

        assertFalse(gymFacade.authenticateTrainer("mike", "wrong"));
        verify(trainerService).authenticate("mike", "wrong");
    }

    @Test
    void getTraineeTrainings_shouldCallServiceWithAllParameters() {
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 12, 31);
        List<Training> trainings = List.of(mock(Training.class));
        when(trainingService.getTraineeTrainings(any(Auth.class), eq(fromDate), eq(toDate), eq("trainerName"), eq(1L)))
                .thenReturn(trainings);

        List<Training> result = gymFacade.getTraineeTrainings("john", "pass", fromDate, toDate, "trainerName", 1L);
        assertEquals(1, result.size());
        verify(trainingService).getTraineeTrainings(any(Auth.class), eq(fromDate), eq(toDate), eq("trainerName"), eq(1L));
    }

    @Test
    void getTraineeTrainings_shouldCallServiceWithNullParameters() {
        List<Training> trainings = List.of(mock(Training.class));
        when(trainingService.getTraineeTrainings(any(Auth.class), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(trainings);

        List<Training> result = gymFacade.getTraineeTrainings("john", "pass", null, null, null, null);
        assertEquals(1, result.size());
        verify(trainingService).getTraineeTrainings(any(Auth.class), isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void getTrainerTrainings_shouldCallServiceWithAllParameters() {
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 12, 31);
        List<Training> trainings = List.of(mock(Training.class));
        when(trainingService.getTrainerTrainings(any(Auth.class), eq(fromDate), eq(toDate), eq("traineeName")))
                .thenReturn(trainings);

        List<Training> result = gymFacade.getTrainerTrainings("mike", "pass", fromDate, toDate, "traineeName");
        assertEquals(1, result.size());
        verify(trainingService).getTrainerTrainings(any(Auth.class), eq(fromDate), eq(toDate), eq("traineeName"));
    }

    @Test
    void getTrainerTrainings_shouldCallServiceWithNullParameters() {
        List<Training> trainings = List.of(mock(Training.class));
        when(trainingService.getTrainerTrainings(any(Auth.class), isNull(), isNull(), isNull()))
                .thenReturn(trainings);

        List<Training> result = gymFacade.getTrainerTrainings("mike", "pass", null, null, null);
        assertEquals(1, result.size());
        verify(trainingService).getTrainerTrainings(any(Auth.class), isNull(), isNull(), isNull());
    }

    @Test
    void getUnassignedTrainers_shouldCallService() {
        List<Trainer> trainers = List.of(mock(Trainer.class));
        when(traineeService.getUnassignedTrainers(any(Auth.class))).thenReturn(trainers);

        List<Trainer> result = gymFacade.getUnassignedTrainers("john", "pass");
        assertEquals(1, result.size());
        verify(traineeService).getUnassignedTrainers(any(Auth.class));
    }

    @Test
    void getUnassignedTrainers_shouldReturnEmptyList() {
        when(traineeService.getUnassignedTrainers(any(Auth.class))).thenReturn(List.of());

        List<Trainer> result = gymFacade.getUnassignedTrainers("john", "pass");
        assertTrue(result.isEmpty());
        verify(traineeService).getUnassignedTrainers(any(Auth.class));
    }
}