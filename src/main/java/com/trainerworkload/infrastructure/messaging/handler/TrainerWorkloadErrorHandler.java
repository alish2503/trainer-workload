package com.trainerworkload.infrastructure.messaging.handler;

import com.rabbitmq.client.Channel;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component("trainerWorkloadErrorHandler")
public class TrainerWorkloadErrorHandler implements RabbitListenerErrorHandler {
    private final RabbitTemplate rabbitTemplate;
    private final String dlqName;

    @Autowired
    public TrainerWorkloadErrorHandler(RabbitTemplate rabbitTemplate, @Value("${dlq-name}") String dlqName) {
        this.dlqName = dlqName;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public @Nullable Object handleError(Message amqpMessage, @Nullable Channel channel,
                                        org.springframework.messaging.@Nullable Message<?> message,
                                        ListenerExecutionFailedException exception)
    {
        Throwable cause = exception.getCause();
        try {
            if (cause instanceof ConstraintViolationException) {
                log.warn("Validation failed for TrainerWorkloadEvent, sending to DLQ");
                rabbitTemplate.convertAndSend(dlqName,
                        message.getPayload(),
                        m -> {
                            String transactionId = amqpMessage.getMessageProperties().getHeader("transactionId");
                            if (transactionId != null) {
                                m.getMessageProperties().setHeader("transactionId", transactionId);
                            }
                            return m;
                        });

                return null;
            }
            else if (cause instanceof MessageConversionException) {
                log.warn("TrainerWorkloadEvent conversion failed");
                return null;
            }
            log.error("Unexpected exception", cause);
        }
        finally {
            MDC.clear();
        }
        return null;
    }
}

