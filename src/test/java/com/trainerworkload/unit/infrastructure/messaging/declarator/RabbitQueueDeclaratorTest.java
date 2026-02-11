package com.trainerworkload.unit.infrastructure.messaging.declarator;

import com.trainerworkload.infrastructure.messaging.declarator.RabbitQueueDeclarator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RabbitQueueDeclaratorTest {

    @Mock
    private RabbitAdmin rabbitAdmin;

    @InjectMocks
    private RabbitQueueDeclarator declarator;

    @Test
    void shouldDeclareQueueAndDlq() {
        declarator = new RabbitQueueDeclarator(rabbitAdmin, "test-queue");
        declarator.declareQueues();
        ArgumentCaptor<Queue> queueCaptor = ArgumentCaptor.forClass(Queue.class);
        verify(rabbitAdmin, times(2)).declareQueue(queueCaptor.capture());
        Queue mainQueue = queueCaptor.getAllValues().get(0);
        Queue dlqQueue = queueCaptor.getAllValues().get(1);
        assertEquals("test-queue", mainQueue.getName());
        assertEquals("test-queue-dlq", dlqQueue.getName());
        assertEquals("", mainQueue.getArguments().get("x-dead-letter-exchange"));
        assertEquals("test-queue-dlq", mainQueue.getArguments().get("x-dead-letter-routing-key"));
    }
}
