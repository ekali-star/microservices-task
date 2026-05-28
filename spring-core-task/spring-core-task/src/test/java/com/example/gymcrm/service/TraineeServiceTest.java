package com.example.gymcrm.service;

import com.example.gymcrm.dto.Auth;
import com.example.gymcrm.dto.AuthCredentials;
import com.example.gymcrm.model.Trainee;
import com.example.gymcrm.model.Trainer;
import com.example.gymcrm.model.User;
import com.example.gymcrm.repository.TraineeRepository;
import com.example.gymcrm.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    private TraineeService traineeService;

    private User user;
    private Trainee trainee;
    private Auth validAuth;

    @BeforeEach
    void setUp() {
        traineeService = new TraineeService(traineeRepository, trainerRepository);
        user = new User(1L, "John", "Doe", "john.doe", "password", true);
        trainee = new Trainee(1L, LocalDate.now(), "Address", user, new ArrayList<>(), new ArrayList<>());
        validAuth = new Auth("john.doe", "password");
    }

    @Test
    void create_ShouldGenerateCredentialsAndSave() {
        when(traineeRepository.findAll()).thenReturn(List.of());
        when(traineeRepository.save(any())).thenReturn(trainee);

        try (MockedStatic<CredentialsGenerator> mocked = mockStatic(CredentialsGenerator.class)) {
            mocked.when(() -> CredentialsGenerator.generateUsername(any(), any(), anyList()))
                    .thenReturn("john.doe");
            mocked.when(CredentialsGenerator::generatePassword).thenReturn("password");

            AuthCredentials result = traineeService.create(trainee);

            assertEquals("john.doe", result.getUsername());
            assertEquals("password", result.getPassword());
        }
    }

    @Test
    void findById_ShouldReturnTrainee() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        assertEquals(trainee, traineeService.findById(1L));
    }

    @Test
    void findById_ShouldReturnNullWhenNotFound() {
        when(traineeRepository.findById(99L)).thenReturn(Optional.empty());
        assertNull(traineeService.findById(99L));
    }

    @Test
    void findByUsername_ShouldReturnTrainee() {
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        assertEquals(trainee, traineeService.findByUsername("john.doe"));
    }

    @Test
    void findByUsername_ShouldReturnNullWhenNotFound() {
        when(traineeRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());
        assertNull(traineeService.findByUsername("unknown"));
    }

    @Test
    void findAll_ShouldReturnAllTrainees() {
        when(traineeRepository.findAll()).thenReturn(List.of(trainee));
        assertEquals(1, traineeService.findAll().size());
    }

    @Test
    void authenticate_ShouldReturnTrueForValidCredentials() {
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        assertTrue(traineeService.authenticate("john.doe", "password"));
    }

    @Test
    void authenticate_ShouldReturnFalseForInvalidPassword() {
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        assertFalse(traineeService.authenticate("john.doe", "wrong"));
    }

    @Test
    void authenticate_ShouldReturnFalseForUnknownUser() {
        when(traineeRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());
        assertFalse(traineeService.authenticate("unknown", "pass"));
    }

    @Test
    void authenticateWithAuth_ShouldReturnTrueForValidCredentials() {
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        assertTrue(traineeService.authenticate(validAuth));
    }

    @Test
    void changePassword_ShouldUpdatePassword() {
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        traineeService.changePassword(validAuth, "newPass");
        assertEquals("newPass", user.getPassword());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void changePassword_ShouldThrowWhenAuthFails() {
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        assertThrows(IllegalArgumentException.class,
                () -> traineeService.changePassword(new Auth("john.doe", "wrong"), "newPass"));
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void changePassword_ShouldThrowWhenUserNotFound() {
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> traineeService.changePassword(validAuth, "newPass"));
    }

    @Test
    void setActiveStatus_ShouldUpdateStatus() {
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        traineeService.setActiveStatus(validAuth, false);
        assertFalse(user.getIsActive());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void setActiveStatus_ShouldThrowWhenAuthFails() {
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        assertThrows(IllegalArgumentException.class,
                () -> traineeService.setActiveStatus(new Auth("john.doe", "wrong"), true));
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void updateTrainee_ShouldUpdateFields() {
        Trainee updated = new Trainee(null, LocalDate.now(), "New Address",
                new User(null, "Jane", "Smith", null, null, true), null, null);

        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        Trainee result = traineeService.updateTrainee(validAuth, updated);

        assertEquals("New Address", result.getAddress());
        assertEquals("Jane", result.getUser().getFirstName());
        assertEquals("Smith", result.getUser().getLastName());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void updateTrainee_ShouldThrowWhenAuthFails() {
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        assertThrows(IllegalArgumentException.class,
                () -> traineeService.updateTrainee(new Auth("john.doe", "wrong"), new Trainee()));
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void deleteTrainee_ShouldDelete() {
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));

        traineeService.deleteTrainee(validAuth);
        verify(traineeRepository).delete(trainee);
    }

    @Test
    void deleteTrainee_ShouldThrowWhenAuthFails() {
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        assertThrows(IllegalArgumentException.class,
                () -> traineeService.deleteTrainee(new Auth("john.doe", "wrong")));
        verify(traineeRepository, never()).delete(any());
    }

    @Test
    void getUnassignedTrainers_ShouldReturnList() {
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findNotAssignedToTrainee("john.doe")).thenReturn(List.of(new Trainer()));

        assertEquals(1, traineeService.getUnassignedTrainers(validAuth).size());
    }

    @Test
    void getUnassignedTrainers_ShouldThrowWhenAuthFails() {
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        assertThrows(IllegalArgumentException.class,
                () -> traineeService.getUnassignedTrainers(new Auth("john.doe", "wrong")));
    }

    @Test
    void updateTrainers_ShouldAddAndRemoveTrainers() {
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));

        Trainer existing = new Trainer();
        existing.setUser(new User(2L, "Keep", "Trainer", "keep", "pass", true));
        trainee.getTrainers().add(existing);

        Trainer newTrainer = new Trainer();
        newTrainer.setUser(new User(3L, "New", "Trainer", "new", "pass", true));

        when(traineeRepository.findWithTrainersByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsernameIn(Set.of("new"))).thenReturn(List.of(newTrainer));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        traineeService.updateTrainers(validAuth, List.of("keep", "new"));

        assertTrue(trainee.getTrainers().contains(existing));
        assertTrue(trainee.getTrainers().contains(newTrainer));
        assertEquals(2, trainee.getTrainers().size());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void updateTrainers_ShouldRemoveAllWhenEmptyList() {
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));

        Trainer trainer = new Trainer();
        trainer.setUser(new User(2L, "Some", "Trainer", "some", "pass", true));
        trainee.getTrainers().add(trainer);

        when(traineeRepository.findWithTrainersByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsernameIn(Set.of())).thenReturn(List.of());
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        traineeService.updateTrainers(validAuth, List.of());

        assertTrue(trainee.getTrainers().isEmpty());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void updateTrainers_ShouldThrowWhenTrainerNotFound() {
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(traineeRepository.findWithTrainersByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsernameIn(Set.of("nonexistent"))).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.updateTrainers(validAuth, List.of("nonexistent")));
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void updateTrainers_ShouldThrowWhenAuthFails() {
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        assertThrows(IllegalArgumentException.class,
                () -> traineeService.updateTrainers(new Auth("john.doe", "wrong"), List.of("trainer")));
    }
}