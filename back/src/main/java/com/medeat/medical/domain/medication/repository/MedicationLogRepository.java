package com.medeat.medical.domain.medication.repository;

import com.medeat.medical.domain.medication.entity.MedicationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicationLogRepository extends JpaRepository<MedicationLog, Long> {

    List<MedicationLog> findByUserUserIdAndTakenAtBetweenOrderByTakenAtAsc(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<MedicationLog> findByMedicationMedicationIdAndTakenAtBetweenOrderByTakenIndexAsc(
            Long medicationId,
            LocalDateTime start,
            LocalDateTime end
    );
}
