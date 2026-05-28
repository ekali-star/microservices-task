package com.example.gymcrm.controller;

import com.example.gymcrm.dto.request.ChangePasswordRequest;
import com.example.gymcrm.dto.request.LoginRequest;
import com.example.gymcrm.facade.GymFacade;
import com.example.gymcrm.model.User;
import com.example.gymcrm.repository.UserRepository;
import com.example.gymcrm.security.BruteForceProtectionService;
import com.example.gymcrm.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GymFacade facade;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private BruteForceProtectionService bruteForce;
    @MockBean
    private UserRepository userRepository;

    @Test
    void login_success() throws Exception {
        User user = User.builder().username("u").build();

        when(userRepository.findByUsername("u")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("u")).thenReturn("token");

        mockMvc.perform(post("/api/auth/login")
                        .param("username", "u")
                        .param("password", "p"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"));
    }

    @Test
    void login_blocked() throws Exception {
        User user = User.builder().username("u").build();

        when(userRepository.findByUsername("u")).thenReturn(Optional.of(user));
        when(bruteForce.isBlocked(user)).thenReturn(true);

        mockMvc.perform(post("/api/auth/login")
                        .param("username", "u")
                        .param("password", "p"))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void login_fail() throws Exception {
        User user = User.builder().username("u").build();

        when(userRepository.findByUsername("u")).thenReturn(Optional.of(user));
        doThrow(new RuntimeException()).when(facade).login(any());

        mockMvc.perform(post("/api/auth/login")
                        .param("username", "u")
                        .param("password", "p"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_success_recordsLoginSucceeded() throws Exception {
        User user = User.builder().username("u").build();

        when(userRepository.findByUsername("u")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("u")).thenReturn("token");

        mockMvc.perform(post("/api/auth/login")
                        .param("username", "u")
                        .param("password", "p"))
                .andExpect(status().isOk());

        verify(bruteForce).loginSucceeded(user);
    }

    @Test
    void login_fail_recordsLoginFailed() throws Exception {
        User user = User.builder().username("u").build();

        when(userRepository.findByUsername("u")).thenReturn(Optional.of(user));
        doThrow(new RuntimeException()).when(facade).login(any());

        mockMvc.perform(post("/api/auth/login")
                        .param("username", "u")
                        .param("password", "p"))
                .andExpect(status().isUnauthorized());

        verify(bruteForce).loginFailed(user);
    }

    @Test
    void login_blocked_doesNotCallFacade() throws Exception {
        User user = User.builder().username("u").build();

        when(userRepository.findByUsername("u")).thenReturn(Optional.of(user));
        when(bruteForce.isBlocked(user)).thenReturn(true);

        mockMvc.perform(post("/api/auth/login")
                        .param("username", "u")
                        .param("password", "p"))
                .andExpect(status().isTooManyRequests());

        verify(facade, never()).login(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_unknownUser_succeeds_withoutBruteForceTracking() throws Exception {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        when(jwtService.generateToken("ghost")).thenReturn("token");

        mockMvc.perform(post("/api/auth/login")
                        .param("username", "ghost")
                        .param("password", "p"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"));

        verify(bruteForce, never()).loginSucceeded(any());
        verify(bruteForce, never()).loginFailed(any());
    }

    @Test
    void login_unknownUser_fails_withoutBruteForceTracking() throws Exception {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        doThrow(new RuntimeException()).when(facade).login(any());

        mockMvc.perform(post("/api/auth/login")
                        .param("username", "ghost")
                        .param("password", "wrong"))
                .andExpect(status().isUnauthorized());

        verify(bruteForce, never()).loginFailed(any());
    }

}