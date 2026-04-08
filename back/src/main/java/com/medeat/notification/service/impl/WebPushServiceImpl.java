package com.medeat.notification.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeat.notification.dao.PushSubscriptionDao;
import com.medeat.notification.dto.PushSubscriptionDto;
import com.medeat.notification.service.WebPushService;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class WebPushServiceImpl implements WebPushService {

    private static final Logger log = LoggerFactory.getLogger(WebPushServiceImpl.class);

    private final PushService pushService;
    private final PushSubscriptionDao pushSubscriptionDao;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebPushServiceImpl(PushService pushService, PushSubscriptionDao pushSubscriptionDao) {
        this.pushService = pushService;
        this.pushSubscriptionDao = pushSubscriptionDao;
    }

    @Override
    public void sendMedicationNotification(Long userId, Long medicationId, int doseIndex, String title, String body) {
        if (pushSubscriptionDao.getPushEnabled(userId) != 1) {
            return;
        }

        try {
            List<PushSubscriptionDto> subscriptions = pushSubscriptionDao.selectByUserId(userId);
            if (subscriptions == null || subscriptions.isEmpty()) {
                return;
            }

            String payload = objectMapper.writeValueAsString(Map.of(
                    "title", title,
                    "body", body,
                    "medicationId", medicationId,
                    "doseIndex", doseIndex
            ));
            byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);

            for (PushSubscriptionDto subscription : subscriptions) {
                Notification notification = new Notification(
                        subscription.getEndpoint(),
                        subscription.getP256dh(),
                        subscription.getAuth(),
                        payloadBytes
                );
                pushService.send(notification);
            }
        } catch (Exception e) {
            log.warn("Failed to send web push notification. userId={}, medicationId={}", userId, medicationId, e);
        }
    }
}
