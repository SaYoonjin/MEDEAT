package com.medeat.challenge.dao;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.medeat.challenge.dto.ChallengeLogDto;

public interface ChallengeLogDao {

    List<ChallengeLogDto> selectByUserChallenge(@Param("userChallengeId") Long userChallengeId);

    int insertLog(ChallengeLogDto dto);

    ChallengeLogDto selectById(@Param("logId") Long logId);

    void deleteLog(@Param("logId") Long logId);

    void deleteByChallengeId(@Param("challengeId") Long challengeId);

    ChallengeLogDto selectByUserChallengeAndDate(
            @Param("userChallengeId") Long userChallengeId,
            @Param("logDate") LocalDate logDate);
    
    int countSuccessLogs(Long userChallengeId);
    
    int countByUserChallengeAndStatus(
            @Param("userChallengeId") Long userChallengeId,
            @Param("status") String status
        );

}