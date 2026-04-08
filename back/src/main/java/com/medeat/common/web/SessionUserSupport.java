package com.medeat.common.web;

import com.medeat.auth.dto.UserDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class SessionUserSupport {

    public static final String LOGIN_USER = "loginUser";

    public UserDto getRequiredUser(HttpSession session) {
        UserDto user = getOptionalUser(session);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return user;
    }

    public UserDto getOptionalUser(HttpSession session) {
        return (UserDto) session.getAttribute(LOGIN_USER);
    }

    public void updateSessionUser(HttpSession session, UserDto user) {
        session.setAttribute(LOGIN_USER, user);
    }
}
