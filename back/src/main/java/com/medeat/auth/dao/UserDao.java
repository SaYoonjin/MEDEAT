package com.medeat.auth.dao;

import java.util.List;

import com.medeat.auth.dto.UserDto;

public interface UserDao {

    // PK 기반 조회
    UserDto selectById(Long userId);

    // 로그인 ID 기반 조회 (로그인용)
    UserDto selectByLoginId(String loginId);
    
    String selectNicknameById(Long userId);

    // 회원가입
    int insert(UserDto dto);

    // 회원정보 수정
    int update(UserDto user);

    // 회원 삭제
    int delete(Long userId);
    
    List<String> findLoginIdsByEmail(String email);

    int updatePasswordByEmail(String email, String password);

}