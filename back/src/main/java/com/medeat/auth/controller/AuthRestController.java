package com.medeat.auth.controller;

import com.medeat.auth.dto.UserDto;
import com.medeat.auth.service.EmailAuthService;
import com.medeat.auth.service.UserService;
import com.medeat.medical.dto.MedInfoDto;
import com.medeat.medical.dto.MedInfoRequest;
import com.medeat.medical.service.MedInfoService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private MedInfoService medInfoService;
    
    @Autowired
    private EmailAuthService emailAuthService;


    /** ****************************************
     * 1. 회원가입 (기본 정보 등록)
     ******************************************/
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto user) {

        boolean result = userService.signup(user);

        if (!result) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "회원가입에 실패했습니다."));
        }

        return ResponseEntity.ok(Map.of("message", "회원가입 완료"));
    }

    @GetMapping("/checkLogin")
    public ResponseEntity<?> checkLogin(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute("loginUser");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        return ResponseEntity.ok(user);
    }


    /** ****************************************
     * 2. MEDI-EAT 추가 의학 정보 입력
     ******************************************/
    @PostMapping("/med-info")
    public ResponseEntity<?> saveMedInfo(
            @RequestBody MedInfoRequest req,
            HttpSession session) {

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        Long userId = loginUser.getUserId();

        // 기존 User 정보 업데이트
        UserDto origin = userService.getUserById(userId);
        origin.setPregnantStatus(req.getPregnant());
        origin.setPregnancyWeek("yes".equals(req.getPregnant()) ? req.getPregnancyWeek() : null);

        userService.update(origin);

        // MedInfo 저장
        MedInfoDto info = new MedInfoDto();
        info.setUserId(userId);
        info.setDisease(req.getDisease());
        info.setMedicine(req.getMedicine());
        medInfoService.save(info);

        return ResponseEntity.ok(Map.of("message", "의학 정보 저장 완료"));
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData,
                                   HttpSession session) {

        String loginId = loginData.get("loginId");
        String password = loginData.get("password");

        UserDto loginUser = userService.login(loginId, password);

        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "아이디 또는 비밀번호가 올바르지 않습니다."));
        }
        
        // 🔍 추가: 세션에 저장될 유저 정보 로그 찍기
        System.out.println("================================");
        System.out.println("[로그인 시도 성공]");
        System.out.println("세션에 저장될 User ID: " + loginUser.getUserId());
        System.out.println("세션 ID: " + session.getId());
        System.out.println("================================");

        // 🔥 세션 유지 시간 설정 (2시간)  — 단위: 초
        session.setMaxInactiveInterval(60 * 60 * 2);

        // 세션 저장
        session.setAttribute("loginUser", loginUser);

        return ResponseEntity.ok(loginUser);
    }


    /** ****************************************
     * 4. 로그아웃
     ******************************************/
    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "로그아웃 완료"));
    }


    /** ****************************************
     * 5. 로그인된 사용자 조회 (SPA에서 필요)
     ******************************************/
    @GetMapping("/user")
    public ResponseEntity<?> getLoginUser(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute("loginUser");
        return ResponseEntity.ok(user);
    }
    
    /** ****************************************
     * 6. 푸시 알림 ON/OFF 설정 저장
     ******************************************/
    @PostMapping("/push-enabled")
    public ResponseEntity<?> updatePushEnabled(
            @RequestBody Map<String, Object> body,
            HttpSession session) {

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        Boolean enabled = (Boolean) body.get("enabled");
        if (enabled == null) enabled = true;

        // DB 업데이트
        loginUser.setPushEnabled(enabled);
        userService.update(loginUser);

        // ⭐⭐ 세션에 최신 값 저장 (이게 없으면 계속 OFF로 돌아감)
        session.setAttribute("loginUser", loginUser);

        return ResponseEntity.ok(Map.of(
                "message", "푸시 설정 변경 완료",
                "pushEnabled", enabled
        ));
    }
    
    @PostMapping("/send-email-code")
    public ResponseEntity<?> sendEmailCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        emailAuthService.sendCodeToEmail(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-email-code")
    public ResponseEntity<?> verifyEmailCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");

        boolean result = emailAuthService.verifyCode(email, code);
        return ResponseEntity.ok(Map.of("valid", result));
    }

    @PostMapping("/find-id")
    public ResponseEntity<?> findId(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        List<String> loginIds = userService.findLoginIdsByEmail(email);

        if (loginIds == null || loginIds.isEmpty()) {
            return ResponseEntity.status(404).body("NO_USER");
        }

        return ResponseEntity.ok(Map.of("loginIds", loginIds));
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String newPw = body.get("newPassword");

        boolean updated = userService.updatePasswordByEmail(email, newPw);

        return updated ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(404).body("NO_USER");
    }

}
