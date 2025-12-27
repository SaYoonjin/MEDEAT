package com.medeat.medical.dto;

public class MedicationDto {

    private Long medicationId;
    private Long userId;
    private String drugName;
    private Long itemSeq;
    private String ingredient;
    private String dose;
    private String intakeTime;
    private Integer intervalHour;
    private String memo;
    private int dailyCount;
    private String recommended;

    public MedicationDto() {}

    

    public MedicationDto(Long medicationId, Long userId, String drugName, Long itemSeq, String ingredient, String dose,
			String intakeTime, Integer intervalHour, String memo, int dailyCount, String recommended) {
		super();
		this.medicationId = medicationId;
		this.userId = userId;
		this.drugName = drugName;
		this.itemSeq = itemSeq;
		this.ingredient = ingredient;
		this.dose = dose;
		this.intakeTime = intakeTime;
		this.intervalHour = intervalHour;
		this.memo = memo;
		this.dailyCount = dailyCount;
		this.recommended = recommended;
	}



	public Long getMedicationId() { return medicationId; }
    public void setMedicationId(Long medicationId) { this.medicationId = medicationId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getDrugName() { return drugName; }
    public void setDrugName(String drugName) { this.drugName = drugName; }

    public String getIngredient() { return ingredient; }
    public void setIngredient(String ingredient) { this.ingredient = ingredient; }

    public String getDose() { return dose; }
    public void setDose(String dose) { this.dose = dose; }

    public String getIntakeTime() { return intakeTime; }
    public void setIntakeTime(String intakeTime) { this.intakeTime = intakeTime; }

    public Integer getIntervalHour() { return intervalHour; }
    public void setIntervalHour(Integer intervalHour) { this.intervalHour = intervalHour; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }

	public int getDailyCount() {
		return dailyCount;
	}

	public void setDailyCount(int dailyCount) {
		this.dailyCount = dailyCount;
	}
	
	
	
	

	public Long getItemSeq() {
		return itemSeq;
	}



	public void setItemSeq(Long itemSeq) {
		this.itemSeq = itemSeq;
	}



	public String getRecommended() {
		return recommended;
	}

	public void setRecommended(String recommended) {
		this.recommended = recommended;
	}



	@Override
	public String toString() {
		return "MedicationDto [medicationId=" + medicationId + ", userId=" + userId + ", drugName=" + drugName
				+ ", itemSeq=" + itemSeq + ", ingredient=" + ingredient + ", dose=" + dose + ", intakeTime="
				+ intakeTime + ", intervalHour=" + intervalHour + ", memo=" + memo + ", dailyCount=" + dailyCount
				+ ", recommended=" + recommended + "]";
	}

	
	
}
