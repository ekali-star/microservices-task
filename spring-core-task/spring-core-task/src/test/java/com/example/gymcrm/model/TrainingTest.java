package com.example.gymcrm.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingTest {

    private Validator validator;
    private Trainee trainee;
    private Trainer trainer;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        User traineeUser = User.builder()
                .firstName("Alice").lastName("A").password("pass").build();
        User trainerUser = User.builder()
                .firstName("Bob").lastName("B").password("pass").build();

        trainee = new Trainee();
        trainee.setUser(traineeUser);

        trainer = new Trainer();
        trainer.setUser(trainerUser);

        trainingType = TrainingType.builder().id(1L).trainingTypeName("Yoga").build();
    }

    private Training buildValid() {
        return Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingName("Morning Session")
                .trainingType(trainingType)
                .trainingDate(LocalDate.now())
                .trainingDuration(60)
                .build();
    }

    @Test
    void builder_shouldCreateTrainingWithAllFields() {
        LocalDate date = LocalDate.of(2024, 6, 15);

        Training t = Training.builder()
                .id(10L)
                .trainee(trainee)
                .trainer(trainer)
                .trainingName("Evening Yoga")
                .trainingType(trainingType)
                .trainingDate(date)
                .trainingDuration(45)
                .build();

        assertThat(t.getId()).isEqualTo(10L);
        assertThat(t.getTrainee()).isSameAs(trainee);
        assertThat(t.getTrainer()).isSameAs(trainer);
        assertThat(t.getTrainingName()).isEqualTo("Evening Yoga");
        assertThat(t.getTrainingType()).isSameAs(trainingType);
        assertThat(t.getTrainingDate()).isEqualTo(date);
        assertThat(t.getTrainingDuration()).isEqualTo(45);
    }

    @Test
    void setters_shouldUpdateAllFields() {
        Training t = buildValid();

        LocalDate newDate = LocalDate.of(2025, 1, 1);
        t.setTrainingName("Updated");
        t.setTrainingDate(newDate);
        t.setTrainingDuration(90);

        assertThat(t.getTrainingName()).isEqualTo("Updated");
        assertThat(t.getTrainingDate()).isEqualTo(newDate);
        assertThat(t.getTrainingDuration()).isEqualTo(90);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t"})
    void validation_shouldFailWhenTrainingNameIsBlank(String name) {
        Training t = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingName(name)
                .trainingType(trainingType)
                .trainingDate(LocalDate.now())
                .trainingDuration(60)
                .build();

        Set<ConstraintViolation<Training>> violations = validator.validate(t);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("trainingName");
    }

    @Test
    void validation_shouldFailWhenTrainingDateIsNull() {
        Training t = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingName("Session")
                .trainingType(trainingType)
                .trainingDate(null)
                .trainingDuration(60)
                .build();

        Set<ConstraintViolation<Training>> violations = validator.validate(t);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("trainingDate");
    }

    @Test
    void validation_shouldFailWhenTrainingDurationIsNull() {
        Training t = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingName("Session")
                .trainingType(trainingType)
                .trainingDate(LocalDate.now())
                .trainingDuration(null)
                .build();

        Set<ConstraintViolation<Training>> violations = validator.validate(t);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("trainingDuration");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    void validation_shouldFailWhenTrainingDurationIsZeroOrNegative(int duration) {
        Training t = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingName("Session")
                .trainingType(trainingType)
                .trainingDate(LocalDate.now())
                .trainingDuration(duration)
                .build();

        Set<ConstraintViolation<Training>> violations = validator.validate(t);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("trainingDuration");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 30, 60, 120, 1440})
    void validation_shouldPassForPositiveDuration(int duration) {
        Training t = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingName("Session")
                .trainingType(trainingType)
                .trainingDate(LocalDate.now())
                .trainingDuration(duration)
                .build();

        Set<ConstraintViolation<Training>> violations = validator.validate(t);
        assertThat(violations).isEmpty();
    }

    @Test
    void validation_shouldPassForCompletelyValidTraining() {
        Training t = buildValid();
        assertThat(validator.validate(t)).isEmpty();
    }

    @Test
    void trainingDate_shouldSupportPastDates() {
        Training t = buildValid();
        t.setTrainingDate(LocalDate.of(2000, 1, 1));
        assertThat(t.getTrainingDate()).isEqualTo(LocalDate.of(2000, 1, 1));
    }

    @Test
    void trainingDate_shouldSupportFutureDates() {
        Training t = buildValid();
        t.setTrainingDate(LocalDate.of(2099, 12, 31));
        assertThat(t.getTrainingDate()).isEqualTo(LocalDate.of(2099, 12, 31));
    }

    @Test
    void traineeAndTrainer_shouldBeAssignableAndRetrievable() {
        Training t = buildValid();
        assertThat(t.getTrainee()).isSameAs(trainee);
        assertThat(t.getTrainer()).isSameAs(trainer);
    }

    @Test
    void trainingType_shouldBeAssignableAndRetrievable() {
        Training t = buildValid();
        assertThat(t.getTrainingType()).isSameAs(trainingType);
    }
}