package com.medeat.food.service;

import java.util.List;

import com.medeat.food.dto.FoodDto;

public interface FoodService {

    // 검색
    List<FoodDto> search(String keyword);

    // 추천 탭용
    List<FoodDto> findLowCalorieFoods();      // 저칼로리
    List<FoodDto> findSubstitutes();          // 대체 음식
    List<FoodDto> findHighProteinLowFat();    // 고단백 저지방
    List<FoodDto> findLowCarbFoods();         // 저탄수
    List<FoodDto> findLowFatFoods();          // 저지방
    List<FoodDto> findHealthyAlternatives();  // 건강 대체식
}
