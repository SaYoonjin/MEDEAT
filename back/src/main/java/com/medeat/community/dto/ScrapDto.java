package com.medeat.community.dto;

public class ScrapDto {

    private Long postScrapId;
    private Long postId;
    private Long userId;

    public ScrapDto() {}

    public ScrapDto(Long postScrapId, Long postId, Long userId) {
        this.postScrapId = postScrapId;
        this.postId = postId;
        this.userId = userId;
    }

    public Long getPostScrapId() { return postScrapId; }
    public void setPostScrapId(Long postScrapId) { this.postScrapId = postScrapId; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "ScrapDto{" +
                "postScrapId=" + postScrapId +
                ", postId=" + postId +
                ", userId=" + userId +
                '}';
    }
}