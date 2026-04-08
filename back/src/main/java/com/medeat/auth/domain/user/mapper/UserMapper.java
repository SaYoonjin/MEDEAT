package com.medeat.auth.domain.user.mapper;

import com.medeat.auth.domain.user.entity.User;
import com.medeat.auth.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User entity) {
        if (entity == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setUserId(entity.getUserId());
        dto.setLoginId(entity.getLoginId());
        dto.setPassword(entity.getPassword());
        dto.setName(entity.getName());
        dto.setNickname(entity.getNickname());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setGender(entity.getGender());
        dto.setAge(entity.getAge());
        dto.setHeight(entity.getHeight());
        dto.setWeight(entity.getWeight());
        dto.setGoalType(entity.getGoalType());
        dto.setDefaultMode(entity.getDefaultMode());
        dto.setPregnantStatus(entity.getPregnantStatus());
        dto.setPregnancyWeek(entity.getPregnancyWeek());
        dto.setPushEnabled(Boolean.TRUE.equals(entity.getPushEnabled()));
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public User toNewEntity(UserDto dto) {
        User entity = new User();
        entity.setLoginId(dto.getLoginId());
        entity.setPassword(dto.getPassword());
        applyProfile(dto, entity);
        return entity;
    }

    public void apply(UserDto dto, User entity) {
        applyProfile(dto, entity);
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            entity.setPassword(dto.getPassword());
        }
    }

    private void applyProfile(UserDto dto, User entity) {
        entity.setName(dto.getName());
        entity.setNickname(dto.getNickname());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setGender(dto.getGender());
        entity.setAge(dto.getAge());
        entity.setHeight(dto.getHeight());
        entity.setWeight(dto.getWeight());
        entity.setGoalType(dto.getGoalType());
        entity.setDefaultMode(dto.getDefaultMode());
        entity.setPregnantStatus(dto.getPregnantStatus());
        entity.setPregnancyWeek(dto.getPregnancyWeek());
        entity.setPushEnabled(dto.isPushEnabled());
    }
}
