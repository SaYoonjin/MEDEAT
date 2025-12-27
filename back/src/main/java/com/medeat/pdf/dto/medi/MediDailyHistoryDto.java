package com.medeat.pdf.dto.medi;

public class MediDailyHistoryDto {

    private String date;

    private String medicationStatus; // O / X / △
    private String mealSummary;       // 음주 / 고지방 / 금식
    private String symptom;           // 사용자 기록

    private boolean highlight;        // 특이일 강조
    
    public MediDailyHistoryDto() {
		// TODO Auto-generated constructor stub
	}

	public MediDailyHistoryDto(String date, String medicationStatus, String mealSummary, String symptom,
			boolean highlight) {
		super();
		this.date = date;
		this.medicationStatus = medicationStatus;
		this.mealSummary = mealSummary;
		this.symptom = symptom;
		this.highlight = highlight;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMedicationStatus() {
		return medicationStatus;
	}

	public void setMedicationStatus(String medicationStatus) {
		this.medicationStatus = medicationStatus;
	}

	public String getMealSummary() {
		return mealSummary;
	}

	public void setMealSummary(String mealSummary) {
		this.mealSummary = mealSummary;
	}

	public String getSymptom() {
		return symptom;
	}

	public void setSymptom(String symptom) {
		this.symptom = symptom;
	}

	public boolean isHighlight() {
		return highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

	@Override
	public String toString() {
		return "MediDailyHistoryDto [date=" + date + ", medicationStatus=" + medicationStatus + ", mealSummary="
				+ mealSummary + ", symptom=" + symptom + ", highlight=" + highlight + "]";
	}
    
    
}
