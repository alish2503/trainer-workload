package com.trainerworkload.infrastructure.messaging.handler;

import com.rabbitmq.client.Channel;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.slf4j.MDC;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.ImmediateAcknowledgeAmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.stereotype.Component;

@Slf4j
@Component("trainerWorkloadErrorHandler")
public class TrainerWorkloadErrorHandler implements RabbitListenerErrorHandler {

    @Override
    public @Nullable Object handleError(Message amqpMessage, @Nullable Channel channel,
                                        org.springframework.messaging.@Nullable Message<?> message,
                                        ListenerExecutionFailedException exception)
    {
        Throwable cause = exception.getCause();
        try {
            if (cause instanceof ConstraintViolationException) {
                log.warn("Validation failed for TrainerWorkloadEvent, sending to DLQ");
                throw new AmqpRejectAndDontRequeueException("Validation failed");
            }
            else if (cause instanceof MessageConversionException) {
                log.warn("TrainerWorkloadEvent conversion failed");
                throw new ImmediateAcknowledgeAmqpException("Malformed JSON");
            }
            log.error("Unexpected exception", cause);
            throw exception;
        }
        finally {
            MDC.clear();
        }
    }
}

