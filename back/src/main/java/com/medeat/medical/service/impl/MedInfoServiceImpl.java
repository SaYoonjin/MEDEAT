package com.medeat.medical.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medeat.medical.dao.MedInfoDao;
import com.medeat.medical.dto.MedInfoDto;
import com.medeat.medical.service.MedInfoService;

@Service
public class MedInfoServiceImpl implements MedInfoService {

    @Autowired
    private MedInfoDao medInfoDao;

    @Override
    public void save(MedInfoDto dto) {
        medInfoDao.insert(dto);
    }
}