package com.example.trainerworkload.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class TrainerSummary {

    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;

    private List<YearSummary> years = new ArrayList<>();

    @Data
    @NoArgsConstructor
    public static class YearSummary {
        private int year;
        private List<MonthSummary> months = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    public static class MonthSummary {
        private int month;
        private int trainingSummaryDuration;
    }
}
