package com.medeat.pdf.dto.medi;

public class MediMedicationDailyLogDto {
    private String date;
    private String drugName;
    private String intakeTime;
    private String status; // 복용 / 미복용
    
    public MediMedicationDailyLogDto() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "MediMedicationDailyLogDto [date=" + date + ", drugName=" + drugName + ", intakeTime=" + intakeTime
				+ ", status=" + status + "]";
	}

	public MediMedicationDailyLogDto(String date, String drugName, String intakeTime, String status) {
		super();
		this.date = date;
		this.drugName = drugName;
		this.intakeTime = intakeTime;
		this.status = status;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public String getIntakeTime() {
		return intakeTime;
	}

	public void setIntakeTime(String intakeTime) {
		this.intakeTime = intakeTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
    
    
}

