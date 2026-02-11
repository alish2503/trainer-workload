package com.trainerworkload.infrastructure.persistence.mongorepo;

import com.trainerworkload.infrastructure.persistence.document.TrainerTrainingSummaryDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

@Profile("integration")
public interface TrainerTrainingSummaryRepository extends MongoRepository<TrainerTrainingSummaryDocument, String> {
    Optional<TrainerTrainingSummaryDocument> findByTrainerUsername(String trainerUsername);
}
