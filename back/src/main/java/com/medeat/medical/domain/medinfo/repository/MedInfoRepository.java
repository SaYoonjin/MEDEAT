package com.medeat.medical.domain.medinfo.repository;

import com.medeat.medical.domain.medinfo.entity.MedInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedInfoRepository extends JpaRepository<MedInfo, Long> {
}
