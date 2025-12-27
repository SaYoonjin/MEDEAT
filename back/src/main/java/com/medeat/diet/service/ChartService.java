package com.medeat.diet.service;

import java.awt.image.BufferedImage;
import java.util.List;

import com.medeat.dashboard.dto.DailyChartData;
import com.medeat.dashboard.dto.ScoreSummary;
import com.medeat.pdf.dto.eat.EatPatternSummary;
import com.medeat.pdf.dto.eat.EatPeriodSummary;

public interface ChartService {
    String generateSnackChart(EatPatternSummary pattern);
    String generateDailyChart(DailyChartData data);
    String generateDailyChart(List<DailyChartData> dataList);
    
    String generateMacroRatioChart(EatPeriodSummary period); 
    String generateScoreGaugeChart(ScoreSummary score);
    String generateScoreTrendChart(List<DailyChartData> dataList, int goalKcal);
    
    BufferedImage generateMacroRatioImage(EatPeriodSummary period);

}
