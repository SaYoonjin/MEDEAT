package com.medeat.diet.dto;

import com.medeat.food.dto.FoodDto;

public class AlternativeFoodDto {

    private String originalFoodName; // 원래 음식명
    private FoodDto originalFood;    // 원래 먹은 음식 정보(선택)
    private FoodDto alternativeFood; // 대체 음식 정보
    private String reason;           // 칼로리 절감/영양 개선 등

    public AlternativeFoodDto() {}

    public String getOriginalFoodName() {
        return originalFoodName;
    }

    public void setOriginalFoodName(String originalFoodName) {
        this.originalFoodName = originalFoodName;
    }

    public FoodDto getOriginalFood() {
        return originalFood;
    }

    public void setOriginalFood(FoodDto originalFood) {
        this.originalFood = originalFood;
    }

    public FoodDto getAlternativeFood() {
        return alternativeFood;
    }

    public void setAlternativeFood(FoodDto alternativeFood) {
        this.alternativeFood = alternativeFood;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
