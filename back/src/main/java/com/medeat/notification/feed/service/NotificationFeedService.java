package com.medeat.notification.feed.service;

import java.util.List;

import com.medeat.notification.feed.dto.NotificationFeedDto;
import com.medeat.notification.feed.dto.NotificationFeedType;

public interface NotificationFeedService {

    // ===============================
    // 알림 생성
    // ===============================
    void notify(Long userId, NotificationFeedType type);

    void notify(Long userId,
                NotificationFeedType type,
                String targetType,
                Long targetId);

    // ===============================
    // 조회
    // ===============================

    // 📄 전체 알림
    List<NotificationFeedDto> getMyFeeds(Long userId);

    // 🔔 최근 알림 (드롭다운)
    List<NotificationFeedDto> getRecent(Long userId, int limit);

    // 🔢 읽지 않은 알림 수
    int getUnreadCount(Long userId);

    // ===============================
    // 읽음 처리
    // ===============================
    void read(Long userId, Long notificationId);

    void readAll(Long userId);
    
    void readRecent(Long userId);
    
    void readBatch(Long userId, List<Long> ids);

}
