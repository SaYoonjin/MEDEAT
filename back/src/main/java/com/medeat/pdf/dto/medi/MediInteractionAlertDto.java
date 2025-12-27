package com.medeat.pdf.dto.medi;

public class MediInteractionAlertDto {

    private String type;            // 예: "위험", "주의"
    private String message;         // 예: "위험 상호작용 2건"
    private String recommendation;  // 예: "전문의 상담 권장"

    public MediInteractionAlertDto() {}

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
}
