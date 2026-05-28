package com.example.gymcrm.service;

import com.example.gymcrm.dto.Auth;
import com.example.gymcrm.metric.TrainingMetrics;
import com.example.gymcrm.model.*;
import com.example.gymcrm.repository.TrainingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock private TrainingRepository trainingRepository;
    @Mock private TraineeService traineeService;
    @Mock private TrainerService trainerService;
    @Mock private TrainingMetrics trainingMetrics;

    @InjectMocks
    private TrainingService trainingService;

    private Trainee activeTrainee;
    private Trainer activeTrainer;
    private Training training;

    @BeforeEach
    void setUp() {
        User traineeUser = User.builder()
                .username("alice.smith").firstName("Alice").lastName("Smith")
                .password("pass").isActive(true).build();
        User trainerUser = User.builder()
                .username("tom.brown").firstName("Tom").lastName("Brown")
                .password("pass").isActive(true).build();

        TrainingType type = TrainingType.builder().id(1L).trainingTypeName("Yoga").build();

        activeTrainee = new Trainee();
        activeTrainee.setId(1L);
        activeTrainee.setUser(traineeUser);
        activeTrainee.setTrainers(new ArrayList<>());
        activeTrainee.setTrainings(new ArrayList<>());

        activeTrainer = new Trainer();
        activeTrainer.setId(1L);
        activeTrainer.setUser(trainerUser);
        activeTrainer.setSpecialization(type);
        activeTrainer.setTrainees(new ArrayList<>());
        activeTrainer.setTrainings(new ArrayList<>());

        training = Training.builder()
                .trainingName("Morning Yoga")
                .trainingType(type)
                .trainingDate(LocalDate.of(2024, 6, 1))
                .trainingDuration(60)
                .build();
    }

    // ── createTraining ────────────────────────────────────────────────────────

    @Test
    void createTraining_shouldSaveAndReturnTraining_whenBothActive() {
        when(traineeService.findByUsername("alice.smith")).thenReturn(activeTrainee);
        when(trainerService.findByUsername("tom.brown")).thenReturn(activeTrainer);
        when(trainingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Training result = trainingService.createTraining("alice.smith", "tom.brown", training);

        assertThat(result.getTrainee()).isSameAs(activeTrainee);
        assertThat(result.getTrainer()).isSameAs(activeTrainer);
        verify(trainingRepository).save(training);
    }

    @Test
    void createTraining_shouldIncrementMetric_whenSuccessful() {
        when(traineeService.findByUsername("alice.smith")).thenReturn(activeTrainee);
        when(trainerService.findByUsername("tom.brown")).thenReturn(activeTrainer);
        when(trainingRepository.save(any())).thenReturn(training);

        trainingService.createTraining("alice.smith", "tom.brown", training);

        verify(trainingMetrics).increment();
    }

    @Test
    void createTraining_shouldThrow_whenTraineeNotFound() {
        when(traineeService.findByUsername("ghost")).thenReturn(null);

        assertThatThrownBy(() ->
                trainingService.createTraining("ghost", "tom.brown", training))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainee not found: ghost");

        verify(trainingRepository, never()).save(any());
        verify(trainingMetrics, never()).increment();
    }

    @Test
    void createTraining_shouldThrow_whenTrainerNotFound() {
        when(traineeService.findByUsername("alice.smith")).thenReturn(activeTrainee);
        when(trainerService.findByUsername("ghost")).thenReturn(null);

        assertThatThrownBy(() ->
                trainingService.createTraining("alice.smith", "ghost", training))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainer not found: ghost");

        verify(trainingRepository, never()).save(any());
    }

    @Test
    void createTraining_shouldThrow_whenTraineeIsInactive() {
        activeTrainee.getUser().setIsActive(false);
        when(traineeService.findByUsername("alice.smith")).thenReturn(activeTrainee);
        when(trainerService.findByUsername("tom.brown")).thenReturn(activeTrainer);

        assertThatThrownBy(() ->
                trainingService.createTraining("alice.smith", "tom.brown", training))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee or Trainer is not active");

        verify(trainingRepository, never()).save(any());
    }

    @Test
    void createTraining_shouldThrow_whenTrainerIsInactive() {
        activeTrainer.getUser().setIsActive(false);
        when(traineeService.findByUsername("alice.smith")).thenReturn(activeTrainee);
        when(trainerService.findByUsername("tom.brown")).thenReturn(activeTrainer);

        assertThatThrownBy(() ->
                trainingService.createTraining("alice.smith", "tom.brown", training))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee or Trainer is not active");

        verify(trainingRepository, never()).save(any());
    }

    @Test
    void createTraining_shouldThrow_whenBothInactive() {
        activeTrainee.getUser().setIsActive(false);
        activeTrainer.getUser().setIsActive(false);
        when(traineeService.findByUsername("alice.smith")).thenReturn(activeTrainee);
        when(trainerService.findByUsername("tom.brown")).thenReturn(activeTrainer);

        assertThatThrownBy(() ->
                trainingService.createTraining("alice.smith", "tom.brown", training))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── getTraineeTrainings (with Auth) ───────────────────────────────────────

    @Test
    void getTraineeTrainings_withAuth_shouldReturnTrainings_whenAuthenticated() {
        Auth auth = new Auth("alice.smith", "pass");
        when(traineeService.authenticate(auth)).thenReturn(true);
        when(trainingRepository.findTraineeTrainings("alice.smith", null, null, null, null))
                .thenReturn(List.of(training));

        List<Training> result = trainingService.getTraineeTrainings(auth, null, null, null, null);

        assertThat(result).containsExactly(training);
    }

    @Test
    void getTraineeTrainings_withAuth_shouldThrow_whenNotAuthenticated() {
        Auth auth = new Auth("alice.smith", "wrongPass");
        when(traineeService.authenticate(auth)).thenReturn(false);

        assertThatThrownBy(() ->
                trainingService.getTraineeTrainings(auth, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Authentication failed");

        verify(trainingRepository, never()).findTraineeTrainings(any(), any(), any(), any(), any());
    }

    // ── getTraineeTrainings (by username) ─────────────────────────────────────

    @Test
    void getTraineeTrainings_byUsername_shouldDelegateToRepository() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to   = LocalDate.of(2024, 12, 31);
        when(trainingRepository.findTraineeTrainings("alice.smith", from, to, "Tom", 1L))
                .thenReturn(List.of(training));

        List<Training> result = trainingService.getTraineeTrainings("alice.smith", from, to, "Tom", 1L);

        assertThat(result).containsExactly(training);
        verify(trainingRepository).findTraineeTrainings("alice.smith", from, to, "Tom", 1L);
    }

    @Test
    void getTraineeTrainings_byUsername_shouldReturnEmpty_whenNoResults() {
        when(trainingRepository.findTraineeTrainings(any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        List<Training> result = trainingService.getTraineeTrainings("alice.smith", null, null, null, null);

        assertThat(result).isEmpty();
    }

    @Test
    void getTraineeTrainings_byUsername_shouldPassNullFiltersThrough() {
        when(trainingRepository.findTraineeTrainings("alice.smith", null, null, null, null))
                .thenReturn(List.of());

        trainingService.getTraineeTrainings("alice.smith", null, null, null, null);

        verify(trainingRepository).findTraineeTrainings("alice.smith", null, null, null, null);
    }

    // ── getTrainerTrainings (with Auth) ───────────────────────────────────────

    @Test
    void getTrainerTrainings_withAuth_shouldReturnTrainings_whenAuthenticated() {
        Auth auth = new Auth("tom.brown", "pass");
        when(trainerService.authenticate(auth)).thenReturn(true);
        when(trainingRepository.findTrainerTrainings("tom.brown", null, null, null))
                .thenReturn(List.of(training));

        List<Training> result = trainingService.getTrainerTrainings(auth, null, null, null);

        assertThat(result).containsExactly(training);
    }

    @Test
    void getTrainerTrainings_withAuth_shouldThrow_whenNotAuthenticated() {
        Auth auth = new Auth("tom.brown", "wrongPass");
        when(trainerService.authenticate(auth)).thenReturn(false);

        assertThatThrownBy(() ->
                trainingService.getTrainerTrainings(auth, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Authentication failed");

        verify(trainingRepository, never()).findTrainerTrainings(any(), any(), any(), any());
    }

    // ── getTrainerTrainings (by username) ─────────────────────────────────────

    @Test
    void getTrainerTrainings_byUsername_shouldDelegateToRepository() {
        LocalDate from = LocalDate.of(2024, 3, 1);
        LocalDate to   = LocalDate.of(2024, 9, 30);
        when(trainingRepository.findTrainerTrainings("tom.brown", from, to, "Alice"))
                .thenReturn(List.of(training));

        List<Training> result = trainingService.getTrainerTrainings("tom.brown", from, to, "Alice");

        assertThat(result).containsExactly(training);
        verify(trainingRepository).findTrainerTrainings("tom.brown", from, to, "Alice");
    }

    @Test
    void getTrainerTrainings_byUsername_shouldReturnEmpty_whenNoResults() {
        when(trainingRepository.findTrainerTrainings(any(), any(), any(), any()))
                .thenReturn(List.of());

        List<Training> result = trainingService.getTrainerTrainings("tom.brown", null, null, null);

        assertThat(result).isEmpty();
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    void findById_shouldReturnTraining_whenExists() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));

        Optional<Training> result = trainingService.findById(1L);

        assertThat(result).isPresent().contains(training);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        when(trainingRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Training> result = trainingService.findById(99L);

        assertThat(result).isEmpty();
    }

    // ── findAll ───────────────────────────────────────────────────────────────

    @Test
    void findAll_shouldReturnAllTrainings() {
        when(trainingRepository.findAll()).thenReturn(List.of(training));

        List<Training> result = trainingService.findAll();

        assertThat(result).hasSize(1).containsExactly(training);
    }

    @Test
    void findAll_shouldReturnEmpty_whenNoTrainings() {
        when(trainingRepository.findAll()).thenReturn(List.of());

        List<Training> result = trainingService.findAll();

        assertThat(result).isEmpty();
    }
}