package com.medeat.diet.service;

import java.time.LocalDate;
import java.util.List;

import com.medeat.dashboard.dto.EatModeAnalysisResponse;
import com.medeat.food.dto.FoodDto;

public interface EatAnalysisService {

	EatModeAnalysisResponse analyze(Long userId, LocalDate startDate, LocalDate endDate);

	EatModeAnalysisResponse getEatModeAnalysis(String start, String end);

	// 🔥 PDF / IT 추천용
	List<FoodDto> getRecommendedFoods(Long userId, LocalDate startDate, LocalDate endDate);

}
