// src/api/gamification.js
import api from "./axios";

export default {
  getProgress(userId, date) {
    return api.get("/progress", { params: { userId, date } });
  },

  // (선택) 개발/테스트용
  earnXp(payload) {
    return api.post("/progress/earn", payload);
  },
};
