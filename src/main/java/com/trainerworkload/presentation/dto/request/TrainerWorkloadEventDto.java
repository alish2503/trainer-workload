package com.trainerworkload.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TrainerWorkloadEventDto(

        @Schema(example = "John.Doe")
        @NotBlank(message = "Username cannot be blank")
        @Pattern(regexp = "^[A-Za-z]+$")
        @Size(max = 50)
        String username,

        @Schema(example = "John")
        @NotBlank(message = "First name cannot be blank")
        @Pattern(regexp = "^[A-Za-z]+$")
        @Size(max = 50)
        String firstName,

        @Schema(example = "Doe")
        @NotBlank(message = "Last name cannot be blank")
        @Pattern(regexp = "^[A-Za-z]+$")
        @Size(max = 50)
        String lastName,

        @Schema(example = "false")
        @NotNull(message = "Activity cannot be null")
        Boolean active,

        @Schema(example = "2026-10-10")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @NotNull(message = "Training date cannot be blank")
        LocalDate date,

        @Schema(example = "80")
        @Positive(message = "Duration must be positive")
        int duration,

        @Schema(example = "ADD")
        @NotNull(message = "Action type cannot be blank")
        ActionType actionType
) {}
