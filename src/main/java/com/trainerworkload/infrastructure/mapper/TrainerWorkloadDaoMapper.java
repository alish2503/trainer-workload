package com.trainerworkload.infrastructure.mapper;

import com.trainerworkload.domain.model.TrainerWorkload;
import com.trainerworkload.infrastructure.dao.TrainerWorkloadDao;

import java.util.Map;

public class TrainerWorkloadDaoMapper {
    private TrainerWorkloadDaoMapper(){}

    public static TrainerWorkloadDao toDao(TrainerWorkload trainerWorkload) {
        TrainerWorkloadDao dao = new TrainerWorkloadDao(trainerWorkload.getFirstName(),
                trainerWorkload.getLastName(), trainerWorkload.isActive());

        Map<Integer, Map<Integer, Integer>> summary = trainerWorkload.getMonthlySummary();
        dao.setMonthlySummary(summary);
        return dao;
    }

    public static TrainerWorkload toDomain(TrainerWorkloadDao dao, String username) {
        TrainerWorkload trainerWorkload = new TrainerWorkload(username, dao.getTrainerFirstName(),
                dao.getTrainerLastName(), dao.isActive());

        Map<Integer, Map<Integer, Integer>> summary = dao.getMonthlySummary();
        trainerWorkload.setMonthlySummary(summary);
        return trainerWorkload;
    }
}
