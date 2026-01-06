package com.trainerworkload.presentation.controller.port;

import com.trainerworkload.presentation.dto.TrainerWorkloadEventDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Internal Trainer Workload API")
@RequestMapping("/internal/trainer-workload")
public interface TrainerWorkloadControllerApi {
    void receiveEvent(TrainerWorkloadEventDto trainerWorkloadEventDto);
}
