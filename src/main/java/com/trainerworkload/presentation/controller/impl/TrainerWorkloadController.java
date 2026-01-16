package com.trainerworkload.presentation.controller.impl;

import com.trainerworkload.application.service.TrainerWorkloadService;
import com.trainerworkload.presentation.controller.TrainerWorkloadControllerApi;
import com.trainerworkload.presentation.dto.response.TrainerMonthlyWorkloadDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrainerWorkloadController implements TrainerWorkloadControllerApi {
    private final TrainerWorkloadService trainerWorkloadService;

    @Autowired
    public TrainerWorkloadController(TrainerWorkloadService trainerWorkloadService) {
        this.trainerWorkloadService = trainerWorkloadService;
    }

    @GetMapping("/{username}")
    public TrainerMonthlyWorkloadDto getWorkload(@PathVariable String username, @RequestParam int year,
                                                 @RequestParam int month) {

        int hours = trainerWorkloadService.getMonthlyHours(username, year, month);
        return new TrainerMonthlyWorkloadDto(username, year, month, hours);
    }
}
