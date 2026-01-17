package com.trainerworkload.infrastructure.messaging.handler;

import com.rabbitmq.client.Channel;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.ImmediateAcknowledgeAmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.MessageConversionException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class TrainerWorkloadErrorHandlerTest {

    @Mock
    private Channel channel;

    @Mock
    private Message amqpMessage;

    @InjectMocks
    private TrainerWorkloadErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        MDC.put("test", "value");
    }

    @Test
    void testConstraintViolationExceptionThrowsAmqpReject() {
        ConstraintViolationException cause = new ConstraintViolationException("invalid", null);
        ListenerExecutionFailedException ex = new ListenerExecutionFailedException("listener failed", cause);
        AmqpRejectAndDontRequeueException thrown = assertThrows(
                AmqpRejectAndDontRequeueException.class,
                () -> errorHandler.handleError(amqpMessage, channel, null, ex)
        );

        assertEquals("Validation failed", thrown.getMessage());
        assertNull(MDC.get("test"));
    }

    @Test
    void testMessageConversionExceptionThrowsImmediateAcknowledge() {
        MessageConversionException cause = new MessageConversionException("bad JSON");
        ListenerExecutionFailedException ex = new ListenerExecutionFailedException("listener failed", cause);
        ImmediateAcknowledgeAmqpException thrown = assertThrows(
                ImmediateAcknowledgeAmqpException.class,
                () -> errorHandler.handleError(amqpMessage, channel, null, ex)
        );

        assertEquals("Malformed JSON", thrown.getMessage());
        assertNull(MDC.get("test"));
    }

    @Test
    void testUnexpectedExceptionPropagatesOriginal() {
        RuntimeException cause = new RuntimeException("unexpected");
        ListenerExecutionFailedException ex = new ListenerExecutionFailedException("listener failed", cause);
        ListenerExecutionFailedException thrown = assertThrows(
                ListenerExecutionFailedException.class,
                () -> errorHandler.handleError(amqpMessage, channel, null, ex)
        );

        assertEquals(ex, thrown);
        assertNull(MDC.get("test"));
    }
}
