package com.medeat.pdf.dto.medi;

public class MediMedicationSummaryDto {

    private String medicationName;
    private String ingredient;
    private String dosage;      // 1회 투여량
    private int timesPerDay;    // 하루 복용 횟수
    
    public MediMedicationSummaryDto() {
		// TODO Auto-generated constructor stub
	}

	public MediMedicationSummaryDto(String medicationName, String ingredient, String dosage, int timesPerDay) {
		super();
		this.medicationName = medicationName;
		this.ingredient = ingredient;
		this.dosage = dosage;
		this.timesPerDay = timesPerDay;
	}

	public String getMedicationName() {
		return medicationName;
	}

	public void setMedicationName(String medicationName) {
		this.medicationName = medicationName;
	}

	public String getIngredient() {
		return ingredient;
	}

	public void setIngredient(String ingredient) {
		this.ingredient = ingredient;
	}

	public String getDosage() {
		return dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
	}

	public int getTimesPerDay() {
		return timesPerDay;
	}

	public void setTimesPerDay(int timesPerDay) {
		this.timesPerDay = timesPerDay;
	}

	@Override
	public String toString() {
		return "MediMedicationSummaryDto [medicationName=" + medicationName + ", ingredient=" + ingredient + ", dosage="
				+ dosage + ", timesPerDay=" + timesPerDay + "]";
	}
    
    
}
