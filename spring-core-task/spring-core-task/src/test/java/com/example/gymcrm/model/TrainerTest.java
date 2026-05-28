package com.example.gymcrm.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TrainerTest {

    private User user;
    private TrainingType specialization;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("Bob")
                .lastName("Coach")
                .username("bob.coach")
                .password("pass")
                .isActive(true)
                .build();

        specialization = TrainingType.builder()
                .id(1L)
                .trainingTypeName("Strength Training")
                .build();

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(user);
        trainer.setSpecialization(specialization);
        trainer.setTrainees(new ArrayList<>());
        trainer.setTrainings(new ArrayList<>());
    }

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        assertThat(trainer.getId()).isEqualTo(1L);
        assertThat(trainer.getUser()).isSameAs(user);
        assertThat(trainer.getSpecialization()).isSameAs(specialization);
        assertThat(trainer.getTrainees()).isEmpty();
        assertThat(trainer.getTrainings()).isEmpty();
    }

    @Test
    void setSpecialization_shouldUpdateSpecialization() {
        TrainingType newSpec = TrainingType.builder().id(2L).trainingTypeName("Yoga").build();
        trainer.setSpecialization(newSpec);
        assertThat(trainer.getSpecialization()).isSameAs(newSpec);
        assertThat(trainer.getSpecialization().getTrainingTypeName()).isEqualTo("Yoga");
    }

    @Test
    void getUser_shouldImplementUserComparableInterface() {
        UserComparable comparable = trainer;
        assertThat(comparable.getUser()).isSameAs(user);
    }

    @Test
    void setUser_shouldReplaceUser() {
        User newUser = User.builder()
                .firstName("Charlie").lastName("New").password("pass").build();
        trainer.setUser(newUser);
        assertThat(trainer.getUser()).isSameAs(newUser);
    }

    @Test
    void addTrainee_shouldAppendToList() {
        Trainee trainee = new Trainee();
        trainee.setId(10L);

        trainer.getTrainees().add(trainee);

        assertThat(trainer.getTrainees()).hasSize(1);
        assertThat(trainer.getTrainees().get(0).getId()).isEqualTo(10L);
    }

    @Test
    void removeTrainee_shouldShrinkList() {
        Trainee t1 = new Trainee();
        t1.setId(10L);
        Trainee t2 = new Trainee();
        t2.setId(11L);

        trainer.getTrainees().add(t1);
        trainer.getTrainees().add(t2);
        trainer.getTrainees().remove(t1);

        assertThat(trainer.getTrainees()).containsExactly(t2);
    }

    @Test
    void setTrainees_shouldReplaceList() {
        List<Trainee> newList = new ArrayList<>();
        Trainee t = new Trainee();
        t.setId(99L);
        newList.add(t);

        trainer.setTrainees(newList);

        assertThat(trainer.getTrainees()).isSameAs(newList);
    }

    @Test
    void addTraining_shouldAppendToList() {
        Training training = Training.builder()
                .trainingName("Strength Session")
                .trainingDate(LocalDate.now())
                .trainingDuration(60)
                .build();

        trainer.getTrainings().add(training);

        assertThat(trainer.getTrainings()).hasSize(1);
        assertThat(trainer.getTrainings().get(0).getTrainingName()).isEqualTo("Strength Session");
    }

    @Test
    void removeTraining_shouldShrinkList() {
        Training t1 = Training.builder().trainingName("A").trainingDate(LocalDate.now()).trainingDuration(30).build();
        Training t2 = Training.builder().trainingName("B").trainingDate(LocalDate.now()).trainingDuration(45).build();

        trainer.getTrainings().add(t1);
        trainer.getTrainings().add(t2);
        trainer.getTrainings().remove(t1);

        assertThat(trainer.getTrainings()).containsExactly(t2);
    }

    @Test
    void allArgsConstructor_shouldInitializeAllFields() {
        List<Trainee> trainees = new ArrayList<>();
        List<Training> trainings = new ArrayList<>();

        Trainer t = new Trainer(2L, specialization, user, trainees, trainings);

        assertThat(t.getId()).isEqualTo(2L);
        assertThat(t.getSpecialization()).isSameAs(specialization);
        assertThat(t.getUser()).isSameAs(user);
        assertThat(t.getTrainees()).isSameAs(trainees);
        assertThat(t.getTrainings()).isSameAs(trainings);
    }

    @Test
    void noArgsConstructor_shouldCreateInstanceWithNullFields() {
        Trainer empty = new Trainer();
        assertThat(empty.getId()).isNull();
        assertThat(empty.getUser()).isNull();
        assertThat(empty.getSpecialization()).isNull();
        assertThat(empty.getTrainees()).isNull();
        assertThat(empty.getTrainings()).isNull();
    }

    @Test
    void trainer_shouldHandleMultipleTraineesAndTrainings() {
        for (int i = 1; i <= 5; i++) {
            Trainee t = new Trainee();
            t.setId((long) i);
            trainer.getTrainees().add(t);

            Training session = Training.builder()
                    .trainingName("Session " + i)
                    .trainingDate(LocalDate.now())
                    .trainingDuration(i * 10)
                    .build();
            trainer.getTrainings().add(session);
        }

        assertThat(trainer.getTrainees()).hasSize(5);
        assertThat(trainer.getTrainings()).hasSize(5);
    }
}