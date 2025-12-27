package com.medeat.chat.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.medeat.challenge.dao.UserChallengeDao;
import com.medeat.challenge.dto.UserChallengeDto;
import com.medeat.chat.dto.ChatMessageDto;
import com.medeat.chat.service.ChatService;

@RestController
public class ChatRestController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private UserChallengeDao userChallengeDao;

    // 과거 채팅 조회 (REST)
    @GetMapping("/api/challenge/{challengeId}/chat")
    public List<ChatMessageDto> getMessages(@PathVariable Long challengeId) {
        return chatService.getMessages(challengeId);
    }

    // 실시간 채팅 (WebSocket)
    @MessageMapping("/chat/send")
    public void send(
            ChatMessageDto dto,
            @Header("simpSessionAttributes") Map<String, Object> sessionAttrs
    ) {
        String userIdStr = (String) sessionAttrs.get("userId");

        if (userIdStr == null) {
            throw new IllegalStateException("WebSocket userId 없음");
        }

        Long userId = Long.valueOf(userIdStr);

        dto.setUserId(userId);

        chatService.sendMessage(dto);

        messagingTemplate.convertAndSend(
            "/sub/chat/room/" + dto.getChallengeId(),
            dto
        );
    }

}
