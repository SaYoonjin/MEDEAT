package com.medeat.diet.dto;

import java.util.ArrayList;
import java.util.List;

public class DietLogDto {

    private Long dietId;
    private Long userId;
    private String logDate;      // yyyy-MM-dd
    private String mealTime;     // 아침/점심/저녁/간식/야식
    private String memo;

    private Integer totalCalorie;
    private Double totalCarb;
    private Double totalProtein;
    private Double totalFat;

    private String createdAt;
    private String photoPath;

    private List<DietItemDto> items = new ArrayList<>();

    public DietLogDto() {}

    public Long getDietId() { return dietId; }
    public void setDietId(Long dietId) { this.dietId = dietId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getLogDate() { return logDate; }
    public void setLogDate(String logDate) { this.logDate = logDate; }

    public String getMealTime() { return mealTime; }
    public void setMealTime(String mealTime) { this.mealTime = mealTime; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }

    public Integer getTotalCalorie() { return totalCalorie; }
    public void setTotalCalorie(Integer totalCalorie) { this.totalCalorie = totalCalorie; }

    public Double getTotalCarb() { return totalCarb; }
    public void setTotalCarb(Double totalCarb) { this.totalCarb = totalCarb; }

    public Double getTotalProtein() { return totalProtein; }
    public void setTotalProtein(Double totalProtein) { this.totalProtein = totalProtein; }

    public Double getTotalFat() { return totalFat; }
    public void setTotalFat(Double totalFat) { this.totalFat = totalFat; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public List<DietItemDto> getItems() { return items; }
    public void setItems(List<DietItemDto> items) { this.items = items; }
}
