package com.medeat.medical.domain.medinfo.mapper;

import com.medeat.auth.domain.user.entity.User;
import com.medeat.medical.domain.medinfo.entity.MedInfo;
import com.medeat.medical.dto.MedInfoDto;
import org.springframework.stereotype.Component;

@Component
public class MedInfoMapper {

    public MedInfo toEntity(MedInfoDto dto, User user) {
        MedInfo entity = new MedInfo();
        entity.setUser(user);
        entity.setUserId(user.getUserId());
        entity.setDisease(dto.getDisease());
        entity.setMedicine(dto.getMedicine());
        return entity;
    }
}
