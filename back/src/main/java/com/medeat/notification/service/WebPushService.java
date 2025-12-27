package com.medeat.notification.service;

public interface WebPushService {

    void sendMedicationNotification(Long userId, Long medicationId, int doseIndex,
                                    String title, String body);
}
