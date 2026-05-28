package com.example.gymcrm.facade;

import com.example.gymcrm.dto.Auth;
import com.example.gymcrm.dto.AuthCredentials;
import com.example.gymcrm.dto.request.*;
import com.example.gymcrm.dto.response.*;
import com.example.gymcrm.model.*;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private GymFacade facade;

    @Test
    void login_success_whenTraineeAuthenticated() {
        when(traineeService.authenticate("user", "pass")).thenReturn(true);

        assertDoesNotThrow(() ->
                facade.login(new LoginRequest("user", "pass"))
        );
    }

    @Test
    void login_fail_whenBothFail() {
        when(traineeService.authenticate(any(), any())).thenReturn(false);
        when(trainerService.authenticate(any(), any())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                facade.login(new LoginRequest("user", "wrong"))
        );
    }

    @Test
    void changePassword_traineePath() {
        ChangePasswordRequest req =
                new ChangePasswordRequest("user", "old", "new");

        when(traineeService.authenticate(any(Auth.class))).thenReturn(true);

        facade.changePassword("user", req);

        verify(traineeService).changePassword(any(Auth.class), eq("new"));
    }

    @Test
    void changePassword_trainerPath() {
        ChangePasswordRequest req = new ChangePasswordRequest("user", "old", "new");

        when(traineeService.authenticate(any())).thenReturn(false);
        when(trainerService.authenticate(any())).thenReturn(true);

        facade.changePassword("user", req);

        verify(trainerService).changePassword(any(Auth.class), eq("new"));
    }

    @Test
    void changePassword_fail() {
        when(traineeService.authenticate(any())).thenReturn(false);
        when(trainerService.authenticate(any())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                facade.changePassword("user", new ChangePasswordRequest("u","o","n"))
        );
    }

    @Test
    void createTrainee_callsService() {
        TraineeRegistrationRequest req = new TraineeRegistrationRequest();
        req.setFirstName("John");
        req.setLastName("Doe");

        AuthCredentials creds = new AuthCredentials("john.doe", "pass");
        when(traineeService.create(any())).thenReturn(creds);

        AuthCredentials result = facade.createTrainee(req);

        assertEquals("john.doe", result.getUsername());
    }

    @Test
    void deleteTrainee_callsService() {
        facade.deleteTrainee("user");
        verify(traineeService).deleteTrainee("user");
    }

    @Test
    void setTraineeActiveStatus_callsService() {
        facade.setTraineeActiveStatus("user", true);
        verify(traineeService).setActiveStatus("user", true);
    }

    @Test
    void getUnassignedTrainers_mapsCorrectly() {
        Trainer trainer = new Trainer();
        User user = User.builder().username("t1").firstName("A").lastName("B").build();
        TrainingType type = TrainingType.builder().trainingTypeName("Yoga").build();

        trainer.setUser(user);
        trainer.setSpecialization(type);

        when(traineeService.getUnassignedTrainers("user"))
                .thenReturn(List.of(trainer));

        List<TrainerSummaryDTO> result = facade.getUnassignedTrainers("user");

        assertEquals(1, result.size());
        assertEquals("t1", result.get(0).getUsername());
    }

    @Test
    void createTraining_callsService() {
        AddTrainingRequest req = new AddTrainingRequest();
        req.setTrainingName("Test");
        req.setTraineeUsername("trainee");
        req.setTrainerUsername("trainer");

        facade.createTraining(req);

        verify(trainingService).createTraining(eq("trainee"), eq("trainer"), any(Training.class));
    }

    @Test
    void changePassword_usesMethodUsername_notRequestUsername() {
        ChangePasswordRequest req =
                new ChangePasswordRequest("WRONG_USER", "old", "new");

        when(traineeService.authenticate(any(Auth.class))).thenReturn(true);

        facade.changePassword("realUser", req);

        ArgumentCaptor<Auth> captor = ArgumentCaptor.forClass(Auth.class);
        verify(traineeService).changePassword(captor.capture(), eq("new"));

        assertEquals("realUser", captor.getValue().getUsername());
    }
}