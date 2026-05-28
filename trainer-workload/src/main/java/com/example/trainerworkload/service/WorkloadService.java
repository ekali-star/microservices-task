package com.example.trainerworkload.service;

import com.example.trainerworkload.dto.TrainerWorkloadRequest;
import com.example.trainerworkload.model.ActionType;
import com.example.trainerworkload.model.TrainerSummary;
import com.example.trainerworkload.repository.WorkloadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WorkloadService {

    private static final Logger log = LoggerFactory.getLogger(WorkloadService.class);

    private final WorkloadRepository workloadRepository;

    public WorkloadService(WorkloadRepository workloadRepository) {
        this.workloadRepository = workloadRepository;
    }

    public void processWorkload(TrainerWorkloadRequest request) {
        log.debug("Processing workload: trainer={}, action={}, date={}, duration={}",
                request.getTrainerUsername(), request.getActionType(),
                request.getTrainingDate(), request.getTrainingDuration());

        TrainerSummary summary = workloadRepository
                .findByUsername(request.getTrainerUsername())
                .orElseGet(() -> createNewSummary(request));

        summary.setFirstName(request.getTrainerFirstName());
        summary.setLastName(request.getTrainerLastName());
        summary.setIsActive(request.getIsActive());

        int year = request.getTrainingDate().getYear();
        int month = request.getTrainingDate().getMonthValue();
        int duration = request.getTrainingDuration();

        if (request.getActionType() == ActionType.ADD) {
            addDuration(summary, year, month, duration);
        } else {
            deleteDuration(summary, year, month, duration);
        }

        workloadRepository.save(summary);
        log.info("Workload updated for trainer={}, year={}, month={}, action={}",
                request.getTrainerUsername(), year, month, request.getActionType());
    }

    public TrainerSummary getSummary(String username) {
        return workloadRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No workload data found for trainer: " + username));
    }

    private TrainerSummary createNewSummary(TrainerWorkloadRequest request) {
        TrainerSummary summary = new TrainerSummary();
        summary.setUsername(request.getTrainerUsername());
        summary.setFirstName(request.getTrainerFirstName());
        summary.setLastName(request.getTrainerLastName());
        summary.setIsActive(request.getIsActive());
        return summary;
    }

    private void addDuration(TrainerSummary summary, int year, int month, int duration) {
        TrainerSummary.YearSummary yearSummary = findOrCreateYear(summary, year);
        TrainerSummary.MonthSummary monthSummary = findOrCreateMonth(yearSummary, month);
        monthSummary.setTrainingSummaryDuration(
                monthSummary.getTrainingSummaryDuration() + duration);
    }

    private void deleteDuration(TrainerSummary summary, int year, int month, int duration) {
        summary.getYears().stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .ifPresent(yearSummary -> {
                    yearSummary.getMonths().stream()
                            .filter(m -> m.getMonth() == month)
                            .findFirst()
                            .ifPresent(monthSummary -> {
                                int newDuration = monthSummary.getTrainingSummaryDuration() - duration;
                                if (newDuration <= 0) {
                                    yearSummary.getMonths().remove(monthSummary);
                                } else {
                                    monthSummary.setTrainingSummaryDuration(newDuration);
                                }
                            });

                    if (yearSummary.getMonths().isEmpty()) {
                        summary.getYears().remove(yearSummary);
                    }
                });
    }

    private TrainerSummary.YearSummary findOrCreateYear(TrainerSummary summary, int year) {
        return summary.getYears().stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseGet(() -> {
                    TrainerSummary.YearSummary y = new TrainerSummary.YearSummary();
                    y.setYear(year);
                    summary.getYears().add(y);
                    return y;
                });
    }

    private TrainerSummary.MonthSummary findOrCreateMonth(TrainerSummary.YearSummary yearSummary, int month) {
        return yearSummary.getMonths().stream()
                .filter(m -> m.getMonth() == month)
                .findFirst()
                .orElseGet(() -> {
                    TrainerSummary.MonthSummary m = new TrainerSummary.MonthSummary();
                    m.setMonth(month);
                    yearSummary.getMonths().add(m);
                    return m;
                });
    }
}
