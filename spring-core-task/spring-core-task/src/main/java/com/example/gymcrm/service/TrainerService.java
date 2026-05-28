package com.example.gymcrm.service;

import com.example.gymcrm.dto.Auth;
import com.example.gymcrm.model.Trainer;
import com.example.gymcrm.repository.TrainerRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@Transactional
public class TrainerService extends UserService<Trainer> {

    private final TrainerRepository trainerRepository;

    public TrainerService(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Override
    protected JpaRepository<Trainer, Long> getRepository() {
        return trainerRepository;
    }

    @Override
    protected Collection<Trainer> findAllUsers() {
        return trainerRepository.findAll();
    }

    @Override
    protected Long getId(Trainer user) {
        return user.getId();
    }

    @Override
    protected Optional<Trainer> findByUsernameOptional(String username) {
        return trainerRepository.findByUserUsername(username);
    }

    public Trainer updateTrainer(Auth auth, Trainer updatedTrainer) {
        if (!authenticate(auth)) {
            throw new IllegalArgumentException("Authentication failed");
        }

        Trainer existing = findByUsernameOptional(auth.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));

        existing.setSpecialization(updatedTrainer.getSpecialization());
        existing.getUser().setFirstName(updatedTrainer.getUser().getFirstName());
        existing.getUser().setLastName(updatedTrainer.getUser().getLastName());

        return trainerRepository.save(existing);
    }
}