package com.medeat.chat.service;

import java.util.List;

import com.medeat.chat.dto.ChatMessageDto;

public interface ChatService {

    void sendMessage(ChatMessageDto dto);

    List<ChatMessageDto> getMessages(Long challengeId);
}
