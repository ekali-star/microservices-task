package com.example.gymcrm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivateRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotNull(message = "isActive is required")
    private Boolean isActive;
}