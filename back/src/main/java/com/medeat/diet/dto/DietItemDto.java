package com.medeat.diet.dto;

public class DietItemDto {

    private Long itemId;
    private Long dietId;
    private Long foodId;

    private String foodName;
    private Integer calorie;
    private Double carb;
    private Double protein;
    private Double fat;
    private Integer gram;

    public DietItemDto() {}

    // getter / setter
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public Long getDietId() { return dietId; }
    public void setDietId(Long dietId) { this.dietId = dietId; }

    public Long getFoodId() { return foodId; }
    public void setFoodId(Long foodId) { this.foodId = foodId; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public Integer getCalorie() { return calorie; }
    public void setCalorie(Integer calorie) { this.calorie = calorie; }

    public Double getCarb() { return carb; }
    public void setCarb(Double carb) { this.carb = carb; }

    public Double getProtein() { return protein; }
    public void setProtein(Double protein) { this.protein = protein; }

    public Double getFat() { return fat; }
    public void setFat(Double fat) { this.fat = fat; }

    public Integer getGram() { return gram; }
    public void setGram(Integer gram) { this.gram = gram; }
}
