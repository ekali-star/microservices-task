package com.example.gymcrm.service;

import com.example.gymcrm.metric.UserMetrics;
import com.example.gymcrm.model.Trainee;
import com.example.gymcrm.model.Trainer;
import com.example.gymcrm.model.User;
import com.example.gymcrm.repository.TraineeRepository;
import com.example.gymcrm.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock private TraineeRepository traineeRepository;
    @Mock private TrainerRepository trainerRepository;
    @Mock private UserMetrics userMetrics;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TraineeService traineeService;

    private Trainee existingTrainee;
    private User traineeUser;

    @BeforeEach
    void setUp() {
        traineeUser = User.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Smith")
                .username("alice.smith")
                .password("encoded")
                .isActive(true)
                .build();

        existingTrainee = new Trainee();
        existingTrainee.setId(1L);
        existingTrainee.setUser(traineeUser);
        existingTrainee.setDateOfBirth(LocalDate.of(1990, 5, 20));
        existingTrainee.setAddress("123 Main St");
        existingTrainee.setTrainers(new ArrayList<>());
        existingTrainee.setTrainings(new ArrayList<>());
    }

    // ── updateTrainee ─────────────────────────────────────────────────────────

    @Test
    void updateTrainee_shouldReturnSavedTrainee_whenFound() {
        Trainee updated = new Trainee();
        updated.setDateOfBirth(LocalDate.of(1992, 3, 10));
        updated.setAddress("456 Oak Ave");
        User updatedUser = User.builder().firstName("Alicia").lastName("Johnson").password("p").build();
        updated.setUser(updatedUser);

        when(traineeRepository.findByUserUsername("alice.smith"))
                .thenReturn(Optional.of(existingTrainee));
        when(traineeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Trainee result = traineeService.updateTrainee("alice.smith", updated);

        assertThat(result.getDateOfBirth()).isEqualTo(LocalDate.of(1992, 3, 10));
        assertThat(result.getAddress()).isEqualTo("456 Oak Ave");
        assertThat(result.getUser().getFirstName()).isEqualTo("Alicia");
        assertThat(result.getUser().getLastName()).isEqualTo("Johnson");
    }

    @Test
    void updateTrainee_shouldCallSaveWithExistingEntity() {
        Trainee update = new Trainee();
        update.setAddress("New Address");
        update.setUser(User.builder().firstName("A").lastName("B").password("p").build());

        when(traineeRepository.findByUserUsername("alice.smith"))
                .thenReturn(Optional.of(existingTrainee));
        when(traineeRepository.save(existingTrainee)).thenReturn(existingTrainee);

        traineeService.updateTrainee("alice.smith", update);

        verify(traineeRepository).save(existingTrainee);
    }

    @Test
    void updateTrainee_shouldThrow_whenTraineeNotFound() {
        when(traineeRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.updateTrainee("unknown", new Trainee()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee not found");
    }

    @Test
    void updateTrainee_shouldNotCallSave_whenTraineeNotFound() {
        when(traineeRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.updateTrainee("unknown", new Trainee()));
        verify(traineeRepository, never()).save(any());
    }

    // ── deleteTrainee ─────────────────────────────────────────────────────────

    @Test
    void deleteTrainee_shouldCallDelete_whenFound() {
        when(traineeRepository.findByUserUsername("alice.smith"))
                .thenReturn(Optional.of(existingTrainee));
        doNothing().when(traineeRepository).delete(existingTrainee);

        traineeService.deleteTrainee("alice.smith");

        verify(traineeRepository).delete(existingTrainee);
    }

    @Test
    void deleteTrainee_shouldThrow_whenNotFound() {
        when(traineeRepository.findByUserUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.deleteTrainee("ghost"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee not found");
    }

    @Test
    void deleteTrainee_shouldNotCallDelete_whenNotFound() {
        when(traineeRepository.findByUserUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.deleteTrainee("ghost"));
        verify(traineeRepository, never()).delete(any());
    }

    // ── getUnassignedTrainers ─────────────────────────────────────────────────

    @Test
    void getUnassignedTrainers_shouldReturnTrainerList() {
        Trainer t1 = makeTrainer(10L, "trainer1");
        Trainer t2 = makeTrainer(11L, "trainer2");

        when(trainerRepository.findNotAssignedToTrainee("alice.smith"))
                .thenReturn(List.of(t1, t2));

        List<Trainer> result = traineeService.getUnassignedTrainers("alice.smith");

        assertThat(result).hasSize(2).containsExactly(t1, t2);
        verify(trainerRepository).findNotAssignedToTrainee("alice.smith");
    }

    @Test
    void getUnassignedTrainers_shouldReturnEmpty_whenAllAssigned() {
        when(trainerRepository.findNotAssignedToTrainee("alice.smith"))
                .thenReturn(List.of());

        List<Trainer> result = traineeService.getUnassignedTrainers("alice.smith");

        assertThat(result).isEmpty();
    }

    // ── updateTrainers ────────────────────────────────────────────────────────

    @Test
    void updateTrainers_shouldAddNewTrainers_whenNotAlreadyAssigned() {
        when(traineeRepository.findWithTrainersByUserUsername("alice.smith"))
                .thenReturn(Optional.of(existingTrainee));

        Trainer newTrainer = makeTrainer(20L, "new.trainer");
        when(trainerRepository.findByUserUsernameIn(anySet()))
                .thenReturn(List.of(newTrainer));
        when(traineeRepository.save(any())).thenReturn(existingTrainee);

        traineeService.updateTrainers("alice.smith", List.of("new.trainer"));

        assertThat(existingTrainee.getTrainers()).contains(newTrainer);
        verify(traineeRepository).save(existingTrainee);
    }

    @Test
    void updateTrainers_shouldRemoveTrainers_notInNewList() {
        Trainer existing = makeTrainer(30L, "old.trainer");
        existingTrainee.getTrainers().add(existing);

        when(traineeRepository.findWithTrainersByUserUsername("alice.smith"))
                .thenReturn(Optional.of(existingTrainee));
        when(trainerRepository.findByUserUsernameIn(anySet()))
                .thenReturn(List.of());
        when(traineeRepository.save(any())).thenReturn(existingTrainee);

        traineeService.updateTrainers("alice.smith", List.of("new.trainer.not.in.db"));
        // "old.trainer" is not in requested list → removed
        assertThat(existingTrainee.getTrainers())
                .noneMatch(t -> t.getUser().getUsername().equals("old.trainer"));
    }

    @Test
    void updateTrainers_shouldSkipAlreadyAssignedTrainers() {
        Trainer already = makeTrainer(40L, "already.trainer");
        existingTrainee.getTrainers().add(already);

        when(traineeRepository.findWithTrainersByUserUsername("alice.smith"))
                .thenReturn(Optional.of(existingTrainee));
        // already.trainer is filtered out before querying DB
        when(trainerRepository.findByUserUsernameIn(anySet()))
                .thenReturn(List.of());
        when(traineeRepository.save(any())).thenReturn(existingTrainee);

        traineeService.updateTrainers("alice.smith", List.of("already.trainer"));

        // Should not have duplicate
        long count = existingTrainee.getTrainers().stream()
                .filter(t -> t.getUser().getUsername().equals("already.trainer"))
                .count();
        assertThat(count).isEqualTo(1);
    }

    @Test
    void updateTrainers_shouldThrow_whenTraineeNotFound() {
        when(traineeRepository.findWithTrainersByUserUsername("ghost"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                traineeService.updateTrainers("ghost", List.of("some.trainer")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee not found");
    }

    @Test
    void updateTrainers_shouldThrow_whenARequestedTrainerDoesNotExist() {
        when(traineeRepository.findWithTrainersByUserUsername("alice.smith"))
                .thenReturn(Optional.of(existingTrainee));
        when(trainerRepository.findByUserUsernameIn(anySet()))
                .thenReturn(List.of()); // DB returned nothing for "ghost.trainer"

        assertThatThrownBy(() ->
                traineeService.updateTrainers("alice.smith", List.of("ghost.trainer")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainers not found");
    }

    @Test
    void updateTrainers_shouldNotSave_whenTraineeNotFound() {
        when(traineeRepository.findWithTrainersByUserUsername("ghost"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                traineeService.updateTrainers("ghost", List.of()));
        verify(traineeRepository, never()).save(any());
    }

    // ── afterCreate ───────────────────────────────────────────────────────────

    @Test
    void afterCreate_shouldIncrementTraineeMetric() {
        traineeService.afterCreate(existingTrainee);
        verify(userMetrics).incrementTrainee();
    }

    // ── getRepository / findByUsernameOptional ────────────────────────────────

    @Test
    void findByUsernameOptional_shouldReturnTrainee_whenFound() {
        when(traineeRepository.findByUserUsername("alice.smith"))
                .thenReturn(Optional.of(existingTrainee));

        // Exercise through updateTrainee which delegates to findByUsernameOptional
        Trainee update = new Trainee();
        update.setAddress("new");
        update.setUser(User.builder().firstName("A").lastName("B").password("p").build());
        when(traineeRepository.save(any())).thenReturn(existingTrainee);

        Trainee result = traineeService.updateTrainee("alice.smith", update);
        assertThat(result).isNotNull();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Trainer makeTrainer(Long id, String username) {
        User u = User.builder()
                .id(id)
                .firstName("Trainer")
                .lastName("X")
                .username(username)
                .password("p")
                .isActive(true)
                .build();
        Trainer t = new Trainer();
        t.setId(id);
        t.setUser(u);
        t.setTrainees(new ArrayList<>());
        t.setTrainings(new ArrayList<>());
        return t;
    }
}