package com.example.gymcrm.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TraineeTest {

    private User user;
    private Trainee trainee;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Smith")
                .username("alice.smith")
                .password("pass")
                .isActive(true)
                .build();

        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.of(1990, 5, 20));
        trainee.setAddress("123 Main St");
        trainee.setTrainers(new ArrayList<>());
        trainee.setTrainings(new ArrayList<>());
    }

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        assertThat(trainee.getId()).isEqualTo(1L);
        assertThat(trainee.getUser()).isSameAs(user);
        assertThat(trainee.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 5, 20));
        assertThat(trainee.getAddress()).isEqualTo("123 Main St");
        assertThat(trainee.getTrainers()).isEmpty();
        assertThat(trainee.getTrainings()).isEmpty();
    }

    @Test
    void setters_shouldUpdateFieldValues() {
        LocalDate newDob = LocalDate.of(1985, 3, 15);
        trainee.setDateOfBirth(newDob);
        trainee.setAddress("456 Oak Ave");

        assertThat(trainee.getDateOfBirth()).isEqualTo(newDob);
        assertThat(trainee.getAddress()).isEqualTo("456 Oak Ave");
    }

    @Test
    void getUser_shouldImplementUserComparableInterface() {
        UserComparable comparable = trainee;
        assertThat(comparable.getUser()).isSameAs(user);
    }

    @Test
    void setUser_shouldReplaceUser() {
        User newUser = User.builder()
                .firstName("Bob").lastName("Jones").password("pass2").build();
        trainee.setUser(newUser);
        assertThat(trainee.getUser()).isSameAs(newUser);
    }

    @Test
    void addTrainer_shouldAppendToList() {
        Trainer trainer = new Trainer();
        trainer.setId(10L);

        trainee.getTrainers().add(trainer);

        assertThat(trainee.getTrainers()).hasSize(1);
        assertThat(trainee.getTrainers().get(0).getId()).isEqualTo(10L);
    }

    @Test
    void removeTrainer_shouldShrinkList() {
        Trainer t1 = new Trainer();
        t1.setId(10L);
        Trainer t2 = new Trainer();
        t2.setId(11L);

        trainee.getTrainers().add(t1);
        trainee.getTrainers().add(t2);
        trainee.getTrainers().remove(t1);

        assertThat(trainee.getTrainers()).containsExactly(t2);
    }

    @Test
    void setTrainers_shouldReplaceList() {
        List<Trainer> newList = new ArrayList<>();
        Trainer t = new Trainer();
        t.setId(99L);
        newList.add(t);

        trainee.setTrainers(newList);

        assertThat(trainee.getTrainers()).isSameAs(newList);
    }

    @Test
    void addTraining_shouldAppendToList() {
        Training training = Training.builder()
                .trainingName("Session 1")
                .trainingDate(LocalDate.now())
                .trainingDuration(60)
                .build();

        trainee.getTrainings().add(training);

        assertThat(trainee.getTrainings()).hasSize(1);
        assertThat(trainee.getTrainings().get(0).getTrainingName()).isEqualTo("Session 1");
    }

    @Test
    void removeTraining_shouldShrinkList() {
        Training t1 = Training.builder().trainingName("A").trainingDate(LocalDate.now()).trainingDuration(30).build();
        Training t2 = Training.builder().trainingName("B").trainingDate(LocalDate.now()).trainingDuration(45).build();

        trainee.getTrainings().add(t1);
        trainee.getTrainings().add(t2);
        trainee.getTrainings().remove(t1);

        assertThat(trainee.getTrainings()).containsExactly(t2);
    }

    @Test
    void dateOfBirth_shouldAllowNull() {
        trainee.setDateOfBirth(null);
        assertThat(trainee.getDateOfBirth()).isNull();
    }

    @Test
    void address_shouldAllowNull() {
        trainee.setAddress(null);
        assertThat(trainee.getAddress()).isNull();
    }

    @Test
    void allArgsConstructor_shouldInitializeAllFields() {
        List<Trainer> trainers = new ArrayList<>();
        List<Training> trainings = new ArrayList<>();
        LocalDate dob = LocalDate.of(2000, 1, 1);

        Trainee t = new Trainee(2L, dob, "99 Elm St", user, trainers, trainings);

        assertThat(t.getId()).isEqualTo(2L);
        assertThat(t.getDateOfBirth()).isEqualTo(dob);
        assertThat(t.getAddress()).isEqualTo("99 Elm St");
        assertThat(t.getUser()).isSameAs(user);
        assertThat(t.getTrainers()).isSameAs(trainers);
        assertThat(t.getTrainings()).isSameAs(trainings);
    }

    @Test
    void noArgsConstructor_shouldCreateInstanceWithNullFields() {
        Trainee empty = new Trainee();
        assertThat(empty.getId()).isNull();
        assertThat(empty.getUser()).isNull();
        assertThat(empty.getDateOfBirth()).isNull();
        assertThat(empty.getAddress()).isNull();
    }
}