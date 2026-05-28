package com.example.gymcrm.initialize;

import com.example.gymcrm.model.*;
import com.example.gymcrm.storage.TraineeStorage;
import com.example.gymcrm.storage.TrainerStorage;
import com.example.gymcrm.storage.TrainingStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class StorageInitializer implements ResourceLoaderAware {

    private static final Logger log = LoggerFactory.getLogger(StorageInitializer.class);

    @Value("${storage.init.file}")
    private String initFilePath;

    private ResourceLoader resourceLoader;
    private final TraineeStorage traineeStorage;
    private final TrainerStorage trainerStorage;
    private final TrainingStorage trainingStorage;

    public StorageInitializer(TraineeStorage traineeStorage,
                              TrainerStorage trainerStorage,
                              TrainingStorage trainingStorage) {
        this.traineeStorage = traineeStorage;
        this.trainerStorage = trainerStorage;
        this.trainingStorage = trainingStorage;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void initializeStorage() {
        try {
            Resource resource = resourceLoader.getResource(initFilePath);
            if (resource.exists()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                    String line;
                    String currentSection = "";

                    while ((line = reader.readLine()) != null) {
                        line = line.trim();

                        if (line.isEmpty() || line.startsWith("#")) {
                            continue;
                        }

                        if (line.equals("[TRAINEE]")) {
                            currentSection = "TRAINEE";
                            continue;
                        } else if (line.equals("[TRAINER]")) {
                            currentSection = "TRAINER";
                            continue;
                        } else if (line.equals("[TRAINING]")) {
                            currentSection = "TRAINING";
                            continue;
                        }

                        switch (currentSection) {
                            case "TRAINEE":
                                parseAndSaveTrainee(line);
                                break;
                            case "TRAINER":
                                parseAndSaveTrainer(line);
                                break;
                            case "TRAINING":
                                parseAndSaveTraining(line);
                                break;
                        }
                    }

                    log.info("Storage initialized successfully from: {}", initFilePath);
                }
            } else {
                log.warn("Initialization file not found: {}", initFilePath);
                initializeDefaultData();
            }
        } catch (Exception e) {
            log.error("Failed to initialize storage from file", e);
            initializeDefaultData();
        }
    }

    private void parseAndSaveTrainee(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 8) {
            try {
                Trainee trainee = new Trainee();
                Long id = Long.parseLong(parts[0].trim());
                trainee.setId(id);
                trainee.setFirstName(parts[1].trim());
                trainee.setLastName(parts[2].trim());
                trainee.setUsername(parts[3].trim());
                trainee.setPassword(parts[4].trim());
                trainee.setActive(Boolean.parseBoolean(parts[5].trim()));
                trainee.setDateOfBirth(LocalDate.parse(parts[6].trim(), DateTimeFormatter.ISO_LOCAL_DATE));
                trainee.setAddress(parts[7].trim());

                traineeStorage.getStorage().put(id, trainee);
                log.debug("Loaded trainee: {}", trainee.getUsername());
            } catch (Exception e) {
                log.error("Failed to parse trainee line: {}", line, e);
            }
        }
    }

    private void parseAndSaveTrainer(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 7) {
            try {
                Trainer trainer = new Trainer();
                Long id = Long.parseLong(parts[0].trim());
                trainer.setId(id);
                trainer.setFirstName(parts[1].trim());
                trainer.setLastName(parts[2].trim());
                trainer.setUsername(parts[3].trim());
                trainer.setPassword(parts[4].trim());
                trainer.setActive(Boolean.parseBoolean(parts[5].trim()));
                trainer.setSpecialization(parts[6].trim());

                trainerStorage.getStorage().put(id, trainer);
                log.debug("Loaded trainer: {}", trainer.getUsername());
            } catch (Exception e) {
                log.error("Failed to parse trainer line: {}", line, e);
            }
        }
    }

    private void parseAndSaveTraining(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 7) {
            try {
                Training training = new Training();
                Long id = Long.parseLong(parts[0].trim());
                training.setId(id);
                training.setTraineeId(Long.parseLong(parts[1].trim()));
                training.setTrainerId(Long.parseLong(parts[2].trim()));
                training.setTrainingName(parts[3].trim());
                training.setTrainingType(TrainingType.fromString(parts[4].trim()));
                training.setTrainingDate(LocalDate.parse(parts[5].trim(), DateTimeFormatter.ISO_LOCAL_DATE));
                training.setTrainingDuration(Integer.parseInt(parts[6].trim()));

                trainingStorage.getStorage().put(id, training);
                log.debug("Loaded training: {}", training.getTrainingName());
            } catch (Exception e) {
                log.error("Failed to parse training line: {}", line, e);
            }
        }
    }

    private void initializeDefaultData() {
        log.info("Initializing with default data");

        Trainee trainee1 = new Trainee();
        trainee1.setId(1L);
        trainee1.setFirstName("John");
        trainee1.setLastName("Doe");
        trainee1.setUsername("John.Doe");
        trainee1.setPassword("password123");
        trainee1.setActive(true);
        trainee1.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee1.setAddress("123 Main St, New York, NY");
        traineeStorage.getStorage().put(1L, trainee1);

        Trainee trainee2 = new Trainee();
        trainee2.setId(2L);
        trainee2.setFirstName("Alice");
        trainee2.setLastName("Johnson");
        trainee2.setUsername("Alice.Johnson");
        trainee2.setPassword("password123");
        trainee2.setActive(true);
        trainee2.setDateOfBirth(LocalDate.of(1988, 5, 15));
        trainee2.setAddress("456 Oak Ave, Los Angeles, CA");
        traineeStorage.getStorage().put(2L, trainee2);

        Trainer trainer1 = new Trainer();
        trainer1.setId(1L);
        trainer1.setFirstName("Jane");
        trainer1.setLastName("Smith");
        trainer1.setUsername("Jane.Smith");
        trainer1.setPassword("password123");
        trainer1.setSpecialization("Fitness");
        trainer1.setActive(true);
        trainerStorage.getStorage().put(1L, trainer1);

        Trainer trainer2 = new Trainer();
        trainer2.setId(2L);
        trainer2.setFirstName("Mike");
        trainer2.setLastName("Wilson");
        trainer2.setUsername("Mike.Wilson");
        trainer2.setPassword("password123");
        trainer2.setSpecialization("Yoga");
        trainer2.setActive(true);
        trainerStorage.getStorage().put(2L, trainer2);

        Training training1 = new Training();
        training1.setId(1L);
        training1.setTraineeId(1L);
        training1.setTrainerId(1L);
        training1.setTrainingName("Morning Cardio");
        training1.setTrainingType(TrainingType.CARDIO);
        training1.setTrainingDate(LocalDate.now());
        training1.setTrainingDuration(60);
        trainingStorage.getStorage().put(1L, training1);

        Training training2 = new Training();
        training2.setId(2L);
        training2.setTraineeId(2L);
        training2.setTrainerId(2L);
        training2.setTrainingName("Evening Yoga");
        training2.setTrainingType(TrainingType.YOGA);
        training2.setTrainingDate(LocalDate.now().plusDays(1));
        training2.setTrainingDuration(45);
        trainingStorage.getStorage().put(2L, training2);

        log.info("Default data initialized: 2 trainees, 2 trainers, 2 trainings");
    }
}