package com.example.gymcrm.storage;

import com.example.gymcrm.model.Trainee;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TraineeStorage {
    private final Map<Long, Trainee> storage = new HashMap<>();

    public Map<Long, Trainee> getStorage() {
        return storage;
    }
}