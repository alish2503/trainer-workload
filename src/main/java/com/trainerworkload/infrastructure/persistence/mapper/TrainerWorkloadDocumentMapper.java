package com.trainerworkload.infrastructure.persistence.mapper;

import com.trainerworkload.domain.model.TrainerWorkload;
import com.trainerworkload.infrastructure.persistence.document.MonthDocument;
import com.trainerworkload.infrastructure.persistence.document.TrainerTrainingSummaryDocument;
import com.trainerworkload.infrastructure.persistence.document.YearDocument;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TrainerWorkloadDocumentMapper {
    private TrainerWorkloadDocumentMapper(){}

    public static TrainerTrainingSummaryDocument toDao(TrainerWorkload trainerWorkload) {
        List<YearDocument> years = trainerWorkload.getMonthlySummary().entrySet().stream().map(
                e -> {
                            YearDocument yearDocument = new YearDocument(e.getKey());
                            List<MonthDocument> monthDocuments = e.getValue().entrySet().stream()
                                    .map(m -> new MonthDocument(m.getKey(), m.getValue()))
                                    .toList();

                            yearDocument.setMonths(monthDocuments);
                            return yearDocument;
                        }).toList();

         TrainerTrainingSummaryDocument dao = new TrainerTrainingSummaryDocument(trainerWorkload.getId(),
                 trainerWorkload.getUsername(), trainerWorkload.getFirstName(), trainerWorkload.getLastName(),
                 trainerWorkload.isActive());

         dao.setYears(years);
         return dao;
    }

    public static TrainerWorkload toDomain(TrainerTrainingSummaryDocument dao) {
        Map<Integer, Map<Integer, Integer>> monthlySummary = dao.getYears().stream()
                .collect(Collectors.toMap(YearDocument::getYear, y -> y.getMonths().stream()
                                .collect(Collectors.toMap(MonthDocument::month, MonthDocument::trainingsSummaryDuration))));

       TrainerWorkload trainerWorkload = new TrainerWorkload(dao.getId(), dao.getTrainerUsername(),
               dao.getTrainerFirstName(), dao.getTrainerLastName(), dao.isTrainerStatus());

       trainerWorkload.setMonthlySummary(monthlySummary);
       return trainerWorkload;
    }
}
