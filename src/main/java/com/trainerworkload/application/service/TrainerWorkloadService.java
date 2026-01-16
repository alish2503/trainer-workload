package com.trainerworkload.application.service;

import com.trainerworkload.application.event.TrainerWorkloadEvent;

public interface TrainerWorkloadService {
    void updateWorkload(TrainerWorkloadEvent event);
    int getMonthlyHours(String username, int year, int month);
}
