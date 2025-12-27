package com.medeat.notification.dto;

public class PushSubscriptionDto {

    private Long id;
    private Long userId;
    private String endpoint;
    private String p256dh;
    private String auth;
    private String createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getP256dh() { return p256dh; }
    public void setP256dh(String p256dh) { this.p256dh = p256dh; }

    public String getAuth() { return auth; }
    public void setAuth(String auth) { this.auth = auth; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "PushSubscriptionDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", endpoint='" + endpoint + '\'' +
                ", p256dh='" + p256dh + '\'' +
                ", auth='" + auth + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
