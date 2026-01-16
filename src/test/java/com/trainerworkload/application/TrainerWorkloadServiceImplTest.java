package com.trainerworkload.application;

import com.trainerworkload.application.event.ActionType;
import com.trainerworkload.application.event.TrainerWorkloadEvent;
import com.trainerworkload.application.service.impl.TrainerWorkloadServiceImpl;
import com.trainerworkload.domain.exception.EntityNotFoundException;
import com.trainerworkload.domain.model.TrainerWorkload;
import com.trainerworkload.domain.port.TrainerWorkloadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceImplTest {

    @Mock
    private TrainerWorkloadRepository repository;

    @InjectMocks
    private TrainerWorkloadServiceImpl service;

    @Test
    void createsNewTrainerWorkloadWhenTrainerDoesNotExist() {
        TrainerWorkloadEvent event = new TrainerWorkloadEvent(
                "trainer1", "John", "Doe", true,
                LocalDate.of(2023, 10, 1), 1,ActionType.ADD);

        when(repository.findByUsername("trainer1")).thenReturn(Optional.empty());
        service.updateWorkload(event);
        verify(repository).save(argThat(workload ->
                workload.getUsername().equals("trainer1") &&
                        workload.getFirstName().equals("John") &&
                        workload.getLastName().equals("Doe") &&
                        workload.isActive()
        ));
    }

    @Test
    void updatesExistingTrainerWorkloadWhenTrainerExists() {
        TrainerWorkload existingWorkload = new TrainerWorkload("trainer1", "John",
                "Doe", true);

        TrainerWorkloadEvent event = new TrainerWorkloadEvent(
                "trainer1", "John", "Doe", true,
                LocalDate.of(2023, 10, 1), 2, ActionType.ADD);

        when(repository.findByUsername("trainer1")).thenReturn(Optional.of(existingWorkload));
        service.updateWorkload(event);
        verify(repository).save(existingWorkload);
        assertEquals(2, existingWorkload.getMonthlySummary().get(2023).get(10));
    }

    @Test
    void subtractsWorkloadWhenActionTypeIsRemove() {
        TrainerWorkload existingWorkload = new TrainerWorkload("trainer1", "John",
                "Doe", true);

        existingWorkload.updateWorkload(2023, 10, 3, true);
        TrainerWorkloadEvent event = new TrainerWorkloadEvent(
                "trainer1", "John", "Doe", true,
                LocalDate.of(2023, 10, 1), 1, ActionType.DELETE);

        when(repository.findByUsername("trainer1")).thenReturn(Optional.of(existingWorkload));
        service.updateWorkload(event);
        verify(repository).save(existingWorkload);
        assertEquals(2, existingWorkload.getMonthlySummary().get(2023).get(10));
    }

    @Test
    void throwsEntityNotFoundExceptionWhenTrainerDoesNotExistForMonthlyHours() {
        when(repository.findByUsername("trainer1")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                service.getMonthlyHours("trainer1", 2023, 10));
    }

    @Test
    void returnsMonthlyHoursForExistingTrainer() {
        TrainerWorkload existingWorkload = new TrainerWorkload("trainer1", "John",
                "Doe", true);

        existingWorkload.updateWorkload(2023, 10, 3, true);
        when(repository.findByUsername("trainer1")).thenReturn(Optional.of(existingWorkload));
        int hours = service.getMonthlyHours("trainer1", 2023, 10);
        assertEquals(3, hours);
    }
}

