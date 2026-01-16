package com.trainerworkload.infrastructure.messaging.handler;

import com.rabbitmq.client.Channel;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.messaging.core.MessagePostProcessor;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class TrainerWorkloadErrorHandlerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private Channel channel;
    private TrainerWorkloadErrorHandler errorHandler;
    private final String DLQ_NAME = "test-dlq";
    private Message amqpMessage;
    private org.springframework.messaging.Message<String> springMessage;

    @BeforeEach
    void setUp() {
        errorHandler = new TrainerWorkloadErrorHandler(rabbitTemplate, DLQ_NAME);
        amqpMessage = new Message("payload".getBytes(), new org.springframework.amqp.core.MessageProperties());
        springMessage = new org.springframework.messaging.support.GenericMessage<>("payload",
                Collections.emptyMap());
    }

    @Test
    void handleError_shouldSendToDlq_whenConstraintViolationException() {
        ConstraintViolationException cause = mock(ConstraintViolationException.class);
        ListenerExecutionFailedException ex = new ListenerExecutionFailedException("failed", cause);
        amqpMessage.getMessageProperties().setHeader("transactionId", "tx-123");
        errorHandler.handleError(amqpMessage, channel, springMessage, ex);
        ArgumentCaptor<org.springframework.amqp.core.MessagePostProcessor> processorCaptor =
                ArgumentCaptor.forClass(org.springframework.amqp.core.MessagePostProcessor.class);

        verify(rabbitTemplate, times(1))
                .convertAndSend(eq(DLQ_NAME), eq((Object) springMessage.getPayload()), processorCaptor.capture());

        Message message = new Message("payload".getBytes(), new org.springframework.amqp.core.MessageProperties());
        Message processed = processorCaptor.getValue().postProcessMessage(message);
        assertEquals(
                "tx-123",
                processed.getMessageProperties().getHeaders().get("transactionId")
        );
    }


    @Test
    void handleError_shouldLogWarning_whenMessageConversionException() {
        MessageConversionException cause = new MessageConversionException("conversion failed");
        ListenerExecutionFailedException ex = new ListenerExecutionFailedException("failed", cause);
        Object result = errorHandler.handleError(amqpMessage, null, springMessage, ex);
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(),
                ArgumentMatchers.<MessagePostProcessor>any());

        assertNull(result);
    }

    @Test
    void handleError_shouldLogError_whenUnexpectedException() {
        RuntimeException cause = new RuntimeException("unexpected");
        ListenerExecutionFailedException ex = new ListenerExecutionFailedException("failed", cause);
        Object result = errorHandler.handleError(amqpMessage, null, springMessage, ex);
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(),
                ArgumentMatchers.<MessagePostProcessor>any());

        assertNull(result);
    }
}
