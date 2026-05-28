package com.example.gymcrm.security;

import com.example.gymcrm.config.JwtProperties;
import com.example.gymcrm.model.User;
import com.example.gymcrm.repository.UserRepository;
import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private UserRepository userRepository;

    private JwtService jwtService;

    // 32+ bytes secret required for HS256
    private static final String SECRET = "12345678901234567890123456789012";
    private static final long EXPIRATION_MS = 3_600_000L; // 1 hour

    @BeforeEach
    void setUp() {
        when(jwtProperties.getSecret()).thenReturn(SECRET);
        when(jwtProperties.getExpirationMs()).thenReturn(EXPIRATION_MS);
        jwtService = new JwtService(jwtProperties, userRepository);
    }

    // --- generateToken ---

    @Test
    void generateToken_ShouldReturnNonNullToken() {
        String token = jwtService.generateToken("john.doe");
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void generateToken_ShouldReturnValidJwtFormat() {
        String token = jwtService.generateToken("john.doe");
        // JWT has 3 parts separated by dots
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void generateToken_ShouldProduceDifferentTokensEachCall() {
        String token1 = jwtService.generateToken("john.doe");
        String token2 = jwtService.generateToken("john.doe");
        assertThat(token1).isNotEqualTo(token2);
    }

    // --- validateAndParse ---

    @Test
    void validateAndParse_ShouldReturnClaims_ForValidToken() {
        User user = User.builder().build();
        user.setUsername("john.doe");
        user.setLastLogout(null);
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

        String token = jwtService.generateToken("john.doe");
        JWTClaimsSet claims = jwtService.validateAndParse(token);

        assertThat(claims.getSubject()).isEqualTo("john.doe");
    }

    @Test
    void validateAndParse_ShouldThrow_ForInvalidSignature() {
        // Tamper with the token by replacing the signature part
        String token = jwtService.generateToken("john.doe");
        String tampered = token.substring(0, token.lastIndexOf('.') + 1) + "invalidsignature";

        assertThatThrownBy(() -> jwtService.validateAndParse(tampered))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void validateAndParse_ShouldThrow_ForMalformedToken() {
        assertThatThrownBy(() -> jwtService.validateAndParse("not.a.jwt"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void validateAndParse_ShouldThrow_WhenTokenIssuedBeforeLastLogout() {
        String token = jwtService.generateToken("john.doe");

        User user = User.builder().build();
        user.setUsername("john.doe");
        // Set lastLogout to future — simulates that the token was issued before the logout
        user.setLastLogout(Instant.now().plusSeconds(60));
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> jwtService.validateAndParse(token))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("invalidated");
    }

    @Test
    void validateAndParse_ShouldThrow_ForExpiredToken() throws Exception {
        when(jwtProperties.getExpirationMs()).thenReturn(-1000L); // already expired
        JwtService shortLivedService = new JwtService(jwtProperties, userRepository);

        String token = shortLivedService.generateToken("john.doe");

        assertThatThrownBy(() -> shortLivedService.validateAndParse(token))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("expired");
    }

    // --- logout ---

    @Test
    void logout_ShouldSetLastLogoutToNow() {
        User user = User.builder().build();
        user.setUsername("john.doe");
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

        Instant before = Instant.now();
        jwtService.logout("john.doe");
        Instant after = Instant.now();

        assertThat(user.getLastLogout()).isNotNull();
        assertThat(user.getLastLogout()).isAfterOrEqualTo(before);
        assertThat(user.getLastLogout()).isBeforeOrEqualTo(after);
    }

    @Test
    void logout_ShouldSaveUser() {
        User user = User.builder().build();
        user.setUsername("john.doe");
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

        jwtService.logout("john.doe");

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void logout_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jwtService.logout("ghost"))
                .isInstanceOf(RuntimeException.class);
    }
}