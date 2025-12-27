<template>
  <div class="page-wrapper">
    <!-- 🌟 배경: 마이페이지/회원가입 스타일 완벽 동기화 (연두 + 하늘 공존) -->
    <div class="background-decorations">
      <div class="blob blob-eat"></div>
      <div class="blob blob-medi"></div>
    </div>

    <!-- 전체 화면을 채우는 글래스 레이어 -->
    <main class="main-viewport">
      <div class="content-wrapper">
        <!-- 상단 헤더 영역 -->
        <header class="header-section">
          <div class="brand-tag font-black italic">MEDEAT</div>
          <h1 class="main-title font-black italic tracking-tighter">LOGIN</h1>
          <p class="description">등록한 아이디와 비밀번호를 입력해 주세요.</p>
        </header>

        <!-- 로그인 폼 섹션 -->
        <section class="form-section">
          <form @submit.prevent="doLogin" class="input-stack">
            <!-- 아이디 입력 -->
            <div class="input-group">
              <label>ID <span class="required">*</span></label>
              <input
                v-model="loginId"
                type="text"
                placeholder="아이디를 입력하세요"
                class="premium-input"
              />
            </div>

            <!-- 비밀번호 입력 -->
            <div class="input-group">
              <label>PASSWORD <span class="required">*</span></label>
              <input
                v-model="password"
                type="password"
                placeholder="비밀번호를 입력하세요"
                class="premium-input"
              />
            </div>

            <!-- 버튼 및 메시지 영역 -->
            <div class="action-trigger">
              <button
                type="submit"
                class="cta-submit font-black"
                :disabled="!canSubmit || loading"
                :class="{ disabled: !canSubmit || loading }"
              >
                {{ loading ? "로그인 중..." : "로그인" }}
              </button>

              <!-- 상태 메시지 (에러 및 힌트) -->
              <div class="status-container">
                <Transition name="slide-fade">
                  <p v-if="!canSubmit && !loading" class="status-msg info centered">
                    {{ disabledHint }}
                  </p>
                </Transition>
                
                <Transition name="slide-fade">
                  <p v-if="errorMessage" class="status-msg error centered">
                    {{ errorMessage }}
                  </p>
                </Transition>
              </div>
            </div>
          </form>
        </section>

        <!-- 하단 네비게이션 -->
        <footer class="footer-section">
          <div class="helper-links">
            <router-link to="/auth/find-id" class="nav-link font-black">아이디 찾기</router-link>
            <span class="divider">/</span>
            <router-link to="/auth/reset-password" class="nav-link font-black">비밀번호 찾기</router-link>
            <span class="divider">/</span>
            <router-link to="/signup" class="nav-link font-black">회원가입</router-link>
          </div>
        </footer>
      </div>
    </main>
  </div>
</template>

<script>
import authApi from "@/api/auth";

