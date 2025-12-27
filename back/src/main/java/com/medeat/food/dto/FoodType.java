package com.medeat.food.dto;

public enum FoodType {

    // 실제 우리가 지금 쓰고 싶은 타입들
    MEAL,       // 진짜 식사/반찬류
    DRINK,      // 음료
    SNACK,      // 과자/디저트/바
    SUPP,       // 보조식품(콜라겐/젤리/영양제 등)
    SAUCE,      // 소스/가루/시즈닝 등

    // 예전에 쓰던 값이 DB에 남아 있을 수도 있으니까 남겨두기
    FOOD_DB,
    PRODUCT_DB,

    UNKNOWN;    // 혹시 예상 못 한 값 들어오면 여기로 (필요하면 사용)
}
