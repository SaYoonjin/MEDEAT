package com.medeat.diet.dao;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.medeat.dashboard.dto.DailyChartData;
import com.medeat.dashboard.dto.DailyLogSummary;
import com.medeat.dashboard.dto.MedMealSummary;
import com.medeat.medical.dto.MedMedicationSummary;

@Repository
public class DietAnalysisDao {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private final String NS = "DietAnalysisMapper.";

    /**
     * (기존) 메딧모드: 일별 로그 요약
     */
    public List<DailyLogSummary> selectDailySummary(Long userId,
                                                    LocalDate startDate,
                                                    LocalDate endDate) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userId);
        map.put("startDate", startDate);
        map.put("endDate", endDate);

        return sqlSession.selectList(NS + "selectDailySummary", map);
    }

    /**
     * ⭐ 잇(EAT)모드: 일별 칼로리·탄단지·야식/간식 요약
     *
     *  - DietAnalysisMapper.xml 에서
     *    <select id="selectEatDailyChart" ...> 로 쿼리 정의해두고
     *    DailyChartData 의 필드(totalKcal, totalCarb, totalProtein,
     *    totalFat, nightCount, snackCount 등)에 매핑되도록 resultMap 맞춰주면 됨.
     */
    public List<DailyChartData> selectEatDailyChart(Long userId,
                                                    LocalDate startDate,
                                                    LocalDate endDate) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userId);
        map.put("startDate", startDate);
        map.put("endDate", endDate);

        return sqlSession.selectList(NS + "selectEatDailyChart", map);
    }

    /**
     * (기존) 메딧모드: 약물과 연결된 끼니
     */
    public List<MedMealSummary> selectMedMeals(Long userId,
                                               LocalDate start,
                                               LocalDate end) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userId);
        map.put("startDate", start);
        map.put("endDate", end);
        return sqlSession.selectList(NS + "selectMedMeals", map);
    }

    public List<String> selectUserDiseases(Long userId) {
        return sqlSession.selectList(NS + "selectUserDiseases", userId);
    }

    public List<MedMedicationSummary> selectUserMedications(Long userId) {
        return sqlSession.selectList(NS + "selectUserMedications", userId);
    }
}
