package com.trainerworkload.infrastructure.persistence.mapper;

import com.trainerworkload.domain.model.TrainerWorkload;
import com.trainerworkload.infrastructure.persistence.dao.MonthDao;
import com.trainerworkload.infrastructure.persistence.dao.TrainerTrainingSummaryDao;
import com.trainerworkload.infrastructure.persistence.dao.YearDao;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TrainerWorkloadDaoMapper {
    private TrainerWorkloadDaoMapper(){}

    public static TrainerTrainingSummaryDao toDao(TrainerWorkload trainerWorkload) {
        List<YearDao> years = trainerWorkload.getMonthlySummary().entrySet().stream().map(
                e -> {
                            YearDao yearDao = new YearDao(e.getKey());
                            List<MonthDao> monthDaos = e.getValue().entrySet().stream()
                                    .map(m -> new MonthDao(m.getKey(), m.getValue()))
                                    .toList();

                            yearDao.setMonths(monthDaos);
                            return yearDao;
                        }).toList();

         TrainerTrainingSummaryDao dao = new TrainerTrainingSummaryDao(trainerWorkload.getId(),
                 trainerWorkload.getUsername(), trainerWorkload.getFirstName(), trainerWorkload.getLastName(),
                 trainerWorkload.isActive());

         dao.setYears(years);
         return dao;
    }

    public static TrainerWorkload toDomain(TrainerTrainingSummaryDao dao) {
        Map<Integer, Map<Integer, Integer>> monthlySummary = dao.getYears().stream()
                .collect(Collectors.toMap(YearDao::getYear, y -> y.getMonths().stream()
                                .collect(Collectors.toMap(MonthDao::month, MonthDao::trainingsSummaryDuration))));

       TrainerWorkload trainerWorkload = new TrainerWorkload(dao.getId(), dao.getTrainerUsername(),
               dao.getTrainerFirstName(), dao.getTrainerLastName(), dao.isTrainerStatus());

       trainerWorkload.setMonthlySummary(monthlySummary);
       return trainerWorkload;
    }
}
