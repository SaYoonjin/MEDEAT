package com.medeat.diet.controller;

import com.medeat.auth.dto.UserDto;
import com.medeat.common.file.FileStorageService;
import com.medeat.common.web.SessionUserSupport;
import com.medeat.dashboard.dto.EatModeAnalysisResponse;
import com.medeat.dashboard.dto.MedModeAnalysisResponse;
import com.medeat.diet.dto.DietLogDto;
import com.medeat.diet.dto.RecommendationResponse;
import com.medeat.diet.service.DietRecommendationService;
import com.medeat.diet.service.DietService;
import com.medeat.diet.service.EatAnalysisService;
import com.medeat.medical.service.MedAnalysisService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/diet")
public class DietRestController {

    private final DietService dietService;
    private final EatAnalysisService eatAnalysisService;
    private final MedAnalysisService medAnalysisService;
    private final DietRecommendationService dietRecommendationService;
    private final SessionUserSupport sessionUserSupport;
    private final FileStorageService fileStorageService;

    public DietRestController(
            DietService dietService,
            @Qualifier("eatAnalysisServiceImpl") EatAnalysisService eatAnalysisService,
            MedAnalysisService medAnalysisService,
            DietRecommendationService dietRecommendationService,
            SessionUserSupport sessionUserSupport,
            FileStorageService fileStorageService
    ) {
        this.dietService = dietService;
        this.eatAnalysisService = eatAnalysisService;
        this.medAnalysisService = medAnalysisService;
        this.dietRecommendationService = dietRecommendationService;
        this.sessionUserSupport = sessionUserSupport;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("")
    public ResponseEntity<List<DietLogDto>> list(@RequestParam(required = false) String date, HttpSession session) {
        UserDto loginUser = sessionUserSupport.getRequiredUser(session);
        String targetDate = (date == null || date.isBlank()) ? LocalDate.now().toString() : date;
        return ResponseEntity.ok(dietService.getDietList(loginUser.getUserId(), targetDate));
    }

    @GetMapping("/{dietId}")
    public ResponseEntity<DietLogDto> detail(@PathVariable Long dietId, HttpSession session) {
        UserDto loginUser = sessionUserSupport.getRequiredUser(session);
        return ResponseEntity.ok(dietService.getDietDetail(dietId, loginUser.getUserId()));
    }

    @PostMapping("")
    public ResponseEntity<Map<String, String>> save(
            @RequestPart("diet") DietLogDto dto,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            HttpSession session
    ) throws IOException {
        UserDto loginUser = sessionUserSupport.getRequiredUser(session);
        dto.setUserId(loginUser.getUserId());

        String fileName = fileStorageService.store(photo, "diet");
        if (fileName != null) {
            dto.setPhotoPath("/uploads/diet/" + fileName);
        }

        dietService.saveDiet(dto);
        return ResponseEntity.ok(Map.of("message", "식단 저장이 완료되었습니다."));
    }

    @PutMapping("/{dietId}")
    public ResponseEntity<Map<String, String>> update(
            @PathVariable Long dietId,
            @RequestPart("diet") DietLogDto dto,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            HttpSession session
    ) throws IOException {
        UserDto loginUser = sessionUserSupport.getRequiredUser(session);
        dto.setDietId(dietId);
        dto.setUserId(loginUser.getUserId());

        String fileName = fileStorageService.store(photo, "diet");
        if (fileName != null) {
            dto.setPhotoPath("/uploads/diet/" + fileName);
        }

        dietService.saveDiet(dto);
        return ResponseEntity.ok(Map.of("message", "식단 수정이 완료되었습니다."));
    }

    @DeleteMapping("/{dietId}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long dietId, HttpSession session) {
        UserDto loginUser = sessionUserSupport.getRequiredUser(session);
        dietService.deleteDiet(dietId, loginUser.getUserId());
        return ResponseEntity.ok(Map.of("message", "식단 삭제가 완료되었습니다."));
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Map<String, String>> deleteItem(@PathVariable Long itemId, HttpSession session) {
        UserDto loginUser = sessionUserSupport.getRequiredUser(session);
        Long dietId = dietService.deleteItem(itemId, loginUser.getUserId());
        dietService.recalcTotals(dietId, loginUser.getUserId());
        return ResponseEntity.ok(Map.of("message", "식단 항목 삭제가 완료되었습니다."));
    }

    @GetMapping("/calendar")
    public ResponseEntity<List<Map<String, Object>>> calendar(@RequestParam String month, HttpSession session) {
        UserDto loginUser = sessionUserSupport.getRequiredUser(session);
        YearMonth ym = YearMonth.parse(month);
        String startDate = ym.atDay(1).toString();
        String endDate = ym.atEndOfMonth().toString();

        List<DietLogDto> logs = dietService.getCalendarLogs(loginUser.getUserId(), startDate, endDate);
        Map<String, Set<String>> mealsByDate = new LinkedHashMap<>();
        for (DietLogDto log : logs) {
            mealsByDate.computeIfAbsent(log.getLogDate(), key -> new LinkedHashSet<>()).add(log.getMealTime());
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : mealsByDate.entrySet()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("date", entry.getKey());
            row.put("meals", entry.getValue());
            result.add(row);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/analysis/eat")
    public ResponseEntity<EatModeAnalysisResponse> analyzeIt(
            @RequestParam String startDate,
            @RequestParam String endDate,
            HttpSession session
    ) {
        Long userId = sessionUserSupport.getRequiredUser(session).getUserId();
        return ResponseEntity.ok(eatAnalysisService.analyze(userId, LocalDate.parse(startDate), LocalDate.parse(endDate)));
    }

    @GetMapping("/analysis/med")
    public ResponseEntity<MedModeAnalysisResponse> analyzeMed(
            @RequestParam String startDate,
            @RequestParam String endDate,
            HttpSession session
    ) {
        Long userId = sessionUserSupport.getRequiredUser(session).getUserId();
        return ResponseEntity.ok(medAnalysisService.analyze(userId, LocalDate.parse(startDate), LocalDate.parse(endDate)));
    }

    @GetMapping("/recommend")
    public ResponseEntity<RecommendationResponse> recommend(
            @RequestParam("userId") Long userId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpSession session
    ) {
        sessionUserSupport.getRequiredUser(session);
        return ResponseEntity.ok(dietRecommendationService.recommendAll(userId, date));
    }
}
