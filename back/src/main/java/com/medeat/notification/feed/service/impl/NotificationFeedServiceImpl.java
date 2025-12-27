package com.medeat.notification.feed.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medeat.notification.feed.dao.NotificationFeedDao;
import com.medeat.notification.feed.dto.NotificationFeedDto;
import com.medeat.notification.feed.dto.NotificationFeedType;
import com.medeat.notification.feed.service.NotificationFeedService;

@Service
@Transactional
public class NotificationFeedServiceImpl implements NotificationFeedService {

    @Autowired
    private NotificationFeedDao notificationFeedDao;

    // ===============================
    // 알림 생성
    // ===============================
    @Override
    public void notify(Long userId, NotificationFeedType type) {
        NotificationFeedDto dto = new NotificationFeedDto();
        dto.setUserId(userId);
        dto.setType(type.name());
        dto.setMessage(type.message());
        notificationFeedDao.insert(dto);
    }

    @Override
    public void notify(Long userId,
                       NotificationFeedType type,
                       String targetType,
                       Long targetId) {

        NotificationFeedDto dto = new NotificationFeedDto();
        dto.setUserId(userId);
        dto.setType(type.name());
        dto.setMessage(type.message());
        dto.setTargetType(targetType);
        dto.setTargetId(targetId);

        notificationFeedDao.insert(dto);
    }

    // ===============================
    // 조회
    // ===============================
    @Override
    public List<NotificationFeedDto> getMyFeeds(Long userId) {
        return notificationFeedDao.selectAllByUser(userId);
    }

    @Override
    public List<NotificationFeedDto> getRecent(Long userId, int limit) {
        return notificationFeedDao.selectRecent(userId, limit);
    }

    @Override
    public int getUnreadCount(Long userId) {
        return notificationFeedDao.countUnread(userId);
    }

    // ===============================
    // 읽음 처리
    // ===============================
    @Override
    public void read(Long userId, Long notificationId) {
        notificationFeedDao.markAsRead(notificationId, userId);
    }

    @Override
    public void readAll(Long userId) {
        notificationFeedDao.markAllAsRead(userId);
    }

	@Override
	public void readRecent(Long userId) {
		notificationFeedDao.markRecentAsRead(userId, 5);
	}

	@Override
	public void readBatch(Long userId, List<Long> ids) {
		if (ids == null || ids.isEmpty()) return;
	    notificationFeedDao.markBatchAsRead(userId, ids);
	}
	
}
