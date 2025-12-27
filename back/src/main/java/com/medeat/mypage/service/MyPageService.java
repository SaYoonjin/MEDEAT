package com.medeat.mypage.service;

import java.util.List;

import com.medeat.auth.dto.UserDto;
import com.medeat.challenge.dto.ChallengeDto;
import com.medeat.community.dto.MyPostPreviewDto;
import com.medeat.community.dto.PostDto;

public interface MyPageService {

    // 개인정보
    UserDto getUser(Long userId);
    void updateUser(UserDto dto);
    void deleteUser(Long userId);

    // 내 글
    List<PostDto> getMyPosts(Long userId);

    // 내 스크랩
    List<PostDto> getMyScraps(Long userId);
    
    // 내가 만든 챌린지 목록
    List<ChallengeDto> getMyChallenges(Long userId);
    
    List<MyPostPreviewDto> getMyLikes(Long userId);
}