export default {
  name: "LoginView",
  data() {
    return {
      loginId: "",
      password: "",
      errorMessage: "",
      loading: false,
    };
  },
  computed: {
    canSubmit() {
      const id = this.loginId.trim();
      const pw = this.password.trim();
      return id.length > 0 && pw.length > 0;
    },
    disabledHint() {
      const missing = [];
      if (!this.loginId.trim()) missing.push("아이디");
      if (!this.password.trim()) missing.push("비밀번호");

      if (missing.length > 0) {
        return `필수 입력 항목: ${missing.join(", ")}`;
      }
      return "";
    },
  },
  methods: {
    async doLogin() {
      this.errorMessage = "";
      if (!this.canSubmit) return;
      this.loading = true;

      try {
        const res = await authApi.login({
          loginId: this.loginId,
          password: this.password,
        });

        // 사용자 정보 저장
        localStorage.setItem(
          "loginUser",
          JSON.stringify({
            userId: res.data.userId,
            loginId: res.data.loginId,
            nickname: res.data.nickname,
          })
        );

        console.log("로그인 성공:", res.data);
        // 기본 대시보드로 이동 (EAT 모드 우선순위)
        this.$router.push("/dashboard?mode=EAT");
      } catch (err) {
        console.error("로그인 오류:", err);
        this.errorMessage =
          err?.response?.data?.message ||
          "로그인 실패. 아이디/비밀번호를 확인하세요.";
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped>
/* 1. Base Layout */
.page-wrapper {
  width: 100vw;
  min-height: 100vh;
  background-color: #f8f9fc;
  position: relative;
  overflow-x: hidden;
  color: #1a1a1a;
}

/* 2. Dynamic Background - 마이페이지/회원가입 스타일 동기화 */
.background-decorations {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%; z-index: 0; pointer-events: none;
}

.blob {
  position: fixed;
  border-radius: 50%;
  filter: blur(160px);
  z-index: 0;
  opacity: 0.25;
  pointer-events: none;
}

.blob-eat { 
  top: -10%; left: -10%; width: 55vw; height: 55vw; 
  background: radial-gradient(circle, rgba(163, 230, 53, 0.8) 0%, transparent 70%); 
} 
.blob-medi { 
  bottom: -15%; right: -10%; width: 50vw; height: 50vw; 
  background: radial-gradient(circle, rgba(56, 189, 248, 0.7) 0%, transparent 70%); 
}

/* 3. Main Viewport */
.main-viewport {
  position: relative;
  z-index: 10;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.content-wrapper {
  width: 100%;
  max-width: 480px;
  display: flex;
  flex-direction: column;
}

/* 4. Header Section */
.header-section {
  text-align: center;
  margin-bottom: 50px;
}

.brand-tag {
  color: #7c4dff;
  font-weight: 950;
  font-size: 0.85rem;
  letter-spacing: 4px;
  margin-bottom: 20px;
  display: block;
}

.main-title {
  font-size: clamp(3rem, 7vw, 4.2rem);
  letter-spacing: -3px;
  line-height: 0.85;
  margin-bottom: 20px;
  color: #1a1a1a;
}

.description {
  font-size: 1.05rem;
  color: #4b5563;
  font-weight: 600;
  line-height: 1.6;
  opacity: 0.6;
}

/* 5. Form & Inputs */
.input-stack {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.input-group {
  display: flex;
  flex-direction: column;
}

label {
  font-size: 0.7rem;
  font-weight: 900;
  letter-spacing: 0.15em;
  color: #1a1a1a;
  opacity: 0.4;
  margin-bottom: 10px;
  padding-left: 8px;
  text-transform: uppercase;
}

.required { color: #ef4444; margin-left: 2px; }

.premium-input {
  width: 100%;
  padding: 18px 24px;
  background: rgba(255, 255, 255, 0.5);
  border: 1.5px solid rgba(255, 255, 255, 0.7);
  border-radius: 20px;
  font-size: 1.05rem;
  font-weight: 800;
  color: #1a1a1a;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  box-sizing: border-box;
}

.premium-input:focus {
  outline: none;
  background: #ffffff;
  border-color: #7c4dff;
  box-shadow: 0 10px 30px rgba(124, 77, 255, 0.1);
  transform: translateY(-2px);
}

/* 6. Action Area */
.cta-submit {
  width: 100%;
  padding: 22px;
  background: #111827;
  color: #ffffff;
  border: none;
  border-radius: 22px;
  font-size: 1.15rem;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.cta-submit:hover:not(.disabled) {
  background: #7c4dff;
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(124, 77, 255, 0.2);
}

.cta-submit.disabled {
  opacity: 0.4;
  cursor: not-allowed;
  background: #9ca3af;
  box-shadow: none;
}

/* 7. Status & Messages */
.status-container {
  min-height: 40px;
  margin-top: 16px;
}

.status-msg {
  font-size: 0.8rem;
  font-weight: 800;
  line-height: 1.5;
}
.status-msg.info { color: #94a3b8; }
.status-msg.error { color: #f43f5e; }
.status-msg.centered { text-align: center; }

/* 8. Footer Section */
.footer-section {
  margin-top: 40px;
  text-align: center;
}

.helper-links {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
}

.nav-link {
  font-size: 0.9rem;
  color: #94a3b8;
  text-decoration: none;
  transition: all 0.3s;
}

.nav-link:hover {
  color: #7c4dff;
}

.divider {
  color: #cbd5e1;
  font-size: 0.8rem;
  opacity: 0.5;
}

/* 9. Utilities & Transitions */
.font-black { font-weight: 950; }
.italic { font-style: italic; }
.tracking-tighter { letter-spacing: -0.06em; }

.gradient-text {
  background: linear-gradient(to right, #1a1a1a, #7c4dff, #38bdf8);
  -webkit-background-clip: text; background-clip: text; color: transparent;
}

.slide-fade-enter-active, .slide-fade-leave-active { transition: all 0.4s ease; }
.slide-fade-enter-from, .slide-fade-leave-to { opacity: 0; transform: translateY(5px); }

@media (max-width: 600px) {
  .main-viewport { padding: 24px; }
  .main-title { font-size: 2.8rem; }
  .cta-submit { padding: 18px; }
}
</style>