package com.medeat.challenge.dto;

import java.time.LocalDateTime;

public class ChallengeDto {

    private Long challengeId;
    private String modeType;
    private String category;
    private String title;
    private String description;
    private Integer periodDays;
    private String difficulty;
    private LocalDateTime createdAt;
    private Long userId;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private Integer rejoinCooldownDays;

    public ChallengeDto() {}

    public ChallengeDto(Long challengeId, String modeType, String category, String title,
                        String description, Integer periodDays, String difficulty,
                        LocalDateTime createdAt, Long userId, Integer maxParticipants, Integer currentParticipants) {
        this.challengeId = challengeId;
        this.modeType = modeType;
        this.category = category;
        this.title = title;
        this.description = description;
        this.periodDays = periodDays;
        this.difficulty = difficulty;
        this.createdAt = createdAt;
        this.userId = userId;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = currentParticipants;
    }

    public Long getChallengeId() { return challengeId; }
    public void setChallengeId(Long challengeId) { this.challengeId = challengeId; }

    public String getModeType() { return modeType; }
    public void setModeType(String modeType) { this.modeType = modeType; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPeriodDays() { return periodDays; }
    public void setPeriodDays(Integer periodDays) { this.periodDays = periodDays; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    

    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getMaxParticipants() {
		return maxParticipants;
	}

	public void setMaxParticipants(Integer maxParticipants) {
		this.maxParticipants = maxParticipants;
	}

	public Integer getCurrentParticipants() {
		return currentParticipants;
	}

	public void setCurrentParticipants(Integer currentParticipants) {
		this.currentParticipants = currentParticipants;
	}
	
	public Integer getRejoinCooldownDays() {
	    return rejoinCooldownDays;
	}

	public void setRejoinCooldownDays(Integer rejoinCooldownDays) {
	    this.rejoinCooldownDays = rejoinCooldownDays;
	}

	@Override
	public String toString() {
		return "ChallengeDto [challengeId=" + challengeId + ", modeType=" + modeType + ", category=" + category
				+ ", title=" + title + ", description=" + description + ", periodDays=" + periodDays + ", difficulty="
				+ difficulty + ", createdAt=" + createdAt + ", userId=" + userId + ", maxParticipants="
				+ maxParticipants + ", currentParticipants=" + currentParticipants + "]";
	}

	
}
