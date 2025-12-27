package com.medeat.diet.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.medeat.auth.dto.UserDto;
import com.medeat.dashboard.dto.EatModeAnalysisResponse;
import com.medeat.dashboard.dto.MedModeAnalysisResponse;
import com.medeat.diet.dto.DietLogDto;
import com.medeat.diet.dto.RecommendationResponse;
import com.medeat.diet.service.DietRecommendationService;
import com.medeat.diet.service.DietService;
import com.medeat.diet.service.EatAnalysisService;
import com.medeat.medical.service.MedAnalysisService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/diet")
public class DietRestController {

    @Autowired
    private DietService dietService;

    @Autowired
    @Qualifier("eatAnalysisServiceImpl")
    private EatAnalysisService itAnalysisService;

    @Autowired
    private MedAnalysisService medAnalysisService;

    @Autowired
    private DietRecommendationService dietRecommendationService;

    /** ****************************************
     * 1. 날짜별 식단 목록 조회
     *    GET /api/diet?date=YYYY-MM-DD
     ******************************************/
    @GetMapping("")
    public ResponseEntity<?> list(
            @RequestParam(required = false) String date,
            HttpSession session) {

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        if (date == null || date.isBlank()) {
            date = LocalDate.now().toString();
        }

        List<DietLogDto> list = dietService.getDietList(loginUser.getUserId(), date);
        return ResponseEntity.ok(list);
    }


    /** ****************************************
     * 2. 단일 식단 조회 (수정/상세 보기 둘 다 사용)
     *    GET /api/diet/{dietId}
     ******************************************/
    @GetMapping("/{dietId}")
    public ResponseEntity<?> detail(
            @PathVariable Long dietId,
            HttpSession session) {

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        DietLogDto log = dietService.getDietDetail(dietId, loginUser.getUserId());
        return ResponseEntity.ok(log);
    }


    /** ****************************************
     * 3. 신규 등록 (POST /api/diet)
     ******************************************/
    @PostMapping("")
    public ResponseEntity<?> save(
            @RequestPart("diet") DietLogDto dto,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            HttpSession session) throws IOException {

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        dto.setUserId(loginUser.getUserId());

        // 사진 업로드 처리
        if (photo != null && !photo.isEmpty()) {

            String uploadDir = "C:/medeat/upload/diet/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
            File dest = new File(dir, fileName);
            photo.transferTo(dest);

            dto.setPhotoPath("/uploads/diet/" + fileName);
        }

        dietService.saveDiet(dto);   // 신규
        return ResponseEntity.ok("저장 완료");
    }


    /** ****************************************
     * 4. 수정 (PUT /api/diet/{dietId})
     ******************************************/
    @PutMapping("/{dietId}")
    public ResponseEntity<?> update(
            @PathVariable Long dietId,
            @RequestPart("diet") DietLogDto dto,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            HttpSession session) throws IOException {

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        dto.setDietId(dietId);
        dto.setUserId(loginUser.getUserId());

        // 사진 업로드
        if (photo != null && !photo.isEmpty()) {

            String uploadDir = "C:/medeat/upload/diet/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
            File dest = new File(dir, fileName);
            photo.transferTo(dest);

            dto.setPhotoPath("/uploads/diet/" + fileName);
        }

        dietService.saveDiet(dto);
        return ResponseEntity.ok("수정 완료");
    }


    /** ****************************************
     * 5. 식단 삭제 (DELETE /api/diet/{dietId})
     ******************************************/
    @DeleteMapping("/{dietId}")
    public ResponseEntity<?> delete(
            @PathVariable Long dietId,
            HttpSession session) {

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        dietService.deleteDiet(dietId, loginUser.getUserId());
        return ResponseEntity.ok("삭제 완료");
    }


    /** ****************************************
     * 6. 음식 1개 삭제 (DELETE /api/diet/item/{itemId})
     ******************************************/
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<?> deleteItem(
            @PathVariable Long itemId,
            HttpSession session) {

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        Long dietId = dietService.deleteItem(itemId, loginUser.getUserId());

        // 삭제 후 자동 재계산
        dietService.recalcTotals(dietId, loginUser.getUserId());
        return ResponseEntity.ok("삭제 완료");
    }


    /** ****************************************
     * 7. 달력용 한달 식단 요약
     *    GET /api/diet/calendar?month=2025-11
     ******************************************/
    @GetMapping("/calendar")
    public ResponseEntity<?> calendar(
            @RequestParam String month,
            HttpSession session) {

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        java.time.YearMonth ym = java.time.YearMonth.parse(month);
        String startDate = ym.atDay(1).toString();
        String endDate = ym.atEndOfMonth().toString();

        List<DietLogDto> logs =
                dietService.getCalendarLogs(
                        loginUser.getUserId(),
                        startDate,
                        endDate
                );

        java.util.Map<String, java.util.Set<String>> map = new java.util.LinkedHashMap<>();

        for (DietLogDto log : logs) {
            String date = log.getLogDate();
            String meal = log.getMealTime();
            map.computeIfAbsent(date, k -> new java.util.LinkedHashSet<>()).add(meal);
        }

        java.util.List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        for (var entry : map.entrySet()) {
            java.util.Map<String, Object> row = new java.util.HashMap<>();
            row.put("date", entry.getKey());
            row.put("meals", entry.getValue());
            result.add(row);
        }

        return ResponseEntity.ok(result);
    }


    /** ****************************************
     * 8. 잇모드 식단 분석
     *    GET /api/diet/analysis/eat?startDate=...&endDate=...
     ******************************************/
    @GetMapping("/analysis/eat")
    public ResponseEntity<?> analyzeIt(
            @RequestParam String startDate,
            @RequestParam String endDate,
            HttpSession session) {

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        Long userId = loginUser.getUserId();

        LocalDate s = LocalDate.parse(startDate);
        LocalDate e = LocalDate.parse(endDate);

        EatModeAnalysisResponse result = itAnalysisService.analyze(userId, s, e);

        return ResponseEntity.ok(result);
    }


    /** ****************************************
     * 9. 메딧모드 식단·약물 분석
     *    GET /api/diet/analysis/med?startDate=...&endDate=...
     ******************************************/
    @GetMapping("/analysis/med")
    public ResponseEntity<?> analyzeMed(
            @RequestParam String startDate,
            @RequestParam String endDate,
            HttpSession session) {

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        Long userId = loginUser.getUserId();
        LocalDate s = LocalDate.parse(startDate);
        LocalDate e = LocalDate.parse(endDate);

        MedModeAnalysisResponse result = medAnalysisService.analyze(userId, s, e);
        return ResponseEntity.ok(result);
    }


    /** ****************************************
     * 10. 식단 추천
     *     GET /api/diet/recommend?userId=1&date=YYYY-MM-DD
     ******************************************/
    @GetMapping("/recommend")
    public ResponseEntity<?> recommend(
            @RequestParam("userId") Long userId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpSession session) {

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // ★ 원하면 아래 주석 풀어서 세션 user와 userId 일치 여부도 체크 가능
        // if (!loginUser.getUserId().equals(userId)) {
        //     return ResponseEntity.status(403).body("잘못된 접근입니다.");
        // }

        RecommendationResponse res = dietRecommendationService.recommendAll(userId, date);
        return ResponseEntity.ok(res);
    }
}
