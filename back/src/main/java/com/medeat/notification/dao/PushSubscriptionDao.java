package com.medeat.notification.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.medeat.notification.dto.PushSubscriptionDto;

@Mapper
public interface PushSubscriptionDao {

    void insertSubscription(PushSubscriptionDto dto);

    List<PushSubscriptionDto> selectByUserId(Long userId);

    PushSubscriptionDto selectByEndpoint(String endpoint);

    void deleteByEndpoint(String endpoint);

	void updatePushEnabled(Long userId, boolean enabled);
	
	int getPushEnabled(Long userId);
	
	void updateSubscription(PushSubscriptionDto dto);

}
