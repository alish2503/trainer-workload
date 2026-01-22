package com.trainerworkload.infrastructure.persistence.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
@CompoundIndex(name = "trainer_name_idx", def = "{'trainerFirstName': 1, 'trainerLastName': 1}")
@RequiredArgsConstructor
@Getter
public class TrainerTrainingSummaryDao {

    @Id
    private final String id;
    private final String trainerUsername;
    private final String trainerFirstName;
    private final String trainerLastName;
    private final boolean trainerStatus;

    @Setter
    private List<YearDao> years = new ArrayList<>();
}
