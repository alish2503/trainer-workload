package com.trainerworkload.application.request;

import java.time.LocalDate;

public record TrainerWorkloadEventRequest(
        String username,
        String firstName,
        String lastName,
        boolean active,
        LocalDate date,
        int duration,
        String actionType
) {}
