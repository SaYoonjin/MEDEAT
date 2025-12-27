package com.medeat.pdf.dto.eat;

public class EatPeriodSummary {

    private String startDate;   // "2025-11-24"
    private String endDate;     // "2025-11-27"

    private double totalKcal;
    private double avgKcal;

    private double carbRatio;     // 0~1
    private double proteinRatio;  // 0~1
    private double fatRatio;      // 0~1
    
    private int goalKcal;       // 계산된 목표 칼로리
    private double kcalDiff;    // avgKcal - goalKcal
    private double kcalDiffRatio;

    public EatPeriodSummary() {
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getTotalKcal() {
        return totalKcal;
    }

    public void setTotalKcal(double totalKcal) {
        this.totalKcal = totalKcal;
    }

    public double getAvgKcal() {
        return avgKcal;
    }

    public void setAvgKcal(double avgKcal) {
        this.avgKcal = avgKcal;
    }

    public double getCarbRatio() {
        return carbRatio;
    }

    public void setCarbRatio(double carbRatio) {
        this.carbRatio = carbRatio;
    }

    public double getProteinRatio() {
        return proteinRatio;
    }

    public void setProteinRatio(double proteinRatio) {
        this.proteinRatio = proteinRatio;
    }

    public double getFatRatio() {
        return fatRatio;
    }

    public void setFatRatio(double fatRatio) {
        this.fatRatio = fatRatio;
    }
    
    public int getGoalKcal() {
        return goalKcal;
    }
    public void setGoalKcal(int goalKcal) {
        this.goalKcal = goalKcal;
    }

    public double getKcalDiff() {
        return kcalDiff;
    }
    public void setKcalDiff(double kcalDiff) {
        this.kcalDiff = kcalDiff;
    }

    public double getKcalDiffRatio() {
        return kcalDiffRatio;
    }
    
    public void setKcalDiffRatio(double kcalDiffRatio) {
        this.kcalDiffRatio = kcalDiffRatio;
    }
}
