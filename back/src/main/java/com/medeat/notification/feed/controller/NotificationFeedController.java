package com.medeat.notification.feed.controller;

import com.medeat.common.web.SessionUserSupport;
import com.medeat.notification.feed.dto.NotificationFeedDto;
import com.medeat.notification.feed.service.NotificationFeedService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications/feed")
public class NotificationFeedController {

    private static final Logger log = LoggerFactory.getLogger(NotificationFeedController.class);

    private final NotificationFeedService notificationFeedService;
    private final SessionUserSupport sessionUserSupport;

    public NotificationFeedController(
            NotificationFeedService notificationFeedService,
            SessionUserSupport sessionUserSupport
    ) {
        this.notificationFeedService = notificationFeedService;
        this.sessionUserSupport = sessionUserSupport;
    }

    @GetMapping
    public List<NotificationFeedDto> recent(HttpSession session) {
        return notificationFeedService.getRecent(sessionUserSupport.getRequiredUser(session).getUserId(), 5);
    }

    @GetMapping("/all")
    public List<NotificationFeedDto> all(HttpSession session) {
        return notificationFeedService.getMyFeeds(sessionUserSupport.getRequiredUser(session).getUserId());
    }

    @GetMapping("/unread-count")
    public int unreadCount(HttpSession session) {
        return notificationFeedService.getUnreadCount(sessionUserSupport.getRequiredUser(session).getUserId());
    }

    @PatchMapping("/{id}/read")
    public void read(@PathVariable Long id, HttpSession session) {
        notificationFeedService.read(sessionUserSupport.getRequiredUser(session).getUserId(), id);
    }

    @PatchMapping("/read-recent")
    public void readRecent(HttpSession session) {
        notificationFeedService.readRecent(sessionUserSupport.getRequiredUser(session).getUserId());
    }

    @PatchMapping("/read-all")
    public void readAll(HttpSession session) {
        notificationFeedService.readAll(sessionUserSupport.getRequiredUser(session).getUserId());
    }

    @PatchMapping("/read-batch")
    public void readBatch(@RequestBody Map<String, List<Long>> body, HttpSession session) {
        List<Long> ids = body.get("ids");
        log.debug("Marking notification batch as read. ids={}", ids);
        notificationFeedService.readBatch(sessionUserSupport.getRequiredUser(session).getUserId(), ids);
    }
}
