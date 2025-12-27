package com.medeat.community.dto;

import java.time.LocalDateTime;

public class CommentDto {

    private Long commentId;
    private Long postId;
    private Long userId;
    private Long parentId;
    private String writerId;

    private String content;

    private int isDeleted;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentDto() {}

    public CommentDto(Long commentId, Long postId, Long userId, Long parentId, String writerId,
                      String content, int isDeleted,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {

        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.parentId = parentId;
        this.writerId = writerId;
        this.content = content;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public String getWriterId() {return writerId;}

	public void setWriterId(String writerId) {this.writerId = writerId;}

	public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getIsDeleted() { return isDeleted; }
    public void setIsDeleted(int isDeleted) { this.isDeleted = isDeleted; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

	@Override
	public String toString() {
		return "CommentDto [commentId=" + commentId + ", postId=" + postId + ", userId=" + userId + ", parentId="
				+ parentId + ", writerId=" + writerId + ", content=" + content + ", isDeleted=" + isDeleted
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}

}