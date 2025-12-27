package com.medeat.community.service;

import java.util.List;

import com.medeat.community.dto.CommentDto;
import com.medeat.community.dto.PostDto;

public interface CommunityService {

    /* 게시글 */
	List<PostDto> getPostList(String modeType, String keyword);

    PostDto getPost(Long postId);
    void writePost(PostDto dto);
    void updatePost(PostDto dto);
    void deletePost(Long postId);

    /* 댓글 */
    List<CommentDto> getCommentList(Long postId);
    void writeComment(CommentDto dto);
    CommentDto getComment(Long commentId);
    void updateComment(CommentDto dto);
    void deleteComment(Long commentId);

    /* 좋아요 & 스크랩 */
    boolean toggleLike(Long postId, Long userId);
    boolean toggleScrap(Long postId, Long userId);
	PostDto getTopPost();
}
