package com.example.gymcrm.controller;

import com.example.gymcrm.dto.AuthCredentials;
import com.example.gymcrm.dto.request.*;
import com.example.gymcrm.dto.response.TraineeProfileResponse;
import com.example.gymcrm.dto.response.TrainerSummaryDTO;
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
class TraineeControllerTest {

    @Mock
    private GymFacade facade;

    @InjectMocks
    private TraineeController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // ── POST /api/trainees ────────────────────────────────────────────────────

    @Test
    void register_shouldReturn200AndCredentials_whenValidRequest() throws Exception {
        TraineeRegistrationRequest req = new TraineeRegistrationRequest();
        req.setFirstName("Alice");
        req.setLastName("Smith");

        AuthCredentials creds = new AuthCredentials("alice.smith", "generatedPass");
        when(facade.createTrainee(any())).thenReturn(creds);

        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice.smith"))
                .andExpect(jsonPath("$.password").value("generatedPass"));

        verify(facade).createTrainee(any(TraineeRegistrationRequest.class));
    }

    @Test
    void register_shouldCallFacadeOnce() throws Exception {
        TraineeRegistrationRequest req = new TraineeRegistrationRequest();
        req.setFirstName("Bob");
        req.setLastName("Jones");

        when(facade.createTrainee(any())).thenReturn(new AuthCredentials("bob.jones", "pass"));

        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(facade, times(1)).createTrainee(any());
    }

    // ── GET /api/trainees ─────────────────────────────────────────────────────

    @Test
    void get_shouldReturn200AndProfile_whenUsernameExists() throws Exception {
        TraineeProfileResponse profile = new TraineeProfileResponse();
        profile.setFirstName("Alice");
        profile.setLastName("Smith");
        profile.setIsActive(true);

        when(facade.getTraineeByUsername("alice.smith")).thenReturn(profile);

        mockMvc.perform(get("/api/trainees")
                        .param("username", "alice.smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.active").value(true));

        verify(facade).getTraineeByUsername("alice.smith");
    }

    @Test
    void get_shouldPassUsernameExactlyToFacade() throws Exception {
        when(facade.getTraineeByUsername("exact.username"))
                .thenReturn(new TraineeProfileResponse());

        mockMvc.perform(get("/api/trainees")
                        .param("username", "exact.username"))
                .andExpect(status().isOk());

        verify(facade).getTraineeByUsername("exact.username");
    }

    // ── PUT /api/trainees ─────────────────────────────────────────────────────

    @Test
    void update_shouldReturn200AndUpdatedProfile() throws Exception {
        UpdateTraineeRequest req = new UpdateTraineeRequest();
        req.setUsername("alice.smith");
        req.setFirstName("Alicia");
        req.setLastName("Smith");
        req.setIsActive(true);

        TraineeProfileResponse updated = new TraineeProfileResponse();
        updated.setFirstName("Alicia");
        updated.setLastName("Smith");
        updated.setIsActive(true);

        when(facade.updateTrainee(eq("alice.smith"), any())).thenReturn(updated);

        mockMvc.perform(put("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alicia"));

        verify(facade).updateTrainee(eq("alice.smith"), any(UpdateTraineeRequest.class));
    }

