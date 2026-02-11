package com.trainerworkload.infrastructure.config;

import com.trainerworkload.domain.model.TrainerWorkload;
import com.trainerworkload.domain.port.TrainerWorkloadRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.util.Optional;

@Configuration
@Profile("no-integration")
public class StubTrainerWorkloadRepositoryConfig {

    @Bean
    public TrainerWorkloadRepository trainerWorkloadRepositoryDummy() {
        return new TrainerWorkloadRepository() {

            @Override
            public void save(TrainerWorkload trainerWorkload) {}

            @Override
            public Optional<TrainerWorkload> findTrainerWorkloadByUsername(String username) {
                return Optional.empty();
            }
        };
    }
}
