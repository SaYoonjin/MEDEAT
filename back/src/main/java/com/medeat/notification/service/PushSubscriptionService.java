package com.medeat.notification.service;

import java.util.List;

import com.medeat.notification.dto.PushSubscriptionDto;

public interface PushSubscriptionService {

    void save(PushSubscriptionDto dto);

    List<PushSubscriptionDto> getByUserId(Long userId);

    PushSubscriptionDto getByEndpoint(String endpoint);

    void deleteByEndpoint(String endpoint);

	void updatePushEnabled(Long userId, boolean enabled);
	
	boolean isPushEnabled(Long userId);
}
