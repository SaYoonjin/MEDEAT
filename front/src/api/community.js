import axios from "./axios";

export default {
  /** -------------------------
   * 게시글 목록 (+ 제목 검색)
   * GET /api/community?mode=EAT&keyword=다이어트
   --------------------------*/
  getList(mode = "EAT", keyword = "") {
    return axios.get(`/community`, {
      params: {
        mode,
        // 🔥 keyword가 있을 때만 전달
        ...(keyword && { keyword }),
      },
    });
  },

  /** -------------------------
   * 게시글 상세
   --------------------------*/
  getDetail(postId) {
    return axios.get(`/community/${postId}`);
  },

  /** -------------------------
   * 게시글 작성
   --------------------------*/
  writeFormData(formData) {
    return axios.post(`/community`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  },

  /** -------------------------
   * 게시글 수정
   --------------------------*/
  updateFormData(postId, formData) {
    return axios.put(`/community/${postId}`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  },

  /** -------------------------
   * 게시글 삭제
   --------------------------*/
  delete(postId) {
    return axios.delete(`/community/${postId}`);
  },

  /** -------------------------
   * 댓글 작성
   --------------------------*/
  addComment(postId, content) {
    return axios.post(`/community/comment`, {
      postId,
      content,
    });
  },

  /** -------------------------
   * 댓글 수정
   --------------------------*/
  updateComment(commentId, content) {
    return axios.put(`/community/comment`, {
      commentId,
      content,
    });
  },

  /** -------------------------
   * 댓글 삭제
   --------------------------*/
  deleteComment(commentId) {
    return axios.delete(`/community/comment/${commentId}`);
  },

  /** -------------------------
   * 좋아요 토글
   --------------------------*/
  toggleLike(postId) {
    return axios.post(`/community/like`, null, {
      params: { postId },
    });
  },

  /** -------------------------
   * 스크랩 토글
   --------------------------*/
  toggleScrap(postId) {
    return axios.post(`/community/scrap`, null, {
      params: { postId },
    });
  },

  /** -------------------------
   * 인기 게시글 1개
   --------------------------*/
  getTopPost() {
    return axios.get("/community/top");
  },
};
