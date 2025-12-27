package com.medeat.challenge.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ChallengeLogDto {

    private Long challengeLogId;
    private Long userChallengeId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate logDate;
    private String status;
    private String memo;

    public ChallengeLogDto() {}

    public ChallengeLogDto(Long challengeLogId, Long userChallengeId,
                           LocalDate logDate, String status, String memo) {
        this.challengeLogId = challengeLogId;
        this.userChallengeId = userChallengeId;
        this.logDate = logDate;
        this.status = status;
        this.memo = memo;
    }

    public Long getChallengeLogId() { return challengeLogId; }
    public void setChallengeLogId(Long challengeLogId) { this.challengeLogId = challengeLogId; }

    public Long getUserChallengeId() { return userChallengeId; }
    public void setUserChallengeId(Long userChallengeId) { this.userChallengeId = userChallengeId; }

    public LocalDate getLogDate() { return logDate; }
    public void setLogDate(LocalDate logDate) { this.logDate = logDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }

    @Override
    public String toString() {
        return "ChallengeLogDto{" +
                "challengeLogId=" + challengeLogId +
                ", userChallengeId=" + userChallengeId +
                ", logDate=" + logDate +
                ", status='" + status + '\'' +
                ", memo='" + memo + '\'' +
                '}';
    }
}
