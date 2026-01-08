package com.trainerworkload.presentation.controller.port;

import com.trainerworkload.presentation.dto.request.TrainerWorkloadEventDto;
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
            summary = "Receive trainer workload event",
            description = "Receives a workload event (training added or removed) from another service" +
                    " and updates the trainer's monthly workload"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Workload event successfully processed"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Invalid token"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    void registerTrainerWorkloadEvent(TrainerWorkloadEventDto trainerWorkloadEventDto);

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
