package com.medeat.pdf.assembler;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.medeat.dashboard.dto.MedDashboard;
import com.medeat.diet.dto.DietLogDto;
import com.medeat.medical.dto.*;
import com.medeat.pdf.dto.medi.*;

@Component
public class MediEatPdfAssembler {

    // =========================================================
    // Entry Point
    // =========================================================
    public MediEatPdfDto assemble(
            MedDashboard medDashboard,
            List<MedicationDto> medications,
            List<MedicationLogDto> medicationLogs,
            List<DietLogDto> dietLogs,
            String userName,
            String patientId,
            int age,
            LocalDate startDate,
            LocalDate endDate,
            List<DrugInfoDto> drugInfoList          // ⭐ 추가
    ) {

        MediEatPdfDto pdf = new MediEatPdfDto();

        pdf.setStartDate(startDate);
        pdf.setEndDate(endDate);
        pdf.setGeneratedAt(LocalDate.now());

        pdf.setPatientSummary(
                buildPatientSummary(medDashboard, medicationLogs, userName, patientId, age));

        pdf.setAdherenceStats(
                buildAdherenceStats(medications, medicationLogs, startDate, endDate));

        pdf.setMedicationDailyLogs(
                buildMedicationDailyLogs(medications, medicationLogs));

        pdf.setMedicationList(medications);      // ⭐ 현재 복용 약 리스트
        pdf.setDrugInfoList(drugInfoList);       // ⭐ 약물 효능/주의사항

        pdf.setSafetyReport(buildSafetyReport(medDashboard));
        pdf.setClinicalDiet(buildClinicalDiet(dietLogs));
        pdf.setDailyHistories(buildDailyHistories(medicationLogs));
        pdf.setDisclaimer(buildDisclaimer());

        return pdf;
    }

    // =========================================================
    // 환자 요약
    // =========================================================
    private MediPatientSummaryDto buildPatientSummary(
            MedDashboard dashboard,
            List<MedicationLogDto> logs,
            String name,
            String patientId,
            int age
    ) {
        MediPatientSummaryDto dto = new MediPatientSummaryDto();

        dto.setName(name);
        dto.setPatientId(patientId);
        dto.setAge(age);

        int adherenceRate = calculateAdherenceRateByCount(logs);

        dto.setAdherenceRate(adherenceRate);
        dto.setMissedDoseCount(calculateMissedCount(logs));
        dto.setSummaryComment(buildSummaryComment(adherenceRate));

        if (dashboard != null) {
            dto.setOverallGrade(calculateGrade(dashboard, adherenceRate));
            dto.setGradeReason(buildGradeReason(dashboard, adherenceRate));
        } else {
            dto.setOverallGrade("GOOD");
            dto.setGradeReason("복약 기록이 충분하지 않습니다.");
        }
        return dto;
    }

    // =========================================================
    // 복약 이행 분석 (시간대별)
    // =========================================================
    private MediAdherenceStatsDto buildAdherenceStats(
            List<MedicationDto> meds,
            List<MedicationLogDto> logs,
            LocalDate start,
            LocalDate end
    ) {
        MediAdherenceStatsDto dto = new MediAdherenceStatsDto();

        List<MediMedicationAdherenceDto> rates = new ArrayList<>();
        long days = ChronoUnit.DAYS.between(start, end) + 1;
        
        if (meds == null || meds.isEmpty()) {
            dto.setMedicationRates(List.of());
            dto.setAnalysisText("복용 중인 약물이 없습니다.");
            return dto;
        }

        for (MedicationDto med : meds) {
            int expected = med.getDailyCount() * (int) days;

            long taken = logs.stream()
                .filter(l -> l.getMedicationId().equals(med.getMedicationId()))
                .filter(l -> l.getTakenIndex() == 1)
                .count();

            int rate = expected == 0 ? 0
                    : (int) Math.round(taken * 100.0 / expected);

            MediMedicationAdherenceDto m = new MediMedicationAdherenceDto();
            m.setDrugName(med.getDrugName());
            m.setDailyCount(med.getDailyCount());   // ⭐⭐⭐ 이 줄 추가
            m.setExpectedCount(expected);
            m.setTakenCount((int) taken);
            m.setAdherenceRate(rate);

            rates.add(m);
        }

        dto.setMedicationRates(rates);
        dto.setAnalysisText("약물별 복약 이행률 분석 결과입니다.");

        return dto;
    }

