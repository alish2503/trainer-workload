package com.trainerworkload.infrastructure.persistence.mongorepo;

import com.trainerworkload.infrastructure.persistence.dao.TrainerTrainingSummaryDao;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TrainerTrainingSummaryRepository extends MongoRepository<TrainerTrainingSummaryDao, String> {
    Optional<TrainerTrainingSummaryDao> findByTrainerUsername(String trainerUsername);
}
