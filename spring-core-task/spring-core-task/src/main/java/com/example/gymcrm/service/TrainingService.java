package com.example.gymcrm.service;

import com.example.gymcrm.dto.Auth;
import com.example.gymcrm.model.Trainee;
import com.example.gymcrm.model.Trainer;
import com.example.gymcrm.model.Training;
import com.example.gymcrm.repository.TrainingRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public TrainingService(TrainingRepository trainingRepository,
                           TraineeService traineeService,
                           TrainerService trainerService) {
        this.trainingRepository = trainingRepository;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    public Training createTraining(String traineeUsername, String trainerUsername, Training training) {
        Trainee trainee = traineeService.findByUsername(traineeUsername);
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee not found: " + traineeUsername);
        }

        Trainer trainer = trainerService.findByUsername(trainerUsername);
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer not found: " + trainerUsername);
        }

        if (!trainee.getUser().getIsActive() || !trainer.getUser().getIsActive()) {
            throw new IllegalArgumentException("Trainee or Trainer is not active");
        }

        training.setTrainee(trainee);
        training.setTrainer(trainer);

        return trainingRepository.save(training);
    }

    public List<Training> getTraineeTrainings(Auth auth,
                                              LocalDate fromDate,
                                              LocalDate toDate,
                                              String trainerName,
                                              Long trainingTypeId) {
        if (!traineeService.authenticate(auth)) {
            throw new IllegalArgumentException("Authentication failed");
        }

        return trainingRepository.findTraineeTrainings(
                auth.getUsername(), fromDate, toDate, trainerName, trainingTypeId
        );
    }

    public List<Training> getTrainerTrainings(Auth auth,
                                              LocalDate fromDate,
                                              LocalDate toDate,
                                              String traineeName) {
        if (!trainerService.authenticate(auth)) {
            throw new IllegalArgumentException("Authentication failed");
        }

        return trainingRepository.findTrainerTrainings(
                auth.getUsername(), fromDate, toDate, traineeName
        );
    }

    public Optional<Training> findById(Long id) {
        return trainingRepository.findById(id);
    }

    public List<Training> findAll() {
        return trainingRepository.findAll();
    }
}