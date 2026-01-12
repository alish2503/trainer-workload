package com.trainerworkload.presentation.controller.impl;

import com.trainerworkload.application.service.port.TrainerWorkloadService;
import com.trainerworkload.presentation.controller.port.TrainerWorkloadControllerApi;
import com.trainerworkload.presentation.dto.request.TrainerWorkloadEventDto;
import com.trainerworkload.presentation.dto.response.TrainerMonthlyWorkloadDto;
import com.trainerworkload.presentation.mapper.TrainerWorkloadDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
    @ResponseStatus(HttpStatus.OK)
    public void registerTrainerWorkloadEvent(@RequestBody TrainerWorkloadEventDto trainerWorkloadEventDto) {
        trainerWorkloadService.updateWorkload(TrainerWorkloadDtoMapper.toCommand(trainerWorkloadEventDto));
    }

    @GetMapping("/{username}")
    public TrainerMonthlyWorkloadDto getWorkload(@PathVariable String username, @RequestParam int year,
                                                 @RequestParam int month) {

        int hours = trainerWorkloadService.getMonthlyHours(username, year, month);
        return new TrainerMonthlyWorkloadDto(username, year, month, hours);
    }
}
