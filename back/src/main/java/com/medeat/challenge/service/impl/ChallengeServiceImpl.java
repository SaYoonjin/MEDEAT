package com.medeat.challenge.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medeat.gamification.model.ActionType;
import com.medeat.gamification.service.GamificationService;
import com.medeat.notification.feed.dto.NotificationFeedType;
import com.medeat.notification.feed.service.NotificationFeedService;
import com.medeat.challenge.dao.ChallengeDao;
import com.medeat.challenge.dao.ChallengeLogDao;
import com.medeat.challenge.dao.UserChallengeDao;
import com.medeat.challenge.dto.ChallengeDto;
import com.medeat.challenge.dto.ChallengeLogDto;
import com.medeat.challenge.dto.UserChallengeDto;
import com.medeat.challenge.service.ChallengeService;

@Service
public class ChallengeServiceImpl implements ChallengeService {

    @Autowired
    private ChallengeDao challengeDao;

    @Autowired
    private UserChallengeDao userChallengeDao;

    @Autowired
    private ChallengeLogDao challengeLogDao;
    
    @Autowired
    private GamificationService gamificationService;
    
    @Autowired
    private NotificationFeedService notificationFeedService;


    // ===============================
    // 조회
    // ===============================
    @Override
    public List<ChallengeDto> getPopularChallenges(String modeType) {
        return challengeDao.selectPopularByMode(modeType);
    }

    @Override
    public List<ChallengeDto> getAvailableChallenges(Long userId, String modeType) {
        return challengeDao.selectAvailableForUser(userId, modeType);
    }

    @Override
    public List<UserChallengeDto> getOngoingChallenges(Long userId, String mode) {

        List<UserChallengeDto> list =
            userChallengeDao.selectByUserAndMode(userId, mode);

        for (UserChallengeDto uc : list) {

            int success = challengeLogDao.countByUserChallengeAndStatus(
                uc.getUserChallengeId(), "SUCCESS"
            );
            int fail = challengeLogDao.countByUserChallengeAndStatus(
                uc.getUserChallengeId(), "FAIL"
            );

            int attemptedDays = success + fail;

            int successRate =
                attemptedDays == 0 ? 0
                : (int) Math.round((success * 100.0) / attemptedDays);

            // ✅ 정확한 세팅
            uc.setSuccessCount(success);
            uc.setFailCount(fail);
            uc.setSuccessRate(successRate); // ⭐ 여기!!
        }

        return list;
    }


    @Override
    public ChallengeDto getChallengeDetail(Long challengeId) {
        return challengeDao.selectById(challengeId);
    }

    @Override
    public UserChallengeDto getUserChallenge(Long userChallengeId) {
        return userChallengeDao.selectById(userChallengeId);
    }

    // ===============================
    // 생성
    // ===============================
    @Override
    public void createChallenge(ChallengeDto dto) {
        challengeDao.insertChallenge(dto);
    }

