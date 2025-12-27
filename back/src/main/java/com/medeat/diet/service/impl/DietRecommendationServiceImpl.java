package com.medeat.diet.service.impl;

import com.medeat.auth.dto.UserDto;
import com.medeat.auth.service.UserService;
import com.medeat.dashboard.dto.EatModeAnalysisResponse;
import com.medeat.diet.dto.AlternativeFoodDto;
import com.medeat.diet.dto.CookingAdviceDto;
import com.medeat.diet.dto.MealRecommendationDto;
import com.medeat.diet.dto.RecommendationResponse;
import com.medeat.diet.service.DietRecommendationService;
import com.medeat.diet.service.EatAnalysisService;
import com.medeat.food.dao.FoodDao;
import com.medeat.food.dto.FoodDto;
import com.medeat.food.dto.FoodType;
import com.medeat.pdf.dto.eat.EatPeriodSummary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class DietRecommendationServiceImpl implements DietRecommendationService {

    @Autowired
    private UserService userService;

    @Autowired
    private EatAnalysisService eatAnalysisService;

    @Autowired
    private FoodDao foodDao;

    /* ===========================
       공통 필터 유틸
     =========================== */

    // 이름에 들어가면 추천에서 빼고 싶은 키워드들
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

        // 타입이 없으면 일단 식사 후보로 인정
        if (type == null) return true;

        // 우리가 명시적으로 "식사" 라고 분류한 애들
        if (type == FoodType.MEAL) return true;

        // 옛날에 쓰던 타입들은 대부분 일반 음식이라 식사로 간주
        if (type == FoodType.FOOD_DB || type == FoodType.PRODUCT_DB || type == FoodType.UNKNOWN)
            return true;

        // DRINK / SNACK / SUPP / SAUCE 는 제외
        return false;
    }


    // 저칼로리 식사 후보 필터 (최대 10개)
    private List<FoodDto> filterLowCalorieFoods(List<FoodDto> candidates) {
        return candidates.stream()
                .filter(this::isMealLike)
                .filter(f -> !containsBannedKeyword(f.getName()))
                .filter(f -> f.getCalorie() != null && f.getCalorie() <= 200)
                .filter(f -> f.getFat() == null || f.getFat() <= 5)
                .filter(f -> f.getProtein() == null || f.getProtein() >= 5)
                .sorted(Comparator.comparingDouble(f -> f.getCalorie() == null ? 0.0 : f.getCalorie()))
                .limit(10)
                .toList();
    }

    // 고단백 식사 후보 필터 (실제 쿼리는 고단백+저지방이라 결과는 건강한 고단백 위주)
    private List<FoodDto> filterHighProteinLowFatFoods(List<FoodDto> candidates) {
        return candidates.stream()
                .filter(this::isMealLike)
                .filter(f -> !containsBannedKeyword(f.getName()))
                .filter(f -> f.getProtein() != null && f.getProtein() >= 15)
                .filter(f -> f.getFat() == null || f.getFat() <= 5)
                .filter(f -> {
                    if (f.getCalorie() == null) return true;
                    return f.getCalorie() >= 50 && f.getCalorie() <= 400;
                })
                .sorted(
                        Comparator.<FoodDto>comparingDouble(f -> f.getProtein() == null ? 0.0 : f.getProtein())
                                .reversed()
                                .thenComparingDouble(f -> f.getCalorie() == null ? 0.0 : f.getCalorie())
                )
                .limit(10)
                .toList();
    }

    // 공통 “식사에 쓸만한 건강식” 필터 (최대 10개, 0kcal 제외)
    private List<FoodDto> filterMealHealthyFoods(List<FoodDto> candidates) {
        return candidates.stream()
                .filter(this::isMealLike)
                .filter(f -> !containsBannedKeyword(f.getName()))
                // 🔥 0kcal 음식은 제외
                .filter(f -> f.getCalorie() == null || f.getCalorie() > 0)
                .limit(10)
                .toList();
    }

    @Override
    public RecommendationResponse recommendAll(Long userId, LocalDate date) {

        RecommendationResponse res = new RecommendationResponse();
        List<String> messages = new ArrayList<>();

        // 0. 유저 & 오늘 분석
        UserDto user = userService.getUserById(userId);

        EatModeAnalysisResponse analysis =
                eatAnalysisService.analyze(userId, date, date);

        EatPeriodSummary ps = analysis.getPeriodSummary();
        double todayKcal = ps.getTotalKcal();
        int goalKcal = ps.getGoalKcal();
        double remainingKcal = Math.max(0, goalKcal - todayKcal);

        /*
         ============================
         ① 남은 칼로리 기반 추천
         ============================
        */
        List<MealRecommendationDto> remainingMeals = new ArrayList<>();

        List<FoodDto> lowKcalCandidates = foodDao.findLowCalorieFoods();
        List<FoodDto> lowKcalFiltered = filterLowCalorieFoods(lowKcalCandidates);

        List<FoodDto> filtered = new ArrayList<>();
        int limitKcal = (int) Math.min(remainingKcal, 500);
        int sum = 0;

        for (FoodDto f : lowKcalFiltered) {
            if (f.getCalorie() == null) continue;
            int kcal = f.getCalorie();
            if (kcal <= limitKcal && (sum + kcal) <= limitKcal) {
                filtered.add(f);
                sum += kcal;
            }
        }

        MealRecommendationDto rMeal = new MealRecommendationDto();
        rMeal.setTitle("남은 칼로리 내에서 가능한 식단");
        rMeal.setDescription("현재 남은 칼로리 기준으로 부담 적은 음식들을 모았어요.");
        rMeal.setFoods(filtered);
        rMeal.setTotalKcal(filtered.stream()
                                   .filter(f -> f.getCalorie() != null)
                                   .mapToDouble(FoodDto::getCalorie)
                                   .sum());
        remainingMeals.add(rMeal);

        res.setRemainingKcalMeals(remainingMeals);

        /*
         ============================
         ② 체중/체력 목표 기반 식단
         - '고단백' + 건강 대체식
         ============================
        */
        List<MealRecommendationDto> goalMeals = new ArrayList<>();

        List<FoodDto> highProteinLowFatRaw = foodDao.findHighProteinLowFat();
        List<FoodDto> highProteinLowFat = filterHighProteinLowFatFoods(highProteinLowFatRaw);

        List<FoodDto> healthyAltRaw = foodDao.findHealthyAlternatives();
        List<FoodDto> healthyAlt = filterMealHealthyFoods(healthyAltRaw);

        List<FoodDto> goalFoods = new ArrayList<>();
        // 고단백 일부 (최대 5개)
        for (int i = 0; i < Math.min(5, highProteinLowFat.size()); i++) {
            goalFoods.add(highProteinLowFat.get(i));
        }
        // 건강 대체식 일부 (최대 5개)
        for (int i = 0; i < Math.min(5, healthyAlt.size()); i++) {
            goalFoods.add(healthyAlt.get(i));
        }

        MealRecommendationDto goal = new MealRecommendationDto();
        goal.setTitle("체중/체력 목표 기반 하루 식단 추천");
        goal.setDescription("단백질 비중이 높고 칼로리가 과하지 않은 식품 위주로 구성했어요.");
        goal.setFoods(goalFoods);
        goalMeals.add(goal);

        res.setGoalBasedMeals(goalMeals);

        /*
         ============================
         ③ 고칼로리 → 저칼로리 대체 음식
         ============================
        */
        List<AlternativeFoodDto> altList = new ArrayList<>();

        List<FoodDto> highCalToday =
                foodDao.findHighCalorieFoodsByUserRange(
                        userId,
                        date.toString(),
                        date.toString(),
                        300
                );

        for (FoodDto high : highCalToday) {
            if (high.getCalorie() == null) continue;
            if (containsBannedKeyword(high.getName())) continue;

            String name = high.getName() != null ? high.getName() : "";
            int originalKcal = high.getCalorie();

            String type;
            String lowerName = name.toLowerCase();

            if (lowerName.contains("커피") || lowerName.contains("라떼")
                    || lowerName.contains("콜라") || lowerName.contains("사이다")
                    || lowerName.contains("탄산") || lowerName.contains("에이드")
                    || lowerName.contains("주스") || lowerName.contains("티")
                    || lowerName.contains("차") || lowerName.contains("음료")
                    || lowerName.contains("스무디")) {
                type = "DRINK";
            } else {
                type = "FOOD";
            }

            int maxKcal = (int) (originalKcal * 0.7);

            List<FoodDto> lowerList =
                    foodDao.findLowerKcalAlternativeByType(
                            name,
                            maxKcal,
                            type
                    );

            if (!lowerList.isEmpty()) {
                FoodDto altFood = lowerList.get(0);

                AlternativeFoodDto alt = new AlternativeFoodDto();
                alt.setOriginalFoodName(high.getName());
                alt.setOriginalFood(high);
                alt.setAlternativeFood(altFood);

                int diff = (altFood.getCalorie() != null)
                        ? (originalKcal - altFood.getCalorie())
                        : 0;

                if ("DRINK".equals(type)) {
                    alt.setReason("현재 마신 음료보다 약 " + diff + "kcal 낮은 음료예요.");
                } else {
                    alt.setReason("비슷한 메뉴 중 더 가벼운 선택으로 약 " + diff + "kcal 절감이 가능해요.");
                }

                altList.add(alt);
            }

            if (altList.size() >= 10) break;
        }

        res.setLowerKcalAlternatives(altList);

        /*
         ============================
         ④ 탄단지 불균형 교정 식단
         ============================
        */
        List<MealRecommendationDto> macroMeals = new ArrayList<>();

        if (ps.getProteinRatio() < 0.15) {
            List<FoodDto> highProtein = filterHighProteinLowFatFoods(foodDao.findHighProteinLowFat());
            MealRecommendationDto m = new MealRecommendationDto();
            m.setTitle("단백질 보충 추천");
            m.setDescription("단백질 비율이 낮아서, 고단백 식품을 추천해요.");
            m.setFoods(highProtein);
            macroMeals.add(m);
        }

        if (ps.getCarbRatio() > 0.65) {
            List<FoodDto> lowCarb = filterMealHealthyFoods(foodDao.findLowCarbFoods());
            MealRecommendationDto m = new MealRecommendationDto();
            m.setTitle("탄수화물 줄이기 추천");
            m.setDescription("탄수화물 비중이 높아서, 저탄수 식품을 추천해요.");
            m.setFoods(lowCarb);
            macroMeals.add(m);
        }

        if (ps.getFatRatio() > 0.35) {
            List<FoodDto> lowFat = filterMealHealthyFoods(foodDao.findLowFatFoods());
            MealRecommendationDto m = new MealRecommendationDto();
            m.setTitle("지방 줄이기 추천");
            m.setDescription("지방 섭취가 높아서, 저지방 식품을 추천해요.");
            m.setFoods(lowFat);
            macroMeals.add(m);
        }

        res.setMacroBalanceMeals(macroMeals);

        /*
         ============================
         ⑤ 건강한 음식 추천
         ============================
        */
        res.setHealthyFoodCandidates(
                filterMealHealthyFoods(foodDao.findHealthyAlternatives())
        );

        /*
         ============================
         ⑥ 조리법 변경 추천 (문자열 기반)
         ============================
        */
        List<CookingAdviceDto> cookingList = new ArrayList<>();

        int cookedCount = 0;
        for (FoodDto high : highCalToday) {
            CookingAdviceDto c = new CookingAdviceDto();
            c.setFoodName(high.getName());
            c.setFromMethod("기존 조리/선택");
            c.setToMethod("구이/찜/샐러드 등 가벼운 방식");
            c.setTip("튀김이나 크림 기반 메뉴는, 구이·찜·샐러드로 바꾸면 칼로리를 꽤 줄일 수 있어요.");
            cookingList.add(c);

            cookedCount++;
            if (cookedCount >= 10) break;
        }

        res.setCookingAdvices(cookingList);

        messages.add("오늘 섭취량과 사용자의 목표를 기준으로 6가지 식단 추천을 생성했어요.");
        res.setMessages(messages);

        return res;
    }
}
