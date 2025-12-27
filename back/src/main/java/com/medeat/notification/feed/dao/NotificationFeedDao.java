package com.medeat.notification.feed.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.medeat.notification.feed.dto.NotificationFeedDto;

@Mapper
public interface NotificationFeedDao {

    void insert(NotificationFeedDto dto);

    // 📄 전체
    List<NotificationFeedDto> selectAllByUser(Long userId);

    // 🔔 최근 N개
    List<NotificationFeedDto> selectRecent(
        @Param("userId") Long userId,
        @Param("limit") int limit
    );

    int countUnread(Long userId);

    void markAsRead(
        @Param("notificationId") Long notificationId,
        @Param("userId") Long userId
    );

    void markAllAsRead(Long userId);
    
    void markRecentAsRead(
    	    @Param("userId") Long userId,
    	    @Param("limit") int limit
    	);
    
    void markBatchAsRead(
    	    @Param("userId") Long userId,
    	    @Param("ids") List<Long> ids
    	);


}
