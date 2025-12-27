package com.medeat.dashboard.dto;

import java.time.LocalDate;

public class DailyChartData {

    private LocalDate date;

    private double totalKcal;
    private double totalCarb;
    private double totalProtein;
    private double totalFat;

    // 해당 날짜 야식/간식 횟수 (있으면 사용, 없으면 0으로 둬도 됨)
    private int nightCount;
    private int snackCount;

    public DailyChartData() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getTotalKcal() {
        return totalKcal;
    }

    public void setTotalKcal(double totalKcal) {
        this.totalKcal = totalKcal;
    }

    public double getTotalCarb() {
        return totalCarb;
    }

    public void setTotalCarb(double totalCarb) {
        this.totalCarb = totalCarb;
    }

    public double getTotalProtein() {
        return totalProtein;
    }

    public void setTotalProtein(double totalProtein) {
        this.totalProtein = totalProtein;
    }

    public double getTotalFat() {
        return totalFat;
    }

    public void setTotalFat(double totalFat) {
        this.totalFat = totalFat;
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
