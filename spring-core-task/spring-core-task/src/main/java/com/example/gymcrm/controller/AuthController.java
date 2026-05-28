package com.example.gymcrm.controller;

import com.example.gymcrm.dto.request.ChangePasswordRequest;
import com.example.gymcrm.facade.GymFacade;
import com.example.gymcrm.model.User;
import com.example.gymcrm.repository.UserRepository;
import com.example.gymcrm.security.BruteForceProtectionService;
import com.example.gymcrm.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final GymFacade facade;
    private final JwtService jwtService;
    private final BruteForceProtectionService bruteForce;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(GymFacade facade,
                          JwtService jwtService,
                          BruteForceProtectionService bruteForce,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.facade = facade;
        this.jwtService = jwtService;
        this.bruteForce = bruteForce;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,
                                   @RequestParam String password) {

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        }

        if (bruteForce.isBlocked(user)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Account temporarily locked. Try again later."));
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            bruteForce.loginFailed(user);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        }

        bruteForce.loginSucceeded(user);
        String token = jwtService.generateToken(username);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails user) {
        jwtService.logout(user.getUsername());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody ChangePasswordRequest request) {
        facade.changePassword(user.getUsername(), request);
        return ResponseEntity.ok().build();
    }
}