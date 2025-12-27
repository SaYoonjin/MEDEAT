package com.medeat.mediscan.util;

import java.util.Set;

public class ColorNormalizer {

    public static String normalize(String color) {
        if (color == null) return null;

        String c = color.trim();

        // 🔴 빨강 계열
        if (containsAny(c, Set.of("빨강", "적", "갈색", "적갈", "황갈"))) {
            return "RED";
        }

        // ⚪ 하양 계열
        if (containsAny(c, Set.of("하양", "흰", "백"))) {
            return "WHITE";
        }

        // 🟡 노랑 계열
        if (containsAny(c, Set.of("노랑", "황"))) {
            return "YELLOW";
        }

        // 🔵 파랑 계열
        if (containsAny(c, Set.of("파랑", "청"))) {
            return "BLUE";
        }

        // 🟢 초록 계열
        if (containsAny(c, Set.of("초록", "녹"))) {
            return "GREEN";
        }

        return "ETC";
    }

    private static boolean containsAny(String target, Set<String> keys) {
        return keys.stream().anyMatch(target::contains);
    }
}
