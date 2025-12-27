import axios from "axios";

const api = axios.create({
  baseURL: "/api", // ✅ 프록시 없이 직결
  withCredentials: true,
});

export default api;
