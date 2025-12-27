package com.medeat.challenge.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserChallengeDto {

    // user_challenge
    private Long userChallengeId;
    private Long userId;
    private Long challengeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    // 재참여 제어
    private LocalDateTime giveupAt;
    private LocalDateTime rejoinAvailableAt;

    // 조인용 (목록 표시)
    private String title;
    private String modeType;
    private Integer periodDays;
    private Integer maxParticipants;
    private Integer currentParticipants;

    private Integer successCount;
    private Integer failCount;
    private Integer successRate;   // ⭐ 새로 의미 변경
    
 // ===== 챌린지 상세 (JOIN) =====
    private String category;
    private String difficulty;
    private String description;

    
	// ✅ 조인용 (채팅, 목록 공용)
	private String nickname;

    public UserChallengeDto() {
		// TODO Auto-generated constructor stub
	}

    

	public UserChallengeDto(Long userChallengeId, Long userId, Long challengeId, LocalDate startDate, LocalDate endDate,
			String status, LocalDateTime giveupAt, LocalDateTime rejoinAvailableAt, String title, String modeType,
			Integer periodDays, Integer maxParticipants, Integer currentParticipants, Integer successCount,
			Integer failCount, Integer successRate) {
		super();
		this.userChallengeId = userChallengeId;
		this.userId = userId;
		this.challengeId = challengeId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = status;
		this.giveupAt = giveupAt;
		this.rejoinAvailableAt = rejoinAvailableAt;
		this.title = title;
		this.modeType = modeType;
		this.periodDays = periodDays;
		this.maxParticipants = maxParticipants;
		this.currentParticipants = currentParticipants;
		this.successCount = successCount;
		this.failCount = failCount;
		this.successRate = successRate;
	}


	public String getCategory() {
	    return category;
	}

	public void setCategory(String category) {
	    this.category = category;
	}

	public String getDifficulty() {
	    return difficulty;
	}

	public void setDifficulty(String difficulty) {
	    this.difficulty = difficulty;
	}

	public String getDescription() {
	    return description;
	}

	public void setDescription(String description) {
	    this.description = description;
	}


	public String getNickname() {
	    return nickname;
	}

	public void setNickname(String nickname) {
	    this.nickname = nickname;
	}

	public Long getUserChallengeId() {
		return userChallengeId;
	}



	public void setUserChallengeId(Long userChallengeId) {
		this.userChallengeId = userChallengeId;
	}



	public Long getUserId() {
		return userId;
	}



	public void setUserId(Long userId) {
		this.userId = userId;
	}



	public Long getChallengeId() {
		return challengeId;
	}



	public void setChallengeId(Long challengeId) {
		this.challengeId = challengeId;
	}



	public LocalDate getStartDate() {
		return startDate;
	}



	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}



	public LocalDate getEndDate() {
		return endDate;
	}



	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public LocalDateTime getGiveupAt() {
		return giveupAt;
	}



	public void setGiveupAt(LocalDateTime giveupAt) {
		this.giveupAt = giveupAt;
	}



	public LocalDateTime getRejoinAvailableAt() {
		return rejoinAvailableAt;
	}



	public void setRejoinAvailableAt(LocalDateTime rejoinAvailableAt) {
		this.rejoinAvailableAt = rejoinAvailableAt;
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public String getModeType() {
		return modeType;
	}



	public void setModeType(String modeType) {
		this.modeType = modeType;
	}



	public Integer getPeriodDays() {
		return periodDays;
	}



	public void setPeriodDays(Integer periodDays) {
		this.periodDays = periodDays;
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



	public Integer getSuccessCount() {
		return successCount == null ? 0 : successCount;
	}



	public void setSuccessCount(Integer successCount) {
		this.successCount = successCount;
	}



	public Integer getFailCount() {
		return failCount;
	}



	public void setFailCount(Integer failCount) {
		this.failCount = failCount;
	}



	public Integer getSuccessRate() {
		return successRate;
	}



	public void setSuccessRate(Integer successRate) {
		this.successRate = successRate;
	}



	@Override
	public String toString() {
		return "UserChallengeDto [userChallengeId=" + userChallengeId + ", userId=" + userId + ", challengeId="
				+ challengeId + ", startDate=" + startDate + ", endDate=" + endDate + ", status=" + status
				+ ", giveupAt=" + giveupAt + ", rejoinAvailableAt=" + rejoinAvailableAt + ", title=" + title
				+ ", modeType=" + modeType + ", periodDays=" + periodDays + ", maxParticipants=" + maxParticipants
				+ ", currentParticipants=" + currentParticipants + ", successCount=" + successCount + ", failCount="
				+ failCount + ", successRate=" + successRate + "]";
	}

}
