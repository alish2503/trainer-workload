package com.trainerworkload.application.service.impl;

import com.trainerworkload.application.event.ActionType;
import com.trainerworkload.application.event.TrainerWorkloadEvent;
import com.trainerworkload.application.service.TrainerWorkloadService;
import com.trainerworkload.domain.exception.EntityNotFoundException;
import com.trainerworkload.domain.model.TrainerWorkload;
import com.trainerworkload.domain.port.TrainerWorkloadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {
    private final TrainerWorkloadRepository repository;

    @Autowired
    public TrainerWorkloadServiceImpl(TrainerWorkloadRepository repository) {
        this.repository = repository;
    }

    @Override
    public void updateWorkload(TrainerWorkloadEvent event) {
        TrainerWorkload trainerWorkload = repository.findByUsername(event.username())
                .orElseGet(() -> new TrainerWorkload(
                        event.username(),
                        event.firstName(),
                        event.lastName(),
                        event.isActive()
                ));

        LocalDate date = event.date();
        int year = date.getYear();
        int month = date.getMonthValue();
        boolean isAdd = event.actionType().equals(ActionType.ADD);
        trainerWorkload.updateWorkload(year, month, event.durationInHours(), isAdd);
        repository.save(trainerWorkload);
    }

    @Override
    public int getMonthlyHours(String username, int year, int month) {
        TrainerWorkload workload = repository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(
                "No trainer found with username: " + username
        ));
        return workload.getMonthlySummary().get(year).get(month);
    }
}
