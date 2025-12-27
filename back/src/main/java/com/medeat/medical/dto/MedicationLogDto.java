package com.medeat.medical.dto;

import java.time.LocalDateTime;

public class MedicationLogDto {

    private Long logId;
    private Long medicationId;
    private Long userId;

    // ✅ 0-based (프론트 i 그대로)
    private int takenIndex;

    // ✅ DB에 추가한 taken_date (yyyy-MM-dd)
    private String takenDate;

    private LocalDateTime  takenAt;

    public MedicationLogDto() {}

    public MedicationLogDto(Long logId, Long medicationId, Long userId, int takenIndex, String takenDate, LocalDateTime takenAt) {
        this.logId = logId;
        this.medicationId = medicationId;
        this.userId = userId;
        this.takenIndex = takenIndex;
        this.takenDate = takenDate;
        this.takenAt = takenAt;
    }

    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }

    public Long getMedicationId() { return medicationId; }
    public void setMedicationId(Long medicationId) { this.medicationId = medicationId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public int getTakenIndex() { return takenIndex; }
    public void setTakenIndex(int takenIndex) { this.takenIndex = takenIndex; }

    public String getTakenDate() { return takenDate; }
    public void setTakenDate(String takenDate) { this.takenDate = takenDate; }

    public LocalDateTime getTakenAt() { return takenAt; }
    public void setTakenAt(LocalDateTime takenAt) { this.takenAt = takenAt; }
}
