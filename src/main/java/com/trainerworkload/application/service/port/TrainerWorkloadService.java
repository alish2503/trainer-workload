package com.trainerworkload.application.service.port;

import com.trainerworkload.application.request.TrainerWorkloadEventRequest;

public interface TrainerWorkloadService {
    void updateWorkload(TrainerWorkloadEventRequest event);
    int getMonthlyHours(String username, int year, int month);
}
