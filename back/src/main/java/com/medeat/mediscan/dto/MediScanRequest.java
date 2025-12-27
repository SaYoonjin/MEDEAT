package com.medeat.mediscan.dto;

import org.springframework.web.multipart.MultipartFile;

public class MediScanRequest {

    private MultipartFile frontImage;
    private MultipartFile backImage;

    private String drugShape;
    private String frontColor;
    private String backColor;

    // ✅ 추가: 사용자 입력 각인 (앞/뒤)
    private String frontImprint;
    private String backImprint;

    public MultipartFile getFrontImage() { return frontImage; }
    public void setFrontImage(MultipartFile frontImage) { this.frontImage = frontImage; }

    public MultipartFile getBackImage() { return backImage; }
    public void setBackImage(MultipartFile backImage) { this.backImage = backImage; }

    public String getDrugShape() { return drugShape; }
    public void setDrugShape(String drugShape) { this.drugShape = drugShape; }

    public String getFrontColor() { return frontColor; }
    public void setFrontColor(String frontColor) { this.frontColor = frontColor; }

    public String getBackColor() { return backColor; }
    public void setBackColor(String backColor) { this.backColor = backColor; }

    public String getFrontImprint() { return frontImprint; }
    public void setFrontImprint(String frontImprint) { this.frontImprint = frontImprint; }

    public String getBackImprint() { return backImprint; }
    public void setBackImprint(String backImprint) { this.backImprint = backImprint; }
}
