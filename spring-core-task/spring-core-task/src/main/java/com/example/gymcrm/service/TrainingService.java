package com.example.gymcrm.service;

import com.example.gymcrm.client.WorkloadClient;
import com.example.gymcrm.dto.Auth;
import com.example.gymcrm.metric.TrainingMetrics;
import com.example.gymcrm.model.Trainee;
import com.example.gymcrm.model.Trainer;
import com.example.gymcrm.model.Training;
import com.example.gymcrm.repository.TrainingRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);

    private final TrainingRepository trainingRepository;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingMetrics trainingMetrics;
    private final WorkloadClient workloadClient;

    public TrainingService(TrainingRepository trainingRepository,
                           TraineeService traineeService,
                           TrainerService trainerService,
                           TrainingMetrics trainingMetrics,
                           WorkloadClient workloadClient) {
        this.trainingRepository = trainingRepository;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingMetrics = trainingMetrics;
        this.workloadClient = workloadClient;
    }

    public Training createTraining(String traineeUsername, String trainerUsername, Training training) {
        Trainee trainee = traineeService.findByUsername(traineeUsername);
        if (trainee == null) throw new IllegalArgumentException("Trainee not found: " + traineeUsername);

        Trainer trainer = trainerService.findByUsername(trainerUsername);
        if (trainer == null) throw new IllegalArgumentException("Trainer not found: " + trainerUsername);

        if (!trainee.getUser().getIsActive() || !trainer.getUser().getIsActive()) {
            throw new IllegalArgumentException("Trainee or Trainer is not active");
        }

        training.setTrainee(trainee);
        training.setTrainer(trainer);

        Training saved = trainingRepository.save(training);
        trainingMetrics.increment();

        log.info("Training created: id={}, trainer={}, trainee={}, date={}",
                saved.getId(), trainerUsername, traineeUsername, training.getTrainingDate());

        // Notify workload microservice — circuit breaker handles failures
        workloadClient.notifyTrainingAdded(saved);

        return saved;
    }

    /**
     * Delete a training by ID and notify the workload microservice.
     *
     * When can a training be deleted?
     * - A trainee cancels a planned session
     * - A trainer withdraws from a scheduled training
     * Both cases use the same DELETE action, which subtracts from the monthly summary.
     */
    public void deleteTraining(Long trainingId) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new IllegalArgumentException("Training not found: " + trainingId));

        log.info("Deleting training: id={}, trainer={}, date={}",
                trainingId,
                training.getTrainer().getUser().getUsername(),
                training.getTrainingDate());

        trainingRepository.delete(training);

        // Notify workload microservice to subtract the hours
        workloadClient.notifyTrainingDeleted(training);
    }

    public List<Training> getTraineeTrainings(Auth auth, LocalDate fromDate, LocalDate toDate,
                                              String trainerName, Long trainingTypeId) {
        if (!traineeService.authenticate(auth)) {
            throw new IllegalArgumentException("Authentication failed");
        }
        return getTraineeTrainings(auth.getUsername(), fromDate, toDate, trainerName, trainingTypeId);
    }

    public List<Training> getTraineeTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                              String trainerName, Long trainingTypeId) {
        return trainingRepository.findTraineeTrainings(username, fromDate, toDate, trainerName, trainingTypeId);
    }

    public List<Training> getTrainerTrainings(Auth auth, LocalDate fromDate, LocalDate toDate,
                                              String traineeName) {
        if (!trainerService.authenticate(auth)) {
            throw new IllegalArgumentException("Authentication failed");
        }
        return getTrainerTrainings(auth.getUsername(), fromDate, toDate, traineeName);
    }

    public List<Training> getTrainerTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                              String traineeName) {
        return trainingRepository.findTrainerTrainings(username, fromDate, toDate, traineeName);
    }

    public Optional<Training> findById(Long id) {
        return trainingRepository.findById(id);
    }

    public List<Training> findAll() {
        return trainingRepository.findAll();
    }
}
