package com.medeat.notification.service.impl;

import com.medeat.auth.domain.user.entity.User;
import com.medeat.auth.domain.user.repository.UserRepository;
import com.medeat.notification.dao.PushSubscriptionDao;
import com.medeat.notification.dto.PushSubscriptionDto;
import com.medeat.notification.service.PushSubscriptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PushSubscriptionServiceImpl implements PushSubscriptionService {

    private final PushSubscriptionDao pushSubscriptionDao;
    private final UserRepository userRepository;

    public PushSubscriptionServiceImpl(
            PushSubscriptionDao pushSubscriptionDao,
            UserRepository userRepository
    ) {
        this.pushSubscriptionDao = pushSubscriptionDao;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void save(PushSubscriptionDto dto) {
        PushSubscriptionDto existing = pushSubscriptionDao.selectByEndpoint(dto.getEndpoint());

        if (existing == null) {
            pushSubscriptionDao.insertSubscription(dto);
            return;
        }

        pushSubscriptionDao.updateSubscription(dto);
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
    @Transactional
    public void deleteByEndpoint(String endpoint) {
        pushSubscriptionDao.deleteByEndpoint(endpoint);
    }

    @Override
    @Transactional
    public void updatePushEnabled(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        user.setPushEnabled(enabled);
    }

    @Override
    public boolean isPushEnabled(Long userId) {
        return userRepository.findById(userId)
                .map(User::getPushEnabled)
                .map(Boolean.TRUE::equals)
                .orElse(false);
    }
}
