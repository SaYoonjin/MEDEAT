package com.medeat.auth.domain.user.entity;

import com.medeat.common.entity.BaseCreatedEntity;
import com.medeat.medical.domain.medication.entity.Medication;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
public class User extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "login_id", nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private String nickname;
    private String email;
    private String phone;
    private String gender;
    private Integer age;
    private Double height;
    private Double weight;

    @Column(name = "goal_type")
    private String goalType;

    @Column(name = "default_mode")
    private String defaultMode;

    @Column(name = "pregnant_status")
    private String pregnantStatus;

    @Column(name = "pregnancy_week")
    private Integer pregnancyWeek;

    @Column(name = "push_enabled")
    private Boolean pushEnabled;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Medication> medications = new ArrayList<>();

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

    public Boolean getPushEnabled() {
        return pushEnabled;
    }

    public void setPushEnabled(Boolean pushEnabled) {
        this.pushEnabled = pushEnabled;
    }
}
