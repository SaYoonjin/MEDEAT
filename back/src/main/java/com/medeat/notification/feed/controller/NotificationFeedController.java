package com.medeat.notification.feed.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.medeat.auth.dto.UserDto;
import com.medeat.notification.feed.dto.NotificationFeedDto;
import com.medeat.notification.feed.service.NotificationFeedService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/notifications/feed")
public class NotificationFeedController {

    @Autowired
    private NotificationFeedService notificationFeedService;

    private Long loginUserId(HttpSession session) {
        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "로그인이 필요합니다."
            );
        }
        return loginUser.getUserId();
    }

    @GetMapping
    public List<NotificationFeedDto> recent(HttpSession session) {
        return notificationFeedService.getRecent(
            loginUserId(session),
            5
        );
    }

    @GetMapping("/all")
    public List<NotificationFeedDto> all(HttpSession session) {
        return notificationFeedService.getMyFeeds(
            loginUserId(session)
        );
    }


    @GetMapping("/unread-count")
    public int unreadCount(HttpSession session) {
        return notificationFeedService.getUnreadCount(loginUserId(session));
    }

    @PatchMapping("/{id}/read")
    public void read(@PathVariable Long id, HttpSession session) {
        notificationFeedService.read(loginUserId(session), id);
    }
    
    @PatchMapping("/read-recent")
    public void readRecent(HttpSession session) {
        notificationFeedService.readRecent(loginUserId(session));
    }


    @PatchMapping("/read-all")
    public void readAll(HttpSession session) {
        notificationFeedService.readAll(loginUserId(session));
    }
    
    @PatchMapping("/read-batch")
    public void readBatch(
        @RequestBody Map<String, List<Long>> body,
        HttpSession session
    ) {
        List<Long> ids = body.get("ids");
        System.out.println("🔥 read-batch ids = " + ids);
        notificationFeedService.readBatch(loginUserId(session), ids);
    }


}
