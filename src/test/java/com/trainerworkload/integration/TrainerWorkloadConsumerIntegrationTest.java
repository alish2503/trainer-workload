package com.trainerworkload.integration;

import com.trainerworkload.application.event.ActionType;
import com.trainerworkload.application.event.TrainerWorkloadEvent;
import com.trainerworkload.presentation.dto.response.TrainerMonthlyWorkloadDto;
import com.trainerworkload.infrastructure.config.DisabledAuthorizationTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.mongodb.MongoDBContainer;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("no-security")
@Import(DisabledAuthorizationTestConfig.class)
class TrainerWorkloadConsumerIntegrationTest {
    private final TestRestTemplate testRestTemplate;
    private final RabbitTemplate rabbitTemplate;
    private static final String TRAINER_USERNAME = "John.Doe";
    private static final String TRAINER_FIRST = "John";
    private static final String TRAINER_LAST = "Doe";
    private static final int MONTH = 5;
    private static final int YEAR = 2026;

    @Value("${queue-name}")
    private String queueName;

    @Value("${dlq-name}")
    private String dlqQueueName;

    @ServiceConnection
    private final static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @ServiceConnection
    private final static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3-management");

    @Autowired
    public TrainerWorkloadConsumerIntegrationTest(TestRestTemplate testRestTemplate, RabbitTemplate rabbitTemplate) {
        this.testRestTemplate = testRestTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Test
    void testValidMessage_processedSuccessfully() {
        getTrainerWorkloadEvents().forEach(event -> rabbitTemplate.convertAndSend(queueName, event));
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            ResponseEntity<TrainerMonthlyWorkloadDto> response = testRestTemplate.exchange(
                    buildWorkloadUrl(),
                    HttpMethod.GET,
                    buildEntity(),
                    TrainerMonthlyWorkloadDto.class
            );
            assertEquals(1, response.getBody().totalHours());
            assertNotNull(response.getBody());
        });
    }

    @Test
    void testInvalidMessage_sentToDlq() {
        TrainerWorkloadEvent invalidEvent = new TrainerWorkloadEvent("", TRAINER_FIRST, TRAINER_LAST,
                true, LocalDate.now(), 2, ActionType.ADD);

        rabbitTemplate.convertAndSend(queueName, invalidEvent);
        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> assertNotNull(rabbitTemplate.receiveAndConvert(dlqQueueName)));
    }

    private static List<TrainerWorkloadEvent> getTrainerWorkloadEvents() {
        return List.of(new TrainerWorkloadEvent(TRAINER_USERNAME, TRAINER_FIRST, TRAINER_LAST, true,
                        LocalDate.of(2026, 5, 12), 2, ActionType.ADD),
                new TrainerWorkloadEvent(TRAINER_USERNAME, TRAINER_FIRST, TRAINER_LAST, true,
                        LocalDate.of(2026, 5, 11), 2, ActionType.ADD),
                new TrainerWorkloadEvent(TRAINER_USERNAME, TRAINER_FIRST, TRAINER_LAST, true,
                        LocalDate.of(2026, 5, 23), 3, ActionType.DELETE)
        );
    }

    private String buildWorkloadUrl() {
        return UriComponentsBuilder.fromUriString("/workload/{username}")
                .queryParam("month", MONTH)
                .queryParam("year", YEAR)
                .buildAndExpand(TRAINER_USERNAME)
                .toUriString();
    }

    private HttpEntity<Void> buildEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }
}
