package com.medeat.auth.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.medeat.auth.dao.UserDao;
import com.medeat.auth.dto.UserDto;
import com.medeat.auth.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public boolean signup(UserDto user) {
        String encodedPw = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPw);

        // 🔔 가입 시 기본값: 알림 ON
        user.setPushEnabled(true);

        return userDao.insert(user) > 0;
    }

    @Override
    public UserDto login(String loginId, String password) {

        UserDto dbUser = userDao.selectByLoginId(loginId);

        if (dbUser == null) {
            return null;
        }

        if (!passwordEncoder.matches(password, dbUser.getPassword())) {
            return null;
        }

        return dbUser;
    }

    @Override
    public UserDto getUserById(Long userId) {
        return userDao.selectById(userId);
    }

    @Override
    public UserDto getUserByLoginId(String loginId) {
        return userDao.selectByLoginId(loginId);
    }

    @Override
    public void update(UserDto user) {
        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("userId 없음 → update 불가");
        }
        userDao.update(user);
    }

    @Override
    public List<String> findLoginIdsByEmail(String email) {
        return userDao.findLoginIdsByEmail(email);
    }


	@Override
	public boolean updatePasswordByEmail(String email, String newPassword) {
		String encoded = passwordEncoder.encode(newPassword);
	    return userDao.updatePasswordByEmail(email, encoded) > 0;
	}
}