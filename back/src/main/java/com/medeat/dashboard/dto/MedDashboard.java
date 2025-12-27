package com.medeat.dashboard.dto;

import java.util.List;

import com.medeat.medical.dto.MedRiskItem;

public class MedDashboard {
    private int dangerCount;
    private int warningCount;
    private int safeCount;
    private List<MedRiskItem> risks;

    public int getDangerCount() { return dangerCount; }
    public void setDangerCount(int dangerCount) { this.dangerCount = dangerCount; }

    public int getWarningCount() { return warningCount; }
    public void setWarningCount(int warningCount) { this.warningCount = warningCount; }

    public int getSafeCount() { return safeCount; }
    public void setSafeCount(int safeCount) { this.safeCount = safeCount; }

    public List<MedRiskItem> getRisks() { return risks; }
    public void setRisks(List<MedRiskItem> risks) { this.risks = risks; }
}
