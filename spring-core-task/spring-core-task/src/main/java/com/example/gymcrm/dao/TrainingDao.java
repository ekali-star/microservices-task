package com.example.gymcrm.dao;

import com.example.gymcrm.model.Training;
import com.example.gymcrm.storage.TrainingStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class TrainingDao {
    private TrainingStorage storage;

    @Autowired
    public void setStorage(TrainingStorage storage) {
        this.storage = storage;
    }

    public void save(Long id, Training training) {
        training.setId(id);
        storage.getStorage().put(id, training);
    }

    public Training findById(Long id) {
        return storage.getStorage().get(id);
    }

    public void delete(Long id) {
        storage.getStorage().remove(id);
    }

    public Collection<Training> findAll() {
        return storage.getStorage().values();
    }
}