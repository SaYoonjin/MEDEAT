import axios from './axios';

export default {
  getMyInfo() {
    return axios.get('/mypage/info');
  },
  getMyPosts() {
    return axios.get('/mypage/posts');      // 커뮤니티 글
  },
  getMyScraps() {
    return axios.get('/mypage/scraps');     // 커뮤니티 스크랩
  },
  getMyChallenges() {
    return axios.get('/mypage/challenges'); // ⭐ 새로 추가한 API
  },
  deleteChallenge(id) {
    return axios.delete(`/challenge/${id}`);
  },
  deleteCommunity(id) {
    return axios.delete(`/community/${id}`);
  },
  toggleCommunityScrap(id) {
    return axios.post('/community/scrap', null, {
      params: { postId: id },
    });
  },

  getMyLikes() {
  return axios.get('/mypage/likes');
  },

};
