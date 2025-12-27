package com.medeat.diet.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.medeat.diet.dto.DietLogDto;

public interface DietService {

    void saveDiet(DietLogDto log);   // 등록 + 수정
    void deleteDiet(Long dietId, Long userId);
    Long deleteItem(Long itemId, Long userId); 
    void recalcTotals(Long dietId, Long userId);

    DietLogDto getDiet(Long dietId);
    DietLogDto getDietDetail(Long dietId, Long userId);
    List<DietLogDto> getDietList(Long userId, String date);
    List<DietLogDto> getCalendarLogs(Long userId, String startDate, String endDate);
    int countMealsByDate(@Param("userId") Long userId, @Param("logDate") String logDate);

    List<DietLogDto> getLogs(Long userId, String startDate, String endDate);

}