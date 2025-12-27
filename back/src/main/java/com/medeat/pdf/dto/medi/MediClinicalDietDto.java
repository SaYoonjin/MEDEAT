package com.medeat.pdf.dto.medi;

public class MediClinicalDietDto {

    // 권장량 초과 일수
    private int sodiumExceededDays;
    private int sugarExceededDays;
    private int saturatedFatExceededDays;

    // 식사 패턴
    private double mealIntervalStdDev;  // 식사 간격 표준편차
    private int lateNightMealCount;

    // 임상적 해석 문장
    private String clinicalComment;
    
    public MediClinicalDietDto() {
		// TODO Auto-generated constructor stub
	}

	public MediClinicalDietDto(int sodiumExceededDays, int sugarExceededDays, int saturatedFatExceededDays,
			double mealIntervalStdDev, int lateNightMealCount, String clinicalComment) {
		super();
		this.sodiumExceededDays = sodiumExceededDays;
		this.sugarExceededDays = sugarExceededDays;
		this.saturatedFatExceededDays = saturatedFatExceededDays;
		this.mealIntervalStdDev = mealIntervalStdDev;
		this.lateNightMealCount = lateNightMealCount;
		this.clinicalComment = clinicalComment;
	}

	public int getSodiumExceededDays() {
		return sodiumExceededDays;
	}

	public void setSodiumExceededDays(int sodiumExceededDays) {
		this.sodiumExceededDays = sodiumExceededDays;
	}

	public int getSugarExceededDays() {
		return sugarExceededDays;
	}

	public void setSugarExceededDays(int sugarExceededDays) {
		this.sugarExceededDays = sugarExceededDays;
	}

	public int getSaturatedFatExceededDays() {
		return saturatedFatExceededDays;
	}

	public void setSaturatedFatExceededDays(int saturatedFatExceededDays) {
		this.saturatedFatExceededDays = saturatedFatExceededDays;
	}

	public double getMealIntervalStdDev() {
		return mealIntervalStdDev;
	}

	public void setMealIntervalStdDev(double mealIntervalStdDev) {
		this.mealIntervalStdDev = mealIntervalStdDev;
	}

	public int getLateNightMealCount() {
		return lateNightMealCount;
	}

	public void setLateNightMealCount(int lateNightMealCount) {
		this.lateNightMealCount = lateNightMealCount;
	}

	public String getClinicalComment() {
		return clinicalComment;
	}

	public void setClinicalComment(String clinicalComment) {
		this.clinicalComment = clinicalComment;
	}

	@Override
	public String toString() {
		return "MediClinicalDietDto [sodiumExceededDays=" + sodiumExceededDays + ", sugarExceededDays="
				+ sugarExceededDays + ", saturatedFatExceededDays=" + saturatedFatExceededDays + ", mealIntervalStdDev="
				+ mealIntervalStdDev + ", lateNightMealCount=" + lateNightMealCount + ", clinicalComment="
				+ clinicalComment + "]";
	}
    
    
}
