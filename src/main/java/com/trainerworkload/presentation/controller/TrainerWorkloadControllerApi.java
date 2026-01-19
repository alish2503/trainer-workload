package com.trainerworkload.presentation.controller;

import com.trainerworkload.presentation.dto.response.TrainerMonthlyWorkloadDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Internal Trainer Workload API")
@RequestMapping(path = "/workload", produces = "application/json")
public interface TrainerWorkloadControllerApi {

    @Operation(
            summary = "Get trainer monthly workload",
            description = "Retrieves the total training hours of a specific trainer for a given year and month"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer monthly workload successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Invalid token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    TrainerMonthlyWorkloadDto getWorkload(String username, int year, int month);
}
