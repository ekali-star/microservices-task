package com.example.gymcrm.repository;

import com.example.gymcrm.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByUserUsername(String username);

    List<Trainer> findByUserUsernameIn(Set<String> usernames);

    @Query("SELECT t FROM Trainer t WHERE t NOT IN " +
            "(SELECT tr FROM Trainee tn JOIN tn.trainers tr WHERE tn.user.username = :traineeUsername)")
    List<Trainer> findNotAssignedToTrainee(@Param("traineeUsername") String traineeUsername);
}