package com.trainerworkload.infrastructure.repository;

import com.trainerworkload.domain.model.TrainerWorkload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadRepositoryImplTest {

    private TrainerWorkloadRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new TrainerWorkloadRepositoryImpl();
    }

    @Test
    void savesTrainerWorkloadSuccessfully() {
        TrainerWorkload trainerWorkload = new TrainerWorkload("trainer1", "John",
                "Doe", true);

        repository.save(trainerWorkload);
        Optional<TrainerWorkload> result = repository.findByUsername("trainer1");
        assertTrue(result.isPresent());
        assertEquals("trainer1", result.get().getUsername());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
        assertTrue(result.get().isActive());
        assertTrue(result.get().getMonthlySummary().isEmpty());
    }

    @Test
    void findByUsernameReturnsEmptyWhenTrainerDoesNotExist() {
        Optional<TrainerWorkload> result = repository.findByUsername("nonexistent");
        assertTrue(result.isEmpty());
    }

    @Test
    void overwritesExistingTrainerWorkloadOnSave() {
        TrainerWorkload initialWorkload = new TrainerWorkload("trainer1", "John",
                "Doe", true);
        TrainerWorkload updatedWorkload = new TrainerWorkload("trainer1", "Jane",
                "Smith", false);

        repository.save(initialWorkload);
        repository.save(updatedWorkload);
        Optional<TrainerWorkload> result = repository.findByUsername("trainer1");
        assertTrue(result.isPresent());
        assertEquals("trainer1", result.get().getUsername());
        assertEquals("Jane", result.get().getFirstName());
        assertEquals("Smith", result.get().getLastName());
        assertFalse(result.get().isActive());
    }

}

