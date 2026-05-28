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

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private Validator validator;
    private User validUser;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        validUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .username("john.doe")
                .password("securePassword")
                .isActive(true)
                .failedAttempts(0)
                .build();
    }

    @Test
    void builder_shouldCreateUserWithAllFields() {
        Instant blocked = Instant.now();
        Instant logout  = Instant.now();

        User user = User.builder()
                .id(42L)
                .firstName("Jane")
                .lastName("Smith")
                .username("jane.smith")
                .password("pass123")
                .isActive(false)
                .failedAttempts(3)
                .blockedUntil(blocked)
                .lastLogout(logout)
                .build();

        assertThat(user.getId()).isEqualTo(42L);
        assertThat(user.getFirstName()).isEqualTo("Jane");
        assertThat(user.getLastName()).isEqualTo("Smith");
        assertThat(user.getUsername()).isEqualTo("jane.smith");
        assertThat(user.getPassword()).isEqualTo("pass123");
        assertThat(user.getIsActive()).isFalse();
        assertThat(user.getFailedAttempts()).isEqualTo(3);
        assertThat(user.getBlockedUntil()).isEqualTo(blocked);
        assertThat(user.getLastLogout()).isEqualTo(logout);
    }

    @Test
    void setters_shouldUpdateFields() {
        validUser.setFirstName("UpdatedFirst");
        validUser.setLastName("UpdatedLast");
        validUser.setUsername("updated.user");
        validUser.setPassword("newPass");
        validUser.setIsActive(false);
        validUser.setFailedAttempts(5);

        assertThat(validUser.getFirstName()).isEqualTo("UpdatedFirst");
        assertThat(validUser.getLastName()).isEqualTo("UpdatedLast");
        assertThat(validUser.getUsername()).isEqualTo("updated.user");
        assertThat(validUser.getPassword()).isEqualTo("newPass");
        assertThat(validUser.getIsActive()).isFalse();
        assertThat(validUser.getFailedAttempts()).isEqualTo(5);
    }

    @Test
    void setBlockedUntil_shouldStoreAndRetrieveInstant() {
        Instant now = Instant.now();
        validUser.setBlockedUntil(now);
        assertThat(validUser.getBlockedUntil()).isEqualTo(now);
    }

    @Test
    void setLastLogout_shouldStoreAndRetrieveInstant() {
        Instant now = Instant.now();
        validUser.setLastLogout(now);
        assertThat(validUser.getLastLogout()).isEqualTo(now);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void validation_shouldFailWhenFirstNameIsBlank(String firstName) {
        User user = User.builder()
                .firstName(firstName)
                .lastName("Doe")
                .password("pass")
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("firstName");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void validation_shouldFailWhenLastNameIsBlank(String lastName) {
        User user = User.builder()
                .firstName("John")
                .lastName(lastName)
                .password("pass")
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("lastName");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void validation_shouldFailWhenPasswordIsBlank(String password) {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .password(password)
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("password");
    }

    @Test
    void validation_shouldPassForValidUser() {
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        assertThat(violations).isEmpty();
    }

    @Test
    void nullableFields_shouldAcceptNull() {
        validUser.setBlockedUntil(null);
        validUser.setLastLogout(null);

        assertThat(validUser.getBlockedUntil()).isNull();
        assertThat(validUser.getLastLogout()).isNull();
    }

    @Test
    void failedAttempts_defaultShouldBeZero() {
        User user = User.builder()
                .firstName("A")
                .lastName("B")
                .password("p")
                .build();
        assertThat(user.getFailedAttempts()).isZero();
    }

    @Test
    void failedAttempts_shouldAllowIncrement() {
        validUser.setFailedAttempts(validUser.getFailedAttempts() + 1);
        assertThat(validUser.getFailedAttempts()).isEqualTo(1);
    }
}