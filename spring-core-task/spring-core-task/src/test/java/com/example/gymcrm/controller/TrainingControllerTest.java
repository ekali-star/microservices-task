package com.example.gymcrm.controller;

import com.example.gymcrm.dto.request.AddTrainingRequest;
import com.example.gymcrm.facade.GymFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    @Mock
    private GymFacade facade;

    @InjectMocks
    private TrainingController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private AddTrainingRequest buildValidRequest() {
        AddTrainingRequest req = new AddTrainingRequest();
        req.setTraineeUsername("alice.smith");
        req.setTrainerUsername("tom.brown");
        req.setTrainingName("Morning Yoga");
        req.setTrainingDate(LocalDate.of(2024, 6, 15));
        req.setTrainingDuration(60);
        return req;
    }

    // ── POST /api/trainings ───────────────────────────────────────────────────

    @Test
    void add_shouldReturn200_whenValidRequest() throws Exception {
        doNothing().when(facade).createTraining(any());

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidRequest())))
                .andExpect(status().isOk());

        verify(facade).createTraining(any(AddTrainingRequest.class));
    }

    @Test
    void add_shouldCallFacadeExactlyOnce() throws Exception {
        doNothing().when(facade).createTraining(any());

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidRequest())))
                .andExpect(status().isOk());

        verify(facade, times(1)).createTraining(any());
    }

    @Test
    void add_shouldReturnOk_withDifferentDurations() throws Exception {
        doNothing().when(facade).createTraining(any());

        for (int duration : new int[]{1, 30, 90, 120}) {
            AddTrainingRequest req = buildValidRequest();
            req.setTrainingDuration(duration);

            mockMvc.perform(post("/api/trainings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk());
        }

        verify(facade, times(4)).createTraining(any());
    }

    @Test
    void add_shouldReturnOk_withFutureTrainingDate() throws Exception {
        doNothing().when(facade).createTraining(any());

        AddTrainingRequest req = buildValidRequest();
        req.setTrainingDate(LocalDate.of(2099, 12, 31));

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(facade).createTraining(any());
    }

    @Test
    void add_shouldReturnOk_withPastTrainingDate() throws Exception {
        doNothing().when(facade).createTraining(any());

        AddTrainingRequest req = buildValidRequest();
        req.setTrainingDate(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(facade).createTraining(any());
    }

    @Test
    void add_shouldNotCallFacade_whenContentTypeIsMissing() throws Exception {
        mockMvc.perform(post("/api/trainings")
                        .content(objectMapper.writeValueAsString(buildValidRequest())))
                .andExpect(status().isUnsupportedMediaType());

        verifyNoInteractions(facade);
    }
}