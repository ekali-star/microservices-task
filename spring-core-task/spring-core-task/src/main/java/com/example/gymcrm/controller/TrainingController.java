package com.example.gymcrm.controller;

import com.example.gymcrm.dto.request.AddTrainingRequest;
import com.example.gymcrm.facade.GymFacade;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainings")
public class TrainingController {

    private final GymFacade facade;

    public TrainingController(GymFacade facade) {
        this.facade = facade;
    }

    @PostMapping
    public ResponseEntity<Void> add(@Valid @RequestBody AddTrainingRequest req) {
        facade.createTraining(req);
        return ResponseEntity.ok().build();
    }
}