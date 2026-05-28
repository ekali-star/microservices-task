package com.example.gymcrm.controller;

import com.example.gymcrm.dto.AuthCredentials;
import com.example.gymcrm.dto.request.*;
import com.example.gymcrm.dto.response.TrainerProfileResponse;
import com.example.gymcrm.dto.response.TrainingResponse;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    @Mock
    private GymFacade facade;

    @InjectMocks
    private TrainerController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // ── POST /api/trainers ────────────────────────────────────────────────────

    @Test
    void register_shouldReturn200AndCredentials_whenValidRequest() throws Exception {
        TrainerRegistrationRequest req = new TrainerRegistrationRequest();
        req.setFirstName("Tom");
        req.setLastName("Brown");
        req.setSpecializationId(1L);

        AuthCredentials creds = new AuthCredentials("tom.brown", "generatedPass");
        when(facade.createTrainer(any())).thenReturn(creds);

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("tom.brown"))
                .andExpect(jsonPath("$.password").value("generatedPass"));

        verify(facade).createTrainer(any(TrainerRegistrationRequest.class));
    }

    @Test
    void register_shouldCallFacadeExactlyOnce() throws Exception {
        TrainerRegistrationRequest req = new TrainerRegistrationRequest();
        req.setFirstName("Ann");
        req.setLastName("Lee");
        req.setSpecializationId(2L);

        when(facade.createTrainer(any())).thenReturn(new AuthCredentials("ann.lee", "pass"));

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(facade, times(1)).createTrainer(any());
    }

    // ── GET /api/trainers ─────────────────────────────────────────────────────

    @Test
    void get_shouldReturn200AndProfile_whenUsernameExists() throws Exception {
        TrainerProfileResponse profile = new TrainerProfileResponse();
        profile.setFirstName("Tom");
        profile.setLastName("Brown");
        profile.setIsActive(true);
        profile.setSpecialization("Yoga");

        when(facade.getTrainerByUsername("tom.brown")).thenReturn(profile);

        mockMvc.perform(get("/api/trainers")
                        .param("username", "tom.brown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Tom"))
                .andExpect(jsonPath("$.lastName").value("Brown"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.specialization").value("Yoga"));

        verify(facade).getTrainerByUsername("tom.brown");
    }

    @Test
    void get_shouldPassExactUsernameToFacade() throws Exception {
        when(facade.getTrainerByUsername("exact.trainer"))
                .thenReturn(new TrainerProfileResponse());

        mockMvc.perform(get("/api/trainers")
                        .param("username", "exact.trainer"))
                .andExpect(status().isOk());

        verify(facade).getTrainerByUsername("exact.trainer");
    }

    // ── PUT /api/trainers ─────────────────────────────────────────────────────

    @Test
    void update_shouldReturn200AndUpdatedProfile() throws Exception {
        UpdateTrainerRequest req = new UpdateTrainerRequest();
        req.setUsername("tom.brown");
        req.setFirstName("Thomas");
        req.setLastName("Brown");
        req.setIsActive(true);

        TrainerProfileResponse updated = new TrainerProfileResponse();
        updated.setFirstName("Thomas");
        updated.setLastName("Brown");
        updated.setIsActive(true);

        when(facade.updateTrainer(eq("tom.brown"), any())).thenReturn(updated);

        mockMvc.perform(put("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Thomas"));

        verify(facade).updateTrainer(eq("tom.brown"), any(UpdateTrainerRequest.class));
    }

    @Test
    void update_shouldPassUsernameFromBodyToFacade() throws Exception {
        UpdateTrainerRequest req = new UpdateTrainerRequest();
        req.setUsername("specific.trainer");
        req.setFirstName("Name");
        req.setLastName("Last");

        when(facade.updateTrainer(eq("specific.trainer"), any()))
                .thenReturn(new TrainerProfileResponse());

        mockMvc.perform(put("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(facade).updateTrainer(eq("specific.trainer"), any());
    }

    // ── PATCH /api/trainers/activate ──────────────────────────────────────────

    @Test
    void activate_shouldReturn200_whenActivatingTrainer() throws Exception {
        ActivateRequest req = new ActivateRequest();
        req.setUsername("tom.brown");
        req.setIsActive(true);

        doNothing().when(facade).setTrainerActiveStatus("tom.brown", true);

        mockMvc.perform(patch("/api/trainers/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(facade).setTrainerActiveStatus("tom.brown", true);
    }

    @Test
    void activate_shouldReturn200_whenDeactivatingTrainer() throws Exception {
        ActivateRequest req = new ActivateRequest();
        req.setUsername("tom.brown");
        req.setIsActive(false);

        doNothing().when(facade).setTrainerActiveStatus("tom.brown", false);

        mockMvc.perform(patch("/api/trainers/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(facade).setTrainerActiveStatus("tom.brown", false);
    }

    // ── GET /api/trainers/trainings ───────────────────────────────────────────

    @Test
    void getTrainings_shouldReturn200AndTrainingList() throws Exception {
        TrainingResponse tr = new TrainingResponse();
        tr.setTrainingName("Evening Strength");
        tr.setTrainingDate(LocalDate.of(2024, 7, 10));
        tr.setTrainingDuration(45);

        when(facade.getTrainerTrainings(eq("tom.brown"), any(), any(), any()))
                .thenReturn(List.of(tr));

        mockMvc.perform(get("/api/trainers/trainings")
                        .param("username", "tom.brown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].trainingName").value("Evening Strength"));

        verify(facade).getTrainerTrainings(eq("tom.brown"), any(), any(), any());
    }

    @Test
    void getTrainings_shouldPassAllQueryParamsToFacade() throws Exception {
        when(facade.getTrainerTrainings(any(), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/trainers/trainings")
                        .param("username", "tom.brown")
                        .param("periodFrom", "2024-01-01")
                        .param("periodTo", "2024-12-31")
                        .param("traineeName", "Alice"))
                .andExpect(status().isOk());

        verify(facade).getTrainerTrainings(eq("tom.brown"), any(), any(), eq("Alice"));
    }

    @Test
    void getTrainings_shouldReturnEmptyList_whenNoTrainings() throws Exception {
        when(facade.getTrainerTrainings(any(), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/trainers/trainings")
                        .param("username", "tom.brown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}