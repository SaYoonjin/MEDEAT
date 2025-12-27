package com.medeat.chat.dao;

import java.util.List;

import com.medeat.chat.dto.ChatMessageDto;

public interface ChatDao {

    int insertMessage(ChatMessageDto dto);

    List<ChatMessageDto> selectByChallengeId(Long challengeId);
}
