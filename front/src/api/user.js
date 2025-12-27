import axios from "./axios";

export default {
  getMyInfo() {
    return axios.get("/mypage/info");
  },
  updateInfo(data) {
    return axios.put("/mypage/info", data);
  },
  updatePassword(data) {
    return axios.put("/mypage/password", data);
  },
  deleteUser() {
    return axios.delete("/mypage/delete");
  },
  getMyMedical() {
    return axios.get("/mypage/medical");
  },
  signupMedInfo(payload) {
    return axios.post("/mypage/med-info", payload); // ✅ api -> axios
  },
};
