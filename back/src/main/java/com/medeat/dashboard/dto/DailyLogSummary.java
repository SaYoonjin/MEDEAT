package com.medeat.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

// 하루 요약용
public class DailyLogSummary {

    private LocalDate logDate;
    private int totalCalorie;
    private BigDecimal totalCarb;
    private BigDecimal totalProtein;
    private BigDecimal totalFat;
    private int lateSnackCount;

    public LocalDate getLogDate() { return logDate; }
    public void setLogDate(LocalDate logDate) { this.logDate = logDate; }

    public int getTotalCalorie() { return totalCalorie; }
    public void setTotalCalorie(int totalCalorie) { this.totalCalorie = totalCalorie; }

    public BigDecimal getTotalCarb() { return totalCarb; }
    public void setTotalCarb(BigDecimal totalCarb) { this.totalCarb = totalCarb; }

    public BigDecimal getTotalProtein() { return totalProtein; }
    public void setTotalProtein(BigDecimal totalProtein) { this.totalProtein = totalProtein; }

    public BigDecimal getTotalFat() { return totalFat; }
    public void setTotalFat(BigDecimal totalFat) { this.totalFat = totalFat; }

    public int getLateSnackCount() { return lateSnackCount; }
    public void setLateSnackCount(int lateSnackCount) { this.lateSnackCount = lateSnackCount; }
}
