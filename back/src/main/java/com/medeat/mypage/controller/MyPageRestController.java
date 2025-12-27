package com.medeat.mypage.controller;

import java.util.List;

import com.medeat.auth.dto.UserDto;
import com.medeat.medical.dto.DiseaseDto;
import com.medeat.medical.dto.MedicationDto;
import com.medeat.medical.service.DiseaseService;
import com.medeat.medical.service.MedicationService;
import com.medeat.mypage.service.MyPageService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
public class MyPageRestController {

    @Autowired
    private MedicationService medicationService;

    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private MyPageService myPageService;

    /** ****************************************
     * 1. 나의 의학 정보 조회
     ******************************************/
    @GetMapping("/medical")
    public ResponseEntity<?> myMedical(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        Long userId = user.getUserId();

        return ResponseEntity.ok(
            new Object() {
                public List<MedicationDto> medications = medicationService.getMedicationList(userId);
                public List<DiseaseDto> diseases = diseaseService.getUserDiseaseList(userId);
            }
        );
    }

    /** ****************************************
     * 2. 개인정보 조회 (마이페이지)
     ******************************************/
    @GetMapping("/info")
    public ResponseEntity<?> myInfo(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        return ResponseEntity.ok(myPageService.getUser(user.getUserId()));
    }

    /** ****************************************
     * 3. 개인정보 수정
     *  - ✅ userId는 세션 기준으로 강제 (프론트 값 신뢰 X)
     ******************************************/
    @PutMapping("/info")
    public ResponseEntity<?> updateInfo(@RequestBody UserDto updatedUser, HttpSession session) {

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        // ✅ 보안: 세션 userId로 강제
        updatedUser.setUserId(loginUser.getUserId());

        myPageService.updateUser(updatedUser);

        // ✅ 세션 반영 (최신값 다시 조회)
        session.setAttribute("loginUser", myPageService.getUser(loginUser.getUserId()));

        return ResponseEntity.ok("수정 완료");
    }

    /** ****************************************
     * 4. 회원 탈퇴
     *  - ✅ 세션 userId로 삭제
     ******************************************/
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount(HttpSession session) {

        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        myPageService.deleteUser(user.getUserId());
        session.invalidate();

        return ResponseEntity.ok("회원 탈퇴 완료");
    }

    /** ****************************************
     * 5. 내 게시글 목록
     ******************************************/
    @GetMapping("/posts")
    public ResponseEntity<?> myPosts(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        return ResponseEntity.ok(myPageService.getMyPosts(user.getUserId()));
    }

    /** ****************************************
     * 6. 내 스크랩 목록
     ******************************************/
    @GetMapping("/scraps")
    public ResponseEntity<?> myScraps(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        return ResponseEntity.ok(myPageService.getMyScraps(user.getUserId()));
    }

    /** ****************************************
     * 7. 내 좋아요 목록
     ******************************************/
    @GetMapping("/likes")
    public ResponseEntity<?> myLikes(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        return ResponseEntity.ok(myPageService.getMyLikes(user.getUserId()));
    }

    /** ****************************************
     * 8. 내가 만든 챌린지 목록
     ******************************************/
    @GetMapping("/challenges")
    public ResponseEntity<?> myChallenges(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        return ResponseEntity.ok(myPageService.getMyChallenges(user.getUserId()));
    }
}
