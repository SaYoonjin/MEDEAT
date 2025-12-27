package com.medeat.diet.service.impl;

import com.medeat.auth.dto.UserDto;
import com.medeat.auth.service.UserService;
import com.medeat.dashboard.dto.DailyChartData;
import com.medeat.dashboard.dto.EatModeAnalysisResponse;
import com.medeat.dashboard.dto.ScoreSummary;
import com.medeat.diet.dao.DietAnalysisDao;
import com.medeat.diet.dao.DietDao;
import com.medeat.diet.service.EatAnalysisService;
import com.medeat.food.dto.FoodDto;
import com.medeat.pdf.dto.eat.EatPatternSummary;
import com.medeat.pdf.dto.eat.EatPeriodSummary;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EatAnalysisServiceImpl implements EatAnalysisService {

	@Autowired
	private DietAnalysisDao dietAnalysisDao;

	@Autowired
	private UserService userService;

	@Autowired
	private DietDao dietDao;

	@Autowired
	private HttpSession session;

	@Override
	public EatModeAnalysisResponse analyze(Long userId, LocalDate startDate, LocalDate endDate) {

		EatModeAnalysisResponse response = new EatModeAnalysisResponse();

		// 1. 사용자 정보 조회 (성별/나이/키/몸무게)
		UserDto user = userService.getUserById(userId);

		int goalKcal = calcGoalKcal(user);

		// 2. 기간 내 일별 집계 (칼로리/탄단지 + 야식/간식 카운트 포함)
		List<DailyChartData> dailyList = dietAnalysisDao.selectEatDailyChart(userId, startDate, endDate);

		response.setDailyChart(dailyList);

		// 데이터 없으면 기본 요약 정보만
		if (dailyList == null || dailyList.isEmpty()) {
			EatPeriodSummary emptySummary = new EatPeriodSummary();
			emptySummary.setStartDate(startDate.toString());
			emptySummary.setEndDate(endDate.toString());
			emptySummary.setGoalKcal(goalKcal);
			emptySummary.setTotalKcal(0.0);
			emptySummary.setAvgKcal(0.0);
			emptySummary.setCarbRatio(0.0);
			emptySummary.setProteinRatio(0.0);
			emptySummary.setFatRatio(0.0);
			emptySummary.setKcalDiff(0.0);
			emptySummary.setKcalDiffRatio(0.0);

			response.setPeriodSummary(emptySummary);

			EatPatternSummary emptyPattern = new EatPatternSummary();
			emptyPattern.setNightCount(0);
			emptyPattern.setSnackCount(0);
			response.setPattern(emptyPattern);

			ScoreSummary score = new ScoreSummary();
			score.setValue(0);
			score.setReasons(List.of("선택한 기간에 기록된 식단이 없습니다."));
			score.setNextAdvice(List.of("식사 기록을 남겨야 더 정확한 분석이 가능해요."));
			response.setScore(score);

			return response;
		}

		// 3. 기간 요약(총 kcal, 평균 kcal, 탄단지 비율)
		double sumKcal = 0.0;
		double sumCarb = 0.0;
		double sumProtein = 0.0;
		double sumFat = 0.0;

		int totalNightCount = 0;
		int totalSnackCount = 0;

		for (DailyChartData d : dailyList) {
			sumKcal += d.getTotalKcal();
			sumCarb += d.getTotalCarb();
			sumProtein += d.getTotalProtein();
			sumFat += d.getTotalFat();

			totalNightCount += d.getNightCount();
			totalSnackCount += d.getSnackCount();
		}

		int days = dailyList.size();
		double avgKcal = days > 0 ? sumKcal / days : 0.0;

		double totalEnergy = (sumKcal > 0 ? sumKcal : 1); // 0 나누기 방지

		double carbRatio = (sumCarb * 4.0) / totalEnergy;
		double proteinRatio = (sumProtein * 4.0) / totalEnergy;
		double fatRatio = (sumFat * 9.0) / totalEnergy;

		EatPeriodSummary summary = new EatPeriodSummary();
		summary.setStartDate(startDate.toString());
		summary.setEndDate(endDate.toString());
		summary.setTotalKcal(sumKcal);
		summary.setAvgKcal(avgKcal);
		summary.setCarbRatio(carbRatio);
		summary.setProteinRatio(proteinRatio);
		summary.setFatRatio(fatRatio);
		summary.setGoalKcal(goalKcal);

		double kcalDiff = avgKcal - goalKcal; // + 과다 / - 부족
		double diffRatio = goalKcal > 0 ? Math.abs(kcalDiff) / goalKcal : 0.0;

		summary.setKcalDiff(kcalDiff);
		summary.setKcalDiffRatio(diffRatio);

		response.setPeriodSummary(summary);

		// 4. 패턴 요약 (야식/간식 횟수)
		EatPatternSummary pattern = new EatPatternSummary();
		pattern.setNightCount(totalNightCount);
		pattern.setSnackCount(totalSnackCount);
		response.setPattern(pattern);

		// 5. 점수 계산
		ScoreSummary score = calcScore(summary, pattern);
		response.setScore(score);

		return response;
	}

	/**
	 * 🔥 키/몸무게/성별/나이 기반 목표 칼로리 계산 - Mifflin–St Jeor + 활동지수 1.375 (가벼운 활동) 기준
	 */
	private int calcGoalKcal(UserDto user) {
		if (user == null) {
			// 기본값: 1800
			return 1800;
		}

		Double height = user.getHeight(); // cm
		Double weight = user.getWeight(); // kg
		Integer ageObj = user.getAge();
		String gender = user.getGender();

		if (height == null || weight == null) {
			return 1800; // 정보가 없으면 기본값
		}

		int age = (ageObj != null ? ageObj : 30);

		double bmr;
		if ("M".equalsIgnoreCase(gender)) {
			bmr = 10 * weight + 6.25 * height - 5 * age + 5;
		} else {
			// gender 없음/기타 → 여성 공식으로
			bmr = 10 * weight + 6.25 * height - 5 * age - 161;
		}

		double activityFactor = 1.375; // 가벼운 활동 기준
		double tdee = bmr * activityFactor;

		return (int) Math.round(tdee);
	}

	/**
	 * 🔥 점수 계산 로직
	 */
	private ScoreSummary calcScore(EatPeriodSummary summary, EatPatternSummary pattern) {
		int score = 100;
		List<String> reasons = new ArrayList<>();
		List<String> nextAdvice = new ArrayList<>();

		double avgKcal = summary.getAvgKcal();
		int goalKcal = summary.getGoalKcal();

		double kcalDiff = summary.getKcalDiff(); // + 과다 / - 부족
		double overRatio = (goalKcal > 0 && kcalDiff > 0) ? (kcalDiff / goalKcal) : 0.0;
		double underRatio = (goalKcal > 0 && kcalDiff < 0) ? (-kcalDiff / goalKcal) : 0.0;

		double carbRatio = summary.getCarbRatio();
		double proteinRatio = summary.getProteinRatio();
		double fatRatio = summary.getFatRatio();

		int nightCount = pattern.getNightCount();
		int snackCount = pattern.getSnackCount();

		// ①-1 목표 칼로리 과다 섭취 (20% 이상 초과)
		if (goalKcal > 0 && overRatio >= 0.2) {
			score -= 15;
			reasons.add("목표 칼로리보다 20% 이상 많이 섭취했습니다.");
			nextAdvice.add("고칼로리 간식/야식을 줄이고, 저열량 식단 비중을 높여보세요.");
		}

		// ①-2 목표 칼로리 부족 섭취 (20% 이상 부족)
		if (goalKcal > 0 && underRatio >= 0.2) {
			score -= 5; // 부족은 과다보다 패널티를 조금만
			reasons.add("목표 칼로리보다 섭취량이 많이 부족합니다.");
			nextAdvice.add("끼니를 거르지 말고 단백질 위주의 식사를 한 끼 더 추가해보세요.");
		}

		// ② 단백질 비율 낮음
		if (proteinRatio < 0.15) {
			score -= 10;
			reasons.add("단백질 비율이 다소 낮습니다.");
			nextAdvice.add("닭가슴살, 두부, 계란 등 단백질 식품을 한 끼에 한 가지 이상 넣어보세요.");
		}

		// ③ 탄수화물 과다
		if (carbRatio > 0.65) {
			score -= 8;
			reasons.add("탄수화물 비율이 높습니다.");
			nextAdvice.add("흰쌀밥/빵 대신 현미, 채소, 단백질 위주로 비율을 조정해보세요.");
		}

		// ④ 지방 과다
		if (fatRatio > 0.35) {
			score -= 8;
			reasons.add("지방 비율이 높은 편입니다.");
			nextAdvice.add("튀김류, 버터/크림류를 줄이고, 구이/찜 조리법을 늘려보세요.");
		}

		// ⑤ 야식/간식 패턴
		int totalSnackLike = nightCount + snackCount;
		if (totalSnackLike >= 5) {
			score -= 10;
			reasons.add("야식/간식 빈도가 잦습니다.");
			nextAdvice.add("주 3회 이하로 야식/간식을 제한해보는 건 어떨까요?");
		} else if (totalSnackLike >= 2) {
			score -= 5;
			reasons.add("야식/간식 빈도가 다소 높은 편입니다.");
		}

		if (score < 0)
			score = 0;
		if (score > 100)
			score = 100;

		ScoreSummary result = new ScoreSummary();
		result.setValue(score);
		result.setReasons(reasons);
		result.setNextAdvice(nextAdvice);

		return result;
	}

	@Override
	public EatModeAnalysisResponse getEatModeAnalysis(String start, String end) {

		LocalDate startDate = LocalDate.parse(start);
		LocalDate endDate = LocalDate.parse(end);

		UserDto loginUser = (UserDto) session.getAttribute("loginUser");

		if (loginUser == null) {
			throw new RuntimeException("로그인이 필요합니다.");
		}

		Long userId = loginUser.getUserId();

		return analyze(userId, startDate, endDate);
	}

	@Override
	public List<FoodDto> getRecommendedFoods(Long userId, LocalDate startDate, LocalDate endDate) {
		return dietDao.selectRecommendedFoods(userId, startDate.toString(), endDate.toString());
	}

}
