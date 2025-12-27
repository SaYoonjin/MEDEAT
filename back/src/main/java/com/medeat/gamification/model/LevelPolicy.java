package com.medeat.gamification.model;

public final class LevelPolicy {
    private LevelPolicy() {}

    public static int calcLevel(int totalXp) {
        if (totalXp >= 3200) return 7;
        if (totalXp >= 2200) return 6;
        if (totalXp >= 1400) return 5;
        if (totalXp >= 800)  return 4;
        if (totalXp >= 400)  return 3;
        if (totalXp >= 150)  return 2;
        return 1;
    }

    public static int nextLevelXp(int level) {
        switch (level) {
            case 1: return 150;
            case 2: return 400;
            case 3: return 800;
            case 4: return 1400;
            case 5: return 2200;
            case 6: return 3200;
            default: return 3200;
        }
    }

    // 스트릭 보너스 정책 (3~6:2, 7~13:4, 14~29:6, 30+:8)
    public static int streakBonus(int streak) {
        if (streak >= 30) return 8;
        if (streak >= 14) return 6;
        if (streak >= 7)  return 4;
        if (streak >= 3)  return 2;
        return 0;
    }
}
