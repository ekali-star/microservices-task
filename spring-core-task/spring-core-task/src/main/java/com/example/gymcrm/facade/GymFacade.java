package com.example.gymcrm.facade;

import com.example.gymcrm.dto.Auth;
import com.example.gymcrm.dto.AuthCredentials;
import com.example.gymcrm.model.*;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.service.TrainingService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
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

    public AuthCredentials createTrainee(Trainee trainee) {
        return traineeService.create(trainee);
    }

    public AuthCredentials createTrainer(Trainer trainer) {
        return trainerService.create(trainer);
    }

    public Training createTraining(String traineeUsername, String trainerUsername, Training training) {
        return trainingService.createTraining(traineeUsername, trainerUsername, training);
    }

    public Trainee getTraineeById(Long id) {
        return traineeService.findById(id);
    }

    public Trainee getTraineeByUsername(String username) {
        return traineeService.findByUsername(username);
    }

    public Collection<Trainee> getAllTrainees() {
        return traineeService.findAll();
    }

    public Trainer getTrainerById(Long id) {
        return trainerService.findById(id);
    }

    public Trainer getTrainerByUsername(String username) {
        return trainerService.findByUsername(username);
    }

    public Collection<Trainer> getAllTrainers() {
        return trainerService.findAll();
    }

    public Collection<Training> getAllTrainings() {
        return trainingService.findAll();
    }

    public List<Training> getTraineeTrainings(String username, String password,
                                              LocalDate fromDate, LocalDate toDate,
                                              String trainerName, Long trainingTypeId) {
        Auth auth = new Auth(username, password);
        return trainingService.getTraineeTrainings(auth, fromDate, toDate, trainerName, trainingTypeId);
    }

    public List<Training> getTrainerTrainings(String username, String password,
                                              LocalDate fromDate, LocalDate toDate,
                                              String traineeName) {
        Auth auth = new Auth(username, password);
        return trainingService.getTrainerTrainings(auth, fromDate, toDate, traineeName);
    }

    public List<Trainer> getUnassignedTrainers(String username, String password) {
        Auth auth = new Auth(username, password);
        return traineeService.getUnassignedTrainers(auth);
    }

    public Trainee updateTrainee(String username, String password, Trainee trainee) {
        Auth auth = new Auth(username, password);
        return traineeService.updateTrainee(auth, trainee);
    }

    public Trainer updateTrainer(String username, String password, Trainer trainer) {
        Auth auth = new Auth(username, password);
        return trainerService.updateTrainer(auth, trainer);
    }

    public void updateTraineeTrainers(String username, String password, List<String> trainerUsernames) {
        Auth auth = new Auth(username, password);
        traineeService.updateTrainers(auth, trainerUsernames);
    }

    public void deleteTrainee(String username, String password) {
        Auth auth = new Auth(username, password);
        traineeService.deleteTrainee(auth);
    }

    public void changeTraineePassword(String username, String password, String newPassword) {
        Auth auth = new Auth(username, password);
        traineeService.changePassword(auth, newPassword);
    }

    public void changeTrainerPassword(String username, String password, String newPassword) {
        Auth auth = new Auth(username, password);
        trainerService.changePassword(auth, newPassword);
    }

    public void activateTrainee(String username, String password) {
        Auth auth = new Auth(username, password);
        traineeService.setActiveStatus(auth, true);
    }

    public void deactivateTrainee(String username, String password) {
        Auth auth = new Auth(username, password);
        traineeService.setActiveStatus(auth, false);
    }

    public void activateTrainer(String username, String password) {
        Auth auth = new Auth(username, password);
        trainerService.setActiveStatus(auth, true);
    }

    public void deactivateTrainer(String username, String password) {
        Auth auth = new Auth(username, password);
        trainerService.setActiveStatus(auth, false);
    }

    public boolean authenticateTrainee(String username, String password) {
        return traineeService.authenticate(username, password);
    }

    public boolean authenticateTrainer(String username, String password) {
        return trainerService.authenticate(username, password);
    }
}