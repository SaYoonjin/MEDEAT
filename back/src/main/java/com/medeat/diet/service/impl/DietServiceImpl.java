package com.medeat.diet.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.medeat.gamification.model.ActionType;
import com.medeat.gamification.service.GamificationService;

import java.time.LocalDate;

import com.medeat.diet.dao.DietDao;
import com.medeat.diet.dto.DietItemDto;
import com.medeat.diet.dto.DietLogDto;
import com.medeat.diet.service.DietService;

@Service
public class DietServiceImpl implements DietService {

    @Autowired
    private DietDao dietDao;
    
    @Autowired
    private GamificationService gamificationService;

    /* ------------------------
     * 단일 조회 (items 포함)
     ------------------------ */
    @Override
    public DietLogDto getDiet(Long dietId) {
        return dietDao.selectDietLog(dietId);
    }

    /* ------------------------
     * 날짜별 목록 조회
     ------------------------ */
    @Override
    public List<DietLogDto> getDietList(Long userId, String date) {
        return dietDao.selectDietListByDate(userId, date);
    }

    /* ------------------------
     * 저장 (등록/수정)
     ------------------------ */
    @Override
    @Transactional
    public void saveDiet(DietLogDto log) {

        boolean isNew = (log.getDietId() == null);

        // (기존 계산 로직 그대로)
        int totalCal = 0;
        double totalCarb = 0;
        double totalProtein = 0;
        double totalFat = 0;

        if (log.getItems() != null) {
            for (DietItemDto item : log.getItems()) {
                if (item.getCalorie() != null) totalCal += item.getCalorie();
                if (item.getCarb() != null) totalCarb += item.getCarb();
                if (item.getProtein() != null) totalProtein += item.getProtein();
                if (item.getFat() != null) totalFat += item.getFat();
            }
        }

        log.setTotalCalorie(totalCal);
        log.setTotalCarb(totalCarb);
        log.setTotalProtein(totalProtein);
        log.setTotalFat(totalFat);

        // 신규 등록
        if (isNew) {
            dietDao.insertDietLog(log);  // ✅ 여기서 log.dietId가 채워져야 함 (중요)
        } else {
            dietDao.updateDietLog(log);
            dietDao.deleteDietItems(log.getDietId());
        }

        // item 재등록
        if (log.getItems() != null) {
            for (DietItemDto item : log.getItems()) {
                item.setDietId(log.getDietId());
                dietDao.insertDietItem(item);
            }
        }

        // =========================
        // ✅ XP 트리거 (신규만)
        // =========================
        if (isNew) {
            Long userId = log.getUserId();

            LocalDate day = LocalDate.parse(log.getLogDate());

            String refId = "diet:" + log.getDietId();

            // 1) 끼니 1회 기록 +10
            gamificationService.earnXp(userId, ActionType.MEAL_LOG, refId, 10, day);

            // 2) 음식 3개 이상 또는 메모/사진 +3
            int itemCount = (log.getItems() == null) ? 0 : log.getItems().size();
            boolean hasMemo = log.getMemo() != null && !log.getMemo().isBlank();
            boolean hasPhoto = log.getPhotoPath() != null && !log.getPhotoPath().isBlank();

            if (itemCount >= 3 || hasMemo || hasPhoto) {
                gamificationService.earnXp(userId, ActionType.MEAL_BONUS, refId, 3, day);
            }

            int mealCount = countMealsByDate(userId, log.getLogDate());
            if (mealCount >= 2) {
                gamificationService.earnXp(userId, ActionType.DAY_2MEALS, null, 5, day);
            }

        }
    }

    /* ------------------------
     * 삭제
     ------------------------ */
    @Override
    public void deleteDiet(Long dietId, Long userId) {
        dietDao.deleteDietItems(dietId);
        dietDao.deleteDiet(dietId, userId);
    }
    
    @Override
    public Long deleteItem(Long itemId, Long userId) {
        // item 정보 조회 (dietId를 알기 위해)
        DietItemDto item = dietDao.getItemById(itemId);

        dietDao.deleteItem(itemId, userId);
        return item.getDietId();
    }

    /* ------------------------
     * 상세 조회 (수정폼)
     ------------------------ */
    @Override
    public DietLogDto getDietDetail(Long dietId, Long userId) {
        return dietDao.selectDietDetail(dietId, userId);
    }
    
    @Override
    public void recalcTotals(Long dietId, Long userId) {

        // 현재 남아있는 item들 가져오기
        List<DietItemDto> items = dietDao.selectDietItems(dietId);

        int totalCal = 0;
        double totalCarb = 0;
        double totalProtein = 0;
        double totalFat = 0;

        for (DietItemDto it : items) {
            totalCal += it.getCalorie();
            totalCarb += it.getCarb();
            totalProtein += it.getProtein();
            totalFat += it.getFat();
        }

        // diet_log 업데이트 호출
        dietDao.updateDietTotals(dietId, userId, totalCal, totalCarb, totalProtein, totalFat);
    }
    
    @Override
    public List<DietLogDto> getCalendarLogs(Long userId, String startDate, String endDate) {
        return dietDao.selectCalendarLogs(userId, startDate, endDate);
    }
    
    @Override
    public List<DietLogDto> getLogs(
            Long userId,
            String startDate,
            String endDate
    ) {
        return dietDao.selectLogsByPeriod(userId, startDate, endDate);
    }
    
    @Override
    public int countMealsByDate(Long userId, String logDate) {
        return dietDao.countMealsByDate(userId, logDate);
    }


}
