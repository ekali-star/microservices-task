package com.example.gymcrm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainingResponse {
    private String trainingName;
    private LocalDate trainingDate;
    private String trainingType;
    private Integer trainingDuration;
    private String partnerName;
}
