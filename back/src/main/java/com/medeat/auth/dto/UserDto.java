package com.medeat.auth.dto;

import java.time.LocalDateTime;

public class UserDto {

	private Long userId;
	private String loginId;
	private String password;
	private String name;
	private String nickname;
	private String email;
	private String phone;
	private String gender;
	private Integer age;
	private Double height;
	private Double weight;
	private String goalType;
	private String defaultMode;
	private LocalDateTime createdAt;
	private String pregnantStatus; // yes/no
	private Integer pregnancyWeek; // 1~40, null 가능
	private boolean pushEnabled; // 기본 true

	public UserDto() {
	}

	public UserDto(Long userId, String loginId, String password, String name, String nickname, String email,
			String phone, String gender, Integer age, Double height, Double weight, String goalType, String defaultMode,
			LocalDateTime createdAt, String pregnantStatus, Integer pregnancyWeek) {
		this.userId = userId;
		this.loginId = loginId;
		this.password = password;
		this.name = name;
		this.nickname = nickname;
		this.email = email;
		this.phone = phone;
		this.gender = gender;
		this.age = age;
		this.height = height;
		this.weight = weight;
		this.goalType = goalType;
		this.defaultMode = defaultMode;
		this.createdAt = createdAt;
		this.pregnantStatus = pregnantStatus;
		this.pregnancyWeek = pregnancyWeek;
		this.pushEnabled = pushEnabled;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public String getGoalType() {
		return goalType;
	}

	public void setGoalType(String goalType) {
		this.goalType = goalType;
	}

	public String getDefaultMode() {
		return defaultMode;
	}

	public void setDefaultMode(String defaultMode) {
		this.defaultMode = defaultMode;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getPregnantStatus() {
		return pregnantStatus;
	}

	public void setPregnantStatus(String pregnantStatus) {
		this.pregnantStatus = pregnantStatus;
	}

	public Integer getPregnancyWeek() {
		return pregnancyWeek;
	}

	public void setPregnancyWeek(Integer pregnancyWeek) {
		this.pregnancyWeek = pregnancyWeek;
	}

	public boolean isPushEnabled() {
		return pushEnabled;
	}

	public void setPushEnabled(boolean pushEnabled) {
		this.pushEnabled = pushEnabled;
	}

	@Override
	public String toString() {
		return "UserDto [userId=" + userId + ", loginId=" + loginId + ", password=" + password + ", name=" + name
				+ ", nickname=" + nickname + ", email=" + email + ", phone=" + phone + ", gender=" + gender + ", age="
				+ age + ", height=" + height + ", weight=" + weight + ", goalType=" + goalType + ", defaultMode="
				+ defaultMode + ", createdAt=" + createdAt + ", pregnantStatus=" + pregnantStatus + ", pregnancyWeek="
				+ pregnancyWeek + ", pushEnabled=" + pushEnabled + "]";
	}

}
