package com.medeat.pdf.dto.eat;

import java.util.List;

import com.medeat.dashboard.dto.DailyChartData;
import com.medeat.dashboard.dto.EatModeAnalysisResponse;
import com.medeat.dashboard.dto.MedDashboard;
import com.medeat.dashboard.dto.ScoreSummary;
import com.medeat.diet.dto.AlternativeFoodDto;
import com.medeat.diet.dto.CookingAdviceDto;
import com.medeat.food.dto.FoodDto;

public class EatPdfRequestDto {

    /* ===== 공통 ===== */
    private String mode;          // EAT / MEDI_EAT
    private String startDate;
    private String endDate;
    private EatPdfUserDto user;

    /* ===== EAT 분석 ===== */
    private ScoreSummary score;
    private EatPeriodSummary periodSummary;
    private EatPatternSummary pattern;
    private List<DailyChartData> dailyChart;

    /* ===== MEDI_EAT 분석 ===== */
    private MedDashboard medDashboard;

    /* ===== 추천 ===== */
    private List<FoodDto> foods;
    private List<AlternativeFoodDto> alternatives;
    private List<CookingAdviceDto> cookingAdvices;

    /* ===== 차트 (Base64) ===== */
    private String chartSnack;
    private String chartDaily;
    
    public EatPdfRequestDto() {
		// TODO Auto-generated constructor stub
	}

	public EatPdfRequestDto(String mode, String startDate, String endDate, EatPdfUserDto user, ScoreSummary score,
			EatPeriodSummary periodSummary, EatPatternSummary pattern, List<DailyChartData> dailyChart,
			MedDashboard medDashboard, List<FoodDto> foods, List<AlternativeFoodDto> alternatives,
			List<CookingAdviceDto> cookingAdvices, String chartSnack, String chartDaily) {
		super();
		this.mode = mode;
		this.startDate = startDate;
		this.endDate = endDate;
		this.user = user;
		this.score = score;
		this.periodSummary = periodSummary;
		this.pattern = pattern;
		this.dailyChart = dailyChart;
		this.medDashboard = medDashboard;
		this.foods = foods;
		this.alternatives = alternatives;
		this.cookingAdvices = cookingAdvices;
		this.chartSnack = chartSnack;
		this.chartDaily = chartDaily;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
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

	public EatPdfUserDto getUser() {
		return user;
	}

	public void setUser(EatPdfUserDto user) {
		this.user = user;
	}

	public ScoreSummary getScore() {
		return score;
	}

	public void setScore(ScoreSummary score) {
		this.score = score;
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

	public List<DailyChartData> getDailyChart() {
		return dailyChart;
	}

	public void setDailyChart(List<DailyChartData> dailyChart) {
		this.dailyChart = dailyChart;
	}

	public MedDashboard getMedDashboard() {
		return medDashboard;
	}

	public void setMedDashboard(MedDashboard medDashboard) {
		this.medDashboard = medDashboard;
	}

	public List<FoodDto> getFoods() {
		return foods;
	}

	public void setFoods(List<FoodDto> foods) {
		this.foods = foods;
	}

	public List<AlternativeFoodDto> getAlternatives() {
		return alternatives;
	}

	public void setAlternatives(List<AlternativeFoodDto> alternatives) {
		this.alternatives = alternatives;
	}

	public List<CookingAdviceDto> getCookingAdvices() {
		return cookingAdvices;
	}

	public void setCookingAdvices(List<CookingAdviceDto> cookingAdvices) {
		this.cookingAdvices = cookingAdvices;
	}

	public String getChartSnack() {
		return chartSnack;
	}

	public void setChartSnack(String chartSnack) {
		this.chartSnack = chartSnack;
	}

	public String getChartDaily() {
		return chartDaily;
	}

	public void setChartDaily(String chartDaily) {
		this.chartDaily = chartDaily;
	}

	@Override
	public String toString() {
		return "PdfRequestDto [mode=" + mode + ", startDate=" + startDate + ", endDate=" + endDate + ", user=" + user
				+ ", score=" + score + ", periodSummary=" + periodSummary + ", pattern=" + pattern + ", dailyChart="
				+ dailyChart + ", medDashboard=" + medDashboard + ", foods=" + foods + ", alternatives=" + alternatives
				+ ", cookingAdvices=" + cookingAdvices + ", chartSnack=" + chartSnack + ", chartDaily=" + chartDaily
				+ "]";
	}
	
	public static EatPdfRequestDto fromAnalysis(
	        EatModeAnalysisResponse analysis,
	        String startDate,
	        String endDate
	) {
	    EatPdfRequestDto dto = new EatPdfRequestDto();

	    // =========================
	    // 공통
	    // =========================
	    dto.setMode("EAT");
	    dto.setStartDate(startDate);
	    dto.setEndDate(endDate);

	    if (analysis == null) {
	        return dto;
	    }

	    // =========================
	    // 분석 결과 매핑
	    // =========================
	    dto.setScore(analysis.getScore());
	    dto.setPeriodSummary(analysis.getPeriodSummary());
	    dto.setPattern(analysis.getPattern());
	    dto.setDailyChart(analysis.getDailyChart());

	    // =========================
	    // 추천 데이터 (PDF에서는 비워도 OK)
	    // =========================
	    dto.setFoods(null);
	    dto.setAlternatives(null);
	    dto.setCookingAdvices(null);

	    // =========================
	    // 차트 (서버에서 생성할 예정)
	    // =========================
	    dto.setChartSnack(null);
	    dto.setChartDaily(null);

	    return dto;
	}
    
    
}
