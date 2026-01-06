package com.trainerworkload.infrastructure.repository;

import com.trainerworkload.domain.model.TrainerWorkload;
import com.trainerworkload.domain.port.TrainerWorkloadRepository;
import com.trainerworkload.infrastructure.dao.TrainerWorkloadDao;
import com.trainerworkload.infrastructure.mapper.TrainerWorkloadDaoMapper;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TrainerWorkloadRepositoryImpl implements TrainerWorkloadRepository {
    private final Map<String, TrainerWorkloadDao> storage = new ConcurrentHashMap<>();

    @Override
    public void save(TrainerWorkload trainerWorkload) {
        storage.put(trainerWorkload.getUsername(), TrainerWorkloadDaoMapper.toDao(trainerWorkload));
    }

    @Override
    public Optional<TrainerWorkload> findByUsername(String username) {
        return Optional.ofNullable(storage.get(username)).map(t ->
                TrainerWorkloadDaoMapper.toDomain(t, username));
    }
}
