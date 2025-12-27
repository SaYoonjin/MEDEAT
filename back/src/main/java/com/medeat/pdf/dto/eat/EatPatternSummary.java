package com.medeat.pdf.dto.eat;

public class EatPatternSummary {

    // 야식 횟수
    private int nightCount;

    // 간식 횟수
    private int snackCount;

    public EatPatternSummary() {
    }

    public int getNightCount() {
        return nightCount;
    }

    public void setNightCount(int nightCount) {
        this.nightCount = nightCount;
    }

    public int getSnackCount() {
        return snackCount;
    }

    public void setSnackCount(int snackCount) {
        this.snackCount = snackCount;
    }
}
