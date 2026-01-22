package com.trainerworkload.infrastructure.persistence.mongorepo;

import com.trainerworkload.infrastructure.persistence.document.TrainerTrainingSummaryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TrainerTrainingSummaryRepository extends MongoRepository<TrainerTrainingSummaryDocument, String> {
    Optional<TrainerTrainingSummaryDocument> findByTrainerUsername(String trainerUsername);
}
