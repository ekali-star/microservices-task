package com.example.gymcrm.repository;

import com.example.gymcrm.model.Trainee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    Optional<Trainee> findByUserUsername(String username);

    @EntityGraph(attributePaths = {"trainers", "trainers.user"})
    Optional<Trainee> findWithTrainersByUserUsername(String username);
}