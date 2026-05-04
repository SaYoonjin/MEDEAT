package com.medeat.auth.controller;

import com.medeat.auth.dto.UserDto;
import com.medeat.auth.service.EmailAuthService;
import com.medeat.auth.service.UserService;
import com.medeat.common.web.SessionUserSupport;
import com.medeat.medical.dto.MedInfoDto;
import com.medeat.medical.dto.MedInfoRequest;
import com.medeat.medical.service.MedInfoService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private static final Logger log = LoggerFactory.getLogger(AuthRestController.class);

    private final UserService userService;
    private final MedInfoService medInfoService;
    private final EmailAuthService emailAuthService;
    private final SessionUserSupport sessionUserSupport;

    public AuthRestController(
            UserService userService,
            MedInfoService medInfoService,
            EmailAuthService emailAuthService,
            SessionUserSupport sessionUserSupport
    ) {
        this.userService = userService;
        this.medInfoService = medInfoService;
        this.emailAuthService = emailAuthService;
        this.sessionUserSupport = sessionUserSupport;
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody UserDto user) {
        boolean result = userService.signup(user);
        if (!result) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "회원가입에 실패했습니다."));
        }

        return ResponseEntity.ok(Map.of("message", "회원가입이 완료되었습니다."));
    }

    @GetMapping("/checkLogin")
    public ResponseEntity<?> checkLogin(HttpSession session) {
        UserDto user = sessionUserSupport.getOptionalUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        return ResponseEntity.ok(user);
    }

    @PostMapping("/med-info")
    public ResponseEntity<Map<String, String>> saveMedInfo(@RequestBody MedInfoRequest req, HttpSession session) {
        UserDto loginUser = sessionUserSupport.getRequiredUser(session);
        Long userId = loginUser.getUserId();

        UserDto origin = userService.getUserById(userId);
        origin.setPregnantStatus(req.getPregnant());
        origin.setPregnancyWeek("yes".equals(req.getPregnant()) ? req.getPregnancyWeek() : null);
        userService.update(origin);

        MedInfoDto info = new MedInfoDto();
        info.setUserId(userId);
        info.setDisease(req.getDisease());
        info.setMedicine(req.getMedicine());
        medInfoService.save(info);

        return ResponseEntity.ok(Map.of("message", "추가 정보 저장이 완료되었습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        String loginId = loginData.get("loginId");
        String password = loginData.get("password");

        UserDto loginUser = userService.login(loginId, password);
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "아이디 또는 비밀번호가 올바르지 않습니다."));
        }

        session.setMaxInactiveInterval(60 * 60 * 2);
        sessionUserSupport.updateSessionUser(session, loginUser);
        log.info("User login succeeded. userId={}, sessionId={}", loginUser.getUserId(), session.getId());

        return ResponseEntity.ok(loginUser);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "로그아웃이 완료되었습니다."));
    }

    @GetMapping("/user")
    public ResponseEntity<UserDto> getLoginUser(HttpSession session) {
        return ResponseEntity.ok(sessionUserSupport.getOptionalUser(session));
    }

    @PostMapping("/push-enabled")
    public ResponseEntity<Map<String, Object>> updatePushEnabled(
            @RequestBody Map<String, Object> body,
            HttpSession session
    ) {
        UserDto loginUser = sessionUserSupport.getRequiredUser(session);

        boolean enabled = body.get("enabled") instanceof Boolean value ? value : true;
        userService.updatePushEnabled(loginUser.getUserId(), enabled);
        sessionUserSupport.updateSessionUser(session, userService.getUserById(loginUser.getUserId()));

        return ResponseEntity.ok(Map.of(
                "message", "푸시 설정이 변경되었습니다.",
                "pushEnabled", enabled
        ));
    }

    @PostMapping("/send-email-code")
    public ResponseEntity<Void> sendEmailCode(@RequestBody Map<String, String> body) {
        emailAuthService.sendCodeToEmail(body.get("email"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-email-code")
    public ResponseEntity<Map<String, Boolean>> verifyEmailCode(@RequestBody Map<String, String> body) {
        boolean result = emailAuthService.verifyCode(body.get("email"), body.get("code"));
        return ResponseEntity.ok(Map.of("valid", result));
    }

    @PostMapping("/find-id")
    public ResponseEntity<?> findId(@RequestBody Map<String, String> body) {
        List<String> loginIds = userService.findLoginIdsByEmail(body.get("email"));
        if (loginIds == null || loginIds.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "NO_USER"));
        }

        return ResponseEntity.ok(Map.of("loginIds", loginIds));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        boolean updated = userService.updatePasswordByEmail(body.get("email"), body.get("newPassword"));
        if (!updated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "NO_USER"));
        }

        return ResponseEntity.ok().build();
    }
}
