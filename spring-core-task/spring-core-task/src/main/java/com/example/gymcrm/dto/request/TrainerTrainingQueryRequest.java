package com.example.gymcrm.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class TrainerTrainingQueryRequest {
    private String username;
    private LocalDate periodFrom;
    private LocalDate periodTo;
    private String traineeName;
}