package com.trainerworkload.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class TrainerWorkload {
    private final String id;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final boolean isActive;

    @Setter
    private Map<Integer, Map<Integer, Integer>> monthlySummary = new HashMap<>();

    public void updateWorkload(int year, int month, int duration, boolean isAdd) {
        Map<Integer, Integer> months = monthlySummary.computeIfAbsent(year, k -> new HashMap<>());
        months.put(month, months.getOrDefault(month, 0) + (isAdd ? duration : -duration));
    }
}
