package com.medeat.mediscan.util;

public class ImprintNormalizer {

    private ImprintNormalizer() {}

    public static String normalize(String s) {
        if (s == null) return null;

        // 공백 제거 + 대문자 + 특수문자 정리(필요하면 더 조정 가능)
        String t = s.trim().toUpperCase();

        // 흔한 구분기호/공백 제거
        t = t.replaceAll("\\s+", "");
        t = t.replaceAll("[\\-_/]", "");

        // 괄호 같은 것도 제거하고 싶으면:
        t = t.replaceAll("[()\\[\\]{}]", "");

        return t;
    }
}
