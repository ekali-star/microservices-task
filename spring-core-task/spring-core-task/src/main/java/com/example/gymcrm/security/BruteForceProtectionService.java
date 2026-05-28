package com.example.gymcrm.security;

import com.example.gymcrm.model.User;
import com.example.gymcrm.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class BruteForceProtectionService {

    private static final int MAX_ATTEMPTS = 3;
    private static final long BLOCK_DURATION_SECONDS = 300;

    private final UserRepository userRepository;

    public BruteForceProtectionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void loginSucceeded(User user) {
        user.setFailedAttempts(0);
        user.setBlockedUntil(null);
        userRepository.save(user);
    }

    public void loginFailed(User user) {
        int newAttempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(newAttempts);

        if (newAttempts >= MAX_ATTEMPTS) {
            user.setBlockedUntil(Instant.now().plusSeconds(BLOCK_DURATION_SECONDS));
        }

        userRepository.save(user);
    }

    public boolean isBlocked(User user) {
        if (user.getBlockedUntil() == null) return false;

        if (Instant.now().isAfter(user.getBlockedUntil())) {
            user.setFailedAttempts(0);
            user.setBlockedUntil(null);
            userRepository.save(user);
            return false;
        }

        return true;
    }
}