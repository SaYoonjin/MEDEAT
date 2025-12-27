// src/api/diet.js
import axios from "@/api/axios";

export default {
  // 날짜별 목록 조회
  getList(date) {
    return axios.get("/diet", { params: { date } });
  },

  // 단일 상세 조회
  getDetail(id) {
    return axios.get(`/diet/${id}`);
  },

  // 저장 (신규 등록)
  save(formData) {
    return axios.post("/diet", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  },

  // 수정
  update(id, formData) {
    return axios.put(`/diet/${id}`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  },

  // 식단 전체 삭제
  deleteDiet(id) {
    return axios.delete(`/diet/${id}`);
  },

  // 음식 한 개 삭제
  deleteItem(itemId) {
    return axios.delete(`/diet/item/${itemId}`);
  },

  // 음식 검색
  searchFood(keyword) {
    return axios.get("/food/search", {
      params: { keyword },
    });
  },

  // 달력
  getCalendarLogs(month) {
    return axios.get("/diet/calendar", {
      params: { month },
    });
  },

  // 잇모드 분석
  analyzeIt(startDate, endDate) {
    return axios.get("/diet/analysis/eat", {
      params: { startDate, endDate },
    });
  },

  // 메딧모드 분석
  analyzeMed(startDate, endDate) {
    return axios.get("/diet/analysis/med", {
      params: { startDate, endDate },
    });
  },

  getStats(period, baseDate) {
    return axios.get("/diet/analysis", { params: { period, baseDate } });
  },

  getStatsRange(startDate, endDate) {
    return axios.get("/diet/analysis/range", {
      params: { startDate, endDate },
    });
  },

  // ✅ 정교 추천
  // params: { date, userId? } 형태로 넘겨받아서 그대로 쿼리스트링으로 보냄
  getRecommendations(userId, date) {
    return axios.get("/diet/recommend", {
      params: {userId, date},
    });
  },
};
