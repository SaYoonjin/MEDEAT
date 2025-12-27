package com.medeat.medical.dto;

public class MedMedicationSummary {
    private String drugName;
    private String intakeTime; // "아침", "점심", "저녁", "취침전" 등

    public String getDrugName() { return drugName; }
    public void setDrugName(String drugName) { this.drugName = drugName; }

    public String getIntakeTime() { return intakeTime; }
    public void setIntakeTime(String intakeTime) { this.intakeTime = intakeTime; }
}
