package com.trainerworkload.infrastructure.messaging.declarator;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitQueueDeclarator {
    private final RabbitAdmin rabbitAdmin;
    private final String queueName;

    @Autowired
    public RabbitQueueDeclarator(RabbitAdmin rabbitAdmin, @Value("${queue-name}") String queueName) {
        this.rabbitAdmin = rabbitAdmin;
        this.queueName = queueName;
    }

    @PostConstruct
    public void declareQueues() {
        String dlqName = queueName + "-dlq";
        Queue dlq = QueueBuilder.durable(dlqName).build();
        Queue queue = QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", dlqName)
                .build();

        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareQueue(dlq);
        log.info("Declared queues: {} and {}", queueName, dlqName);
    }
}
