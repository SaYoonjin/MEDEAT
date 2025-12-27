package com.medeat.dashboard.dto;

import java.util.List;

public class ScoreSummary {

    private int value;                // 점수
    private List<String> reasons;     // 점수 산출 근거
    private List<String> nextAdvice;  // 다음 식단 교정 추천

    public ScoreSummary() {
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }

    public List<String> getNextAdvice() {
        return nextAdvice;
    }

    public void setNextAdvice(List<String> nextAdvice) {
        this.nextAdvice = nextAdvice;
    }
}
