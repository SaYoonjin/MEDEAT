package com.medeat.notification.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;

import org.springframework.stereotype.Service;

import com.medeat.notification.dao.PushSubscriptionDao;
import com.medeat.notification.dto.PushSubscriptionDto;
import com.medeat.notification.service.WebPushService;

@Service
public class WebPushServiceImpl implements WebPushService {

    private final PushService pushService;
    private final PushSubscriptionDao pushSubscriptionDao;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebPushServiceImpl(PushService pushService, PushSubscriptionDao pushSubscriptionDao) {
        this.pushService = pushService;
        this.pushSubscriptionDao = pushSubscriptionDao;
    }

    @Override
    public void sendMedicationNotification(Long userId, Long medicationId, int doseIndex,
                                           String title, String body) {

    	// ✅ pushEnabled가 꺼져있으면 전송 안 함
    	if (pushSubscriptionDao.getPushEnabled(userId) != 1) {
    	    return;
    	}

    	try {
            List<PushSubscriptionDto> subs = pushSubscriptionDao.selectByUserId(userId);

            if (subs == null || subs.isEmpty()) return;

            // push payload는 JSON 문자열
            String payload = objectMapper.writeValueAsString(
                    Map.of(
                            "title", title,
                            "body", body,
                            "medicationId", medicationId,
                            "doseIndex", doseIndex
                    )
            );

            byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);

            for (PushSubscriptionDto s : subs) {

                Notification notification = new Notification(
                        s.getEndpoint(),
                        s.getP256dh(),
                        s.getAuth(),
                        payloadBytes
                );

                pushService.send(notification);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
