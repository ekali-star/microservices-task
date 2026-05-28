package com.example.gymcrm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

class JacksonConfigTest {

    private final JacksonConfig jacksonConfig = new JacksonConfig();

    @Test
    void objectMapper_ShouldNotBeNull() {
        ObjectMapper mapper = jacksonConfig.objectMapper();
        assertThat(mapper).isNotNull();
    }

    @Test
    void objectMapper_ShouldHaveJavaTimeModuleRegistered() {
        ObjectMapper mapper = jacksonConfig.objectMapper();
        assertThat(mapper.getRegisteredModuleIds())
                .contains(new JavaTimeModule().getTypeId());
    }

    @Test
    void objectMapper_ShouldSerializeLocalDateTimeWithoutError() {
        ObjectMapper mapper = jacksonConfig.objectMapper();
        LocalDateTime now = LocalDateTime.now();

        assertThatNoException().isThrownBy(() -> mapper.writeValueAsString(now));
    }

    @Test
    void objectMapper_ShouldDeserializeLocalDateTimeWithoutError() {
        ObjectMapper mapper = jacksonConfig.objectMapper();
        String json = "\"2024-01-15T10:30:00\"";

        assertThatNoException().isThrownBy(() -> mapper.readValue(json, LocalDateTime.class));
    }

    @Test
    void objectMapper_EachCallShouldReturnNewInstance() {
        ObjectMapper mapper1 = jacksonConfig.objectMapper();
        ObjectMapper mapper2 = jacksonConfig.objectMapper();
        assertThat(mapper1).isNotSameAs(mapper2);
    }
}