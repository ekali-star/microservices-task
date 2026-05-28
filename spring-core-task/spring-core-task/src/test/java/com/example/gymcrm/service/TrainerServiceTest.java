package com.example.gymcrm.service;

import com.example.gymcrm.dto.Auth;
import com.example.gymcrm.dto.AuthCredentials;
import com.example.gymcrm.metric.UserMetrics;
import com.example.gymcrm.model.Trainer;
import com.example.gymcrm.model.TrainingType;
import com.example.gymcrm.model.User;
import com.example.gymcrm.repository.TrainerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMetrics userMetrics;

    @InjectMocks
    private TrainerService trainerService;

    @Test
    void create_success_generatesCredentials_andIncrementsMetrics() {
        Trainer trainer = new Trainer();
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .build();
        trainer.setUser(user);

        when(trainerRepository.findAll()).thenReturn(List.of());
        when(passwordEncoder.encode(any())).thenReturn("encodedPass");
        when(trainerRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AuthCredentials result = trainerService.create(trainer);

        assertNotNull(result.getUsername());
        assertNotNull(result.getPassword());

        assertEquals("encodedPass", trainer.getUser().getPassword());
        assertTrue(trainer.getUser().getIsActive());

        verify(userMetrics).incrementTrainer();
    }

    @Test
    void authenticate_success() {
        User user = User.builder().password("encoded").build();
        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(trainerRepository.findByUserUsername("u"))
                .thenReturn(Optional.of(trainer));
        when(passwordEncoder.matches("raw", "encoded")).thenReturn(true);

        assertTrue(trainerService.authenticate("u", "raw"));
    }

    @Test
    void authenticate_fail_userNotFound() {
        when(trainerRepository.findByUserUsername("u"))
                .thenReturn(Optional.empty());

        assertFalse(trainerService.authenticate("u", "pass"));
    }

    @Test
    void changePassword_success() {
        User user = User.builder().password("encoded").build();
        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(trainerRepository.findByUserUsername("u"))
                .thenReturn(Optional.of(trainer));
        when(passwordEncoder.matches("old", "encoded")).thenReturn(true);
        when(passwordEncoder.encode("new")).thenReturn("newEncoded");

        trainerService.changePassword(new Auth("u", "old"), "new");

        assertEquals("newEncoded", trainer.getUser().getPassword());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void changePassword_fail_wrongPassword() {
        User user = User.builder().password("encoded").build();
        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(trainerRepository.findByUserUsername("u"))
                .thenReturn(Optional.of(trainer));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                trainerService.changePassword(new Auth("u", "wrong"), "new")
        );
    }

    @Test
    void setActiveStatus_success() {
        User user = User.builder().isActive(true).build();
        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(trainerRepository.findByUserUsername("u"))
                .thenReturn(Optional.of(trainer));

        trainerService.setActiveStatus("u", false);

        assertFalse(trainer.getUser().getIsActive());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void setActiveStatus_fail_userNotFound() {
        when(trainerRepository.findByUserUsername("u"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                trainerService.setActiveStatus("u", true)
        );
    }

    @Test
    void updateTrainer_success() {
        Trainer existing = new Trainer();
        User existingUser = User.builder()
                .firstName("Old")
                .lastName("Name")
                .build();
        existing.setUser(existingUser);

        Trainer patch = new Trainer();
        patch.setUser(User.builder()
                .firstName("New")
                .lastName("Name")
                .build());

        when(trainerRepository.findByUserUsername("u"))
                .thenReturn(Optional.of(existing));
        when(trainerRepository.save(any())).thenReturn(existing);

        Trainer result = trainerService.updateTrainer("u", patch);

        assertEquals("New", result.getUser().getFirstName());
        assertEquals("Name", result.getUser().getLastName());
    }

    @Test
    void updateTrainer_fail_notFound() {
        when(trainerRepository.findByUserUsername("u"))
                .thenReturn(Optional.empty());

        Trainer patch = new Trainer();
        patch.setUser(User.builder().firstName("New").lastName("Name").build());

        assertThrows(IllegalArgumentException.class, () ->
                trainerService.updateTrainer("u", patch)
        );
    }
}