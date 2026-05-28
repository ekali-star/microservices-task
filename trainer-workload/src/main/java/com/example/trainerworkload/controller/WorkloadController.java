package com.example.trainerworkload.controller;

import com.example.trainerworkload.dto.TrainerWorkloadRequest;
import com.example.trainerworkload.model.TrainerSummary;
import com.example.trainerworkload.service.WorkloadService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workload")
public class WorkloadController {

    private static final Logger log = LoggerFactory.getLogger(WorkloadController.class);

    private final WorkloadService workloadService;

    public WorkloadController(WorkloadService workloadService) {
        this.workloadService = workloadService;
    }

    @PostMapping
    public ResponseEntity<Void> updateWorkload(@Valid @RequestBody TrainerWorkloadRequest request) {
        log.info("Received workload update: trainer={}, action={}, date={}",
                request.getTrainerUsername(), request.getActionType(), request.getTrainingDate());
        workloadService.processWorkload(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerSummary> getSummary(@PathVariable String username) {
        log.info("Fetching workload summary for trainer={}", username);
        TrainerSummary summary = workloadService.getSummary(username);
        return ResponseEntity.ok(summary);
    }
}
