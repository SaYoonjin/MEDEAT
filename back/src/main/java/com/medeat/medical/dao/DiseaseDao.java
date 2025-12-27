package com.medeat.medical.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.medeat.medical.dto.DiseaseDto;

@Mapper
public interface DiseaseDao {
	
	List<DiseaseDto> getAllDiseases();

    List<DiseaseDto> searchDisease(String keyword);

    DiseaseDto findByName(String name);

    void insertDisease(String name);

    Long getLastInsertedId();

    void insertUserDisease(@Param("userId") Long userId,
                           @Param("diseaseId") Long diseaseId);

    void deleteUserDisease(@Param("userId") Long userId,
                           @Param("diseaseId") Long diseaseId);

	List<DiseaseDto> getUserDiseaseList(Long userId);

	void deleteDisease(Long userDiseaseId);

	int countUserDisease(Long userId, Long diseaseId);

}
