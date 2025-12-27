package com.medeat.community.dto;

public class LikeDto {

    private Long postLikeId;
    private Long postId;
    private Long userId;

    public LikeDto() {}

    public LikeDto(Long postLikeId, Long postId, Long userId) {
        this.postLikeId = postLikeId;
        this.postId = postId;
        this.userId = userId;
    }

    public Long getPostLikeId() { return postLikeId; }
    public void setPostLikeId(Long postLikeId) { this.postLikeId = postLikeId; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "LikeDto{" +
                "postLikeId=" + postLikeId +
                ", postId=" + postId +
                ", userId=" + userId +
                '}';
    }
}