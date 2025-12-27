package com.medeat.challenge.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.medeat.challenge.dao.ChallengeDao;
import com.medeat.challenge.dao.ChallengeLogDao;
import com.medeat.challenge.dao.UserChallengeDao;
import com.medeat.challenge.dto.ChallengeDto;
import com.medeat.challenge.dto.UserChallengeDto;
import com.medeat.notification.feed.dto.NotificationFeedType;
import com.medeat.notification.feed.service.NotificationFeedService;

@Component
public class ChallengeEndScheduler {

    @Autowired
    private UserChallengeDao userChallengeDao;

    @Autowired
    private ChallengeDao challengeDao;

    @Autowired
    private ChallengeLogDao challengeLogDao;

    @Autowired
    private NotificationFeedService notificationFeedService;

    @Scheduled(cron = "0 0 0 * * *") // 매일 00:00
    @Transactional
    public void endChallenges() {

        LocalDate today = LocalDate.now();

        // 1️⃣ 진행 중인 모든 챌린지
        List<UserChallengeDto> list =
            userChallengeDao.selectByStatus("PROGRESS");

        for (UserChallengeDto uc : list) {
        	
        	// 이미 종료된 경우 스킵 (이중 안전)
        	if (!"PROGRESS".equals(uc.getStatus())) continue;


            ChallengeDto c =
                challengeDao.selectById(uc.getChallengeId());

            LocalDate endDate =
                uc.getStartDate().plusDays(c.getPeriodDays() - 1);

            // 아직 기간 안 끝남
            if (today.isBefore(endDate)) continue;

            Long userChallengeId = uc.getUserChallengeId();
            Long userId = uc.getUserId();

            // 2️⃣ 성공 로그 수
            int successCount =
                challengeLogDao.countSuccessLogs(userChallengeId);

            // 3️⃣ 상태 FINISH + 종료일 기록
            userChallengeDao.finish(userChallengeId, today);


            // 4️⃣ 알림 분기
            if (successCount == c.getPeriodDays()) {

                // 🎉 챌린지 성공
                notificationFeedService.notify(
                    userId,
                    NotificationFeedType.CHALLENGE_SUCCESS,
                    "CHALLENGE",
                    userChallengeId
                );

            } else {

                // 😢 챌린지 종료 (실패)
                notificationFeedService.notify(
                    userId,
                    NotificationFeedType.CHALLENGE_END,
                    "CHALLENGE",
                    userChallengeId
                );
            }
        }
    }
}
