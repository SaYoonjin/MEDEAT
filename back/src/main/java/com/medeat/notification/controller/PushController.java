package com.medeat.notification.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medeat.auth.dto.UserDto;
import com.medeat.notification.dto.PushSubscriptionDto;
import com.medeat.notification.service.PushSubscriptionService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/push")
public class PushController {

    private final PushSubscriptionService pushSubscriptionService;

    public PushController(PushSubscriptionService pushSubscriptionService) {
        this.pushSubscriptionService = pushSubscriptionService;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(
            @RequestBody Map<String, Object> body,
            HttpSession session) {

        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        Map<String, Object> keys = (Map<String, Object>) body.get("keys");

        PushSubscriptionDto dto = new PushSubscriptionDto();
        dto.setUserId(user.getUserId());
        dto.setEndpoint((String) body.get("endpoint"));
        dto.setP256dh((String) keys.get("p256dh"));
        dto.setAuth((String) keys.get("auth"));

        pushSubscriptionService.save(dto);
        return ResponseEntity.ok("saved");
    }
    
    @PostMapping("/toggle")
    public ResponseEntity<?> togglePush(@RequestBody Map<String, Object> body,
                                        HttpSession session) {

        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        boolean enabled = Boolean.parseBoolean(body.get("enabled").toString());
        pushSubscriptionService.updatePushEnabled(user.getUserId(), enabled);

        // 세션에도 반영
        user.setPushEnabled(enabled);
        session.setAttribute("loginUser", user);

        return ResponseEntity.ok(Map.of("enabled", enabled));
    }

}
