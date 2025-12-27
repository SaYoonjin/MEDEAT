package com.medeat.mypage.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.medeat.community.dto.MyPostPreviewDto;
import com.medeat.community.dto.PostDto;

public interface MyPageDao {

    /* =========================
       조회
    ========================= */

    // 내 글 목록
    List<PostDto> selectMyPosts(Long userId);

    // 내가 저장한 글 목록
    List<PostDto> selectMyScraps(Long userId);

    // 내가 좋아요한 글 목록(프리뷰)
    List<MyPostPreviewDto> selectMyLikes(Long userId);


    /* =========================
       회원 탈퇴용 삭제(정리 삭제)
       - 최종 스키마 기준 테이블들
    ========================= */

    // 1) COMMUNITY (내가 누른 좋아요/스크랩/댓글)
    int deletePostLikesByUser(@Param("userId") Long userId);
    int deletePostScrapsByUser(@Param("userId") Long userId);
    int deleteCommentsByUser(@Param("userId") Long userId);

    // 1-2) 내가 쓴 글에 달린 좋아요/스크랩/댓글 (글 삭제 전에 정리)
    int deletePostLikesOnUserPosts(@Param("userId") Long userId);
    int deletePostScrapsOnUserPosts(@Param("userId") Long userId);
    int deleteCommentsOnUserPosts(@Param("userId") Long userId);

    // 1-3) 내가 쓴 글 삭제
    int deletePostsByUser(@Param("userId") Long userId);

    // 2) DIET
    int deleteDietItemsByUser(@Param("userId") Long userId);
    int deleteDietLogsByUser(@Param("userId") Long userId);

    // 3) MED / DISEASE / MED_INFO
    int deleteMedicationLogsByUser(@Param("userId") Long userId);
    int deleteMedicationsByUser(@Param("userId") Long userId);
    int deleteMedInfoByUser(@Param("userId") Long userId);
    int deleteUserDiseaseByUser(@Param("userId") Long userId);

    // 4) CHALLENGE
    int deleteChallengeLogsByUser(@Param("userId") Long userId);      // user_challenge -> challenge_log
    int deleteUserChallengesByUser(@Param("userId") Long userId);     // user_challenge
    int deleteChallengeChatByUser(@Param("userId") Long userId);      // challenge_chat_message
    int deleteChallengesByUser(@Param("userId") Long userId);         // challenge (내가 만든 것)

    // 5) NOTIFICATION / PUSH / BADGE
    int deleteNotificationsByUser(@Param("userId") Long userId);
    int deletePushSubscriptionsByUser(@Param("userId") Long userId);
    int deleteUserBadgesByUser(@Param("userId") Long userId);

    // 6) GAMIFICATION
    int deleteDailyXpByUser(@Param("userId") Long userId);
    int deleteXpLogByUser(@Param("userId") Long userId);
    int deleteUserStreakByUser(@Param("userId") Long userId);
    int deleteUserProgressByUser(@Param("userId") Long userId);
}
