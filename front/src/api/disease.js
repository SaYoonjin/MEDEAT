import api from "./axios";   // ✔ axios 인스턴스(api) 가져오기

export default {
  // 🔍 자동완성 검색
  search(keyword) {
    return api.get("/disease/search", { params: { keyword } });
  },

  // 📌 로그인한 유저의 질병 목록
  getMyDiseases() {
    return api.get("/disease/user");
  },

  // ➕ 기존 질병 추가
  addUserDisease(diseaseId) {
    return api.post("/disease/add", null, {
      params: { diseaseId },
    });
  },

  // ❌ 유저 질병 삭제
  deleteUserDisease(userDiseaseId) {
    return api.delete(`/disease/${userDiseaseId}`);
  },

  getAllDiseases() {
    return api.get("/disease/all");
  },

  // ✏ 사용자 정의 질병 추가
  addCustomDisease(name) {
    return api.post("/disease/custom", null, {
      params: { diseaseName: name },
    });
  }
};