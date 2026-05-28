package com.example.gymcrm.service;

import com.example.gymcrm.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CredentialsGeneratorTest {

    @Test
    void generatePassword_shouldReturnLength10() {
        String password = CredentialsGenerator.generatePassword();
        assertEquals(10, password.length());
    }

    @Test
    void generateUsername_shouldAddNumberIfExists() {
        User existing = new User(null, "John", "Doe", "John.Doe", "pass", true);

        String username = CredentialsGenerator.generateUsername(
                "John",
                "Doe",
                List.of(existing)
        );

        assertEquals("John.Doe1", username);
    }
}