package com.medeat.gamification.service.impl;

import com.medeat.gamification.dao.GamificationDao;
import com.medeat.gamification.dto.EarnXpResult;
import com.medeat.gamification.dto.ProgressResponse;
import com.medeat.gamification.model.ActionType;
import com.medeat.gamification.model.LevelPolicy;
import com.medeat.gamification.service.GamificationService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.medeat.notification.feed.dto.NotificationFeedType;
import com.medeat.notification.feed.service.NotificationFeedService;


import java.time.LocalDate;
import java.util.Map;

@Service
public class GamificationServiceImpl implements GamificationService {

    private final GamificationDao dao;

    public GamificationServiceImpl(GamificationDao dao, NotificationFeedService notificationFeedService) {
        this.dao = dao;
        this.notificationFeedService = notificationFeedService;
    }

    private static final int DAILY_CAP = 60;
    private static final int MASTER_CYCLE = 500;
    private final NotificationFeedService notificationFeedService;


    @Override
    public ProgressResponse getProgress(long userId, LocalDate day) {
        String d = day.toString();

        Map<String, Object> pr = dao.selectUserProgress(userId);
        int totalXp = 0, level = 1, masterXp = 0;
        if (pr != null && !pr.isEmpty()) {
            totalXp = toInt(pr.get("total_xp"));
            level = toInt(pr.get("level"));
            masterXp = toInt(pr.get("master_xp"));
        }

        // ✅ 안전: DB level이 틀릴 수 있으니 totalXp로 재계산해서 정합성 맞춤
        int calcLevel = LevelPolicy.calcLevel(totalXp);
        if (calcLevel != level) {
            level = calcLevel;
            // (선택) 여기서 DB level도 맞춰두고 싶으면 upsertUserProgress(0, level, 0) 같은 별도 쿼리가 필요함
            // 지금은 read 응답 정합성만 보장
        }

        Integer tx = dao.selectTodayXp(userId, d);
        int todayXp = tx == null ? 0 : tx;

        Map<String, Object> st = dao.selectUserStreak(userId);
        int currentStreak = 0;
        String lastActionDate = null;
        if (st != null && !st.isEmpty()) {
            currentStreak = toInt(st.get("current_streak"));
            Object lad = st.get("last_action_date");
            lastActionDate = lad == null ? null : String.valueOf(lad);
        }

        // ✅ 등급표 기준 계산 (누적 컷)
        int nextLevelXp = LevelPolicy.nextLevelXp(level);
        int levelStartXp = LevelPolicy.nextLevelXp(level - 1); // Lv4면 Lv3 컷=800? -> 주의: nextLevelXp(level-1)=현재레벨 시작컷이 아님
        // 위 라인은 논리상 틀릴 수 있어서 아래처럼 명시적으로 계산해야 함

        // ✅ 현재 레벨 시작 누적컷을 정확히 계산
        int startXp;
        switch (level) {
            case 1: startXp = 0; break;
            case 2: startXp = 150; break;
            case 3: startXp = 400; break;
            case 4: startXp = 800; break;
            case 5: startXp = 1400; break;
            case 6: startXp = 2200; break;
            case 7: startXp = 3200; break;
            default: startXp = 0; break;
        }

        int cap = Math.max(1, nextLevelXp - startXp);              // 예: 1400-800=600
        int inLevel = Math.max(0, totalXp - startXp);              // 예: 800이면 0
        int remain = Math.max(0, nextLevelXp - totalXp);           // 예: 1400-800=600

        ProgressResponse res = new ProgressResponse();
        res.setLevel(level);
        res.setTotalXp(totalXp);
        res.setNextLevelXp(nextLevelXp);

        res.setTodayXp(todayXp);
        res.setDailyCap(DAILY_CAP);

        res.setMasterXp(masterXp);
        res.setMasterCycle(MASTER_CYCLE);
        int percent = (MASTER_CYCLE <= 0) ? 0 : (int) Math.floor(((masterXp % MASTER_CYCLE) * 100.0) / MASTER_CYCLE);
        res.setMasterPercent(percent);

        res.setCurrentStreak(currentStreak);
        res.setLastActionDate(lastActionDate);

        return res;
    }

    @Override
    @Transactional
    public EarnXpResult earnXp(long userId, ActionType actionType, String refId, int requestedXp, LocalDate day) {
        String d = day.toString();

        // ✅ 1) 서버 룰로 XP 고정
        int ruleXp = xpByAction(actionType);
        if (ruleXp <= 0) {
            return buildResult(0, 0, false, false, safeTodayXp(userId, d));
        }

        // ✅ 2) ActionType별 refId 전략 강제(하루1회는 day로 강제)
        String finalRefId = normalizeRefId(actionType, refId, d);

        // ✅ 3) COMMENT_CREATE 하루 3회 제한
        if (actionType == ActionType.COMMENT_CREATE) {
            int cnt = dao.countXpLogByActionDate(userId, actionType.name(), d);
            if (cnt >= 3) {
                // 지급 스킵 (중복/캡이 아니라 "횟수 제한")
                return buildResult(ruleXp, 0, false, false, safeTodayXp(userId, d));
            }
        }

        // ✅ 4) 일일 캡 적용
        int todayXp = safeTodayXp(userId, d);
        int remaining = Math.max(0, DAILY_CAP - todayXp);
        int grant = Math.min(ruleXp, remaining);

        if (grant <= 0) {
            return buildResult(ruleXp, 0, false, true, todayXp);
        }

        // ✅ 5) xp_log 먼저 (중복 방지)
        try {
            dao.insertXpLog(userId, actionType.name(), finalRefId, grant, d);
        } catch (DuplicateKeyException dup) {
            return buildResult(ruleXp, 0, true, false, todayXp);
        }

        // ✅ 6) daily_xp 누적
        dao.upsertDailyXp(userId, d, grant);

        // ✅ 7) user_progress 업데이트 + 레벨/마스터XP
        Map<String, Object> pr = dao.selectUserProgress(userId);

        int currentTotal = 0;
        int currentLevel = 1; // ⭐ 기본값 중요

        if (pr != null && !pr.isEmpty()) {
            currentTotal = toInt(pr.get("total_xp"));
            currentLevel = toInt(pr.get("level"));
        }

        int newTotal = currentTotal + grant;
        int newLevel = LevelPolicy.calcLevel(newTotal);
        int deltaMaster = (newLevel >= 6) ? grant : 0;

        dao.upsertUserProgress(userId, grant, newLevel, deltaMaster);

        // ✅ 레벨업 알림
        if (newLevel > currentLevel) {
            notificationFeedService.notify(
                userId,
                NotificationFeedType.LEVEL_UP,
                "USER",
                userId
            );
        }


        // ✅ 8) 스트릭 갱신 + 보너스 (보너스 지급 자체는 별도 xp_log insert)
        // STREAK_BONUS 자체 지급에서는 스트릭 갱신을 다시 하면 안 됨
        if (actionType != ActionType.STREAK_BONUS) {
            applyStreakAndBonusIfNeeded(userId, day);
        }

        int newTodayXp = safeTodayXp(userId, d);
        boolean capped = (grant < ruleXp);

        return buildResult(ruleXp, grant, false, capped, newTodayXp);
    }

