package com.medeat.diet.service;

import java.time.LocalDate;

import com.medeat.diet.dto.RecommendationResponse;

public interface DietRecommendationService {

    /**
     * 사용자 ID + 특정 날짜 기준으로
     * 1) 남은 칼로리 기반 추천
     * 2) 체중/체력 목표 기반 식단 추천
     * 3) 고칼로리 → 저칼로리 대체 음식 추천
     * 4) 탄단지 불균형 보정 추천
     * 5) 건강한 기본 음식 추천
     * 6) 조리법 변경 추천
     * 을 모두 합친 통합 추천을 반환합니다.
     */
    RecommendationResponse recommendAll(Long userId, LocalDate date);
}