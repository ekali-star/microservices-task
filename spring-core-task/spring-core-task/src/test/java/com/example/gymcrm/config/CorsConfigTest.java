package com.example.gymcrm.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorsConfigTest {

    private CorsProperties corsProperties;
    private CorsConfig corsConfig;

    @BeforeEach
    void setUp() {
        corsProperties = new CorsProperties();
        corsProperties.setAllowedOrigins(List.of("http://localhost:3000", "https://example.com"));
        corsProperties.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        corsProperties.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        corsConfig = new CorsConfig(corsProperties);
    }

    @Test
    void corsConfigurationSource_ShouldReturnUrlBasedCorsConfigurationSource() {
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();
        assertThat(source).isInstanceOf(UrlBasedCorsConfigurationSource.class);
    }

    @Test
    void corsConfigurationSource_ShouldRegisterCorsConfigForAllPaths() {
        UrlBasedCorsConfigurationSource source =
                (UrlBasedCorsConfigurationSource) corsConfig.corsConfigurationSource();

        CorsConfiguration config = source.getCorsConfiguration(
                new org.springframework.mock.web.MockHttpServletRequest("GET", "/api/trainees")
        );

        assertThat(config).isNotNull();
    }

    @Test
    void corsConfigurationSource_ShouldSetAllowedOrigins() {
        UrlBasedCorsConfigurationSource source =
                (UrlBasedCorsConfigurationSource) corsConfig.corsConfigurationSource();

        CorsConfiguration config = source.getCorsConfiguration(
                new org.springframework.mock.web.MockHttpServletRequest("GET", "/any")
        );

        assertThat(config).isNotNull();
        assertThat(config.getAllowedOrigins())
                .containsExactly("http://localhost:3000", "https://example.com");
    }

    @Test
    void corsConfigurationSource_ShouldSetAllowedMethods() {
        UrlBasedCorsConfigurationSource source =
                (UrlBasedCorsConfigurationSource) corsConfig.corsConfigurationSource();

        CorsConfiguration config = source.getCorsConfiguration(
                new org.springframework.mock.web.MockHttpServletRequest("GET", "/any")
        );

        assertThat(config).isNotNull();
        assertThat(config.getAllowedMethods())
                .containsExactlyInAnyOrder("GET", "POST", "PUT", "DELETE");
    }

    @Test
    void corsConfigurationSource_ShouldSetAllowedHeaders() {
        UrlBasedCorsConfigurationSource source =
                (UrlBasedCorsConfigurationSource) corsConfig.corsConfigurationSource();

        CorsConfiguration config = source.getCorsConfiguration(
                new org.springframework.mock.web.MockHttpServletRequest("GET", "/any")
        );

        assertThat(config).isNotNull();
        assertThat(config.getAllowedHeaders())
                .containsExactlyInAnyOrder("Authorization", "Content-Type");
    }

    @Test
    void corsConfigurationSource_ShouldAllowCredentials() {
        UrlBasedCorsConfigurationSource source =
                (UrlBasedCorsConfigurationSource) corsConfig.corsConfigurationSource();

        CorsConfiguration config = source.getCorsConfiguration(
                new org.springframework.mock.web.MockHttpServletRequest("GET", "/any")
        );

        assertThat(config).isNotNull();
        assertThat(config.getAllowCredentials()).isTrue();
    }
}