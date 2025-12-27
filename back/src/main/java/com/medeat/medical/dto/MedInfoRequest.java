package com.medeat.medical.dto;

import lombok.Data;

@Data
public class MedInfoRequest {
    private String disease;
    private String medicine;
    private String pregnant;
    private Integer pregnancyWeek;
    
    public MedInfoRequest() {
		// TODO Auto-generated constructor stub
	}

	public MedInfoRequest(String disease, String medicine, String pregnant, Integer pregnancyWeek) {
		super();
		this.disease = disease;
		this.medicine = medicine;
		this.pregnant = pregnant;
		this.pregnancyWeek = pregnancyWeek;
	}

	public String getDisease() {
		return disease;
	}

	public void setDisease(String disease) {
		this.disease = disease;
	}

	public String getMedicine() {
		return medicine;
	}

	public void setMedicine(String medicine) {
		this.medicine = medicine;
	}

	public String getPregnant() {
		return pregnant;
	}

	public void setPregnant(String pregnant) {
		this.pregnant = pregnant;
	}

	public Integer getPregnancyWeek() {
		return pregnancyWeek;
	}

	public void setPregnancyWeek(Integer pregnancyWeek) {
		this.pregnancyWeek = pregnancyWeek;
	}

	@Override
	public String toString() {
		return "MedInfoRequest [disease=" + disease + ", medicine=" + medicine + ", pregnant=" + pregnant
				+ ", pregnancyWeek=" + pregnancyWeek + "]";
	}
    
    
}