    // =========================================================
    // 일자별 복약 로그
    // =========================================================
    private List<MediMedicationDailyLogDto> buildMedicationDailyLogs(
            List<MedicationDto> meds,
            List<MedicationLogDto> logs
    ) {
        List<MediMedicationDailyLogDto> result = new ArrayList<>();

        for (MedicationLogDto log : logs) {
            MedicationDto med = meds.stream()
                    .filter(m -> m.getMedicationId().equals(log.getMedicationId()))
                    .findFirst()
                    .orElse(null);

            if (med == null) continue;

            result.add(new MediMedicationDailyLogDto(
            		log.getTakenAt().toLocalDate().toString(),
                    med.getDrugName(),
                    toKoreanTime(med.getIntakeTime()),
                    log.getTakenIndex() == 1 ? "복용" : "미복용"
            ));
        }
        return result;
    }

    // =========================================================
    // 시간대 한글 변환
    // =========================================================
    private String toKoreanTime(String intakeTime) {
        if (intakeTime == null) return "-";
        return switch (intakeTime) {
            case "MORNING" -> "아침";
            case "AFTERNOON" -> "점심";
            case "EVENING" -> "저녁";
            case "BEDTIME" -> "취침 전";
            default -> intakeTime;
        };
    }

    // =========================================================
    // 기타
    // =========================================================
    private List<MediDailyHistoryDto> buildDailyHistories(List<MedicationLogDto> logs) {
        if (logs == null) return List.of();

        return logs.stream()
        		.collect(Collectors.groupingBy(
        			    log -> log.getTakenAt().toLocalDate().toString()
        			))

                .entrySet().stream()
                .map(e -> {
                    MediDailyHistoryDto dto = new MediDailyHistoryDto();
                    dto.setDate(e.getKey());
                    dto.setMedicationStatus(
                            e.getValue().stream().anyMatch(l -> l.getTakenIndex() == 1) ? "O" : "X");
                    dto.setHighlight(
                            e.getValue().stream().noneMatch(l -> l.getTakenIndex() == 1));
                    return dto;
                })
                .sorted(Comparator.comparing(MediDailyHistoryDto::getDate))
                .toList();
    }

    private MediSafetyReportDto buildSafetyReport(MedDashboard dashboard) {
        return new MediSafetyReportDto();
    }

    private MediClinicalDietDto buildClinicalDiet(List<DietLogDto> dietLogs) {
        return new MediClinicalDietDto();
    }

    private MediDisclaimerDto buildDisclaimer() {
        MediDisclaimerDto dto = new MediDisclaimerDto();
        dto.setContent("본 리포트는 참고용입니다.");
        return dto;
    }

    private int calculateAdherenceRateByCount(List<MedicationLogDto> logs) {
        if (logs == null || logs.isEmpty()) return 0;
        long taken = logs.stream().filter(l -> l.getTakenIndex() == 1).count();
        return (int) Math.round(taken * 100.0 / logs.size());
    }

    private int calculateMissedCount(List<MedicationLogDto> logs) {
        if (logs == null) return 0;
        return (int) logs.stream().filter(l -> l.getTakenIndex() != 1).count();
    }

    private String buildSummaryComment(int rate) {
        if (rate >= 90) return "복약 이행률이 매우 양호합니다.";
        if (rate >= 70) return "일부 복약 누락이 관찰됩니다.";
        if (rate > 0) return "복약 관리가 필요합니다.";
        return "복약 기록이 없습니다.";
    }

    private String calculateGrade(MedDashboard d, int rate) {
        if (rate < 70) return "WARNING";
        if (rate < 90) return "CAUTION";
        return "GOOD";
    }

    private String buildGradeReason(MedDashboard d, int rate) {
        if (rate < 70) return "복약 이행률이 낮습니다.";
        if (rate < 90) return "일부 누락이 있습니다.";
        return "안정적인 복약 상태입니다.";
    }
}
