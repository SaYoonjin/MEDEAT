package com.medeat.pdf.dto.medi;

import java.util.List;

public class MediSafetyReportDto {

    // 현재 복용 중인 약 요약
    private List<MediMedicationSummaryDto> medications;

    // 상호작용 경고
    private List<MediInteractionAlertDto> interactionAlerts;

    // 중복 성분 목록
    private List<String> duplicatedIngredients;
    
    public MediSafetyReportDto() {
		// TODO Auto-generated constructor stub
	}

	public MediSafetyReportDto(List<MediMedicationSummaryDto> medications,
			List<MediInteractionAlertDto> interactionAlerts, List<String> duplicatedIngredients) {
		super();
		this.medications = medications;
		this.interactionAlerts = interactionAlerts;
		this.duplicatedIngredients = duplicatedIngredients;
	}

	public List<MediMedicationSummaryDto> getMedications() {
		return medications;
	}

	public void setMedications(List<MediMedicationSummaryDto> medications) {
		this.medications = medications;
	}

	public List<MediInteractionAlertDto> getInteractionAlerts() {
		return interactionAlerts;
	}

	public void setInteractionAlerts(List<MediInteractionAlertDto> interactionAlerts) {
		this.interactionAlerts = interactionAlerts;
	}

	public List<String> getDuplicatedIngredients() {
		return duplicatedIngredients;
	}

	public void setDuplicatedIngredients(List<String> duplicatedIngredients) {
		this.duplicatedIngredients = duplicatedIngredients;
	}

	@Override
	public String toString() {
		return "MediSafetyReportDto [medications=" + medications + ", interactionAlerts=" + interactionAlerts
				+ ", duplicatedIngredients=" + duplicatedIngredients + "]";
	}
    
    
}
