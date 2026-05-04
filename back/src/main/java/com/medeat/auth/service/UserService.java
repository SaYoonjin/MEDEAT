package com.medeat.auth.service;

import java.util.List;

import com.medeat.auth.dto.UserDto;

public interface UserService {
    
    boolean signup(UserDto user);

    // 로그인: loginId + password 입력
    UserDto login(String loginId, String password);

    // PK 기반 회원 조회
    UserDto getUserById(Long userId);

    // loginId 기반 회원 조회
    UserDto getUserByLoginId(String loginId);

	void update(UserDto origin);

	void delete(Long userId);

	void updatePushEnabled(Long userId, boolean enabled);
	
	List<String> findLoginIdsByEmail(String email);

	boolean updatePasswordByEmail(String email, String newPassword);

}