    // ===============================
    // 참여
    // ===============================
    @Override
    @Transactional
    public Map<String, Object> joinChallenge(Long userId, Long challengeId) {

        ChallengeDto c = challengeDao.selectById(challengeId);
        if (c == null) {
            throw new IllegalArgumentException("챌린지가 존재하지 않습니다.");
        }

        // ✅ 현재 참여자 수는 COUNT로 계산 (PROGRESS만)
        int joinedCount = userChallengeDao.countByChallengeAndStatus(challengeId, "PROGRESS");
        if (joinedCount >= c.getMaxParticipants()) {
            // 컨트롤러에서 409로 내려서 “정원 마감” 안내 띄우게 만들 것
            throw new IllegalStateException("모집 인원이 마감되었습니다.");
        }

        // ✅ 유니크(user_id, challenge_id)이므로 먼저 존재 여부 조회
        UserChallengeDto existing = userChallengeDao.selectByUserAndChallenge(userId, challengeId);
        LocalDate today = LocalDate.now();
        
        LocalDate endDate = today.plusDays(c.getPeriodDays() - 1);

        if (existing == null) {
            // 최초 참여
            UserChallengeDto dto = new UserChallengeDto();
            dto.setUserId(userId);
            dto.setChallengeId(challengeId);
            dto.setStartDate(today);
            dto.setStatus("PROGRESS");

            // giveup_at / rejoin_available_at 컬럼이 DTO에 없으면 mapper에서 null로 처리되게 해도 됨
            userChallengeDao.insertUserChallenge(dto);
            
            int inc = challengeDao.increaseCurrentParticipants(challengeId);
            if (inc == 0) throw new IllegalStateException("모집 인원이 마감되었습니다.");

            return Map.of(
                    "message", "챌린지 참여 완료",
                    "userChallengeId", dto.getUserChallengeId()
            );
        }

        // 이미 참여 중이면 그냥 OK
        if ("PROGRESS".equals(existing.getStatus())) {
            return Map.of(
                    "message", "이미 참여 중인 챌린지입니다.",
                    "userChallengeId", existing.getUserChallengeId()
            );
        }

        // 포기한 경우: 쿨타임 체크 후 재참여
        if ("GIVEUP".equals(existing.getStatus())) {
            // DB 컬럼이 있고 DTO에 getter가 있으면 사용
            // 없으면 mapper에서 별도 select로 rejoin_available_at만 조회해도 됨
            LocalDateTime availableAt = existing.getRejoinAvailableAt();
            if (availableAt != null && availableAt.isAfter(LocalDateTime.now())) {
                throw new IllegalStateException("재참여는 " + availableAt.toLocalDate() + "부터 가능합니다.");
            }

            userChallengeDao.rejoin(existing.getUserChallengeId(), today);
            
            int inc = challengeDao.increaseCurrentParticipants(challengeId);
            if (inc == 0) throw new IllegalStateException("모집 인원이 마감되었습니다.");

            return Map.of(
                    "message", "챌린지 재참여 완료",
                    "userChallengeId", existing.getUserChallengeId()
            );
        }

        // FINISH면 정책상 막는 걸 추천 (원하면 열어줄 수도 있음)
        if ("FINISH".equals(existing.getStatus())) {
            throw new IllegalStateException("완료한 챌린지는 재참여할 수 없습니다.");
        }

        // 그 외 상태
        throw new IllegalStateException("현재 상태에서는 참여할 수 없습니다.");
    }

    // ===============================
    // 포기
    // ===============================
    @Override
    @Transactional
    public void giveUpChallenge(Long userId, Long userChallengeId) {

        UserChallengeDto uc = userChallengeDao.selectById(userChallengeId);
        if (uc == null) {
            throw new IllegalArgumentException("챌린지 참여 정보가 없습니다.");
        }

        if (!uc.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인만 포기할 수 있습니다.");
        }

        // ⭐ 이미 진행 중이 아니면 차감하면 안 됨
        if (!"PROGRESS".equals(uc.getStatus())) {
            throw new IllegalArgumentException("진행 중인 챌린지만 포기할 수 있습니다.");
        }

        // 1) 상태 변경
        int updated = userChallengeDao.updateStatus(userChallengeId, "GIVEUP");

        // 2) 상태 변경이 실제로 되었을 때만 참여자 수 감소
        if (updated == 1) {
            challengeDao.decreaseCurrentParticipants(uc.getChallengeId());

            // 🔔 종 알림: 챌린지 종료(포기)
            notificationFeedService.notify(
                    userId,
                    NotificationFeedType.CHALLENGE_END,
                    "CHALLENGE",
                    userChallengeId
                );


        }
    }

    // ===============================
    // 로그
    // ===============================
    @Override
    public List<ChallengeLogDto> getLogs(Long userChallengeId) {
        return challengeLogDao.selectByUserChallenge(userChallengeId);
    }

