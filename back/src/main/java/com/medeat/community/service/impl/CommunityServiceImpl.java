package com.medeat.community.service.impl;

import com.medeat.community.dao.CommunityDao;
import com.medeat.community.dto.CommentDto;
import com.medeat.community.dto.PostDto;
import com.medeat.community.service.CommunityService;
import com.medeat.notification.feed.dto.NotificationFeedType;
import com.medeat.notification.feed.service.NotificationFeedService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CommunityServiceImpl implements CommunityService {

    private final CommunityDao communityDao;
    private final NotificationFeedService notificationFeedService;

    public CommunityServiceImpl(CommunityDao communityDao, NotificationFeedService notificationFeedService) {
        this.communityDao = communityDao;
        this.notificationFeedService = notificationFeedService;
    }

    @Override
    public List<PostDto> getPostList(String modeType, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return communityDao.selectPostList(modeType);
        }

        return communityDao.searchPostByTitle(modeType, keyword.trim());
    }

    @Override
    public PostDto getPost(Long postId) {
        return communityDao.selectPostDetail(postId);
    }

    @Override
    public void writePost(PostDto dto) {
        communityDao.insertPost(dto);
    }

    @Override
    public void updatePost(PostDto dto) {
        communityDao.updatePost(dto);
    }

    @Override
    public void deletePost(Long postId) {
        communityDao.deletePost(postId);
    }

    @Override
    public List<CommentDto> getCommentList(Long postId) {
        return communityDao.selectCommentsByPost(postId);
    }

    @Override
    public void writeComment(CommentDto dto) {
        communityDao.insertComment(dto);
        communityDao.updateCommentCount(dto.getPostId(), 1);

        PostDto post = communityDao.selectPostDetail(dto.getPostId());
        if (post == null || post.getUserId().equals(dto.getUserId())) {
            return;
        }

        notificationFeedService.notify(
                post.getUserId(),
                NotificationFeedType.COMMUNITY_COMMENT,
                "POST",
                dto.getPostId()
        );
    }

    @Override
    public CommentDto getComment(Long commentId) {
        return communityDao.selectComment(commentId);
    }

    @Override
    public void updateComment(CommentDto dto) {
        communityDao.updateComment(dto);
    }

    @Override
    public void deleteComment(Long commentId) {
        CommentDto origin = communityDao.selectComment(commentId);
        if (origin == null) {
            return;
        }

        communityDao.deleteComment(commentId);
        communityDao.updateCommentCount(origin.getPostId(), -1);
    }

    @Override
    public boolean toggleLike(Long postId, Long userId) {
        int exists = communityDao.checkLike(postId, userId);
        if (exists == 0) {
            communityDao.insertLike(postId, userId);
            communityDao.updateLikeCount(postId, 1);
            notifyPostOwner(postId, userId, NotificationFeedType.COMMUNITY_LIKE);
            return true;
        }

        communityDao.deleteLike(postId, userId);
        communityDao.updateLikeCount(postId, -1);
        return false;
    }

    @Override
    public boolean toggleScrap(Long postId, Long userId) {
        int exists = communityDao.checkScrap(postId, userId);
        if (exists == 0) {
            communityDao.insertScrap(postId, userId);
            communityDao.updateScrapCount(postId, 1);
            notifyPostOwner(postId, userId, NotificationFeedType.COMMUNITY_SCRAP);
            return true;
        }

        communityDao.deleteScrap(postId, userId);
        communityDao.updateScrapCount(postId, -1);
        return false;
    }

    @Override
    public PostDto getTopPost() {
        return communityDao.selectTopPost();
    }

    private void notifyPostOwner(Long postId, Long actorUserId, NotificationFeedType type) {
        PostDto post = communityDao.selectPostDetail(postId);
        if (post == null || post.getUserId().equals(actorUserId)) {
            return;
        }

        notificationFeedService.notify(post.getUserId(), type, "POST", postId);
    }
}
