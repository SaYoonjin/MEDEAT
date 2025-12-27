package com.medeat.auth.service;

public interface EmailAuthService {

    void sendCodeToEmail(String email);

    boolean verifyCode(String email, String code);
}
