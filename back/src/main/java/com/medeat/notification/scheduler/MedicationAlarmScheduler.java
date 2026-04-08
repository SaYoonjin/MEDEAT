package com.medeat.notification.scheduler;

import com.medeat.medical.dto.MedicationDto;
import com.medeat.medical.service.MedicationService;
import com.medeat.notification.service.WebPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class MedicationAlarmScheduler {

    private static final Logger log = LoggerFactory.getLogger(MedicationAlarmScheduler.class);

    private final MedicationService medicationService;
    private final WebPushService webPushService;

    public MedicationAlarmScheduler(MedicationService medicationService, WebPushService webPushService) {
        this.medicationService = medicationService;
        this.webPushService = webPushService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void runMedicationAlarmCheck() {
        String nowTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        List<MedicationDto> medications = medicationService.getMedicationToAlert(nowTime);
        if (medications == null || medications.isEmpty()) {
            return;
        }

        log.debug("Running medication alarm check. nowTime={}, medicationCount={}", nowTime, medications.size());

        for (MedicationDto medication : medications) {
            try {
                webPushService.sendMedicationNotification(
                        medication.getUserId(),
                        medication.getMedicationId(),
                        medicationService.getNextDoseIndex(medication),
                        "약 복용 알림",
                        medication.getDrugName() + " 복용 시간입니다."
                );
            } catch (Exception e) {
                log.warn("Failed to send scheduled medication notification. medicationId={}, userId={}",
                        medication.getMedicationId(), medication.getUserId(), e);
            }
        }
    }
}
