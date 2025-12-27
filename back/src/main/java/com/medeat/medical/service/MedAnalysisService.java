package com.medeat.medical.service;

import java.time.LocalDate;

import com.medeat.dashboard.dto.MedModeAnalysisResponse;

public interface MedAnalysisService {

    MedModeAnalysisResponse analyze(Long userId, LocalDate startDate, LocalDate endDate);
}
