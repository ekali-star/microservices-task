package com.example.gymcrm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainerProfileResponse {
    private String username;
    private String firstName;
    private String lastName;
    private String specialization;
    private Boolean isActive;
    private List<TraineeSummaryDTO> trainees;
}
