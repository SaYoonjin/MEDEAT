package com.medeat.gamification.model;

public enum ActionType {
    // Diet
    MEAL_LOG,        // 끼니 기록(+10)
    MEAL_BONUS,      // 음식3+ or 메모/사진(+3)
    DAY_2MEALS,      // 하루 2끼 이상(+5)

    // Challenge
    CHALLENGE_SUCCESS, // 오늘 성공(+15)

    // Report
    PDF_DOWNLOAD,    // 하루 1회(+10)

    // Community
    POST_CREATE,     // 하루 1회(+5)
    COMMENT_CREATE,  // 하루 3회(+2)

    // Streak bonus (자동 계산)
    STREAK_BONUS
}
