// src/api/mediscan.js
import api from "./axios";

export default {
  // 🔥 이미지 스캔 전용
  scanImage(file) {
    const formData = new FormData();
    formData.append("image", file);

    return api.post("/mediscan/scan", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  },
};
``