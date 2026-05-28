package com.example.gymcrm.service;

import com.example.gymcrm.metric.UserMetrics;
import com.example.gymcrm.model.Trainer;
import com.example.gymcrm.repository.TrainerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@Transactional
public class TrainerService extends UserService<Trainer> {

    private final TrainerRepository trainerRepository;

    @Autowired
    public TrainerService(TrainerRepository trainerRepository,
                          UserMetrics userMetrics,
                          PasswordEncoder passwordEncoder) {
        super(userMetrics, passwordEncoder);
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

    public Trainer updateTrainer(String username, Trainer updatedTrainer) {
        Trainer existing = findByUsernameOptional(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));

        existing.getUser().setFirstName(updatedTrainer.getUser().getFirstName());
        existing.getUser().setLastName(updatedTrainer.getUser().getLastName());

        return trainerRepository.save(existing);
    }

    @Override
    protected void afterCreate(Trainer entity) {
        userMetrics.incrementTrainer();
    }
}