package com.trainerworkload.infrastructure.messaging.consumer;

import com.trainerworkload.application.event.TrainerWorkloadEvent;
import com.trainerworkload.application.service.TrainerWorkloadService;
import com.trainerworkload.infrastructure.messaging.consumer.impl.TrainerWorkloadConsumerImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TrainerWorkloadConsumerImplTest {

    @Mock
    private TrainerWorkloadService workloadService;

    @Mock
    private Validator validator;

    @InjectMocks
    private TrainerWorkloadConsumerImpl consumer;
    private TrainerWorkloadEvent validEvent;

    @BeforeEach
    void setUp() {
        validEvent = new TrainerWorkloadEvent(
                null, null, null,
                null, null, null, null
        );
    }

    @Test
    void receiveMessage_shouldCallServiceAndSetMdc_whenTransactionIdProvided() {
        String txId = "12345";
        consumer.receiveMessage(validEvent, txId);
        verify(workloadService, times(1)).updateWorkload(validEvent);
        assertNull(MDC.get("transactionId"));
    }

    @Test
    void receiveMessage_shouldGenerateTransactionIdIfNotProvided() {
        consumer.receiveMessage(validEvent, null);
        verify(workloadService, times(1)).updateWorkload(validEvent);
        assertNull(MDC.get("transactionId"));
    }

    @Test
    void receiveMessage_shouldThrowConstraintViolationExceptionIfInvalid() {
        ConstraintViolation<TrainerWorkloadEvent> violation = mock(ConstraintViolation.class);
        when(validator.validate(validEvent)).thenReturn(Set.of(violation));
        ConstraintViolationException ex = assertThrows(
                ConstraintViolationException.class,
                () -> consumer.receiveMessage(validEvent, "tx")
        );

        assertEquals("Validation failed", ex.getMessage());
        verify(workloadService, never()).updateWorkload(any());
    }

    @Test
    void receiveMessage_shouldPassValidationIfNoViolations() {
        when(validator.validate(validEvent)).thenReturn(Collections.emptySet());
        consumer.receiveMessage(validEvent, "tx");
        verify(workloadService, times(1)).updateWorkload(validEvent);
    }
}
