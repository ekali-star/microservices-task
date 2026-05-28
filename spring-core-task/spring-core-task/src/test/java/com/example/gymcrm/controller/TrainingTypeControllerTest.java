package com.example.gymcrm.controller;

import com.example.gymcrm.dto.response.TrainingTypeResponse;
import com.example.gymcrm.facade.GymFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TrainingTypeControllerTest {

    @Mock
    private GymFacade facade;

    @InjectMocks
    private TrainingTypeController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    // ── GET /api/training-types ───────────────────────────────────────────────

    @Test
    void getAll_shouldReturn200AndAllTrainingTypes() throws Exception {
        TrainingTypeResponse yoga  = new TrainingTypeResponse(1L, "Yoga");
        TrainingTypeResponse hiit  = new TrainingTypeResponse(2L, "HIIT");
        TrainingTypeResponse pilates = new TrainingTypeResponse(3L, "Pilates");

        when(facade.getAllTrainingTypes()).thenReturn(List.of(yoga, hiit, pilates));

        mockMvc.perform(get("/api/training-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].trainingTypeName").value("Yoga"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].trainingTypeName").value("HIIT"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].trainingTypeName").value("Pilates"));

        verify(facade).getAllTrainingTypes();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoTypesExist() throws Exception {
        when(facade.getAllTrainingTypes()).thenReturn(List.of());

        mockMvc.perform(get("/api/training-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(facade).getAllTrainingTypes();
    }

    @Test
    void getAll_shouldReturnSingleItem_whenOnlyOneTypeExists() throws Exception {
        when(facade.getAllTrainingTypes())
                .thenReturn(List.of(new TrainingTypeResponse(1L, "Yoga")));

        mockMvc.perform(get("/api/training-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].trainingTypeName").value("Yoga"));

        verify(facade).getAllTrainingTypes();
    }

    @Test
    void getAll_shouldCallFacadeExactlyOnce() throws Exception {
        when(facade.getAllTrainingTypes()).thenReturn(List.of());

        mockMvc.perform(get("/api/training-types"))
                .andExpect(status().isOk());

        verify(facade, times(1)).getAllTrainingTypes();
    }

    @Test
    void getAll_shouldReturnJsonContentType() throws Exception {
        when(facade.getAllTrainingTypes()).thenReturn(List.of());

        mockMvc.perform(get("/api/training-types"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    void getAll_shouldHandleLargeNumberOfTypes() throws Exception {
        List<TrainingTypeResponse> largeList = java.util.stream.IntStream.rangeClosed(1, 50)
                .mapToObj(i -> new TrainingTypeResponse((long) i, "Type " + i))
                .toList();

        when(facade.getAllTrainingTypes()).thenReturn(largeList);

        mockMvc.perform(get("/api/training-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(50))
                .andExpect(jsonPath("$[49].trainingTypeName").value("Type 50"));
    }
}