    private int xpByAction(ActionType actionType) {
        switch (actionType) {
            case MEAL_LOG: return 10;
            case MEAL_BONUS: return 3;
            case DAY_2MEALS: return 5;

            case CHALLENGE_SUCCESS: return 15;

            case PDF_DOWNLOAD: return 10;

            case POST_CREATE: return 5;
            case COMMENT_CREATE: return 2;

            case STREAK_BONUS:
                // streakBonus는 applyStreakAndBonusIfNeeded에서 계산/지급하므로
                // 외부에서 STREAK_BONUS로 earnXp 호출하는 경우는 0 처리(정책상 미사용)
                return 0;

            default:
                return 0;
        }
    }

    private String normalizeRefId(ActionType actionType, String refId, String dayString) {
        // NOT NULL 설계라 공백이면 기본값
        String safe = (refId == null || refId.trim().isEmpty()) ? "ONCE" : refId.trim();

        // 하루 1회형은 refId를 날짜로 강제해서 UNIQUE로 하루 1회 보장
        switch (actionType) {
            case DAY_2MEALS:
            case CHALLENGE_SUCCESS:
            case PDF_DOWNLOAD:
            case POST_CREATE:
                return dayString;

            default:
                // 끼니당 1회/댓글 등은 호출자가 준 refId(=dietId/commentId)를 사용
                return safe;
        }
    }

    private void applyStreakAndBonusIfNeeded(long userId, LocalDate day) {
        Map<String, Object> st = dao.selectUserStreak(userId);
        int currentStreak = 0;
        LocalDate lastDate = null;

        if (st != null && !st.isEmpty()) {
            currentStreak = toInt(st.get("current_streak"));
            Object lad = st.get("last_action_date");
            if (lad != null) lastDate = LocalDate.parse(String.valueOf(lad));
        }

        // 오늘 이미 인정됨
        if (lastDate != null && lastDate.isEqual(day)) {
            return;
        }

        // 연속 여부 판단
        int newStreak;
        if (lastDate != null && lastDate.plusDays(1).isEqual(day)) newStreak = currentStreak + 1;
        else newStreak = 1;

        dao.upsertUserStreak(userId, newStreak, day.toString());

        // 스트릭 보너스 지급
        int bonus = LevelPolicy.streakBonus(newStreak);
        if (bonus <= 0) return;

        // 일일 캡 적용
        String d = day.toString();
        int todayXp = safeTodayXp(userId, d);
        int remaining = Math.max(0, DAILY_CAP - todayXp);
        int grant = Math.min(bonus, remaining);
        if (grant <= 0) return;
        // 중복 방지: (user, STREAK_BONUS, day, refId=day)
        try {
            dao.insertXpLog(userId, ActionType.STREAK_BONUS.name(), d, grant, d);
        } catch (DuplicateKeyException dup) {
            return;
        }

        dao.upsertDailyXp(userId, d, grant);

        Map<String, Object> pr = dao.selectUserProgress(userId);
        int currentTotal = 0;
        if (pr != null && !pr.isEmpty()) {
            currentTotal = toInt(pr.get("total_xp"));
        }
        int newTotal = currentTotal + grant;
        int newLevel = LevelPolicy.calcLevel(newTotal);
        int deltaMaster = (newLevel >= 6) ? grant : 0;
        dao.upsertUserProgress(userId, grant, newLevel, deltaMaster);
    }

    private EarnXpResult buildResult(int requestedXp, int grantedXp, boolean duplicated, boolean capped, int todayXp) {
        EarnXpResult r = new EarnXpResult();
        r.setRequestedXp(requestedXp);
        r.setGrantedXp(grantedXp);
        r.setDuplicated(duplicated);
        r.setCapped(capped);
        r.setTodayXp(todayXp);
        r.setDailyCap(DAILY_CAP);
        return r;
    }

    private int safeTodayXp(long userId, String day) {
        Integer v = dao.selectTodayXp(userId, day);
        return v == null ? 0 : v;
    }

    private int toInt(Object o) {
        if (o == null) return 0;
        if (o instanceof Number) return ((Number) o).intValue();
        try { return Integer.parseInt(String.valueOf(o)); }
        catch (Exception e) { return 0; }
    }
}