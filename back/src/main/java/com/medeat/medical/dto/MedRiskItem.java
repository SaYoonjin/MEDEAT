package com.medeat.medical.dto;

public class MedRiskItem {
    private String level;     // "위험", "주의", "안전"
    private String date;      // 2025-11-28
    private String mealTime;  // 아침/점심/저녁/간식/야식
    private String message;   // "고혈압 + 고나트륨 식단입니다."

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getMealTime() { return mealTime; }
    public void setMealTime(String mealTime) { this.mealTime = mealTime; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
