package com.medeat.chat.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medeat.auth.dao.UserDao;
import com.medeat.challenge.dao.UserChallengeDao;
import com.medeat.challenge.dto.UserChallengeDto;
import com.medeat.chat.dao.ChatDao;
import com.medeat.chat.dto.ChatMessageDto;
import com.medeat.chat.service.ChatService;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatDao chatDao;

    @Autowired
    private UserChallengeDao userChallengeDao;
    
    @Autowired
    private UserDao userDao;

    @Override
    public void sendMessage(ChatMessageDto dto) {

        UserChallengeDto uc =
            userChallengeDao.selectByUserAndChallenge(
                dto.getUserId(),
                dto.getChallengeId()
            );

        if (uc == null || !"PROGRESS".equals(uc.getStatus())) {
            throw new IllegalStateException("챌린지 참여자가 아닙니다.");
        }

        String nickname = userDao.selectNicknameById(dto.getUserId());
        dto.setNickname(nickname);

        chatDao.insertMessage(dto);
    }




    @Override
    public List<ChatMessageDto> getMessages(Long challengeId) {
        return chatDao.selectByChallengeId(challengeId);
    }
}
