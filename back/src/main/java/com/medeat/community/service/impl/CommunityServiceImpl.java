package com.medeat.community.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medeat.community.dao.CommunityDao;
import com.medeat.community.dto.CommentDto;
import com.medeat.community.dto.PostDto;
import com.medeat.community.service.CommunityService;
import com.medeat.notification.feed.service.NotificationFeedService;
import com.medeat.notification.feed.dto.NotificationFeedType;


@Service
public class CommunityServiceImpl implements CommunityService {

    @Autowired
    private CommunityDao communityDao;
    
    @Autowired
    private NotificationFeedService notificationFeedService;


    /* ==============================
           게시글
     ============================== */

    @Override
    public List<PostDto> getPostList(String modeType, String keyword) {

        System.out.println("🔥 Service keyword = [" + keyword + "]");

        if (keyword == null || keyword.trim().isEmpty()) {
            return communityDao.selectPostList(modeType);
        }

        return communityDao.searchPostByTitle(
            modeType,
            keyword.trim()
        );
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


    /* ==============================
           댓글
     ============================== */

    @Override
    public List<CommentDto> getCommentList(Long postId) {
        return communityDao.selectCommentsByPost(postId);
    }

    @Override
    public void writeComment(CommentDto dto) {

        communityDao.insertComment(dto);
        communityDao.updateCommentCount(dto.getPostId(), +1);

        // 🔥 게시글 다시 조회 (DB 기준)
        PostDto post = communityDao.selectPostDetail(dto.getPostId());
        if (post == null) return;

        Long receiverUserId = post.getUserId(); // ✅ FK 기준

        // 본인 글에 본인이 댓글 → 알림 X
        if (receiverUserId.equals(dto.getUserId())) return;

        notificationFeedService.notify(
        	    receiverUserId,
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

        // 🔥 삭제하기 전 postId를 얻어야 함
        CommentDto origin = communityDao.selectComment(commentId);
        if (origin == null) {
            return;  // 안전하게 종료 (NPE 방지)
        }

        // 댓글 삭제
        communityDao.deleteComment(commentId);

        // 🔥 댓글 count 감소
        communityDao.updateCommentCount(origin.getPostId(), -1);
    }


    /* ==============================
           좋아요 / 스크랩
     ============================== */

    @Override
    public boolean toggleLike(Long postId, Long userId) {

        int exists = communityDao.checkLike(postId, userId);

        if (exists == 0) {
            communityDao.insertLike(postId, userId);
            communityDao.updateLikeCount(postId, +1);

            PostDto post = communityDao.selectPostDetail(postId);
            if (post != null && !post.getUserId().equals(userId)) {
            	notificationFeedService.notify(
            		    post.getUserId(),
            		    NotificationFeedType.COMMUNITY_LIKE,
            		    "POST",
            		    postId
            		);


            }

            return true;
        } else {
            communityDao.deleteLike(postId, userId);
            communityDao.updateLikeCount(postId, -1);
            return false;
        }
    }


    @Override
    public boolean toggleScrap(Long postId, Long userId) {

        int exists = communityDao.checkScrap(postId, userId);

        if (exists == 0) {
            communityDao.insertScrap(postId, userId);
            communityDao.updateScrapCount(postId, +1);

            PostDto post = communityDao.selectPostDetail(postId);
            if (post != null && !post.getUserId().equals(userId)) {
            	notificationFeedService.notify(
            		    post.getUserId(),
            		    NotificationFeedType.COMMUNITY_SCRAP,
            		    "POST",
            		    postId
            		);


            }

            return true;
        } else {
            communityDao.deleteScrap(postId, userId);
            communityDao.updateScrapCount(postId, -1);
            return false;
        }
    }


	@Override
	public PostDto getTopPost() {
		return communityDao.selectTopPost();
	}
}	
