package com.trainerworkload.application.service.port;

import com.trainerworkload.application.request.TrainerWorkloadEventRequest;

public interface TrainerWorkloadService {
    void handleEvent(TrainerWorkloadEventRequest event);
}
