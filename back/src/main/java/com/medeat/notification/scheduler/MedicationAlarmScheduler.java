package com.medeat.notification.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.medeat.medical.dto.MedicationDto;
import com.medeat.medical.service.MedicationService;
import com.medeat.notification.service.WebPushService;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class MedicationAlarmScheduler {

    private final MedicationService medicationService;
    private final WebPushService webPushService;

    public MedicationAlarmScheduler(MedicationService medicationService,
                                    WebPushService webPushService) {
        this.medicationService = medicationService;
        this.webPushService = webPushService;
    }

    // 매 분 0초 실행
    @Scheduled(cron = "0 * * * * *")
    public void runMedicationAlarmCheck() {

        String nowTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        System.out.println("[ALARM] nowTime=" + nowTime);

        List<MedicationDto> meds = medicationService.getMedicationToAlert(nowTime);
        if (meds == null || meds.isEmpty()) return;

        System.out.println("[ALARM] meds count=" + (meds == null ? 0 : meds.size()));

        for (MedicationDto m : meds) {
        	  System.out.println("[ALARM] send to userId=" + m.getUserId() + " drug=" + m.getDrugName());

            try {
                int doseIndex = medicationService.getNextDoseIndex(m);

                webPushService.sendMedicationNotification(
                        m.getUserId(),
                        m.getMedicationId(),
                        doseIndex,
                        "약 복용 알림 💊",
                        m.getDrugName() + " 복용 시간입니다!"
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
