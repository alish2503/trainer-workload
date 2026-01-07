package com.trainerworkload.presentation.mapper;

import com.trainerworkload.application.request.TrainerWorkloadEventRequest;
import com.trainerworkload.presentation.dto.request.TrainerWorkloadEventDto;

public class TrainerWorkloadDtoMapper {
    private TrainerWorkloadDtoMapper(){}

    public static TrainerWorkloadEventRequest toCommand(TrainerWorkloadEventDto trainerWorkloadEventDto) {
        return new TrainerWorkloadEventRequest(
                trainerWorkloadEventDto.username(),
                trainerWorkloadEventDto.firstName(),
                trainerWorkloadEventDto.lastName(),
                trainerWorkloadEventDto.active(),
                trainerWorkloadEventDto.date(),
                trainerWorkloadEventDto.duration(),
                trainerWorkloadEventDto.actionType().name()
        );
    }
}
