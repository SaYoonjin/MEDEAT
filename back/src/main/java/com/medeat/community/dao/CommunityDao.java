package com.medeat.community.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.medeat.community.dto.CommentDto;
import com.medeat.community.dto.PostDto;

public interface CommunityDao {

    // ---------- 게시글 ----------
    List<PostDto> selectPostList(@Param("modeType") String modeType);
    
    List<PostDto> searchPostByTitle(
            @Param("modeType") String modeType,
            @Param("keyword") String keyword
    );


    PostDto selectPostDetail(@Param("postId") Long postId);

    int insertPost(PostDto dto);

    int updatePost(PostDto dto);

    int deletePost(@Param("postId") Long postId);


    // ---------- 카운트 업데이트 ----------
    int updateLikeCount(@Param("postId") Long postId,
                        @Param("amount") int amount);

    int updateScrapCount(@Param("postId") Long postId,
                         @Param("amount") int amount);

    int updateCommentCount(@Param("postId") Long postId,
                           @Param("amount") int amount);


    // ---------- 댓글 ----------
    int insertComment(CommentDto dto);

    List<CommentDto> selectCommentsByPost(@Param("postId") Long postId);

    CommentDto selectComment(@Param("commentId") Long commentId);

    int deleteComment(@Param("commentId") Long commentId);

    int updateComment(CommentDto comment);


    // ---------- 좋아요 ----------
    int checkLike(@Param("postId") Long postId,
                  @Param("userId") Long userId);

    int insertLike(@Param("postId") Long postId,
                   @Param("userId") Long userId);

    int deleteLike(@Param("postId") Long postId,
                   @Param("userId") Long userId);


    // ---------- 스크랩 ----------
    int checkScrap(@Param("postId") Long postId,
                   @Param("userId") Long userId);

    int insertScrap(@Param("postId") Long postId,
                    @Param("userId") Long userId);

    int deleteScrap(@Param("postId") Long postId,
                    @Param("userId") Long userId);

	PostDto selectTopPost();
}
