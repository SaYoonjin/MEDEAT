package com.medeat.gamification.dto;

public class EarnXpResult {
    private int requestedXp;
    private int grantedXp;     // 실제 지급 XP(상한/중복 반영)
    private boolean duplicated;
    private boolean capped;    // 상한으로 깎였는지
    private int todayXp;
    private int dailyCap;

    public EarnXpResult() {}

    public int getRequestedXp() { return requestedXp; }
    public void setRequestedXp(int requestedXp) { this.requestedXp = requestedXp; }

    public int getGrantedXp() { return grantedXp; }
    public void setGrantedXp(int grantedXp) { this.grantedXp = grantedXp; }

    public boolean isDuplicated() { return duplicated; }
    public void setDuplicated(boolean duplicated) { this.duplicated = duplicated; }

    public boolean isCapped() { return capped; }
    public void setCapped(boolean capped) { this.capped = capped; }

    public int getTodayXp() { return todayXp; }
    public void setTodayXp(int todayXp) { this.todayXp = todayXp; }

    public int getDailyCap() { return dailyCap; }
    public void setDailyCap(int dailyCap) { this.dailyCap = dailyCap; }
}
