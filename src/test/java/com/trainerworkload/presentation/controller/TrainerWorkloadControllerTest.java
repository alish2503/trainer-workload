package com.trainerworkload.presentation.controller;

import com.trainerworkload.application.service.TrainerWorkloadService;
import com.trainerworkload.presentation.controller.impl.TrainerWorkloadController;
import com.trainerworkload.presentation.dto.response.TrainerMonthlyWorkloadDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadControllerTest {

    @Mock
    private TrainerWorkloadService trainerWorkloadService;

    @InjectMocks
    private TrainerWorkloadController trainerWorkloadController;

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

