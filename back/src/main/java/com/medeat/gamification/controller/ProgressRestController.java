package com.medeat.gamification.controller;

import com.medeat.gamification.dto.EarnXpRequest;
import com.medeat.gamification.dto.EarnXpResult;
import com.medeat.gamification.dto.ProgressResponse;
import com.medeat.gamification.model.ActionType;
import com.medeat.gamification.service.GamificationService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/progress")
public class ProgressRestController {

    private final GamificationService service;

    public ProgressRestController(GamificationService service) {
        this.service = service;
    }

    /**
     * 진행상태 조회
     * GET /api/progress?userId=1
     */
    @GetMapping
    public ProgressResponse getProgress(@RequestParam("userId") long userId,
                                        @RequestParam(value = "date", required = false) String date) {
        LocalDate d = (date == null || date.trim().isEmpty()) ? LocalDate.now() : LocalDate.parse(date);
        return service.getProgress(userId, d);
    }

    /**
     * 개발/테스트용 XP 지급 API
     * 실제 운영에서는 식단 등록/챌린지 성공/다운로드 등 "해당 기능 API 내부에서 service.earnXp()" 호출하는 형태로 바꾸면 됨.
     *
     * POST /api/progress/earn
     * body: { "userId":1, "actionType":"MEAL_LOG", "refId":"2025-12-17:BREAKFAST", "xp":10, "date":"2025-12-17" }
     */
    @PostMapping("/earn")
    public EarnXpResult earn(@RequestBody EarnXpRequest req) {
        LocalDate d = (req.getDate() == null || req.getDate().trim().isEmpty())
                ? LocalDate.now()
                : LocalDate.parse(req.getDate());

        ActionType at = ActionType.valueOf(req.getActionType());
        return service.earnXp(req.getUserId(), at, req.getRefId(), req.getXp(), d);
    }
}
