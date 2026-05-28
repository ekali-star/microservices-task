package com.example.gymcrm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class UpdateTraineeTrainersRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotEmpty(message = "Trainer list must not be empty")
    private List<String> trainerUsernames;
}