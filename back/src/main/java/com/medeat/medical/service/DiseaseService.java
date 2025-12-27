package com.medeat.medical.service;

import java.util.List;

import com.medeat.medical.dto.DiseaseDto;

public interface DiseaseService {
	
	List<DiseaseDto> getAllDiseases();

    List<DiseaseDto> searchDisease(String keyword);

    void addDiseaseToUser(Long userId, Long diseaseId);

    Long findOrCreate(String diseaseName);

	List<DiseaseDto> getUserDiseaseList(Long userId);

	void deleteDisease(Long userDiseaseId);
	
	 boolean hasDisease(Long userId, Long diseaseId);

}
