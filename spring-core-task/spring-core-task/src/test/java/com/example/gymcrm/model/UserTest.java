package com.example.gymcrm.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserGettersAndSetters() {
        User user = new User();

        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("john.doe");
        user.setPassword("password");
        user.setIsActive(true);

        assertEquals(1L, user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john.doe", user.getUsername());
        assertEquals("password", user.getPassword());
        assertTrue(user.getIsActive());
    }

    @Test
    void testUserAllArgsConstructor() {
        User user = new User(1L, "John", "Doe", "john.doe", "password", true);

        assertEquals(1L, user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john.doe", user.getUsername());
        assertEquals("password", user.getPassword());
        assertTrue(user.getIsActive());
    }

    @Test
    void testUserNoArgsConstructor() {
        User user = new User();
        assertNotNull(user);
    }

    @Test
    void testUserEqualsWithDifferentTypes() {
        User user = new User(1L, "John", "Doe", "john.doe", "pass", true);
        String notAUser = "not a user";

        assertNotEquals(user, notAUser);
    }
}