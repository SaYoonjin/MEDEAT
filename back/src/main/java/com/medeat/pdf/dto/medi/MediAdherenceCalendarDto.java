package com.medeat.pdf.dto.medi;

public class MediAdherenceCalendarDto {

    private String date;   // yyyy-MM-dd
    private String status; // O / X / △
    
    public MediAdherenceCalendarDto() {
		// TODO Auto-generated constructor stub
	}

	public MediAdherenceCalendarDto(String date, String status) {
		super();
		this.date = date;
		this.status = status;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "MediAdherenceCalendarDto [date=" + date + ", status=" + status + "]";
	}
    
    
}
