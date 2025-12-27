package com.medeat.medical.service.impl;

import com.medeat.dashboard.dto.MedDashboard;
import com.medeat.dashboard.dto.MedMealSummary;
import com.medeat.dashboard.dto.MedModeAnalysisResponse;
import com.medeat.diet.dao.DietAnalysisDao;
import com.medeat.medical.dto.MedLinkedMeal;
import com.medeat.medical.dto.MedMedicationSummary;
import com.medeat.medical.dto.MedRiskItem;
import com.medeat.medical.service.MedAnalysisService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class MedAnalysisServiceImpl implements MedAnalysisService {

    @Autowired
    private DietAnalysisDao analysisDAO;

    @Override
    public MedModeAnalysisResponse analyze(Long userId, LocalDate startDate, LocalDate endDate) {

        List<MedMealSummary> meals = analysisDAO.selectMedMeals(userId, startDate, endDate);
        List<String> diseases = analysisDAO.selectUserDiseases(userId);
        List<MedMedicationSummary> meds = analysisDAO.selectUserMedications(userId);

        // intakeTime -> [drugName, ...] 매핑
        Map<String, List<String>> medsByTime = new HashMap<>();
        for (MedMedicationSummary m : meds) {
            medsByTime
                    .computeIfAbsent(Objects.toString(m.getIntakeTime(), ""), k -> new ArrayList<>())
                    .add(m.getDrugName());
        }

        boolean hasHypertension = diseases.stream().anyMatch(d -> d.contains("고혈압"));
        boolean hasDiabetes     = diseases.stream().anyMatch(d -> d.contains("당뇨"));

        List<MedLinkedMeal> linkedMeals = new ArrayList<>();
        List<MedRiskItem> riskItems = new ArrayList<>();

        int danger = 0, warning = 0, safe = 0;

        for (MedMealSummary m : meals) {
            MedLinkedMeal lm = new MedLinkedMeal();
            lm.setDietId(m.getDietId());
            lm.setDate(m.getLogDate());
            lm.setMealTime(m.getMealTime());
            lm.setTotalCalorie(m.getTotalCalorie());
            lm.setTotalSodium(m.getTotalSodium());
            lm.setTotalSugar(m.getTotalSugar());

            List<String> relatedDrugs =
                    medsByTime.getOrDefault(m.getMealTime(), Collections.emptyList());
            lm.setDrugNames(relatedDrugs);

            String level = "안전";
            StringBuilder msg = new StringBuilder();

            if (hasHypertension && m.getTotalSodium() >= 2000) {
                level = "위험";
                msg.append("고혈압인데 나트륨이 많은 식단입니다. ");
            }

            if (hasDiabetes && m.getTotalSugar() >= 50) {
                if (!level.equals("위험")) level = "주의";
                msg.append("당뇨병인데 당류 섭취가 많은 편입니다. ");
            }

            if (!relatedDrugs.isEmpty()) {
                msg.append("해당 끼니 근처에 복용하는 약: ").append(String.join(", ", relatedDrugs));
            }

            if (msg.length() == 0) {
                msg.append("큰 위험 요소는 없어요.");
            }

            lm.setLevel(level);
            lm.setHighlightMessage(msg.toString());

            if ("위험".equals(level)) danger++;
            else if ("주의".equals(level)) warning++;
            else safe++;

            MedRiskItem item = new MedRiskItem();
            item.setLevel(level);
            item.setDate(m.getLogDate());
            item.setMealTime(m.getMealTime());
            item.setMessage(msg.toString());
            riskItems.add(item);

            linkedMeals.add(lm);
        }

        MedDashboard dash = new MedDashboard();
        dash.setDangerCount(danger);
        dash.setWarningCount(warning);
        dash.setSafeCount(safe);
        dash.setRisks(riskItems);

        MedModeAnalysisResponse res = new MedModeAnalysisResponse();
        res.setDashboard(dash);
        res.setLinkedMeals(linkedMeals);
        return res;
    }
}
