package com.medeat.auth.service.impl;

import com.medeat.auth.domain.user.entity.User;
import com.medeat.auth.domain.user.mapper.UserMapper;
import com.medeat.auth.domain.user.repository.UserRepository;
import com.medeat.auth.dto.UserDto;
import com.medeat.auth.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            UserMapper userMapper,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public boolean signup(UserDto user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPushEnabled(true);

        User entity = userMapper.toNewEntity(user);
        userRepository.save(entity);
        return true;
    }

    @Override
    public UserDto login(String loginId, String password) {
        return userRepository.findByLoginId(loginId)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(userMapper::toDto)
                .orElse(null);
    }

    @Override
    public UserDto getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElse(null);
    }

    @Override
    public UserDto getUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .map(userMapper::toDto)
                .orElse(null);
    }

    @Override
    @Transactional
    public void update(UserDto user) {
        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("userId가 없습니다.");
        }

        User entity = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        userMapper.apply(user, entity);
    }

    @Override
    public List<String> findLoginIdsByEmail(String email) {
        return userRepository.findByEmail(email)
                .stream()
                .map(User::getLoginId)
                .toList();
    }

    @Override
    @Transactional
    public boolean updatePasswordByEmail(String email, String newPassword) {
        List<User> users = userRepository.findByEmail(email);
        if (users.isEmpty()) {
            return false;
        }

        String encoded = passwordEncoder.encode(newPassword);
        users.forEach(user -> user.setPassword(encoded));
        return true;
    }
}
