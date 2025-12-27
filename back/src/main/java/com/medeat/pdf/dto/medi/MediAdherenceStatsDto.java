package com.medeat.pdf.dto.medi;

import java.util.List;

public class MediAdherenceStatsDto {

    // ⭕ 약물별 이행률
    private List<MediMedicationAdherenceDto> medicationRates;

    // 자동 분석 문장
    private String analysisText;

    // (선택) 나중에 확장
    private List<MediAdherenceCalendarDto> calendar;
    private List<MediMissedReasonStatDto> missedReasons;
    
    public MediAdherenceStatsDto() {
		// TODO Auto-generated constructor stub
	}

	public MediAdherenceStatsDto(List<MediMedicationAdherenceDto> medicationRates, String analysisText,
			List<MediAdherenceCalendarDto> calendar, List<MediMissedReasonStatDto> missedReasons) {
		super();
		this.medicationRates = medicationRates;
		this.analysisText = analysisText;
		this.calendar = calendar;
		this.missedReasons = missedReasons;
	}

	public List<MediMedicationAdherenceDto> getMedicationRates() {
		return medicationRates;
	}

	public void setMedicationRates(List<MediMedicationAdherenceDto> medicationRates) {
		this.medicationRates = medicationRates;
	}

	public String getAnalysisText() {
		return analysisText;
	}

	public void setAnalysisText(String analysisText) {
		this.analysisText = analysisText;
	}

	public List<MediAdherenceCalendarDto> getCalendar() {
		return calendar;
	}

	public void setCalendar(List<MediAdherenceCalendarDto> calendar) {
		this.calendar = calendar;
	}

	public List<MediMissedReasonStatDto> getMissedReasons() {
		return missedReasons;
	}

	public void setMissedReasons(List<MediMissedReasonStatDto> missedReasons) {
		this.missedReasons = missedReasons;
	}

	@Override
	public String toString() {
		return "MediAdherenceStatsDto [medicationRates=" + medicationRates + ", analysisText=" + analysisText
				+ ", calendar=" + calendar + ", missedReasons=" + missedReasons + "]";
	}
    
    
}
