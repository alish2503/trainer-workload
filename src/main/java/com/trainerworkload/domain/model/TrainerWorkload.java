package com.trainerworkload.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class TrainerWorkload {
    private final String username;
    private final String firstName;
    private final String lastName;
    private final boolean isActive;

    @Setter
    private Map<Integer, Map<Integer, Integer>> monthlySummary = new HashMap<>();

    public TrainerWorkload(String username, String trainerFirstName, String trainerLastName,
                           boolean isActive)
    {
        this.username = username;
        this.firstName = trainerFirstName;
        this.lastName = trainerLastName;
        this.isActive = isActive;
    }

    public void updateWorkload(int year, int month, int duration, boolean isAdd) {
        monthlySummary.putIfAbsent(year, new HashMap<>());
        Map<Integer, Integer> months = monthlySummary.get(year);
        months.put(month, months.getOrDefault(month, 0) + (isAdd ? duration : -duration));
    }
}
