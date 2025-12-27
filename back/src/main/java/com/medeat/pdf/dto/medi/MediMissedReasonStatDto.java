package com.medeat.pdf.dto.medi;

public class MediMissedReasonStatDto {

    private String reason; // 망각 / 부작용 / 약 없음 등
    private int count;
    private double ratio;  // %
	public MediMissedReasonStatDto(String reason, int count, double ratio) {
		super();
		this.reason = reason;
		this.count = count;
		this.ratio = ratio;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public double getRatio() {
		return ratio;
	}
	public void setRatio(double ratio) {
		this.ratio = ratio;
	}
	@Override
	public String toString() {
		return "MediMissedReasonStatDto [reason=" + reason + ", count=" + count + ", ratio=" + ratio + "]";
	}
    
    
}
