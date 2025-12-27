package com.medeat.food.dao;

import org.apache.ibatis.annotations.Param;

import com.medeat.food.dto.FoodDto;

import java.util.List;

public interface FoodDao {

    // 🔹 기본 검색 (자동완성/검색창용)
    List<FoodDto> search(@Param("keyword") String keyword);

    // 🔹 추천 탭에서 사용하는 목록들

    // ① 저칼로리 TOP
    List<FoodDto> findLowCalorieFoods();

    // ② 대체 음식 (추가!)
    List<FoodDto> findSubstitutes();

    // ③ 고단백 저지방
    List<FoodDto> findHighProteinLowFat();

    // ④ 저탄수
    List<FoodDto> findLowCarbFoods();

    // ⑤ 저지방
    List<FoodDto> findLowFatFoods();

    // ⑥ 건강 대체식
    List<FoodDto> findHealthyAlternatives();


    // 🔹 “내가 먹은 걸 기준으로” 대체 음식 추천할 때 사용하는 쿼리들

    // 기간 동안 유저가 먹은 고칼로리 음식/음료
    List<FoodDto> findHighCalorieFoodsByUserRange(
            @Param("userId") Long userId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("minCalorie") int minCalorie
    );

    // 같은 타입(DRINK/FOOD)에서 더 저칼로리 대체 음식 찾기
    List<FoodDto> findLowerKcalAlternativeByType(
            @Param("keyword") String keyword,
            @Param("maxCalorie") int maxCalorie,
            @Param("type") String type
    );
}
