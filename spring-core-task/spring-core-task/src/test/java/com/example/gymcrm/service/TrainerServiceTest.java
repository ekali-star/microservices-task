package com.example.gymcrm.service;

import com.example.gymcrm.dto.Auth;
import com.example.gymcrm.dto.AuthCredentials;
import com.example.gymcrm.model.Trainer;
import com.example.gymcrm.model.TrainingType;
import com.example.gymcrm.model.User;
import com.example.gymcrm.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    private TrainerService trainerService;

    private User user;
    private Trainer trainer;
    private Auth validAuth;

    @BeforeEach
    void setUp() {
        trainerService = new TrainerService(trainerRepository);
        user = new User(1L, "Mike", "Smith", "mike.smith", "password", true);
        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(user);
        trainer.setSpecialization(new TrainingType(1L, "Cardio"));
        validAuth = new Auth("mike.smith", "password");
    }

    @Test
    void create_ShouldGenerateCredentialsAndSave() {
        when(trainerRepository.findAll()).thenReturn(List.of());
        when(trainerRepository.save(any())).thenReturn(trainer);

        try (MockedStatic<CredentialsGenerator> mocked = mockStatic(CredentialsGenerator.class)) {
            mocked.when(() -> CredentialsGenerator.generateUsername(any(), any(), anyList()))
                    .thenReturn("mike.smith");
            mocked.when(CredentialsGenerator::generatePassword).thenReturn("password");

            AuthCredentials result = trainerService.create(trainer);

            assertEquals("mike.smith", result.getUsername());
            assertEquals("password", result.getPassword());
        }
    }

    @Test
    void findById_ShouldReturnTrainer() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        assertEquals(trainer, trainerService.findById(1L));
    }

    @Test
    void findByUsername_ShouldReturnTrainer() {
        when(trainerRepository.findByUserUsername("mike.smith")).thenReturn(Optional.of(trainer));
        assertEquals(trainer, trainerService.findByUsername("mike.smith"));
    }

    @Test
    void authenticate_ShouldReturnTrueForValidCredentials() {
        when(trainerRepository.findByUserUsername("mike.smith")).thenReturn(Optional.of(trainer));
        assertTrue(trainerService.authenticate("mike.smith", "password"));
    }

    @Test
    void authenticate_ShouldReturnFalseForInvalidPassword() {
        when(trainerRepository.findByUserUsername("mike.smith")).thenReturn(Optional.of(trainer));
        assertFalse(trainerService.authenticate("mike.smith", "wrong"));
    }

    @Test
    void changePassword_ShouldUpdatePassword() {
        when(trainerRepository.findByUserUsername("mike.smith")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        trainerService.changePassword(validAuth, "newPass");
        assertEquals("newPass", user.getPassword());
    }

    @Test
    void changePassword_ShouldThrowWhenAuthFails() {
        when(trainerRepository.findByUserUsername("mike.smith")).thenReturn(Optional.of(trainer));
        assertThrows(IllegalArgumentException.class,
                () -> trainerService.changePassword(new Auth("mike.smith", "wrong"), "newPass"));
    }

    @Test
    void setActiveStatus_ShouldUpdateStatus() {
        when(trainerRepository.findByUserUsername("mike.smith")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        trainerService.setActiveStatus(validAuth, false);
        assertFalse(user.getIsActive());
    }

    @Test
    void updateTrainer_ShouldUpdateFields() {
        Trainer updated = new Trainer();
        updated.setUser(new User(null, "Michael", "Johnson", null, null, true));
        updated.setSpecialization(new TrainingType(2L, "Strength"));

        when(trainerRepository.findByUserUsername("mike.smith")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        Trainer result = trainerService.updateTrainer(validAuth, updated);

        assertEquals("Michael", result.getUser().getFirstName());
        assertEquals("Johnson", result.getUser().getLastName());
        assertEquals("Strength", result.getSpecialization().getTrainingTypeName());
    }

    @Test
    void updateTrainer_ShouldThrowWhenAuthFails() {
        when(trainerRepository.findByUserUsername("mike.smith")).thenReturn(Optional.of(trainer));
        assertThrows(IllegalArgumentException.class,
                () -> trainerService.updateTrainer(new Auth("mike.smith", "wrong"), new Trainer()));
    }
}