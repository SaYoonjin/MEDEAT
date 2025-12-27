package com.medeat.pdf.dto.medi;

public class MediPatientSummaryDto {

    // 환자 기본 정보
    private String name;
    private String patientId;      // 병원 연계 대비 (내부 ID)
    private int age;

    // 종합 평가
    private String overallGrade;   // GOOD / CAUTION / WARNING
    private String gradeReason;    // 산출 기준 요약 문장

    // 복약 요약 지표
    private int totalPrescriptionDays;
    private double adherenceRate;  // %
    private int missedDoseCount;
    private int interactionAlertCount;
    
    private String summaryComment;
    
    public MediPatientSummaryDto() {
		// TODO Auto-generated constructor stub
	}

	public MediPatientSummaryDto(String name, String patientId, int age, String overallGrade,
			String gradeReason, int totalPrescriptionDays, double adherenceRate, int missedDoseCount,
			int interactionAlertCount, String summaryComment) {
		super();
		this.name = name;
		this.patientId = patientId;
		this.age = age;
		this.overallGrade = overallGrade;
		this.gradeReason = gradeReason;
		this.totalPrescriptionDays = totalPrescriptionDays;
		this.adherenceRate = adherenceRate;
		this.missedDoseCount = missedDoseCount;
		this.interactionAlertCount = interactionAlertCount;
		this.summaryComment = summaryComment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}


	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getOverallGrade() {
		return overallGrade;
	}

	public void setOverallGrade(String overallGrade) {
		this.overallGrade = overallGrade;
	}

	public String getGradeReason() {
		return gradeReason;
	}

	public void setGradeReason(String gradeReason) {
		this.gradeReason = gradeReason;
	}

	public int getTotalPrescriptionDays() {
		return totalPrescriptionDays;
	}

	public void setTotalPrescriptionDays(int totalPrescriptionDays) {
		this.totalPrescriptionDays = totalPrescriptionDays;
	}

	public double getAdherenceRate() {
		return adherenceRate;
	}

	public void setAdherenceRate(double adherenceRate) {
		this.adherenceRate = adherenceRate;
	}

	public int getMissedDoseCount() {
		return missedDoseCount;
	}

	public void setMissedDoseCount(int missedDoseCount) {
		this.missedDoseCount = missedDoseCount;
	}

	public int getInteractionAlertCount() {
		return interactionAlertCount;
	}

	public void setInteractionAlertCount(int interactionAlertCount) {
		this.interactionAlertCount = interactionAlertCount;
	}
	
	

	public String getSummaryComment() {
		return summaryComment;
	}

	public void setSummaryComment(String summaryComment) {
		this.summaryComment = summaryComment;
	}

	@Override
	public String toString() {
		return "MediPatientSummaryDto [name=" + name + ", patientId=" + patientId + ", age=" + age + ", overallGrade="
				+ overallGrade + ", gradeReason=" + gradeReason + ", totalPrescriptionDays=" + totalPrescriptionDays
				+ ", adherenceRate=" + adherenceRate + ", missedDoseCount=" + missedDoseCount
				+ ", interactionAlertCount=" + interactionAlertCount + ", summaryComment=" + summaryComment + "]";
	}

	

	
    
    
}
