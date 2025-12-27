package com.medeat.notification.feed.dto;

import java.time.LocalDateTime;

public class NotificationFeedDto {

    private Long notificationId;
    private Long userId;

    private String type;      // enum name 저장
    private String message;

    private boolean isRead;
    private LocalDateTime createdAt;
    
    private String targetType;  // POST, CHALLENGE, USER
    private Long targetId;      // postId, challengeId

    
    public NotificationFeedDto() {
		// TODO Auto-generated constructor stub
	}

	

	public NotificationFeedDto(Long notificationId, Long userId, String type, String message, boolean isRead,
			LocalDateTime createdAt, String targetType, Long targetId) {
		super();
		this.notificationId = notificationId;
		this.userId = userId;
		this.type = type;
		this.message = message;
		this.isRead = isRead;
		this.createdAt = createdAt;
		this.targetType = targetType;
		this.targetId = targetId;
	}



	public Long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	

	public String getTargetType() {
		return targetType;
	}



	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}



	public Long getTargetId() {
		return targetId;
	}



	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}



	@Override
	public String toString() {
		return "NotificationFeedDto [notificationId=" + notificationId + ", userId=" + userId + ", type=" + type
				+ ", message=" + message + ", isRead=" + isRead + ", createdAt=" + createdAt + ", targetType="
				+ targetType + ", targetId=" + targetId + "]";
	}

	
    
    
}
