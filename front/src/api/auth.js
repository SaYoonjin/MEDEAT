import api from './axios';

export default {
  /* =======================
   * 인증 / 세션
   * ======================= */
  login(data) {
    return api.post('/auth/login', data);
  },

  checkLogin() {
    return api.get('/auth/checkLogin');
  },

  logout() {
    return api.delete('/auth/logout');
  },

  getUser() {
    return api.get('/auth/user');
  },

  /* =======================
   * 회원가입
   * ======================= */
  signup(data) {
    return api.post('/auth/signup', data);
  },

  signupMedInfo(data) {
    return api.post('/auth/med-info', data);
  },

  /* =======================
   * 이메일 인증
   * ======================= */
  sendEmailCode(email) {
    return api.post('/auth/send-email-code', { email });
  },

  verifyEmailCode(email, code) {
    return api.post('/auth/verify-email-code', { email, code });
  },

  /* =======================
   * 아이디 / 비밀번호 찾기
   * ======================= */
  findId(email) {
    return api.post('/auth/find-id', { email });
  },

  resetPassword(email, newPassword) {
    return api.post('/auth/reset-password', { email, newPassword });
  },
};
