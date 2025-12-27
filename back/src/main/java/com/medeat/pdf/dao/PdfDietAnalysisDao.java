package com.medeat.pdf.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.medeat.dashboard.dto.EatModeAnalysisResponse;
import com.medeat.dashboard.dto.MedModeAnalysisResponse;

@Mapper
public interface PdfDietAnalysisDao {

    // EAT 모드 분석 데이터 조회 (기간 기반)
	EatModeAnalysisResponse getItAnalysis(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    // MED 모드 분석 데이터 조회
    MedModeAnalysisResponse getMedAnalysis(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );
}
