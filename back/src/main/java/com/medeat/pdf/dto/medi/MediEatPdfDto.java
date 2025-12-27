package com.medeat.pdf.dto.medi;

import java.time.LocalDate;
import java.util.List;

import com.medeat.medical.dto.DrugInfoDto;
import com.medeat.medical.dto.MedicationDto;
import com.medeat.pdf.dto.medi.MediMedicationDailyLogDto;

public class MediEatPdfDto {

    // 리포트 기간
    private LocalDate startDate;
    private LocalDate endDate;

    // 생성 시점
    private LocalDate generatedAt;

    // 각 페이지 섹션
    private MediPatientSummaryDto patientSummary;
    private MediAdherenceStatsDto adherenceStats;
    private MediSafetyReportDto safetyReport;
    private MediClinicalDietDto clinicalDiet;
    private List<MediDailyHistoryDto> dailyHistories;
    private MediDisclaimerDto disclaimer;
 // 약 복용 상세 로그 (PDF 전용)
    private List<MediMedicationDailyLogDto> medicationDailyLogs;

 // 현재 복용 중인 약물 리스트
    private List<MedicationDto> medicationList;
 // 약물 효능 / 주의사항 리스트
    private List<DrugInfoDto> drugInfoList;

    
    
    
    public List<DrugInfoDto> getDrugInfoList() {
        return drugInfoList;
    }

    public void setDrugInfoList(List<DrugInfoDto> drugInfoList) {
        this.drugInfoList = drugInfoList;
    }


    public List<MedicationDto> getMedicationList() {
        return medicationList;
    }

    public void setMedicationList(List<MedicationDto> medicationList) {
        this.medicationList = medicationList;
    }

    
    public MediEatPdfDto() {
		// TODO Auto-generated constructor stub
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

	public LocalDate getGeneratedAt() {
		return generatedAt;
	}

	public void setGeneratedAt(LocalDate generatedAt) {
		this.generatedAt = generatedAt;
	}

	public MediPatientSummaryDto getPatientSummary() {
		return patientSummary;
	}

	public void setPatientSummary(MediPatientSummaryDto patientSummary) {
		this.patientSummary = patientSummary;
	}

	public MediAdherenceStatsDto getAdherenceStats() {
		return adherenceStats;
	}

	public void setAdherenceStats(MediAdherenceStatsDto adherenceStats) {
		this.adherenceStats = adherenceStats;
	}

	public MediSafetyReportDto getSafetyReport() {
		return safetyReport;
	}

	public void setSafetyReport(MediSafetyReportDto safetyReport) {
		this.safetyReport = safetyReport;
	}

	public MediClinicalDietDto getClinicalDiet() {
		return clinicalDiet;
	}

	public void setClinicalDiet(MediClinicalDietDto clinicalDiet) {
		this.clinicalDiet = clinicalDiet;
	}

	public List<MediDailyHistoryDto> getDailyHistories() {
		return dailyHistories;
	}

	public void setDailyHistories(List<MediDailyHistoryDto> dailyHistories) {
		this.dailyHistories = dailyHistories;
	}

	public MediDisclaimerDto getDisclaimer() {
		return disclaimer;
	}

	public void setDisclaimer(MediDisclaimerDto disclaimer) {
		this.disclaimer = disclaimer;
	}
	
	public List<MediMedicationDailyLogDto> getMedicationDailyLogs() {
	    return medicationDailyLogs;
	}

	public void setMedicationDailyLogs(List<MediMedicationDailyLogDto> medicationDailyLogs) {
	    this.medicationDailyLogs = medicationDailyLogs;
	}

	@Override
	public String toString() {
		return "MediEatPdfDto [startDate=" + startDate + ", endDate=" + endDate + ", generatedAt=" + generatedAt
				+ ", patientSummary=" + patientSummary + ", adherenceStats=" + adherenceStats + ", safetyReport="
				+ safetyReport + ", clinicalDiet=" + clinicalDiet + ", dailyHistories=" + dailyHistories
				+ ", disclaimer=" + disclaimer + ", medicationDailyLogs=" + medicationDailyLogs + ", medicationList="
				+ medicationList + ", drugInfoList=" + drugInfoList + "]";
	}


	
    
    
}
