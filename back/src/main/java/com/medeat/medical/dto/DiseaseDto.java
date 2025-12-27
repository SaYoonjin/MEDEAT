package com.medeat.medical.dto;

public class DiseaseDto {

    private Long diseaseId;
    private String diseaseName;
    private Long userDiseaseId;

    public DiseaseDto() {}

    public DiseaseDto(Long diseaseId, String diseaseName, Long userDiseaseId) {
        this.diseaseId = diseaseId;
        this.diseaseName = diseaseName;
        this.userDiseaseId = userDiseaseId;
    }

    public Long getDiseaseId() { return diseaseId; }
    public void setDiseaseId(Long diseaseId) { this.diseaseId = diseaseId; }

    public String getDiseaseName() { return diseaseName; }
    public void setDiseaseName(String diseaseName) { this.diseaseName = diseaseName; }
    
    

    public Long getUserDiseaseId() {
		return userDiseaseId;
	}

	public void setUserDiseaseId(Long userDiseaseId) {
		this.userDiseaseId = userDiseaseId;
	}

	@Override
	public String toString() {
		return "DiseaseDto [diseaseId=" + diseaseId + ", diseaseName=" + diseaseName + ", userDiseaseId="
				+ userDiseaseId + "]";
	}

	
}
