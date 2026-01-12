package com.trainerworkload.presentation.controller;

import com.trainerworkload.application.request.TrainerWorkloadEventRequest;
import com.trainerworkload.application.service.port.TrainerWorkloadService;
import com.trainerworkload.presentation.controller.impl.TrainerWorkloadController;
import com.trainerworkload.application.request.ActionType;
import com.trainerworkload.presentation.dto.request.TrainerWorkloadEventDto;
import com.trainerworkload.presentation.dto.response.TrainerMonthlyWorkloadDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadControllerTest {

    @Mock
    private TrainerWorkloadService trainerWorkloadService;

    @InjectMocks
    private TrainerWorkloadController trainerWorkloadController;

    @Test
    void registerTrainerWorkloadEvent_DelegatesToService() {
        TrainerWorkloadEventDto eventDto = new TrainerWorkloadEventDto(null, null, null,
                true, null, 6, ActionType.ADD);

        TrainerWorkloadEventRequest command = new TrainerWorkloadEventRequest(null, null, null,
                true, null, 6, ActionType.ADD);

        trainerWorkloadController.registerTrainerWorkloadEvent(eventDto);
        verify(trainerWorkloadService).updateWorkload(command);
    }

    @Test
    void getWorkload_ReturnsCorrectWorkload() {
        String username = "trainer1";
        int year = 2023;
        int month = 10;
        int hours = 40;
        when(trainerWorkloadService.getMonthlyHours(username, year, month)).thenReturn(hours);
        TrainerMonthlyWorkloadDto result = trainerWorkloadController.getWorkload(username, year, month);
        assertEquals(username, result.username());
        assertEquals(year, result.year());
        assertEquals(month, result.month());
        assertEquals(hours, result.totalHours());
    }
}

