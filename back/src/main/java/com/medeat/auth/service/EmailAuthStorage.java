package com.medeat.auth.service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

public class EmailAuthStorage {

    private static final ConcurrentHashMap<String, AuthCode> storage = new ConcurrentHashMap<>();

    public static void saveCode(String email, String code) {
        storage.put(email, new AuthCode(code, LocalDateTime.now().plusMinutes(3)));
    }

    public static boolean verifyCode(String email, String code) {
        AuthCode auth = storage.get(email);

        if (auth == null) return false;
        if (auth.getExpireAt().isBefore(LocalDateTime.now())) return false;

        return auth.getCode().equals(code);
    }

    private static class AuthCode {
        private final String code;
        private final LocalDateTime expireAt;

        public AuthCode(String code, LocalDateTime expireAt) {
            this.code = code;
            this.expireAt = expireAt;
        }

        public String getCode() {
            return code;
        }

        public LocalDateTime getExpireAt() {
            return expireAt;
        }
    }
}
