package com.example.gymcrm.health;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppHealthIndicatorTest {

    @InjectMocks
    private AppHealthIndicator appHealthIndicator;

    @Test
    void health_shouldReturnUp() {
        Health health = appHealthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsEntry("App", "Running smoothly");
    }
}
