package com.example.gymcrm.security;

import com.example.gymcrm.model.User;
import com.example.gymcrm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BruteForceProtectionServiceTest {

    @Mock
    private UserRepository userRepository;

    private BruteForceProtectionService service;

    private User user;

    @BeforeEach
    void setUp() {
        service = new BruteForceProtectionService(userRepository);
        user = User.builder().build();
        user.setFailedAttempts(0);
        user.setBlockedUntil(null);
    }

    // --- loginSucceeded ---

    @Test
    void loginSucceeded_ShouldResetFailedAttemptsToZero() {
        user.setFailedAttempts(2);
        service.loginSucceeded(user);

        assertThat(user.getFailedAttempts()).isZero();
    }

    @Test
    void loginSucceeded_ShouldClearBlockedUntil() {
        user.setBlockedUntil(Instant.now().plusSeconds(300));
        service.loginSucceeded(user);

        assertThat(user.getBlockedUntil()).isNull();
    }

    @Test
    void loginSucceeded_ShouldSaveUser() {
        service.loginSucceeded(user);
        verify(userRepository, times(1)).save(user);
    }

    // --- loginFailed ---

    @Test
    void loginFailed_ShouldIncrementFailedAttempts() {
        user.setFailedAttempts(1);
        service.loginFailed(user);

        assertThat(user.getFailedAttempts()).isEqualTo(2);
    }

    @Test
    void loginFailed_ShouldNotBlockUserBelowMaxAttempts() {
        user.setFailedAttempts(1);
        service.loginFailed(user); // now 2, below threshold of 3

        assertThat(user.getBlockedUntil()).isNull();
    }

    @Test
    void loginFailed_ShouldBlockUserAtMaxAttempts() {
        user.setFailedAttempts(2);
        Instant before = Instant.now();
        service.loginFailed(user); // now 3 = MAX_ATTEMPTS
        Instant after = Instant.now();

        assertThat(user.getBlockedUntil()).isNotNull();
        assertThat(user.getBlockedUntil()).isAfterOrEqualTo(before.plusSeconds(300));
        assertThat(user.getBlockedUntil()).isBeforeOrEqualTo(after.plusSeconds(300));
    }

    @Test
    void loginFailed_ShouldBlockUserAboveMaxAttempts() {
        user.setFailedAttempts(5);
        service.loginFailed(user);

        assertThat(user.getBlockedUntil()).isNotNull();
    }

    @Test
    void loginFailed_ShouldSaveUser() {
        service.loginFailed(user);
        verify(userRepository, times(1)).save(user);
    }

    // --- isBlocked ---

    @Test
    void isBlocked_ShouldReturnFalse_WhenBlockedUntilIsNull() {
        user.setBlockedUntil(null);
        assertThat(service.isBlocked(user)).isFalse();
    }

    @Test
    void isBlocked_ShouldReturnTrue_WhenBlockedUntilIsInFuture() {
        user.setBlockedUntil(Instant.now().plusSeconds(300));
        assertThat(service.isBlocked(user)).isTrue();
    }

    @Test
    void isBlocked_ShouldReturnFalse_AndResetUser_WhenBlockExpired() {
        user.setBlockedUntil(Instant.now().minusSeconds(1));
        user.setFailedAttempts(3);

        boolean result = service.isBlocked(user);

        assertThat(result).isFalse();
        assertThat(user.getFailedAttempts()).isZero();
        assertThat(user.getBlockedUntil()).isNull();
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void isBlocked_ShouldNotSave_WhenBlockedUntilIsNull() {
        user.setBlockedUntil(null);
        service.isBlocked(user);
        verify(userRepository, never()).save(any());
    }
}