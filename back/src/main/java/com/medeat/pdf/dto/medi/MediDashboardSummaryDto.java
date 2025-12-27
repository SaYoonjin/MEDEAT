package com.medeat.pdf.dto.medi;

public class MediDashboardSummaryDto {
    private int dangerCount;
    private int warningCount;
    private int safeCount;
    
    public MediDashboardSummaryDto() {
		// TODO Auto-generated constructor stub
	}

	public MediDashboardSummaryDto(int dangerCount, int warningCount, int safeCount) {
		super();
		this.dangerCount = dangerCount;
		this.warningCount = warningCount;
		this.safeCount = safeCount;
	}

	public int getDangerCount() {
		return dangerCount;
	}

	public void setDangerCount(int dangerCount) {
		this.dangerCount = dangerCount;
	}

	public int getWarningCount() {
		return warningCount;
	}

	public void setWarningCount(int warningCount) {
		this.warningCount = warningCount;
	}

	public int getSafeCount() {
		return safeCount;
	}

	public void setSafeCount(int safeCount) {
		this.safeCount = safeCount;
	}

	@Override
	public String toString() {
		return "PdfMedDashboardDto [dangerCount=" + dangerCount + ", warningCount=" + warningCount + ", safeCount="
				+ safeCount + "]";
	}
    
    
}
