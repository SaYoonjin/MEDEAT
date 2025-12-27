package com.medeat.gamification.dto;

public class EarnXpRequest {
    private long userId;
    private String actionType; // ActionType.name()
    private String refId;      // 중복 방지 키 (NOT NULL)
    private int xp;            // 요청 XP
    private String date;       // yyyy-MM-dd (null이면 오늘)

    public EarnXpRequest() {}

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getRefId() { return refId; }
    public void setRefId(String refId) { this.refId = refId; }

    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
