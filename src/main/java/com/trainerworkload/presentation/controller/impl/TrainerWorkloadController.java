package com.trainerworkload.presentation.controller.impl;

import com.trainerworkload.application.service.port.TrainerWorkloadService;
import com.trainerworkload.presentation.controller.port.TrainerWorkloadControllerApi;
import com.trainerworkload.presentation.dto.TrainerWorkloadEventDto;
import com.trainerworkload.presentation.mapper.TrainerWorkloadDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrainerWorkloadController implements TrainerWorkloadControllerApi {
    private final TrainerWorkloadService trainerWorkloadService;

    @Autowired
    public TrainerWorkloadController(TrainerWorkloadService trainerWorkloadService) {
        this.trainerWorkloadService = trainerWorkloadService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void receiveEvent(@RequestBody TrainerWorkloadEventDto trainerWorkloadEventDto) {
        trainerWorkloadService.handleEvent(TrainerWorkloadDtoMapper.toCommand(trainerWorkloadEventDto));
    }
}
