package com.medeat.diet.dto;

public class CookingAdviceDto {

    private String foodName;     // 예: "치킨", "돈가스"
    private String fromMethod;   // "튀김"
    private String toMethod;     // "구이", "에어프라이어"
    private String tip;          // 조리 팁

    public CookingAdviceDto() {}

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFromMethod() {
        return fromMethod;
    }

    public void setFromMethod(String fromMethod) {
        this.fromMethod = fromMethod;
    }

    public String getToMethod() {
        return toMethod;
    }

    public void setToMethod(String toMethod) {
        this.toMethod = toMethod;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }
}
