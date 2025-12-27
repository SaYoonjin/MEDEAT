package com.medeat.community.dto;

import java.time.LocalDateTime;

public class PostDto {

    private Long postId;
    private Long userId;
    private String writerId;
    private String modeType;
    private String category;
    private String title;
    private String content;
    private String imagePath;

    private int likeCount;
    private int commentCount;
    private int scrapCount;

    private int isDeleted;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PostDto() {}

    public PostDto(Long postId, Long userId, String writerId, String modeType, String category,
                   String title, String content, String imagePath,
                   int likeCount, int commentCount, int scrapCount,
                   int isDeleted,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.postId = postId;
        this.userId = userId;
        this.writerId = writerId;
        this.modeType = modeType;
        this.category = category;
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.scrapCount = scrapCount;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getWriterId() {return writerId;}

	public void setWriterId(String writerId) {this.writerId = writerId;}

	public String getModeType() { return modeType; }
    public void setModeType(String modeType) { this.modeType = modeType; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }

    public int getScrapCount() { return scrapCount; }
    public void setScrapCount(int scrapCount) { this.scrapCount = scrapCount; }

    public int getIsDeleted() { return isDeleted; }
    public void setIsDeleted(int isDeleted) { this.isDeleted = isDeleted; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

	@Override
	public String toString() {
		return "PostDto [postId=" + postId + ", userId=" + userId + ", writerId=" + writerId + ", modeType=" + modeType
				+ ", category=" + category + ", title=" + title + ", content=" + content + ", imagePath=" + imagePath
				+ ", likeCount=" + likeCount + ", commentCount=" + commentCount + ", scrapCount=" + scrapCount
				+ ", isDeleted=" + isDeleted + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}

}