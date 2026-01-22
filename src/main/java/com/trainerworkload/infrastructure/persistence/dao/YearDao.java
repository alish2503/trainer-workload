package com.trainerworkload.infrastructure.persistence.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class YearDao {
    private final int year;

    @Setter
    private List<MonthDao> months = new ArrayList<>();
}
