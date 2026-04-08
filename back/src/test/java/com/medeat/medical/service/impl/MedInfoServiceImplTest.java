package com.medeat.medical.service.impl;

import com.medeat.auth.domain.user.entity.User;
import com.medeat.auth.domain.user.repository.UserRepository;
import com.medeat.medical.domain.medinfo.entity.MedInfo;
import com.medeat.medical.domain.medinfo.mapper.MedInfoMapper;
import com.medeat.medical.domain.medinfo.repository.MedInfoRepository;
import com.medeat.medical.dto.MedInfoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedInfoServiceImplTest {

    @Mock
    private MedInfoRepository medInfoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MedInfoMapper medInfoMapper;

    @InjectMocks
    private MedInfoServiceImpl medInfoService;

    @Test
    void save_loadsUserAndPersistsMappedEntity() {
        MedInfoDto dto = new MedInfoDto();
        dto.setUserId(1L);

        User user = new User();
        user.setUserId(1L);
        MedInfo medInfo = new MedInfo();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(medInfoMapper.toEntity(dto, user)).thenReturn(medInfo);

        medInfoService.save(dto);

        verify(medInfoRepository).save(medInfo);
    }
}