    @Override
    @Transactional
    public void addLog(Long userId, ChallengeLogDto dto) {

        // 0) 필수값 체크
        if (userId == null) {
            throw new IllegalArgumentException("로그인 정보가 없습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("요청 데이터가 없습니다.");
        }
        if (dto.getUserChallengeId() == null) {
            throw new IllegalArgumentException("userChallengeId가 없습니다.");
        }
        if (dto.getLogDate() == null) {
            throw new IllegalArgumentException("logDate가 없습니다.");
        }
        if (dto.getStatus() == null || dto.getStatus().isBlank()) {
            throw new IllegalArgumentException("status가 없습니다.");
        }
        // memo는 선택값이니 null 허용 (원하면 trim 처리)
        // dto.setMemo(dto.getMemo() == null ? "" : dto.getMemo().trim());

        // 1) 본인 & 참여정보 확인
        UserChallengeDto uc = userChallengeDao.selectById(dto.getUserChallengeId());
        if (uc == null) {
            throw new IllegalArgumentException("챌린지 참여 정보가 없습니다.");
        }
        if (!uc.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인만 로그를 등록할 수 있습니다.");
        }

        // 2) 진행 중인 챌린지만 로그 가능
        if (!"PROGRESS".equals(uc.getStatus())) {
            throw new IllegalArgumentException("진행 중인 챌린지에만 로그를 등록할 수 있습니다.");
        }

        LocalDate logDate = dto.getLogDate();
        LocalDate today = LocalDate.now();

        // 3) 미래 날짜 금지
        if (logDate.isAfter(today)) {
            throw new IllegalArgumentException("미래 날짜에는 로그를 등록할 수 없습니다.");
        }

        // 4) 참여 시작일 이전 금지 (시작일부터 오늘까지 가능)
        if (uc.getStartDate() != null && logDate.isBefore(uc.getStartDate())) {
            throw new IllegalArgumentException("참여 시작일 이전 날짜에는 로그를 등록할 수 없습니다.");
        }

        // 5) 같은 날짜 중복 방지 (서비스 1차 방어)
        ChallengeLogDto existing =
            challengeLogDao.selectByUserChallengeAndDate(dto.getUserChallengeId(), logDate);
        if (existing != null) {
            throw new IllegalStateException("이미 해당 날짜에 로그가 존재합니다.");
        }

        // 6) INSERT (DB 유니크가 있으면 여기서도 한 번 더 방어)
        try {
            challengeLogDao.insertLog(dto);
        } catch (DuplicateKeyException e) {
            // (challenge_log에 user_challenge_id + log_date 유니크가 있는 경우)
            throw new IllegalStateException("이미 해당 날짜에 로그가 존재합니다.");
        }
        
        String st = dto.getStatus() == null ? "" : dto.getStatus().trim();
        if ("SUCCESS".equalsIgnoreCase(st) && dto.getLogDate().isEqual(LocalDate.now())) {
            gamificationService.earnXp(
                userId,
                ActionType.CHALLENGE_SUCCESS,
                null,          // ✅ 날짜 refId 강제(하루 1회)니까 null로
                15,
                dto.getLogDate()
            );
        }

   }
    
    @Override
    public ChallengeLogDto getLogById(Long logId) {
        return challengeLogDao.selectById(logId);
    }

    @Override
    public void deleteLog(Long logId) {
        challengeLogDao.deleteLog(logId);
    }

    // ===============================
    // 삭제
    // ===============================
    @Override
    public boolean deleteChallenge(Long challengeId, Long loginUserId) {
        ChallengeDto challenge = challengeDao.selectById(challengeId);
        if (challenge == null) return false;

        if (!challenge.getUserId().equals(loginUserId)) {
            return false;
        }


        challengeLogDao.deleteByChallengeId(challengeId);
        userChallengeDao.deleteByChallengeId(challengeId);
        challengeDao.deleteChallenge(challengeId);

        return true;
    }
}