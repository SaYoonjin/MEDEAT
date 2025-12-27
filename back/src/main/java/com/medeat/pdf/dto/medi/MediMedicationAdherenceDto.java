package com.medeat.pdf.dto.medi;

public class MediMedicationAdherenceDto {
    private String drugName;      // medication.drug_name
    private int dailyCount;      // ⭐ 추가
    private int expectedCount;    // daily_count × days
    private int takenCount;       // log count
    private int adherenceRate;    // %
    
    public MediMedicationAdherenceDto() {
		// TODO Auto-generated constructor stub
	}

	

	public MediMedicationAdherenceDto(String drugName, int dailyCount, int expectedCount, int takenCount,
			int adherenceRate) {
		super();
		this.drugName = drugName;
		this.dailyCount = dailyCount;
		this.expectedCount = expectedCount;
		this.takenCount = takenCount;
		this.adherenceRate = adherenceRate;
	}



	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public int getExpectedCount() {
		return expectedCount;
	}

	public void setExpectedCount(int expectedCount) {
		this.expectedCount = expectedCount;
	}

	public int getTakenCount() {
		return takenCount;
	}

	public void setTakenCount(int takenCount) {
		this.takenCount = takenCount;
	}

	public int getAdherenceRate() {
		return adherenceRate;
	}

	public void setAdherenceRate(int adherenceRate) {
		this.adherenceRate = adherenceRate;
	}
	
	

	public int getDailyCount() {
		return dailyCount;
	}



	public void setDailyCount(int dailyCount) {
		this.dailyCount = dailyCount;
	}

    
}
