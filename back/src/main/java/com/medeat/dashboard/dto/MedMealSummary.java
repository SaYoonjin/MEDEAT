package com.medeat.dashboard.dto;

public class MedMealSummary {
    private Long dietId;
    private String logDate;
    private String mealTime;
    private int totalCalorie;
    private int totalSodium;
    private double totalSugar;

    public Long getDietId() { return dietId; }
    public void setDietId(Long dietId) { this.dietId = dietId; }

    public String getLogDate() { return logDate; }
    public void setLogDate(String logDate) { this.logDate = logDate; }

    public String getMealTime() { return mealTime; }
    public void setMealTime(String mealTime) { this.mealTime = mealTime; }

    public int getTotalCalorie() { return totalCalorie; }
    public void setTotalCalorie(int totalCalorie) { this.totalCalorie = totalCalorie; }

    public int getTotalSodium() { return totalSodium; }
    public void setTotalSodium(int totalSodium) { this.totalSodium = totalSodium; }

    public double getTotalSugar() { return totalSugar; }
    public void setTotalSugar(double totalSugar) { this.totalSugar = totalSugar; }
}
