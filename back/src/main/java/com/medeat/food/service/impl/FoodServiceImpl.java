package com.medeat.food.service.impl;

import com.medeat.food.dao.FoodDao;
import com.medeat.food.dto.FoodDto;
import com.medeat.food.dto.FoodType;
import com.medeat.food.service.FoodService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FoodServiceImpl implements FoodService {

    @Autowired
    private FoodDao foodDao;
    
    @Override
    public List<FoodDto> search(String keyword) {
        // 검색은 그대로 DAO에 위임 (필터 없이 전체 검색)
        return foodDao.search(keyword);
    }


    // 추천에서 빼고 싶은 키워드들 (보충제/간식 느낌)
    private static final String[] BANNED_KEYWORDS = {
            "콜라겐", "젤리", "제로바", "0kcal", "0 kcal", "영양제",
            "보충제", "구미", "스틱", "타블렛", "정 ", "캡슐"
    };

    private boolean containsBannedKeyword(String name) {
        if (name == null) return false;
        String lower = name.toLowerCase();
        for (String kw : BANNED_KEYWORDS) {
            if (lower.contains(kw.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    // 실제 식사/반찬으로 쓰일 수 있는 타입만 허용 (음료/보충제/간식/소스 제외)
    private boolean isMealLike(FoodDto food) {
        FoodType type = food.getFoodType();

        if (type == null) return true;
        if (type == FoodType.MEAL) return true;
        if (type == FoodType.FOOD_DB || type == FoodType.PRODUCT_DB || type == FoodType.UNKNOWN)
            return true;

        // DRINK, SNACK, SUPP, SAUCE 는 제외
        return false;
    }

    /**
     * 공통 후처리:
     *  - (옵션) 식사 타입 필터(isMealLike)
     *  - 키워드 필터(BANNED_KEYWORDS)
     *  - (옵션) 0kcal 제외
     *  - 이름 기준 중복 제거
     *  - 최대 10개
     */
    private List<FoodDto> postProcess(List<FoodDto> src,
                                      boolean excludeZeroKcal,
                                      boolean applyMealFilter) {
        Set<String> seenNames = new HashSet<>();
        List<FoodDto> result = new ArrayList<>();

        for (FoodDto f : src) {
            if (applyMealFilter && !isMealLike(f)) continue;
            if (containsBannedKeyword(f.getName())) continue;

            Integer kcal = f.getCalorie();
            if (excludeZeroKcal && kcal != null && kcal == 0) continue; // 🔥 0kcal 제거

            String name = f.getName();
            if (name != null) {
                if (seenNames.contains(name)) continue; // 같은 이름은 한 번만
                seenNames.add(name);
            }

            result.add(f);
            if (result.size() >= 10) break; // 🔥 최대 10개
        }

        return result;
    }

    // 주요 재료(새우/게/동태 등)별로 1개만 남기고 싶을 때 사용
    private String extractMainIngredient(String name) {
        if (name == null) return "UNKNOWN";

        String[] keywords = {
                "새우", "대게", "홍게", "게살",
                "동태", "오징어", "간재미", "홍어",
                "닭가슴살", "닭가슴", "오리", "소고기", "돼지고기"
        };

        for (String kw : keywords) {
            if (name.contains(kw)) {
                return kw;
            }
        }
        // 못 찾으면 이름 전체를 키로 사용 (이름이 다르면 다른 재료로 취급)
        return name;
    }

    // ==========================
    //   각 추천용 메서드
    // ==========================

    @Override
    public List<FoodDto> findLowCalorieFoods() {
        // 저칼로리: 식사 필터 + 0kcal 제거
        return postProcess(foodDao.findLowCalorieFoods(), true, true);
    }

    @Override
    public List<FoodDto> findSubstitutes() {
        // 1차: 공통 후처리(식사 필터 + 0kcal 제거 + 이름 중복 제거 + 최대 10개)
        List<FoodDto> base = postProcess(foodDao.findSubstitutes(), true, true);

        // 2차: 주요 재료(새우/게/동태 등) 기준으로 다시 중복 제거
        Set<String> ingredientSeen = new HashSet<>();
        List<FoodDto> result = new ArrayList<>();

        for (FoodDto f : base) {
            String key = extractMainIngredient(f.getName()); // "새우", "대게" 같은 키워드
            if (ingredientSeen.contains(key)) continue;       // 같은 재료는 1개만

            ingredientSeen.add(key);
            result.add(f);

            if (result.size() >= 10) break;
        }

        return result;
    }

    @Override
    public List<FoodDto> findHighProteinLowFat() {
        // 고단백(저지방 조건은 쿼리에서 걸려 있음): 식사 필터 + 0kcal 제거
        return postProcess(foodDao.findHighProteinLowFat(), true, true);
    }

    @Override
    public List<FoodDto> findLowCarbFoods() {
        return postProcess(foodDao.findLowCarbFoods(), true, true);
    }

    @Override
    public List<FoodDto> findLowFatFoods() {
        // 저지방: Mapper에서 이미 타입 & 0kcal 필터링 했으니까
        // 여기서는 이름 중복만 막고 10개로 자르기
        return postProcess(foodDao.findLowFatFoods(), false, false);
    }

    @Override
    public List<FoodDto> findHealthyAlternatives() {
        return postProcess(foodDao.findHealthyAlternatives(), true, true);
    }
}
