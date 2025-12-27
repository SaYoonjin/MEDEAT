import api from "./axios";

export default {
  /**
   * 특정 챌린지 채팅 내역 조회
   * @param {number} challengeId
   */
  getMessages(challengeId) {
    return api.get(`/challenge/${challengeId}/chat`);
  },
};
