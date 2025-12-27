
package com.medeat.dashboard.dto;

import java.util.List;

import com.medeat.medical.dto.MedLinkedMeal;

public class MedModeAnalysisResponse {

    private MedDashboard dashboard;
    private List<MedLinkedMeal> linkedMeals;

    public MedDashboard getDashboard() { return dashboard; }
    public void setDashboard(MedDashboard dashboard) { this.dashboard = dashboard; }

    public List<MedLinkedMeal> getLinkedMeals() { return linkedMeals; }
    public void setLinkedMeals(List<MedLinkedMeal> linkedMeals) { this.linkedMeals = linkedMeals; }
}
