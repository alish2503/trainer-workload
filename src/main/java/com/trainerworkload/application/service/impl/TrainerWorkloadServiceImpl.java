package com.trainerworkload.application.service.impl;

import com.trainerworkload.application.request.TrainerWorkloadEventRequest;
import com.trainerworkload.application.service.port.TrainerWorkloadService;
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
    public void handleEvent(TrainerWorkloadEventRequest event) {
        TrainerWorkload trainerWorkload = repository.findByUsername(event.username())
                .orElseGet(() -> new TrainerWorkload(
                        event.username(),
                        event.firstName(),
                        event.lastName(),
                        event.active()
                ));

        LocalDate date = event.date();
        int year = date.getYear();
        int month = date.getMonthValue();
        boolean isAdd = event.actionType().equals("ADD");
        trainerWorkload.updateWorkload(year, month, event.duration(), isAdd);
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
