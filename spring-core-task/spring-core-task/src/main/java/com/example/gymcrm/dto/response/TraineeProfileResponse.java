package com.example.gymcrm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TraineeProfileResponse {
    private String username;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;
    private Boolean isActive;
    private List<TrainerSummaryDTO> trainers;
}
