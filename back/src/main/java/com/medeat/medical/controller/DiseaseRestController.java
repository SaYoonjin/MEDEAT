package com.medeat.medical.controller;

import com.medeat.auth.dto.UserDto;
import com.medeat.medical.dto.DiseaseDto;
import com.medeat.medical.service.DiseaseService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/disease")
public class DiseaseRestController {

    @Autowired
    private DiseaseService diseaseService;

    /** ****************************************
     * 0. 전체 질병 목록 조회 (🔥 추가)
     ******************************************/
    @GetMapping("/all")
    public ResponseEntity<?> getAllDiseases() {
        List<DiseaseDto> list = diseaseService.getAllDiseases();
        return ResponseEntity.ok(list);
    }


    /** ****************************************
     * 1. 질병 자동완성 검색
     ******************************************/
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String keyword) {
        List<DiseaseDto> result = diseaseService.searchDisease(keyword);
        return ResponseEntity.ok(result);
    }


    /** ****************************************
     * 2. 로그인한 유저 질병 목록
     ******************************************/
    @GetMapping("/user")
    public ResponseEntity<?> getUserDiseaseList(HttpSession session) {

        UserDto user = (UserDto) session.getAttribute("loginUser");

        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        List<DiseaseDto> list = diseaseService.getUserDiseaseList(user.getUserId());
        return ResponseEntity.ok(list);
    }


    /** ****************************************
     * 3. 기존 질병 추가 (검색 결과로 선택)
     ******************************************/
    @PostMapping("/add")
    public ResponseEntity<?> addUserDisease(@RequestParam Long diseaseId,
                                            HttpSession session) {

        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        Long userId = user.getUserId();

        // 이미 가지고 있으면 중복 처리
        if (diseaseService.hasDisease(userId, diseaseId)) {
            return ResponseEntity.status(409).body("이미 등록된 질병입니다.");
        }

        diseaseService.addDiseaseToUser(userId, diseaseId);
        return ResponseEntity.ok("등록 완료");
    }


    /** ****************************************
     * 4. 질병 직접 입력 → 신규 생성 + 유저에 추가
     ******************************************/
    @PostMapping("/custom")
    public ResponseEntity<?> addCustomDisease(
            @RequestParam String diseaseName,
            HttpSession session) {

        UserDto user = (UserDto) session.getAttribute("loginUser");

        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        Long diseaseId = diseaseService.findOrCreate(diseaseName);
        diseaseService.addDiseaseToUser(user.getUserId(), diseaseId);

        return ResponseEntity.ok(Map.of("message", "사용자 정의 질병 추가 완료"));
    }


    /** ****************************************
     * 5. 유저 질병 삭제
     ******************************************/
    @DeleteMapping("/{userDiseaseId}")
    public ResponseEntity<?> deleteDisease(@PathVariable Long userDiseaseId) {
        diseaseService.deleteDisease(userDiseaseId);
        return ResponseEntity.ok(Map.of("message", "삭제 완료"));
    }
}
