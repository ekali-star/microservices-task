package com.example.gymcrm.repository;

import com.example.gymcrm.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    @Query("""
                select t from Training t
                where t.trainee.user.username = :username
                  and (:from is null or t.trainingDate >= :from)
                  and (:to is null or t.trainingDate <= :to)
                  and (:trainerName is null or
                       lower(concat(t.trainer.user.firstName,' ',t.trainer.user.lastName))
                       like lower(concat('%', :trainerName, '%')))
                  and (:typeId is null or t.trainingType.id = :typeId)
            """)
    List<Training> findTraineeTrainings(
            @Param("username") String username,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("trainerName") String trainerName,
            @Param("typeId") Long typeId
    );

    @Query("""
        select t from Training t
        where t.trainer.user.username = :username
          and (:from is null or t.trainingDate >= :from)
          and (:to is null or t.trainingDate <= :to)
          and (:traineeName is null or
               lower(concat(t.trainee.user.firstName,' ',t.trainee.user.lastName))
               like lower(concat('%', :traineeName, '%')))
    """)
    List<Training> findTrainerTrainings(
            @Param("username") String username,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("traineeName") String traineeName
    );
}