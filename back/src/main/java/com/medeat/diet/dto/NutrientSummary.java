package com.medeat.diet.dto;

public class NutrientSummary {
	
	private int totalCalorie;
    private double totalCarb;
    private double totalProtein;
    private double totalFat;
    
	public int getTotalCalorie() {
		return totalCalorie;
	}
	
	public void setTotalCalorie(int totalCalorie) {
		this.totalCalorie = totalCalorie;
	}
	
	public double getTotalCarb() {
		return totalCarb;
	}
	
	public void setTotalCarb(double totalCarb) {
		this.totalCarb = totalCarb;
	}
	
	public double getTotalProtein() {
		return totalProtein;
	}
	
	public void setTotalProtein(double totalProtein) {
		this.totalProtein = totalProtein;
	}
	
	public double getTotalFat() {
		return totalFat;
	}
	
	public void setTotalFat(double totalFat) {
		this.totalFat = totalFat;
	}

	@Override
	public String toString() {
		return "NutrientSummary [totalCalorie=" + totalCalorie + ", totalCarb=" + totalCarb + ", totalProtein="
				+ totalProtein + ", totalFat=" + totalFat + "]";
	}
    
}