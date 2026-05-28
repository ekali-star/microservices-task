package com.example.gymcrm.service;

import com.example.gymcrm.model.User;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.stream.Collectors;

public class CredentialsGenerator {
    private static final String CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";

    private static final SecureRandom random = new SecureRandom();

    public static String generatePassword() {
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    public static String generateUsername(String firstName,
                                          String lastName,
                                          Collection<? extends User> existingUsers) {

        String base = firstName + "." + lastName;
        String candidate = base;

        Collection<String> existingUsernames = existingUsers.stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());

        for (int counter = 1; existingUsernames.contains(candidate); counter++) {
            candidate = base + counter;
        }

        return candidate;
    }
}