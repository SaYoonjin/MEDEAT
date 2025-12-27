package com.medeat.diet.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.medeat.diet.dto.DietItemDto;
import com.medeat.diet.dto.DietLogDto;
import com.medeat.food.dto.FoodDto;

public interface DietDao {

    /* -----------------------------
     * diet_log
     ------------------------------ */

    // 등록
    int insertDietLog(DietLogDto log);

    // 수정
    int updateDietLog(DietLogDto log);

    // diet_id로 단일 조회
    DietLogDto selectDietLog(Long dietId);

    // 날짜별 조회 (list 화면용)
    List<DietLogDto> selectDietListByDate(
        @Param("userId") Long userId,
        @Param("date") String date
    );

    // 수정 폼에서 사용 (items 포함)
    DietLogDto selectDietDetail(
        @Param("dietId") Long dietId,
        @Param("userId") Long userId
    );

    // 삭제 (userId도 함께 비교)
    void deleteDiet(
        @Param("dietId") Long dietId,
        @Param("userId") Long userId
    );

    /* -----------------------------
     * diet_item
     ------------------------------ */

    // diet_item 등록
    int insertDietItem(DietItemDto item);

    // diet_item 목록 불러오기 (총합 계산용)
    List<DietItemDto> selectDietItems(Long dietId);

    // diet_id 기준 전체 삭제
    int deleteDietItems(@Param("dietId") Long dietId);

    // 단일 item 조회 (dietId 알기용)
    DietItemDto getItemById(Long itemId);

    // item 삭제 (userId 검증)
    void deleteItem(
        @Param("itemId") Long itemId,
        @Param("userId") Long userId
    );
    
    /* diet_log 총합 업데이트 */
    void updateDietTotals(
    		@Param("dietId") Long dietId,
    		@Param("userId") Long userId,
    		@Param("cal") int cal,
    		@Param("carb") double carb,
    		@Param("protein") double protein,
    		@Param("fat") double fat
    		);
    
    List<DietLogDto> selectCalendarLogs(
    		@Param("userId") Long userId,
    		@Param("startDate") String startDate,
    		@Param("endDate") String endDate
    		);
    
	 // 🔥 PDF / 리포트용 기간 조회
	List<DietLogDto> selectLogsByPeriod(
	     @Param("userId") Long userId,
	     @Param("startDate") String startDate,
	     @Param("endDate") String endDate
	);

	int countMealsByDate(@Param("userId") Long userId,
           @Param("logDate") String logDate);
	
	// 🔥 PDF / IT 추천 식단 조회
	List<FoodDto> selectRecommendedFoods(
	    @Param("userId") Long userId,
	    @Param("startDate") String startDate,
	    @Param("endDate") String endDate
	);

}
