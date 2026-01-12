package com.trainerworkload.application.request;

import java.time.LocalDate;

public record TrainerWorkloadEventRequest(
        String username,
        String firstName,
        String lastName,
        boolean isActive,
        LocalDate date,
        int durationInHours,
        ActionType actionType
) {}
