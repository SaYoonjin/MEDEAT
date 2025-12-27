package com.medeat.medical.service.impl;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medeat.medical.dao.MedicationDao;
import com.medeat.medical.dto.MedicationDto;
import com.medeat.medical.dto.MedicationLogDto;
import com.medeat.medical.service.MedicationService;
import com.medeat.notification.service.PushSubscriptionService;
import com.medeat.notification.service.WebPushService;
import com.medeat.notification.feed.service.NotificationFeedService;
import com.medeat.notification.feed.dto.NotificationFeedType;


@Service
public class MedicationServiceImpl implements MedicationService {

    @Autowired
    private MedicationDao medicationDao;

    @Autowired
    private WebPushService webPushService;

    @Autowired
    private PushSubscriptionService pushSubscriptionService;
    
    @Autowired
    private NotificationFeedService notificationFeedService;



    @Override
    public List<MedicationDto> getMedicationList(Long userId) {
        return medicationDao.selectByUser(userId);
    }

    @Override
    public MedicationDto getMedication(Long medicationId) {
        return medicationDao.selectById(medicationId);
    }

    @Override
    public void addMedication(MedicationDto dto) {

        // 중복 체크
        if (existsMedication(dto.getUserId(), dto.getDrugName())) {
            throw new IllegalArgumentException("이미 등록된 약입니다.");
        }

        medicationDao.insertMedication(dto);
    }

    @Override
    public void updateMedication(MedicationDto dto) {

        // 중복 체크: 자기 자신 제외
        int exists = medicationDao.existsByUserAndNameExcludingId(
                dto.getUserId(),
                dto.getDrugName(),
                dto.getMedicationId()
        );

        if (exists > 0) {
            throw new IllegalArgumentException("이미 같은 이름의 약이 존재합니다.");
        }

        medicationDao.updateMedication(dto);
        
        // 🔔 종 알림: 약 복용 알림 설정 변경
        notificationFeedService.notify(
        	    dto.getUserId(),
        	    NotificationFeedType.MEDICATION_SETTING_CHANGED,
        	    "DISEASE",
        	    dto.getUserId()
        	);


    }

    @Override
    public void deleteMedication(Long medicationId) {
        medicationDao.deleteMedication(medicationId);
    }

    @Override
    public List<MedicationLogDto> getTodayLogs(Long userId) {
        return medicationDao.getTodayLogs(userId);
    }

    @Override
    public void saveLog(Long userId, Long medicationId, int takenIndex) {
        medicationDao.insertLog(userId, medicationId, takenIndex);
    }



    /* -------------------------------------------------------------
       🔔 1. 스케줄러가 매 분 실행 → 약 복용 시간인지 확인 후 알림 발송
    ------------------------------------------------------------- */
    @Override
    public void checkAndSendMedicationAlarms() {

        String nowTime = LocalTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm"));

        List<MedicationDto> meds = getMedicationToAlert(nowTime);

        for (MedicationDto med : meds) {

            // 🔕 유저가 알림 OFF 했으면 skip
            if (!pushSubscriptionService.isPushEnabled(med.getUserId())) {
                continue;
            }

            int doseIndex = getNextDoseIndex(med);

            try {
                webPushService.sendMedicationNotification(
                        med.getUserId(),
                        med.getMedicationId(),
                        doseIndex,
                        "약 복용 알림 💊",
                        med.getDrugName() + " 복용 시간입니다!"
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    /* -------------------------------------------------------------
       🔔 2. nowTime 과 intakeTime 이 일치하는 약만 필터링
    ------------------------------------------------------------- */
    @Override
    public List<MedicationDto> getMedicationToAlert(String nowTime) {

        List<MedicationDto> all = medicationDao.selectAll();

        return all.stream()
                .filter(med -> {
                    if (med.getIntakeTime() == null) return false;

                    for (String t : med.getIntakeTime().split(",")) {
                        if (nowTime.equals(t.trim())) {
                            return true;
                        }
                    }
                    return false;
                })
                .toList();
    }



    /* -------------------------------------------------------------
       🔔 3. 다음 복용 회차 계산 (오늘 복용 기록 수 기반)
    ------------------------------------------------------------- */
    @Override
    public int getNextDoseIndex(MedicationDto med) {

        List<MedicationLogDto> logs =
                medicationDao.getTodayLogsByMedication(med.getMedicationId());

        int takenCount = logs.size();
        return takenCount + 1;  // 1회차 → 2회차 → 3회차...
    }



    /* -------------------------------------------------------------
       기타 기능
    ------------------------------------------------------------- */

    @Override
    public boolean existsMedication(Long userId, String drugName) {
        return medicationDao.existsByUserAndName(userId, drugName) > 0;
    }
    
    @Override
    public List<MedicationLogDto> getLogs(
            Long userId,
            String startDate,
            String endDate
    ) {
        return medicationDao.selectLogsByPeriod(userId, startDate, endDate);
    }

}
