package com.example.gymcrm.facade;

import com.example.gymcrm.dto.Auth;
import com.example.gymcrm.dto.AuthCredentials;
import com.example.gymcrm.dto.request.*;
import com.example.gymcrm.dto.response.*;
import com.example.gymcrm.model.*;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.service.TrainingService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public void login(LoginRequest req) {
        if (!traineeService.authenticate(req.getUsername(), req.getPassword()) &&
                !trainerService.authenticate(req.getUsername(), req.getPassword())) {
            throw new IllegalArgumentException("Authentication failed");
        }
    }

    public void changePassword(String username, ChangePasswordRequest req) {
        Auth auth = new Auth(username, req.getOldPassword());

        if (traineeService.authenticate(auth)) {
            traineeService.changePassword(auth, req.getNewPassword());
        } else if (trainerService.authenticate(auth)) {
            trainerService.changePassword(auth, req.getNewPassword());
        } else {
            throw new IllegalArgumentException("Authentication failed");
        }
    }

    public AuthCredentials createTrainee(TraineeRegistrationRequest req) {
        User user = User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .build();

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(req.getDateOfBirth());
        trainee.setAddress(req.getAddress());

        return traineeService.create(trainee);
    }

    public TraineeProfileResponse getTraineeByUsername(String username) {
        return toTraineeProfile(traineeService.findByUsername(username));
    }

    public TraineeProfileResponse updateTrainee(String username, UpdateTraineeRequest req) {
        Trainee patch = new Trainee();
        patch.setUser(User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .build());
        patch.setDateOfBirth(req.getDateOfBirth());
        patch.setAddress(req.getAddress());

        return toTraineeProfile(traineeService.updateTrainee(username, patch));
    }

    public void deleteTrainee(String username) {
        traineeService.deleteTrainee(username);
    }

    public void setTraineeActiveStatus(String username, Boolean isActive) {
        traineeService.setActiveStatus(username, isActive);
    }

    public List<TrainerSummaryDTO> getUnassignedTrainers(String username) {
        return traineeService.getUnassignedTrainers(username)
                .stream()
                .map(this::toTrainerSummary)
                .toList();
    }

    public List<TrainerSummaryDTO> updateTraineeTrainers(String username, List<String> trainers) {
        traineeService.updateTrainers(username, trainers);
        return getTraineeByUsername(username).getTrainers();
    }

    public List<TrainingResponse> getTraineeTrainings(
            String username,
            java.time.LocalDate from,
            java.time.LocalDate to,
            String trainerName,
            Long typeId) {

        return trainingService.getTraineeTrainings(username, from, to, trainerName, typeId)
                .stream()
                .map(this::toTrainingResponseForTrainee)
                .toList();
    }

    public AuthCredentials createTrainer(TrainerRegistrationRequest req) {
        User user = User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .build();

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(TrainingType.builder().id(req.getSpecializationId()).build());

        return trainerService.create(trainer);
    }

    public TrainerProfileResponse getTrainerByUsername(String username) {
        return toTrainerProfile(trainerService.findByUsername(username));
    }

    public TrainerProfileResponse updateTrainer(String username, UpdateTrainerRequest req) {
        Trainer patch = new Trainer();
        patch.setUser(User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .build());

        return toTrainerProfile(trainerService.updateTrainer(username, patch));
    }

    public void setTrainerActiveStatus(String username, Boolean isActive) {
        trainerService.setActiveStatus(username, isActive);
    }

    public List<TrainingResponse> getTrainerTrainings(
            String username,
            java.time.LocalDate from,
            java.time.LocalDate to,
            String traineeName) {

        return trainingService.getTrainerTrainings(username, from, to, traineeName)
                .stream()
                .map(this::toTrainingResponseForTrainer)
                .toList();
    }

    public void createTraining(AddTrainingRequest req) {
        Training t = Training.builder()
                .trainingName(req.getTrainingName())
                .trainingDate(req.getTrainingDate())
                .trainingDuration(req.getTrainingDuration())
                .build();

        trainingService.createTraining(req.getTraineeUsername(), req.getTrainerUsername(), t);
    }

    public List<TrainingTypeResponse> getAllTrainingTypes() {
        return trainingService.findAll()
                .stream()
                .map(t -> new TrainingTypeResponse(
                        t.getTrainingType().getId(),
                        t.getTrainingType().getTrainingTypeName()))
                .toList();
    }

    private TraineeProfileResponse toTraineeProfile(Trainee t) {
        return new TraineeProfileResponse(
                t.getUser().getUsername(),
                t.getUser().getFirstName(),
                t.getUser().getLastName(),
                t.getDateOfBirth(),
                t.getAddress(),
                t.getUser().getIsActive(),
                t.getTrainers().stream().map(this::toTrainerSummary).toList()
        );
    }

    private TrainerProfileResponse toTrainerProfile(Trainer tr) {
        return new TrainerProfileResponse(
                tr.getUser().getUsername(),
                tr.getUser().getFirstName(),
                tr.getUser().getLastName(),
                tr.getSpecialization().getTrainingTypeName(),
                tr.getUser().getIsActive(),
                tr.getTrainees().stream().map(this::toTraineeSummary).toList()
        );
    }

    private TrainerSummaryDTO toTrainerSummary(Trainer tr) {
        return new TrainerSummaryDTO(
                tr.getUser().getUsername(),
                tr.getUser().getFirstName(),
                tr.getUser().getLastName(),
                tr.getSpecialization().getTrainingTypeName()
        );
    }

    private TraineeSummaryDTO toTraineeSummary(Trainee t) {
        return new TraineeSummaryDTO(
                t.getUser().getUsername(),
                t.getUser().getFirstName(),
                t.getUser().getLastName()
        );
    }

    private TrainingResponse toTrainingResponseForTrainee(Training t) {
        return new TrainingResponse(
                t.getTrainingName(),
                t.getTrainingDate(),
                t.getTrainingType().getTrainingTypeName(),
                t.getTrainingDuration(),
                t.getTrainer().getUser().getUsername()
        );
    }

    private TrainingResponse toTrainingResponseForTrainer(Training t) {
        return new TrainingResponse(
                t.getTrainingName(),
                t.getTrainingDate(),
                t.getTrainingType().getTrainingTypeName(),
                t.getTrainingDuration(),
                t.getTrainee().getUser().getUsername()
        );
    }
}