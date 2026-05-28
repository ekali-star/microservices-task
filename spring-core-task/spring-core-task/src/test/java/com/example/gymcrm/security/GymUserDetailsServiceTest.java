package com.example.gymcrm.security;

import com.example.gymcrm.model.User;
import com.example.gymcrm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private GymUserDetailsService service;

    @BeforeEach
    void setUp() {
        service = new GymUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        User user = User.builder().build();
        user.setUsername("john.doe");
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("john.doe");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("john.doe");
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    void loadUserByUsername_ShouldAssignRoleUser() {
        User user = User.builder().build();
        user.setUsername("jane.doe");
        user.setPassword("pass");

        when(userRepository.findByUsername("jane.doe")).thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("jane.doe");

        assertThat(result.getAuthorities())
                .extracting(a -> a.getAuthority())
                .containsExactly("ROLE_USER");
    }

    @Test
    void loadUserByUsername_ShouldThrowUsernameNotFoundException_WhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("ghost"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("ghost");
    }
}