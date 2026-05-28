package com.example.gymcrm.storage;

import com.example.gymcrm.model.Training;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TrainingStorage {
    private final Map<Long, Training> storage = new HashMap<>();

    public Map<Long, Training> getStorage() {
        return storage;
    }
}