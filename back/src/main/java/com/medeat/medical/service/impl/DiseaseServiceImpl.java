package com.medeat.medical.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medeat.medical.dao.DiseaseDao;
import com.medeat.medical.dto.DiseaseDto;
import com.medeat.medical.service.DiseaseService;

@Service
public class DiseaseServiceImpl implements DiseaseService {

    @Autowired
    private DiseaseDao diseaseDao;

    @Override
    public List<DiseaseDto> getAllDiseases() {
        return diseaseDao.getAllDiseases();
    }

    @Override
    public List<DiseaseDto> searchDisease(String keyword) {
        return diseaseDao.searchDisease(keyword);
    }

    @Override
    public void addDiseaseToUser(Long userId, Long diseaseId) {
        diseaseDao.insertUserDisease(userId, diseaseId);
    }

    @Override
    @Transactional
    public Long findOrCreate(String diseaseName) {
        DiseaseDto existing = diseaseDao.findByName(diseaseName);

        if (existing != null)
            return existing.getDiseaseId();

        diseaseDao.insertDisease(diseaseName);
        return diseaseDao.getLastInsertedId();
    }

	@Override
	public List<DiseaseDto> getUserDiseaseList(Long userId) {
		return diseaseDao.getUserDiseaseList(userId);
	}

	@Override
	public void deleteDisease(Long userDiseaseId) {
	    diseaseDao.deleteDisease(userDiseaseId);
	}

	@Override
	public boolean hasDisease(Long userId, Long diseaseId) {
		return diseaseDao.countUserDisease(userId, diseaseId) > 0;
	}

}
