package com.medeat.medical.domain.medication.repository;

import com.medeat.medical.domain.medication.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicationRepository extends JpaRepository<Medication, Long> {

    List<Medication> findByUserUserIdOrderByMedicationIdDesc(Long userId);

    List<Medication> findAllByOrderByMedicationIdAsc();

    boolean existsByUserUserIdAndDrugName(Long userId, String drugName);

    boolean existsByUserUserIdAndDrugNameAndMedicationIdNot(Long userId, String drugName, Long medicationId);
}
