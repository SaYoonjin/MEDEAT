package com.medeat.medical.service.impl;

import com.medeat.auth.domain.user.entity.User;
import com.medeat.auth.domain.user.repository.UserRepository;
import com.medeat.medical.domain.medinfo.entity.MedInfo;
import com.medeat.medical.domain.medinfo.mapper.MedInfoMapper;
import com.medeat.medical.domain.medinfo.repository.MedInfoRepository;
import com.medeat.medical.dto.MedInfoDto;
import com.medeat.medical.service.MedInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MedInfoServiceImpl implements MedInfoService {

    private final MedInfoRepository medInfoRepository;
    private final UserRepository userRepository;
    private final MedInfoMapper medInfoMapper;

    public MedInfoServiceImpl(
            MedInfoRepository medInfoRepository,
            UserRepository userRepository,
            MedInfoMapper medInfoMapper
    ) {
        this.medInfoRepository = medInfoRepository;
        this.userRepository = userRepository;
        this.medInfoMapper = medInfoMapper;
    }

    @Override
    public void save(MedInfoDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        MedInfo entity = medInfoMapper.toEntity(dto, user);
        medInfoRepository.save(entity);
    }
}
