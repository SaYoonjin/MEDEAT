package com.medeat.mypage.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medeat.auth.dao.UserDao;
import com.medeat.auth.dto.UserDto;
import com.medeat.challenge.dao.ChallengeDao;
import com.medeat.challenge.dto.ChallengeDto;
import com.medeat.community.dto.MyPostPreviewDto;
import com.medeat.community.dto.PostDto;
import com.medeat.mypage.dao.MyPageDao;
import com.medeat.mypage.service.MyPageService;

@Service
public class MyPageServiceImpl implements MyPageService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private MyPageDao myPageDao;

    @Autowired
    private ChallengeDao challengeDao;

    @Override
    public UserDto getUser(Long userId) {
        return userDao.selectById(userId);
    }

    @Override
    public void updateUser(UserDto dto) {
        userDao.update(dto);
    }

    /**
     * ✅ 회원 탈퇴: 유저의 모든 데이터 영구 삭제
     * - 스키마 상 대부분 CASCADE지만,
     * - 커뮤니티(내 글에 달린 좋아요/스크랩/댓글 등)처럼
     *   "내 글"을 참조하는 데이터는 사용자 CASCADE만으로는 안 지워질 수 있어서
     *   안전하게 정리 삭제 후 마지막에 user 삭제
     */
    @Override
    @Transactional
    public void deleteUser(Long userId) {

        // 1) COMMUNITY - 내가 한 행동
        myPageDao.deletePostLikesByUser(userId);
        myPageDao.deletePostScrapsByUser(userId);
        myPageDao.deleteCommentsByUser(userId);

        // 1-2) COMMUNITY - 내 글에 달린 것들
        myPageDao.deletePostLikesOnUserPosts(userId);
        myPageDao.deletePostScrapsOnUserPosts(userId);
        myPageDao.deleteCommentsOnUserPosts(userId);

        // 1-3) COMMUNITY - 내 글 삭제
        myPageDao.deletePostsByUser(userId);

        // 2) DIET
        myPageDao.deleteDietItemsByUser(userId);
        myPageDao.deleteDietLogsByUser(userId);

        // 3) MED / DISEASE / MED_INFO
        myPageDao.deleteMedicationLogsByUser(userId);
        myPageDao.deleteMedicationsByUser(userId);
        myPageDao.deleteMedInfoByUser(userId);
        myPageDao.deleteUserDiseaseByUser(userId);

        // 4) CHALLENGE
        myPageDao.deleteChallengeLogsByUser(userId);
        myPageDao.deleteUserChallengesByUser(userId);
        myPageDao.deleteChallengeChatByUser(userId);
        myPageDao.deleteChallengesByUser(userId);

        // 5) NOTIFICATION / PUSH / BADGE
        myPageDao.deleteNotificationsByUser(userId);
        myPageDao.deletePushSubscriptionsByUser(userId);
        myPageDao.deleteUserBadgesByUser(userId);

        // 6) GAMIFICATION
        myPageDao.deleteDailyXpByUser(userId);
        myPageDao.deleteXpLogByUser(userId);
        myPageDao.deleteUserStreakByUser(userId);
        myPageDao.deleteUserProgressByUser(userId);

        // 7) ✅ 마지막: USER 삭제
        userDao.delete(userId);
    }

    @Override
    public List<PostDto> getMyPosts(Long userId) {
        return myPageDao.selectMyPosts(userId);
    }

    @Override
    public List<PostDto> getMyScraps(Long userId) {
        return myPageDao.selectMyScraps(userId);
    }

    @Override
    public List<ChallengeDto> getMyChallenges(Long userId) {
        return challengeDao.getChallengesByWriter(userId);
    }

    @Override
    public List<MyPostPreviewDto> getMyLikes(Long userId) {
        return myPageDao.selectMyLikes(userId);
    }
}
