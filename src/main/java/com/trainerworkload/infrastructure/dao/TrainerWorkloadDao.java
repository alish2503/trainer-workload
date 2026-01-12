package com.trainerworkload.infrastructure.dao;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class TrainerWorkloadDao {
    private final String trainerFirstName;
    private final String trainerLastName;
    private final boolean isActive;

    @Setter
    private Map<Integer, Map<Integer, Integer>> monthlySummary = new HashMap<>();

    public TrainerWorkloadDao(String trainerFirstName, String trainerLastName, boolean isActive) {
        this.trainerFirstName = trainerFirstName;
        this.trainerLastName = trainerLastName;
        this.isActive = isActive;
    }
}
