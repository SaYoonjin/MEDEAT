package com.medeat.gamification.service;

import com.medeat.gamification.dto.EarnXpResult;
import com.medeat.gamification.dto.ProgressResponse;
import com.medeat.gamification.model.ActionType;

import java.time.LocalDate;

public interface GamificationService {
    ProgressResponse getProgress(long userId, LocalDate day);
    EarnXpResult earnXp(long userId, ActionType actionType, String refId, int requestedXp, LocalDate day);
}
