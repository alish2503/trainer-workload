package com.trainerworkload.domain.port;

import com.trainerworkload.domain.model.TrainerWorkload;

import java.util.Optional;

public interface TrainerWorkloadRepository {
    void save(TrainerWorkload trainerWorkload);
    Optional<TrainerWorkload> findTrainerWorkloadByUsername(String username);
}
