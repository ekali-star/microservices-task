package com.example.gymcrm.controller;

import com.example.gymcrm.dto.response.TrainingTypeResponse;
import com.example.gymcrm.facade.GymFacade;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/training-types")
public class TrainingTypeController {

    private final GymFacade facade;

    public TrainingTypeController(GymFacade facade) {
        this.facade = facade;
    }

    @GetMapping
    public List<TrainingTypeResponse> getAll() {
        return facade.getAllTrainingTypes();
    }
}