    @Test
    void update_shouldPassUsernameFromBodyToFacade() throws Exception {
        UpdateTraineeRequest req = new UpdateTraineeRequest();
        req.setUsername("specific.user");
        req.setFirstName("Name");
        req.setLastName("Last");

        when(facade.updateTrainee(eq("specific.user"), any()))
                .thenReturn(new TraineeProfileResponse());

        mockMvc.perform(put("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(facade).updateTrainee(eq("specific.user"), any());
    }

    // ── DELETE /api/trainees ──────────────────────────────────────────────────

    @Test
    void delete_shouldReturn200_whenTraineeExists() throws Exception {
        doNothing().when(facade).deleteTrainee("alice.smith");

        mockMvc.perform(delete("/api/trainees")
                        .param("username", "alice.smith"))
                .andExpect(status().isOk());

        verify(facade).deleteTrainee("alice.smith");
    }

    @Test
    void delete_shouldCallFacadeExactlyOnce() throws Exception {
        doNothing().when(facade).deleteTrainee(anyString());

        mockMvc.perform(delete("/api/trainees")
                        .param("username", "alice.smith"))
                .andExpect(status().isOk());

        verify(facade, times(1)).deleteTrainee("alice.smith");
    }

    // ── PATCH /api/trainees/activate ─────────────────────────────────────────

    @Test
    void activate_shouldReturn200_whenActivatingTrainee() throws Exception {
        ActivateRequest req = new ActivateRequest();
        req.setUsername("alice.smith");
        req.setIsActive(true);

        doNothing().when(facade).setTraineeActiveStatus("alice.smith", true);

        mockMvc.perform(patch("/api/trainees/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(facade).setTraineeActiveStatus("alice.smith", true);
    }

    @Test
    void activate_shouldReturn200_whenDeactivatingTrainee() throws Exception {
        ActivateRequest req = new ActivateRequest();
        req.setUsername("alice.smith");
        req.setIsActive(false);

        doNothing().when(facade).setTraineeActiveStatus("alice.smith", false);

        mockMvc.perform(patch("/api/trainees/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(facade).setTraineeActiveStatus("alice.smith", false);
    }

    // ── GET /api/trainees/unassigned-trainers ─────────────────────────────────

    @Test
    void unassigned_shouldReturn200AndListOfTrainers() throws Exception {
        TrainerSummaryDTO t1 = new TrainerSummaryDTO("trainer1", "Tom", "Brown", "Yoga");
        TrainerSummaryDTO t2 = new TrainerSummaryDTO("trainer2", "Sue", "White", "HIIT");

        when(facade.getUnassignedTrainers("alice.smith")).thenReturn(List.of(t1, t2));

        mockMvc.perform(get("/api/trainees/unassigned-trainers")
                        .param("username", "alice.smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("trainer1"))
                .andExpect(jsonPath("$[1].username").value("trainer2"));

        verify(facade).getUnassignedTrainers("alice.smith");
    }

    @Test
    void unassigned_shouldReturnEmptyList_whenNoUnassignedTrainers() throws Exception {
        when(facade.getUnassignedTrainers("alice.smith")).thenReturn(List.of());

        mockMvc.perform(get("/api/trainees/unassigned-trainers")
                        .param("username", "alice.smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ── PUT /api/trainees/trainers ────────────────────────────────────────────

    @Test
    void updateTrainers_shouldReturn200AndUpdatedTrainerList() throws Exception {
        UpdateTraineeTrainersRequest req = new UpdateTraineeTrainersRequest();
        req.setUsername("alice.smith");
        req.setTrainerUsernames(List.of("trainer1", "trainer2"));

        TrainerSummaryDTO t1 = new TrainerSummaryDTO("trainer1", "Tom", "Brown", "Yoga");
        TrainerSummaryDTO t2 = new TrainerSummaryDTO("trainer2", "Sue", "White", "HIIT");

        when(facade.updateTraineeTrainers(eq("alice.smith"), anyList()))
                .thenReturn(List.of(t1, t2));

        mockMvc.perform(put("/api/trainees/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("trainer1"));

        verify(facade).updateTraineeTrainers("alice.smith", List.of("trainer1", "trainer2"));
    }

    @Test
    void updateTrainers_shouldPassUsernamesListToFacade() throws Exception {
        UpdateTraineeTrainersRequest req = new UpdateTraineeTrainersRequest();
        req.setUsername("alice.smith");
        req.setTrainerUsernames(List.of("only.trainer"));

        when(facade.updateTraineeTrainers(any(), any())).thenReturn(List.of());

        mockMvc.perform(put("/api/trainees/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(facade).updateTraineeTrainers("alice.smith", List.of("only.trainer"));
    }

    // ── GET /api/trainees/trainings ───────────────────────────────────────────

    @Test
    void getTrainings_shouldReturn200AndTrainingList() throws Exception {
        TrainingResponse tr = new TrainingResponse();
        tr.setTrainingName("Morning Yoga");
        tr.setTrainingDate(LocalDate.of(2024, 6, 1));
        tr.setTrainingDuration(60);

        when(facade.getTraineeTrainings(eq("alice.smith"), any(), any(), any(), any()))
                .thenReturn(List.of(tr));

        mockMvc.perform(get("/api/trainees/trainings")
                        .param("username", "alice.smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].trainingName").value("Morning Yoga"));

        verify(facade).getTraineeTrainings(eq("alice.smith"), any(), any(), any(), any());
    }

    @Test
    void getTrainings_shouldPassAllQueryParamsToFacade() throws Exception {
        when(facade.getTraineeTrainings(any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/trainees/trainings")
                        .param("username", "alice.smith")
                        .param("periodFrom", "2024-01-01")
                        .param("periodTo", "2024-12-31")
                        .param("trainerName", "Tom")
                        .param("trainingTypeId", "1"))
                .andExpect(status().isOk());

        verify(facade).getTraineeTrainings(eq("alice.smith"), any(), any(), eq("Tom"), eq(1L));
    }

    @Test
    void getTrainings_shouldReturnEmptyList_whenNoTrainings() throws Exception {
        when(facade.getTraineeTrainings(any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/trainees/trainings")
                        .param("username", "alice.smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}