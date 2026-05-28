package com.example.gymcrm.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingTypeTest {

    @Test
    void builder_shouldCreateWithAllFields() {
        TrainingType type = TrainingType.builder()
                .id(1L)
                .trainingTypeName("Yoga")
                .build();

        assertThat(type.getId()).isEqualTo(1L);
        assertThat(type.getTrainingTypeName()).isEqualTo("Yoga");
    }

    @Test
    void builder_shouldCreateWithNullId_whenNotProvided() {
        TrainingType type = TrainingType.builder()
                .trainingTypeName("Cardio")
                .build();

        assertThat(type.getId()).isNull();
        assertThat(type.getTrainingTypeName()).isEqualTo("Cardio");
    }

    @Test
    void setter_shouldUpdateTrainingTypeName() {
        TrainingType type = TrainingType.builder()
                .id(1L)
                .trainingTypeName("Yoga")
                .build();

        type.setTrainingTypeName("Pilates");

        assertThat(type.getTrainingTypeName()).isEqualTo("Pilates");
    }

    @Test
    void setter_shouldUpdateId() {
        TrainingType type = TrainingType.builder()
                .trainingTypeName("Yoga")
                .build();

        type.setId(99L);

        assertThat(type.getId()).isEqualTo(99L);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Yoga", "Strength Training", "HIIT", "CrossFit", "Pilates", "Zumba"})
    void trainingTypeName_shouldStoreVariousValidNames(String name) {
        TrainingType type = TrainingType.builder()
                .trainingTypeName(name)
                .build();

        assertThat(type.getTrainingTypeName()).isEqualTo(name);
    }

    @Test
    void trainingTypeName_shouldAllowNullViaBuilder() {
        TrainingType type = TrainingType.builder()
                .id(1L)
                .build();

        assertThat(type.getTrainingTypeName()).isNull();
    }

    @Test
    void trainingTypeName_shouldAllowNullViaSetter() {
        TrainingType type = TrainingType.builder()
                .id(1L)
                .trainingTypeName("Yoga")
                .build();

        type.setTrainingTypeName(null);

        assertThat(type.getTrainingTypeName()).isNull();
    }

    @Test
    void id_shouldSupportLargeValues() {
        TrainingType type = TrainingType.builder()
                .id(Long.MAX_VALUE)
                .trainingTypeName("Strength")
                .build();

        assertThat(type.getId()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    void twoInstances_withSameValues_shouldHaveEqualFields() {
        TrainingType t1 = TrainingType.builder().id(1L).trainingTypeName("Yoga").build();
        TrainingType t2 = TrainingType.builder().id(1L).trainingTypeName("Yoga").build();

        assertThat(t1.getId()).isEqualTo(t2.getId());
        assertThat(t1.getTrainingTypeName()).isEqualTo(t2.getTrainingTypeName());
    }
}