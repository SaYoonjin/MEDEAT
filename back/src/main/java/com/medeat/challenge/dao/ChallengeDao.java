package com.medeat.challenge.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.medeat.challenge.dto.ChallengeDto;

public interface ChallengeDao {

    List<ChallengeDto> selectPopularByMode(String modeType);

    List<ChallengeDto> selectAllByMode(String modeType);

    List<ChallengeDto> selectAvailableForUser(@Param("userId") Long userId,
                                              @Param("modeType") String modeType);

    ChallengeDto selectById(@Param("challengeId") Long challengeId);

    int insertChallenge(ChallengeDto dto);

    // ✅ 참여자 +1 (정원 초과 방지)
    int increaseCurrentParticipants(@Param("challengeId") Long challengeId);

    // ✅ 참여자 -1 (0 미만 방지)
    int decreaseCurrentParticipants(@Param("challengeId") Long challengeId);

    // ✅ (선택/추천) user_challenge(PROGRESS) 기준으로 강제 동기화
    int syncCurrentParticipants(@Param("challengeId") Long challengeId);

    void deleteChallenge(@Param("challengeId") Long challengeId);

    List<ChallengeDto> getChallengesByWriter(@Param("userId") Long userId);
}