package com.trainerworkload.infrastructure.persistence.document;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class YearDocument {
    private final int year;

    @Setter
    private List<MonthDocument> months = new ArrayList<>();
}
