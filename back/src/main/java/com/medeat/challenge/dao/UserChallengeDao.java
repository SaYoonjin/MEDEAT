package com.medeat.challenge.dao;

import java.time.LocalDate;
import java.util.List;


import com.medeat.challenge.dto.UserChallengeDto;

public interface UserChallengeDao {

    // 조회
    List<UserChallengeDto> selectByUserAndMode(Long userId, String modeType);
    UserChallengeDto selectById(Long userChallengeId);
    UserChallengeDto selectByUserAndChallenge(Long userId, Long challengeId);
    List<UserChallengeDto> selectByStatus(String status);
    int countByChallengeAndStatus(Long challengeId, String status);

    // 생성 / 상태 변경
    int insertUserChallenge(UserChallengeDto dto);
    int updateStatus(Long userChallengeId, String status);
    int rejoin(Long userChallengeId, LocalDate startDate);
    int finish(Long userChallengeId, LocalDate endDate);

    // 삭제
    void deleteByChallengeId(Long challengeId);
}
