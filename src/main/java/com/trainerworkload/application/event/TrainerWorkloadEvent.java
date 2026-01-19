package com.trainerworkload.application.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TrainerWorkloadEvent(

        @NotBlank(message = "Trainer username cannot be blank")
        @Size(max = 50)
        String username,

        @NotBlank(message = "First name cannot be blank")
        @Pattern(regexp = "^[A-Za-z]+$")
        @Size(max = 50)
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        @Pattern(regexp = "^[A-Za-z]+$")
        @Size(max = 50)
        String lastName,

        @NotNull(message = "Activity cannot be null")
        Boolean isActive,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @NotNull(message = "Training date cannot be blank")
        LocalDate date,

        @Positive(message = "Duration must be positive")
        Integer durationInHours,
        ActionType actionType
) {}
