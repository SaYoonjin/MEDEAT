package com.medeat.gamification.dto;

public class ProgressResponse {
    private int level;
    private int totalXp;
    private int nextLevelXp;

    private int todayXp;
    private int dailyCap;

    private int masterXp;
    private int masterCycle;
    private int masterPercent;

    private int currentStreak;
    private String lastActionDate;

    public ProgressResponse() {}

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getTotalXp() { return totalXp; }
    public void setTotalXp(int totalXp) { this.totalXp = totalXp; }

    public int getNextLevelXp() { return nextLevelXp; }
    public void setNextLevelXp(int nextLevelXp) { this.nextLevelXp = nextLevelXp; }

    public int getTodayXp() { return todayXp; }
    public void setTodayXp(int todayXp) { this.todayXp = todayXp; }

    public int getDailyCap() { return dailyCap; }
    public void setDailyCap(int dailyCap) { this.dailyCap = dailyCap; }

    public int getMasterXp() { return masterXp; }
    public void setMasterXp(int masterXp) { this.masterXp = masterXp; }

    public int getMasterCycle() { return masterCycle; }
    public void setMasterCycle(int masterCycle) { this.masterCycle = masterCycle; }

    public int getMasterPercent() { return masterPercent; }
    public void setMasterPercent(int masterPercent) { this.masterPercent = masterPercent; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public String getLastActionDate() { return lastActionDate; }
    public void setLastActionDate(String lastActionDate) { this.lastActionDate = lastActionDate; }
}
