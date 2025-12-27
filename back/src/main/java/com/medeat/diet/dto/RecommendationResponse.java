package com.medeat.diet.dto;

import java.util.List;

import com.medeat.food.dto.FoodDto;

public class RecommendationResponse {

    // 기존 추천 항목들
    private List<MealRecommendationDto> remainingKcalMeals;
    private List<MealRecommendationDto> goalBasedMeals;
    private List<AlternativeFoodDto> lowerKcalAlternatives;
    private List<MealRecommendationDto> macroBalanceMeals;
    private List<FoodDto> healthyFoodCandidates;
    private List<CookingAdviceDto> cookingAdvices;
    private List<String> messages;

    // 🔥 프론트 신규 요구사항 반영 (6가지 추천)
    private List<FoodDto> lowCalorie;
    private List<FoodDto> substitutes;
    private List<FoodDto> highProteinLowFat;
    private List<FoodDto> lowCarb;
    private List<FoodDto> lowFat;
    private List<FoodDto> healthyAlternatives;

    public RecommendationResponse() {}

    // getter & setter (기존)
    public List<MealRecommendationDto> getRemainingKcalMeals() { return remainingKcalMeals; }
    public void setRemainingKcalMeals(List<MealRecommendationDto> remainingKcalMeals) { this.remainingKcalMeals = remainingKcalMeals; }

    public List<MealRecommendationDto> getGoalBasedMeals() { return goalBasedMeals; }
    public void setGoalBasedMeals(List<MealRecommendationDto> goalBasedMeals) { this.goalBasedMeals = goalBasedMeals; }

    public List<AlternativeFoodDto> getLowerKcalAlternatives() { return lowerKcalAlternatives; }
    public void setLowerKcalAlternatives(List<AlternativeFoodDto> lowerKcalAlternatives) { this.lowerKcalAlternatives = lowerKcalAlternatives; }

    public List<MealRecommendationDto> getMacroBalanceMeals() { return macroBalanceMeals; }
    public void setMacroBalanceMeals(List<MealRecommendationDto> macroBalanceMeals) { this.macroBalanceMeals = macroBalanceMeals; }

    public List<FoodDto> getHealthyFoodCandidates() { return healthyFoodCandidates; }
    public void setHealthyFoodCandidates(List<FoodDto> healthyFoodCandidates) { this.healthyFoodCandidates = healthyFoodCandidates; }

    public List<CookingAdviceDto> getCookingAdvices() { return cookingAdvices; }
    public void setCookingAdvices(List<CookingAdviceDto> cookingAdvices) { this.cookingAdvices = cookingAdvices; }

    public List<String> getMessages() { return messages; }
    public void setMessages(List<String> messages) { this.messages = messages; }

    // 🔥 신규 getter/setter
    public List<FoodDto> getLowCalorie() { return lowCalorie; }
    public void setLowCalorie(List<FoodDto> lowCalorie) { this.lowCalorie = lowCalorie; }

    public List<FoodDto> getSubstitutes() { return substitutes; }
    public void setSubstitutes(List<FoodDto> substitutes) { this.substitutes = substitutes; }

    public List<FoodDto> getHighProteinLowFat() { return highProteinLowFat; }
    public void setHighProteinLowFat(List<FoodDto> highProteinLowFat) { this.highProteinLowFat = highProteinLowFat; }

    public List<FoodDto> getLowCarb() { return lowCarb; }
    public void setLowCarb(List<FoodDto> lowCarb) { this.lowCarb = lowCarb; }

    public List<FoodDto> getLowFat() { return lowFat; }
    public void setLowFat(List<FoodDto> lowFat) { this.lowFat = lowFat; }

    public List<FoodDto> getHealthyAlternatives() { return healthyAlternatives; }
    public void setHealthyAlternatives(List<FoodDto> healthyAlternatives) { this.healthyAlternatives = healthyAlternatives; }
}
