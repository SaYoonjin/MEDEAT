package com.medeat.mediscan.dto;

import com.medeat.medical.dto.DrugInfoDto;

public class MediScanResult {

    private Long itemSeq;
    private String itemName;
    private double score;
    private DrugInfoDto detail;

    public MediScanResult(
            Long itemSeq,
            String itemName,
            double score,
            DrugInfoDto detail
    ) {
        this.itemSeq = itemSeq;
        this.itemName = itemName;
        this.score = score;
        this.detail = detail;
    }

    public Long getItemSeq() {
        return itemSeq;
    }

    public String getItemName() {
        return itemName;
    }

    public double getScore() {
        return score;
    }

    public DrugInfoDto getDetail() {
        return detail;
    }
}
