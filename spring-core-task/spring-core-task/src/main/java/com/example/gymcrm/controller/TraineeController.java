package com.example.gymcrm.controller;

import com.example.gymcrm.dto.AuthCredentials;
import com.example.gymcrm.dto.request.*;
import com.example.gymcrm.dto.response.TraineeProfileResponse;
import com.example.gymcrm.dto.response.TrainerSummaryDTO;
import com.example.gymcrm.dto.response.TrainingResponse;
import com.example.gymcrm.facade.GymFacade;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainees")
public class TraineeController {

    private final GymFacade facade;

    public TraineeController(GymFacade facade) {
        this.facade = facade;
    }

    @PostMapping
    public AuthCredentials register(@Valid @RequestBody TraineeRegistrationRequest req) {
        return facade.createTrainee(req);
    }

    @GetMapping
    public TraineeProfileResponse get(
            @RequestParam String username) {
        return facade.getTraineeByUsername(username);
    }

    @PutMapping
    public TraineeProfileResponse update(
            @Valid @RequestBody UpdateTraineeRequest req) {
        return facade.updateTrainee(
                req.getUsername(),
                req
        );
    }

    @DeleteMapping
    public void delete(
            @RequestParam String username) {
        facade.deleteTrainee(
                username
        );
    }

    @PatchMapping("/activate")
    public void activate(
            @Valid @RequestBody ActivateRequest req) {
        facade.setTraineeActiveStatus(
                req.getUsername(),
                req.getIsActive()
        );
    }

    @GetMapping("/unassigned-trainers")
    public List<TrainerSummaryDTO> unassigned(
            @RequestParam String username) {
        return facade.getUnassignedTrainers(
                username
        );
    }

    @PutMapping("/trainers")
    public List<TrainerSummaryDTO> updateTrainers(
            @Valid @RequestBody UpdateTraineeTrainersRequest req) {
        return facade.updateTraineeTrainers(
                req.getUsername(),
                req.getTrainerUsernames()
        );
    }

    @GetMapping("/trainings")
    public List<TrainingResponse> getTrainings(
            @RequestParam String username,
            @ModelAttribute TraineeTrainingQueryRequest query) {
        return facade.getTraineeTrainings(
                username,
                query.getPeriodFrom(),
                query.getPeriodTo(),
                query.getTrainerName(),
                query.getTrainingTypeId()
        );
    }
}