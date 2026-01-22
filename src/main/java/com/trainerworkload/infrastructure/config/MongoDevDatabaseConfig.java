package com.trainerworkload.infrastructure.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@Profile({"dev", "local"})
public class MongoDevDatabaseConfig {

    @Bean
    public ApplicationRunner mongoDatabaseCleaner(MongoTemplate mongoTemplate) {
        return args -> mongoTemplate.getDb().drop();
    }
}

