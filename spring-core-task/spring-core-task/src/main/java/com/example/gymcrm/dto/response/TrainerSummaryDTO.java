package com.example.gymcrm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TrainerSummaryDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String specialization;
}