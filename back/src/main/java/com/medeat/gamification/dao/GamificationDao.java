package com.medeat.gamification.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface GamificationDao {

    // user_progress
    Map<String, Object> selectUserProgress(@Param("userId") long userId);

    int upsertUserProgress(@Param("userId") long userId,
                           @Param("deltaXp") int deltaXp,
                           @Param("newLevel") int newLevel,
                           @Param("deltaMasterXp") int deltaMasterXp);

    // daily_xp
    Integer selectTodayXp(@Param("userId") long userId, @Param("day") String day);
    int upsertDailyXp(@Param("userId") long userId, @Param("day") String day, @Param("delta") int delta);

    // xp_log
    int insertXpLog(@Param("userId") long userId,
                    @Param("actionType") String actionType,
                    @Param("refId") String refId,
                    @Param("xp") int xp,
                    @Param("day") String day);
    
 // xp_log count (하루 N회 제한용)
    int countXpLogByActionDate(@Param("userId") long userId,
                               @Param("actionType") String actionType,
                               @Param("day") String day);

    // streak
    Map<String, Object> selectUserStreak(@Param("userId") long userId);
    int upsertUserStreak(@Param("userId") long userId,
                         @Param("currentStreak") int currentStreak,
                         @Param("lastActionDate") String lastActionDate);
}
