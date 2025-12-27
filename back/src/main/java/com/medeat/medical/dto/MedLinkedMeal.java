package com.medeat.medical.dto;

import java.util.List;

public class MedLinkedMeal {
    private Long dietId;
    private String date;
    private String mealTime;
    private int totalCalorie;
    private int totalSodium;
    private double totalSugar;
    private List<String> drugNames; // 이 끼니 근처에 먹는 약 이름들
    private String level;           // "위험" / "주의" / "안전"
    private String highlightMessage;

    public Long getDietId() { return dietId; }
    public void setDietId(Long dietId) { this.dietId = dietId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getMealTime() { return mealTime; }
    public void setMealTime(String mealTime) { this.mealTime = mealTime; }

    public int getTotalCalorie() { return totalCalorie; }
    public void setTotalCalorie(int totalCalorie) { this.totalCalorie = totalCalorie; }

    public int getTotalSodium() { return totalSodium; }
    public void setTotalSodium(int totalSodium) { this.totalSodium = totalSodium; }

    public double getTotalSugar() { return totalSugar; }
    public void setTotalSugar(double totalSugar) { this.totalSugar = totalSugar; }

    public List<String> getDrugNames() { return drugNames; }
    public void setDrugNames(List<String> drugNames) { this.drugNames = drugNames; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getHighlightMessage() { return highlightMessage; }
    public void setHighlightMessage(String highlightMessage) { this.highlightMessage = highlightMessage; }
}
