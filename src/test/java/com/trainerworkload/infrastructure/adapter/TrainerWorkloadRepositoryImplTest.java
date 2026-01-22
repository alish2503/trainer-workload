package com.trainerworkload.infrastructure.adapter;

import com.trainerworkload.domain.model.TrainerWorkload;
import com.trainerworkload.infrastructure.persistence.adapter.TrainerWorkloadRepositoryImpl;
import com.trainerworkload.infrastructure.persistence.dao.MonthDao;
import com.trainerworkload.infrastructure.persistence.dao.TrainerTrainingSummaryDao;
import com.trainerworkload.infrastructure.persistence.dao.YearDao;
import com.trainerworkload.infrastructure.persistence.mongorepo.TrainerTrainingSummaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadRepositoryImplTest {

    @Mock
    private TrainerTrainingSummaryRepository mongoRepository;

    @InjectMocks
    private TrainerWorkloadRepositoryImpl repository;

    @Test
    void save_shouldCallMongoRepositoryWithMappedDao() {
        TrainerWorkload workload = new TrainerWorkload("1", "john.doe", "John",
                "Doe", true);

        workload.updateWorkload(2026, 1, 10, true);
        repository.save(workload);
        ArgumentCaptor<TrainerTrainingSummaryDao> daoCaptor = ArgumentCaptor.forClass(TrainerTrainingSummaryDao.class);
        verify(mongoRepository, times(1)).save(daoCaptor.capture());
        TrainerTrainingSummaryDao capturedDao = daoCaptor.getValue();
        assertEquals("john.doe", capturedDao.getTrainerUsername());
        assertEquals("John", capturedDao.getTrainerFirstName());
        assertEquals("Doe", capturedDao.getTrainerLastName());
        assertFalse(capturedDao.getYears().isEmpty());
    }

    @Test
    void findTrainerWorkloadByUsername_shouldReturnMappedDomainObject() {
        MonthDao month = new MonthDao(1, 20);
        YearDao year = new YearDao(2026);
        year.setMonths(List.of(month));

        TrainerTrainingSummaryDao dao = new TrainerTrainingSummaryDao("1", "john.doe",
                "John", "Doe", true);

        dao.setYears(List.of(year));
        when(mongoRepository.findByTrainerUsername("john.doe")).thenReturn(Optional.of(dao));
        Optional<TrainerWorkload> result = repository.findTrainerWorkloadByUsername("john.doe");
        assertTrue(result.isPresent());
        TrainerWorkload workload = result.get();
        assertEquals("john.doe", workload.getUsername());
        assertEquals("John", workload.getFirstName());
        assertEquals("Doe", workload.getLastName());
        assertTrue(workload.isActive());
        assertEquals(20, workload.getMonthlySummary().get(2026).get(1));
    }

    @Test
    void findTrainerWorkloadByUsername_shouldReturnEmptyWhenNotFound() {
        when(mongoRepository.findByTrainerUsername("unknown")).thenReturn(Optional.empty());
        Optional<TrainerWorkload> result = repository.findTrainerWorkloadByUsername("unknown");
        assertTrue(result.isEmpty());
    }
}

