package com.medeat.auth.service.impl;

import com.medeat.auth.domain.user.entity.User;
import com.medeat.auth.domain.user.mapper.UserMapper;
import com.medeat.auth.domain.user.repository.UserRepository;
import com.medeat.auth.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setUserId(1L);
        userDto.setLoginId("tester");
        userDto.setPassword("plain");
        userDto.setEmail("test@example.com");
    }

    @Test
    void signup_encodesPasswordAndSavesUser() {
        User entity = new User();
        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        when(userMapper.toNewEntity(any(UserDto.class))).thenReturn(entity);

        boolean result = userService.signup(userDto);

        assertThat(result).isTrue();
        assertThat(userDto.getPassword()).isEqualTo("encoded");
        assertThat(userDto.isPushEnabled()).isTrue();
        verify(userRepository).save(entity);
    }

    @Test
    void login_returnsMappedUserWhenPasswordMatches() {
        User entity = new User();
        entity.setPassword("encoded");
        when(userRepository.findByLoginId("tester")).thenReturn(Optional.of(entity));
        when(passwordEncoder.matches("plain", "encoded")).thenReturn(true);
        when(userMapper.toDto(entity)).thenReturn(userDto);

        UserDto result = userService.login("tester", "plain");

        assertThat(result).isSameAs(userDto);
    }

    @Test
    void updatePasswordByEmail_updatesAllMatchedUsers() {
        User user1 = new User();
        User user2 = new User();
        when(userRepository.findByEmail("test@example.com")).thenReturn(List.of(user1, user2));
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-password");

        boolean result = userService.updatePasswordByEmail("test@example.com", "new-password");

        assertThat(result).isTrue();
        assertThat(user1.getPassword()).isEqualTo("encoded-password");
        assertThat(user2.getPassword()).isEqualTo("encoded-password");
    }
}
