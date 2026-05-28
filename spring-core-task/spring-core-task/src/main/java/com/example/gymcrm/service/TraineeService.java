package com.example.gymcrm.service;

import com.example.gymcrm.dto.Auth;
import com.example.gymcrm.model.Trainee;
import com.example.gymcrm.model.Trainer;
import com.example.gymcrm.repository.TraineeRepository;
import com.example.gymcrm.repository.TrainerRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TraineeService extends UserService<Trainee> {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public TraineeService(TraineeRepository traineeRepository, TrainerRepository trainerRepository) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }

    @Override
    protected JpaRepository<Trainee, Long> getRepository() {
        return traineeRepository;
    }

    @Override
    protected Collection<Trainee> findAllUsers() {
        return traineeRepository.findAll();
    }

    @Override
    protected Long getId(Trainee user) {
        return user.getId();
    }

    @Override
    protected Optional<Trainee> findByUsernameOptional(String username) {
        return traineeRepository.findByUserUsername(username);
    }

    public Trainee updateTrainee(Auth auth, Trainee updatedTrainee) {
        if (!authenticate(auth)) {
            throw new IllegalArgumentException("Authentication failed");
        }

        Trainee existing = findByUsernameOptional(auth.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found"));

        existing.setDateOfBirth(updatedTrainee.getDateOfBirth());
        existing.setAddress(updatedTrainee.getAddress());

        existing.getUser().setFirstName(updatedTrainee.getUser().getFirstName());
        existing.getUser().setLastName(updatedTrainee.getUser().getLastName());

        return traineeRepository.save(existing);
    }

    public void deleteTrainee(Auth auth) {
        if (!authenticate(auth)) {
            throw new IllegalArgumentException("Authentication failed");
        }

        Trainee trainee = findByUsernameOptional(auth.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found"));

        traineeRepository.delete(trainee);
    }

    public List<Trainer> getUnassignedTrainers(Auth auth) {
        if (!authenticate(auth)) {
            throw new IllegalArgumentException("Authentication failed");
        }
        return trainerRepository.findNotAssignedToTrainee(auth.getUsername());
    }

    public void updateTrainers(Auth auth, List<String> trainerUsernames) {
        if (!authenticate(auth)) {
            throw new IllegalArgumentException("Authentication failed");
        }

        Trainee trainee = traineeRepository.findWithTrainersByUserUsername(auth.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found"));

        Set<String> currentTrainerUsernames = trainee.getTrainers().stream()
                .map(trainer -> trainer.getUser().getUsername())
                .collect(Collectors.toSet());

        Set<String> newTrainerUsernames = trainerUsernames.stream()
                .filter(username -> !currentTrainerUsernames.contains(username))
                .collect(Collectors.toSet());

        List<Trainer> newTrainers = trainerRepository.findByUserUsernameIn(newTrainerUsernames);

        Set<String> foundUsernames = newTrainers.stream()
                .map(trainer -> trainer.getUser().getUsername())
                .collect(Collectors.toSet());

        Set<String> missingUsernames = newTrainerUsernames.stream()
                .filter(username -> !foundUsernames.contains(username))
                .collect(Collectors.toSet());

        if (!missingUsernames.isEmpty()) {
            throw new IllegalArgumentException("Trainers not found: " + missingUsernames);
        }

        Set<String> requestedUsernamesSet = Set.copyOf(trainerUsernames);

        trainee.getTrainers().removeIf(trainer ->
                !requestedUsernamesSet.contains(trainer.getUser().getUsername()));

        trainee.getTrainers().addAll(newTrainers);

        traineeRepository.save(trainee);
    }
}