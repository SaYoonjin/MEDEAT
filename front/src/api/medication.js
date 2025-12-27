import api from "./axios";   // baseURL '/api' 로 설정된 axios 인스턴스

export default {
  /* -----------------------------------------
      📌 약 목록 조회 (사용자 전체)
  ----------------------------------------- */
  getMyMedications() {
    return api.get("/medication");
  },

  getMyMedication() {
    return api.get("/medication");
  },

  /* -----------------------------------------
      📌 단일 약 조회
  ----------------------------------------- */
  getMedication(id) {
    return api.get(`/medication/${id}`);
  },

  /* -----------------------------------------
      📌 약 등록·수정
      - mediId가 있으면 PUT (수정)
      - 없으면 POST (신규 등록)
  ----------------------------------------- */
  save(data) {
    const id = data.medicationId ?? data.mediId; // 혹시 남아있을까봐 방어
    if (id) {
      return api.put(`/medication/${id}`, data);
    }
    return api.post("/medication", data);
  },

  /* -----------------------------------------
      📌 약 삭제
  ----------------------------------------- */
  remove(medicationId) {
    return api.delete(`/medication/${medicationId}`);
  },

  /* -----------------------------------------
      📌 자동완성 / 약 이름 검색
  ----------------------------------------- */
  search(keyword) {
    return api.get("/medication/search", {
      params: { keyword }
    });
  },

  /* -----------------------------------------
      📌 TODAY 로그 조회 (MEDI Dashboard)
  ----------------------------------------- */
  getTodayLogs() {
    return api.get("/medication/log/today");
  },

  /* -----------------------------------------
      📌 복용 로그 저장  ⭐ 최종본 (params 방식)
      data = { medicationId, takenIndex }
  ----------------------------------------- */
  saveLog({ medicationId, takenIndex }) {
    return api.post("/medication/log", null, {
  params: { medicationId, takenIndex }
    });
  },

  getWarnings() {
    return api.get("/medication/warnings");
  },

  // ✅ 이거 추가
  take({ medicationId, doseIndex }) {
    return api.post("/medication/take", {
      medicationId,
      doseIndex,
    });
  },
};
