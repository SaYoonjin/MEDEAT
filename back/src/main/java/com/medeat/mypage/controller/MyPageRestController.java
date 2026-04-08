package com.medeat.mypage.controller;

import com.medeat.auth.dto.UserDto;
import com.medeat.common.web.SessionUserSupport;
import com.medeat.medical.dto.DiseaseDto;
import com.medeat.medical.dto.MedicationDto;
import com.medeat.medical.service.DiseaseService;
import com.medeat.medical.service.MedicationService;
import com.medeat.mypage.service.MyPageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mypage")
public class MyPageRestController {

    private final MedicationService medicationService;
    private final DiseaseService diseaseService;
    private final MyPageService myPageService;
    private final SessionUserSupport sessionUserSupport;

    public MyPageRestController(
            MedicationService medicationService,
            DiseaseService diseaseService,
            MyPageService myPageService,
            SessionUserSupport sessionUserSupport
    ) {
        this.medicationService = medicationService;
        this.diseaseService = diseaseService;
        this.myPageService = myPageService;
        this.sessionUserSupport = sessionUserSupport;
    }

    @GetMapping("/medical")
    public ResponseEntity<Map<String, Object>> myMedical(HttpSession session) {
        UserDto user = sessionUserSupport.getRequiredUser(session);
        Long userId = user.getUserId();
        return ResponseEntity.ok(Map.of(
                "medications", medicationService.getMedicationList(userId),
                "diseases", diseaseService.getUserDiseaseList(userId)
        ));
    }

    @GetMapping("/info")
    public ResponseEntity<UserDto> myInfo(HttpSession session) {
        UserDto user = sessionUserSupport.getRequiredUser(session);
        return ResponseEntity.ok(myPageService.getUser(user.getUserId()));
    }

    @PutMapping("/info")
    public ResponseEntity<Map<String, String>> updateInfo(@RequestBody UserDto updatedUser, HttpSession session) {
        UserDto loginUser = sessionUserSupport.getRequiredUser(session);
        updatedUser.setUserId(loginUser.getUserId());
        myPageService.updateUser(updatedUser);
        sessionUserSupport.updateSessionUser(session, myPageService.getUser(loginUser.getUserId()));
        return ResponseEntity.ok(Map.of("message", "회원 정보 수정이 완료되었습니다."));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteAccount(HttpSession session) {
        UserDto user = sessionUserSupport.getRequiredUser(session);
        myPageService.deleteUser(user.getUserId());
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 완료되었습니다."));
    }

    @GetMapping("/posts")
    public ResponseEntity<?> myPosts(HttpSession session) {
        return ResponseEntity.ok(myPageService.getMyPosts(sessionUserSupport.getRequiredUser(session).getUserId()));
    }

    @GetMapping("/scraps")
    public ResponseEntity<?> myScraps(HttpSession session) {
        return ResponseEntity.ok(myPageService.getMyScraps(sessionUserSupport.getRequiredUser(session).getUserId()));
    }

    @GetMapping("/likes")
    public ResponseEntity<?> myLikes(HttpSession session) {
        return ResponseEntity.ok(myPageService.getMyLikes(sessionUserSupport.getRequiredUser(session).getUserId()));
    }

    @GetMapping("/challenges")
    public ResponseEntity<?> myChallenges(HttpSession session) {
        return ResponseEntity.ok(myPageService.getMyChallenges(sessionUserSupport.getRequiredUser(session).getUserId()));
    }
}
