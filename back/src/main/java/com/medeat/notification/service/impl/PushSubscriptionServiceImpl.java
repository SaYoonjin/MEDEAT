package com.medeat.notification.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.medeat.notification.dao.PushSubscriptionDao;
import com.medeat.notification.dto.PushSubscriptionDto;
import com.medeat.notification.service.PushSubscriptionService;

@Service
public class PushSubscriptionServiceImpl implements PushSubscriptionService {

    private final PushSubscriptionDao pushSubscriptionDao;

    public PushSubscriptionServiceImpl(PushSubscriptionDao pushSubscriptionDao) {
        this.pushSubscriptionDao = pushSubscriptionDao;
    }

    @Override
    public void save(PushSubscriptionDto dto) {
        PushSubscriptionDto existing = pushSubscriptionDao.selectByEndpoint(dto.getEndpoint());

        if (existing == null) {
            pushSubscriptionDao.insertSubscription(dto);
        } else {
            // ✅ 같은 endpoint면 userId가 바뀌었을 수도 있으니 업데이트로 정리하는 게 베스트
            // (아래 3단계에서 update 쿼리 추가할 거야)
            pushSubscriptionDao.updateSubscription(dto);
        }
    }


    @Override
    public List<PushSubscriptionDto> getByUserId(Long userId) {
        return pushSubscriptionDao.selectByUserId(userId);
    }

    @Override
    public PushSubscriptionDto getByEndpoint(String endpoint) {
        return pushSubscriptionDao.selectByEndpoint(endpoint);
    }

    @Override
    public void deleteByEndpoint(String endpoint) {
        pushSubscriptionDao.deleteByEndpoint(endpoint);
    }

	@Override
	public void updatePushEnabled(Long userId, boolean enabled) {
		pushSubscriptionDao.updatePushEnabled(userId, enabled);
	}

	@Override
	public boolean isPushEnabled(Long userId) {
		return pushSubscriptionDao.getPushEnabled(userId) == 1;
	}
}
