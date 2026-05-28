package com.example.trainerworkload.repository;

import com.example.trainerworkload.model.TrainerSummary;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class WorkloadRepository {

    private final ConcurrentHashMap<String, TrainerSummary> store = new ConcurrentHashMap<>();

    public void save(TrainerSummary summary) {
        store.put(summary.getUsername(), summary);
    }

    public Optional<TrainerSummary> findByUsername(String username) {
        return Optional.ofNullable(store.get(username));
    }

    public boolean existsByUsername(String username) {
        return store.containsKey(username);
    }
}
