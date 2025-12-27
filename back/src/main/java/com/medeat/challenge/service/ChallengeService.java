package com.medeat.challenge.service;

import java.util.List;
import java.util.Map;

import com.medeat.challenge.dto.ChallengeDto;
import com.medeat.challenge.dto.ChallengeLogDto;
import com.medeat.challenge.dto.UserChallengeDto;

public interface ChallengeService {

    List<ChallengeDto> getPopularChallenges(String modeType);

    List<ChallengeDto> getAvailableChallenges(Long userId, String modeType);

    List<UserChallengeDto> getOngoingChallenges(Long userId, String mode);

    ChallengeDto getChallengeDetail(Long challengeId);

    UserChallengeDto getUserChallenge(Long userChallengeId);

    void createChallenge(ChallengeDto dto);

    // ✅ 결과 메시지/재참여/이미참여중 구분하려고 Map 반환 추천
    Map<String, Object> joinChallenge(Long userId, Long challengeId);

    // ✅ 본인만 포기 가능하게 userId 받기
    void giveUpChallenge(Long userId, Long userChallengeId);

    List<ChallengeLogDto> getLogs(Long userChallengeId);

    // ✅ 본인만 로그 가능하게 userId 받기
    void addLog(Long userId, ChallengeLogDto dto);

    ChallengeLogDto getLogById(Long logId);

    void deleteLog(Long logId);

    boolean deleteChallenge(Long challengeId, Long userId);
    
    
}
