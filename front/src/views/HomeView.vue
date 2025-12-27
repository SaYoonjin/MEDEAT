<template>
  <div class="home-wrapper">
    <div class="home-card">

      <div class="title">MEDEAT에 오신 것을 환영합니다 👋</div>
      <div class="subtitle">건강한 매일을 위한 나만의 식단 & 의약 관리 서비스</div>

      <!-- 버튼 그룹 -->
      <div class="button-group">
        <router-link to="/signup" class="btn signup">회원가입</router-link>
        <router-link to="/login" class="btn login">로그인</router-link>
      </div>

      <div class="footer">© 2025 MEDEAT</div>
    </div>
  </div>
</template>

<script>
export default {
  name: "HomeView",
};
</script>

<style scoped>
/* 기본 폰트 및 배경 */
:global(html, body, #app) {
  margin: 0;
  padding: 0;
  height: 100%;
  background: linear-gradient(135deg, #e8f0ff, #f5f7fa);
  font-family: "Noto Sans KR", sans-serif;
}

/* 화면 전체 가운데 정렬 */
.home-wrapper {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

/* 카드 스타일 */
.home-card {
  background: white;
  width: 450px;
  padding: 50px 40px;
  border-radius: 22px;
  box-shadow: 0 10px 28px rgba(0,0,0,0.12);
  text-align: center;

  /* 부드러운 등장 애니메이션 */
  animation: fadeUp 0.7s ease forwards;
}

/* 카드 등장 애니메이션 */
@keyframes fadeUp {
  0% { opacity: 0; transform: translateY(25px); }
  100% { opacity: 1; transform: translateY(0); }
}

/* 제목 */
.title {
  font-size: 2rem;
  font-weight: 800;
  color: #2b2b2b;
  margin-bottom: 12px;
}

/* 부제목 */
.subtitle {
  font-size: 1rem;
  color: #666;
  margin-bottom: 45px;
}

/* 버튼 그룹 (가로 정렬) */
.button-group {
  display: flex;
  justify-content: center;
  gap: 18px;
}

/* 공통 버튼 스타일 */
.btn {
  display: inline-block;
  width: 150px;
  padding: 14px 0;
  font-size: 1.1rem;
  font-weight: 600;
  border-radius: 12px;
  color: white;
  text-align: center;
  text-decoration: none;
  transition: 0.25s ease;
}

/* 회원가입 버튼 */
.btn.signup {
  background: #3c7dff;
  box-shadow: 0 4px 12px rgba(60, 125, 255, 0.3);
}
.btn.signup:hover {
  background: #2e64d6;
}

/* 로그인 버튼 */
.btn.login {
  background: #4CAF50;
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
}
.btn.login:hover {
  background: #3d8a40;
}

/* 푸터 */
.footer {
  margin-top: 35px;
  font-size: 0.85rem;
  color: #888;
}
</style>
