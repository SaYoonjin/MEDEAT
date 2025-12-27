package com.medeat.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
            StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            // 🔥 HTTP 세션에서 로그인 유저 가져오기
            HttpSession session =
                (HttpSession) accessor.getSessionAttributes().get("HTTP_SESSION");

            if (session != null) {
                Long userId = (Long) session.getAttribute("LOGIN_USER_ID");

                if (userId != null) {
                    accessor.setUser(() -> userId.toString());
                }
            }
        }

        return message;
    }
}
