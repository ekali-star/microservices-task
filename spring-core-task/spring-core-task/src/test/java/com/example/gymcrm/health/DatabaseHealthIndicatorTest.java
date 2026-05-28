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
class DatabaseHealthIndicatorTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @InjectMocks
    private DatabaseHealthIndicator databaseHealthIndicator;

    @Test
    void health_whenDatabaseIsAvailable_shouldReturnUp() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(1)).thenReturn(true);

        Health health = databaseHealthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsEntry("Database", "Available");
    }

    @Test
    void health_whenDatabaseIsUnavailable_shouldReturnDown() throws Exception {
        when(dataSource.getConnection()).thenThrow(new RuntimeException("Connection failed"));

        Health health = databaseHealthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsEntry("Database", "Not reachable");
    }
}