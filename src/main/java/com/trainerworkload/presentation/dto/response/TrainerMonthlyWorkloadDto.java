package com.trainerworkload.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record TrainerMonthlyWorkloadDto(

        @Schema(example = "John.Doe")
        String username,

        @Schema(example = "2025")
        int year,

        @Schema(example = "10")
        int month,

        @Schema(example = "26")
        int totalHours
) {}
