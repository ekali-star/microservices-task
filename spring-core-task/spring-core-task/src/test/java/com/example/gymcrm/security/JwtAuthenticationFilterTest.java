package com.example.gymcrm.security;

import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private GymUserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ShouldContinueChain_WhenNoAuthHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_ShouldContinueChain_WhenAuthHeaderIsNotBearer() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic sometoken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void doFilterInternal_ShouldSetAuthentication_WhenValidToken() throws Exception {
        String token = "valid.jwt.token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        JWTClaimsSet claims = new JWTClaimsSet.Builder().subject("john.doe").build();
        when(jwtService.validateAndParse(token)).thenReturn(claims);

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("john.doe")
                .password("pass")
                .roles("USER")
                .build();
        when(userDetailsService.loadUserByUsername("john.doe")).thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("john.doe");
    }

    @Test
    void doFilterInternal_ShouldContinueChain_WhenJwtValidationFails() throws Exception {
        String token = "bad.token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.validateAndParse(token)).thenThrow(new RuntimeException("Invalid JWT"));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_ShouldNotOverwriteAuthentication_WhenAlreadyAuthenticated() throws Exception {
        String token = "valid.jwt.token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Pre-set authentication in context
        UserDetails preAuth = org.springframework.security.core.userdetails.User.builder()
                .username("existing.user")
                .password("pass")
                .roles("USER")
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        preAuth, null, preAuth.getAuthorities()
                )
        );

        JWTClaimsSet claims = new JWTClaimsSet.Builder().subject("john.doe").build();
        when(jwtService.validateAndParse(token)).thenReturn(claims);

        filter.doFilterInternal(request, response, filterChain);

        // Should NOT call loadUserByUsername since auth already set
        verifyNoInteractions(userDetailsService);
        verify(filterChain).doFilter(request, response);
    }
}