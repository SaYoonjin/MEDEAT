package com.medeat.notification.feed.dto;

public enum NotificationFeedType {

    CHALLENGE_SUCCESS("🎉 챌린지를 성공했어요!"),
    CHALLENGE_END("😢 챌린지가 종료되었어요."),

    LEVEL_UP("🏅 다음 등급으로 성장했어요!"),

    MEDICATION_SETTING_CHANGED("🔔 약 복용 알림 설정이 변경되었어요."),

    COMMUNITY_LIKE("❤️ 내 게시글에 누군가 좋아요를 눌렀어요."),
    COMMUNITY_SCRAP("🔖 내 게시글을 누군가 스크랩했어요."),
	COMMUNITY_COMMENT("💬 내 게시글에 누군가 댓글을 달았어요.");

    private final String message;

    NotificationFeedType(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
