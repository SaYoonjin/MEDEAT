package com.medeat.dashboard.dto;

import java.util.List;

import com.medeat.pdf.dto.eat.EatPatternSummary;
import com.medeat.pdf.dto.eat.EatPeriodSummary;

/**
 * 잇모드(EAT 모드) 분석 전체 응답 DTO
 *
 * 프론트에서 사용하는 구조:
 *  itResult.periodSummary.*
 *  itResult.pattern.*
 *  itResult.score.*
 *  itResult.dailyChart[]
 */
public class EatModeAnalysisResponse {

    private EatPeriodSummary periodSummary;
    private EatPatternSummary pattern;
    private ScoreSummary score;
    private List<DailyChartData> dailyChart;

    public EatModeAnalysisResponse() {
    }

    public EatPeriodSummary getPeriodSummary() {
        return periodSummary;
    }

    public void setPeriodSummary(EatPeriodSummary periodSummary) {
        this.periodSummary = periodSummary;
    }

    public EatPatternSummary getPattern() {
        return pattern;
    }

    public void setPattern(EatPatternSummary pattern) {
        this.pattern = pattern;
    }

    public ScoreSummary getScore() {
        return score;
    }

    public void setScore(ScoreSummary score) {
        this.score = score;
    }

    public List<DailyChartData> getDailyChart() {
        return dailyChart;
    }

    public void setDailyChart(List<DailyChartData> dailyChart) {
        this.dailyChart = dailyChart;
    }
    
    
}
