package com.medeat.mypage.service.impl;

import com.medeat.auth.dto.UserDto;
import com.medeat.auth.service.UserService;
import com.medeat.challenge.dao.ChallengeDao;
import com.medeat.challenge.dto.ChallengeDto;
import com.medeat.community.dto.MyPostPreviewDto;
import com.medeat.community.dto.PostDto;
import com.medeat.mypage.dao.MyPageDao;
import com.medeat.mypage.service.MyPageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MyPageServiceImpl implements MyPageService {

    private final UserService userService;
    private final MyPageDao myPageDao;
    private final ChallengeDao challengeDao;

    public MyPageServiceImpl(
            UserService userService,
            MyPageDao myPageDao,
            ChallengeDao challengeDao
    ) {
        this.userService = userService;
        this.myPageDao = myPageDao;
        this.challengeDao = challengeDao;
    }

    @Override
    public UserDto getUser(Long userId) {
        return userService.getUserById(userId);
    }

    @Override
    public void updateUser(UserDto dto) {
        UserDto origin = requireUser(dto.getUserId());
        applyEditableProfileFields(dto, origin);
        userService.update(origin);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        myPageDao.deletePostLikesByUser(userId);
        myPageDao.deletePostScrapsByUser(userId);
        myPageDao.deleteCommentsByUser(userId);

        myPageDao.deletePostLikesOnUserPosts(userId);
        myPageDao.deletePostScrapsOnUserPosts(userId);
        myPageDao.deleteCommentsOnUserPosts(userId);
        myPageDao.deletePostsByUser(userId);

        myPageDao.deleteDietItemsByUser(userId);
        myPageDao.deleteDietLogsByUser(userId);

        myPageDao.deleteMedicationLogsByUser(userId);
        myPageDao.deleteMedicationsByUser(userId);
        myPageDao.deleteMedInfoByUser(userId);
        myPageDao.deleteUserDiseaseByUser(userId);

        myPageDao.deleteChallengeLogsByUser(userId);
        myPageDao.deleteUserChallengesByUser(userId);
        myPageDao.deleteChallengeChatByUser(userId);
        myPageDao.deleteChallengesByUser(userId);

        myPageDao.deleteNotificationsByUser(userId);
        myPageDao.deletePushSubscriptionsByUser(userId);
        myPageDao.deleteUserBadgesByUser(userId);

        myPageDao.deleteDailyXpByUser(userId);
        myPageDao.deleteXpLogByUser(userId);
        myPageDao.deleteUserStreakByUser(userId);
        myPageDao.deleteUserProgressByUser(userId);

        userService.delete(userId);
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

    private UserDto requireUser(Long userId) {
        UserDto user = userService.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        return user;
    }

    private void applyEditableProfileFields(UserDto source, UserDto target) {
        target.setName(source.getName());
        target.setNickname(source.getNickname());
        target.setEmail(source.getEmail());
        target.setPhone(source.getPhone());
        target.setGender(source.getGender());
        target.setAge(source.getAge());
        target.setHeight(source.getHeight());
        target.setWeight(source.getWeight());
        target.setGoalType(source.getGoalType());
        target.setPregnantStatus(source.getPregnantStatus());
        target.setPregnancyWeek(source.getPregnancyWeek());
    }
}
