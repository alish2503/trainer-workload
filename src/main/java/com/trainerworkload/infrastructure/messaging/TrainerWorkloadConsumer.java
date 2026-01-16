package com.trainerworkload.infrastructure.messaging;

import com.trainerworkload.application.event.TrainerWorkloadEvent;

public interface TrainerWorkloadConsumer {
    void receiveMessage(TrainerWorkloadEvent message, String txId);
}
