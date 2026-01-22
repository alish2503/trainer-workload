package com.trainerworkload.infrastructure.persistence.adapter;

import com.trainerworkload.domain.model.TrainerWorkload;
import com.trainerworkload.domain.port.TrainerWorkloadRepository;
import com.trainerworkload.infrastructure.persistence.mapper.TrainerWorkloadDaoMapper;
import com.trainerworkload.infrastructure.persistence.mongorepo.TrainerTrainingSummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TrainerWorkloadRepositoryImpl implements TrainerWorkloadRepository {
    private final TrainerTrainingSummaryRepository repository;

    @Autowired
    public TrainerWorkloadRepositoryImpl(TrainerTrainingSummaryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(TrainerWorkload trainerWorkload) {
        repository.save(TrainerWorkloadDaoMapper.toDao(trainerWorkload));
    }

    @Override
    public Optional<TrainerWorkload> findTrainerWorkloadByUsername(String username) {
        return repository.findByTrainerUsername(username).map(TrainerWorkloadDaoMapper::toDomain);
    }
}
