// src/api/challenge.js
import api from './axios';

export default {
  // 챌린지 목록 조회 (popular, ongoing, available)
  getList(mode = 'EAT') {
    return api.get('/challenge', {
      params: { mode },
    });
  },

  // ✅ 참여 중인 챌린지 상세 (userChallengeId 기준)
  getDetail(userChallengeId, mode) {
    return api.get(`/challenge/${userChallengeId}`, {
      params: { mode },
    });
  },

  // ✅ 내가 만든 챌린지 기본 정보 조회 (challengeId 기준)
  getChallengeInfo(challengeId) {
    return api.get(`/challenge/detail/${challengeId}`);
  },

  // 챌린지 생성
  createChallenge(data) {
    return api.post('/challenge', data);
  },

  // 챌린지 참여
  join(challengeId) {
    return api.post('/challenge/join', null, {
      params: { challengeId },
    });
  },

  // ✅ 챌린지 수정
  updateChallenge(challengeId, data) {
    // 백엔드에서 PUT /challenge/{id} 로 받는다고 가정
    return api.put(`/challenge/${challengeId}`, data);
  },

  // 챌린지 포기
  giveUp(userChallengeId) {
    return api.post('/challenge/giveup', null, {
      params: { userChallengeId },
    });
  },

  // 챌린지 로그 추가 (MEDI-EAT)
  addLog({ userChallengeId, status, memo, logDate }) {
    return api.post('/challenge/log', null, {
      params: { userChallengeId, status, memo, logDate },
    });
  },


  // 내 뱃지 목록
  getMyBadges() {
    return api.get('/challenge/badges');
  },

  // 챌린지 로그 삭제
  deleteLog(logId) {
    return api.post('/challenge/log/delete', null, {
      params: { challengeLogId: logId },
    });
  },
};
