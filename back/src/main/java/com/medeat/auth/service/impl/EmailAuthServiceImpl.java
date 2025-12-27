package com.medeat.auth.service.impl;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.medeat.auth.service.EmailAuthService;
import com.medeat.auth.service.EmailAuthStorage;

import jakarta.mail.internet.MimeMessage;

import java.util.Random;

@Service
public class EmailAuthServiceImpl implements EmailAuthService {

    private final JavaMailSender mailSender;

    public EmailAuthServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendCodeToEmail(String email) {
        String code = generateCode();
        EmailAuthStorage.saveCode(email, code);

        sendMail(email, "MEDEAT 인증코드", "인증코드: " + code + "\n3분 안에 입력해주세요.");
    }

    @Override
    public boolean verifyCode(String email, String code) {
        return EmailAuthStorage.verifyCode(email, code);
    }

    private String generateCode() {
        Random r = new Random();
        return String.valueOf(100000 + r.nextInt(900000));
    }

    private void sendMail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            // ⭐⭐⭐ 반드시 추가해야 함 — 네이버 계정과 동일해야만 발송 가능
            helper.setFrom("tkdbswls1234@naver.com", "MEDEAT");

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }

}
