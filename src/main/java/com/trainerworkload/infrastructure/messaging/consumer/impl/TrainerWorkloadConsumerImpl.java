package com.trainerworkload.infrastructure.messaging.consumer.impl;

import com.trainerworkload.application.event.TrainerWorkloadEvent;
import com.trainerworkload.application.service.TrainerWorkloadService;
import com.trainerworkload.infrastructure.messaging.consumer.TrainerWorkloadConsumer;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
public class TrainerWorkloadConsumerImpl implements TrainerWorkloadConsumer {
    private final TrainerWorkloadService workloadService;
    private final Validator validator;

    @Autowired
    public TrainerWorkloadConsumerImpl(TrainerWorkloadService workloadService, Validator validator) {
        this.workloadService = workloadService;
        this.validator = validator;
    }

    @Override
    @RabbitListener(queues = "${queue-name}", errorHandler = "trainerWorkloadErrorHandler")
    public void receiveMessage(TrainerWorkloadEvent trainerWorkloadEvent,
                               @Header(value = "transactionId", required = false) String transactionId)
    {
        if (transactionId == null) {
            transactionId = UUID.randomUUID().toString();
        }
        MDC.put("transactionId", transactionId);
        validate(trainerWorkloadEvent);
        workloadService.updateWorkload(trainerWorkloadEvent);
        log.info("Message processed successfully");
        MDC.clear();
    }

    private void validate(TrainerWorkloadEvent trainerWorkloadEvent) {
        Set<ConstraintViolation<TrainerWorkloadEvent>> violations = validator.validate(trainerWorkloadEvent);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Validation failed", violations);
        }
    }
}

