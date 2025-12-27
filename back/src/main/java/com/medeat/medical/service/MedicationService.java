package com.medeat.medical.service;

import java.util.List;

import com.medeat.medical.dto.MedicationDto;
import com.medeat.medical.dto.MedicationLogDto;

public interface MedicationService {

    List<MedicationDto> getMedicationList(Long userId);

    MedicationDto getMedication(Long medicationId);

    void addMedication(MedicationDto dto);

    void updateMedication(MedicationDto dto);

    void deleteMedication(Long medicationId);

    // 오늘 복용한 기록 조회
    List<MedicationLogDto> getTodayLogs(Long userId);

    // 복용 기록 저장
    void saveLog(Long userId, Long medicationId, int takenIndex);
    
    // 🔔 알림 스케줄러가 호출하는 메서드
    void checkAndSendMedicationAlarms();

    // 🔔 특정 시간(nowTime)에 맞는 약만 조회 (스케줄러에서 사용)
    List<MedicationDto> getMedicationToAlert(String nowTime);

    // 🔔 다음 복용 회차(doseIndex) 계산 (1~daily_count)
    int getNextDoseIndex(MedicationDto med);
    
    boolean existsMedication(Long userId, String drugName);
    
    List<MedicationLogDto> getLogs(Long userId, String startDate, String endDate);

}