package com.medeat.diet.dto;

import java.util.List;

import com.medeat.food.dto.FoodDto;

public class MealRecommendationDto {

    private String title;            // 예: "저녁 한 끼 추천"
    private String description;      // 한줄 설명
    private List<FoodDto> foods;     // 메뉴 구성

    private double totalKcal;
    private double totalCarb;
    private double totalProtein;
    private double totalFat;

    public MealRecommendationDto() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FoodDto> getFoods() {
        return foods;
    }

    public void setFoods(List<FoodDto> foods) {
        this.foods = foods;
    }

    public double getTotalKcal() {
        return totalKcal;
    }

    public void setTotalKcal(double totalKcal) {
        this.totalKcal = totalKcal;
    }

    public double getTotalCarb() {
        return totalCarb;
    }

    public void setTotalCarb(double totalCarb) {
        this.totalCarb = totalCarb;
    }

    public double getTotalProtein() {
        return totalProtein;
    }

    public void setTotalProtein(double totalProtein) {
        this.totalProtein = totalProtein;
    }

    public double getTotalFat() {
        return totalFat;
    }

    public void setTotalFat(double totalFat) {
        this.totalFat = totalFat;
    }
}
