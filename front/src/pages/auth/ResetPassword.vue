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
          <h1 class="main-title font-black italic tracking-tighter">RESET<br/>PASSWORD</h1>
          <p class="description">안전한 계정 사용을 위해 비밀번호를 재설정합니다.</p>
        </header>

        <!-- 비밀번호 재설정 섹션 -->
        <section class="form-section">
          <div class="input-stack">
            
            <!-- STEP 1: 아이디 + 이메일 입력 -->
            <div class="input-group">
              <label>ID</label>
              <input
                type="text"
                v-model="loginId"
                placeholder="가입한 아이디를 입력하세요"
                class="premium-input"
              />
            </div>

            <div class="input-group">
              <label>EMAIL ADDRESS</label>
              <div class="flex-row">
                <input 
                  type="email" 
                  v-model="email" 
                  placeholder="example@medeat.com" 
                  class="premium-input"
                />
                <button class="util-btn font-black" @click="sendCode" :disabled="sending">
                  {{ sending ? "..." : "SEND" }}
                </button>
              </div>
              <Transition name="slide-fade">
                <p v-if="sendMsg" class="status-msg info">{{ sendMsg }}</p>
              </Transition>
              <p v-if="timer > 0" class="timer-display">남은 시간: {{ formatTime }}</p>
            </div>

            <!-- STEP 2: 인증코드 확인 -->
            <Transition name="slide-fade">
              <div class="input-group" v-if="codeSent">
                <label>VERIFICATION CODE</label>
                <div class="flex-row">
                  <input 
                    type="text" 
                    v-model="code" 
                    maxlength="6" 
                    placeholder="6자리 인증코드" 
                    class="premium-input"
                  />
                  <button class="util-btn check-btn font-black" @click="verifyCode" :disabled="verifying">
                    {{ verifying ? "..." : "CHECK" }}
                  </button>
                </div>
                <Transition name="slide-fade">
                  <p v-if="verifyMessage" :class="['status-msg', isValid ? 'success' : 'error']">
                    {{ verifyMessage }}
                  </p>
                </Transition>
              </div>
            </Transition>

            <!-- STEP 3: 새 비밀번호 입력 -->
            <Transition name="slide-fade">
              <div class="password-reset-fields" v-if="isValid">
                <div class="input-group">
                  <label>NEW PASSWORD</label>
                  <input 
                    type="password" 
                    v-model="newPw" 
                    placeholder="새 비밀번호를 입력하세요" 
                    class="premium-input"
                  />
                </div>

                <div class="input-group mt-24">
                  <label>CONFIRM PASSWORD</label>
                  <input 
                    type="password" 
                    v-model="confirmPw" 
                    placeholder="비밀번호를 다시 입력하세요" 
                    class="premium-input"
                  />
                </div>

                <div class="action-trigger">
                  <button class="cta-submit font-black" @click="resetPassword" :disabled="resetting">
                    {{ resetting ? "변경 중..." : "비밀번호 변경하기" }}
                  </button>
                  <Transition name="slide-fade">
                    <p v-if="resetMessage" :class="['status-msg centered', resetError ? 'error' : 'success']">
                      {{ resetMessage }}
                    </p>
                  </Transition>
                </div>
              </div>
            </Transition>
          </div>
        </section>

        <!-- 하단 네비게이션 -->
        <footer class="footer-section">
          <router-link to="/login" class="nav-link font-black">로그인 페이지로 돌아가기</router-link>
        </footer>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import authApi from "@/api/auth";

const loginId = ref("");
const email = ref("");
const code = ref("");

const sending = ref(false);
const sendMsg = ref("");
const codeSent = ref(false);

const verifying = ref(false);
const verifyMessage = ref("");
const isValid = ref(false);

const newPw = ref("");
const confirmPw = ref("");

const resetting = ref(false);
const resetMessage = ref("");
const resetError = ref(false);

// 타이머
const timer = ref(0);
let timerId = null;

const formatTime = computed(() => {
  const m = Math.floor(timer.value / 60);
  const s = timer.value % 60;
  return `${m}:${s < 10 ? "0" + s : s}`;
});

function startTimer() {
  timer.value = 180;
  if (timerId) clearInterval(timerId);

  timerId = setInterval(() => {
    if (timer.value > 0) timer.value--;
    else clearInterval(timerId);
  }, 1000);
}

// 인증코드 발송
async function sendCode() {
  if (!email.value || !loginId.value) {
    sendMsg.value = "아이디와 이메일을 모두 입력해주세요.";
    return;
  }

  try {
    sending.value = true;
    await authApi.sendEmailCode(email.value);

    sendMsg.value = "인증코드를 이메일로 전송했습니다.";
    codeSent.value = true;
    startTimer();
  } catch {
    sendMsg.value = "전송 실패. 정보를 확인해주세요.";
  } finally {
    sending.value = false;
  }
}

// 인증코드 확인
async function verifyCode() {
  try {
    verifying.value = true;
    const res = await authApi.verifyEmailCode(email.value, code.value);

    if (res.data.valid) {
      isValid.value = true;
      verifyMessage.value = "인증이 완료되었습니다.";
    } else {
      isValid.value = false;
      verifyMessage.value = "인증코드가 올바르지 않습니다.";
    }
  } catch {
    verifyMessage.value = "오류가 발생했습니다.";
  } finally {
    verifying.value = false;
  }
}

// 비밀번호 변경
async function resetPassword() {
  if (newPw.value !== confirmPw.value) {
    resetError.value = true;
    resetMessage.value = "비밀번호가 일치하지 않습니다.";
    return;
  }

  try {
    resetting.value = true;
    await authApi.resetPassword(email.value, newPw.value);
    alert("비밀번호가 정상적으로 변경되었습니다!");
    window.location.href = "/login";
  } catch {
    resetError.value = true;
    resetMessage.value = "변경 실패. 잠시 후 다시 시도해주세요.";
  } finally {
    resetting.value = false;
  }
}
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

/* 2. Dynamic Background - 마이페이지 스타일 동기화 */
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
  padding: 80px 40px;
}

.content-wrapper {
  width: 100%;
  max-width: 520px;
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
  font-size: clamp(2.5rem, 6vw, 3.8rem);
  letter-spacing: -3px;
  line-height: 0.85;
  margin-bottom: 16px;
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
  gap: 28px;
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

.flex-row {
  display: flex;
  gap: 12px;
}

.premium-input {
  flex: 1;
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

.util-btn {
  padding: 0 24px;
  background: #111827;
  border: none;
  border-radius: 18px;
  color: #ffffff;
  font-size: 0.75rem;
  cursor: pointer;
  transition: all 0.3s;
}

.util-btn:hover:not(:disabled) {
  background: #7c4dff;
  transform: translateY(-2px);
}

.util-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.check-btn {
  background: #f1f5f9;
  color: #64748b;
}
.check-btn:hover:not(:disabled) {
  background: #1a1a1a;
  color: #fff;
}

/* 6. Action Area */
.action-trigger {
  margin-top: 32px;
}

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
}

/* 7. Status & Messages */
.status-msg {
  font-size: 0.85rem;
  font-weight: 800;
  margin-top: 10px;
}
.status-msg.info { color: #94a3b8; }
.status-msg.success { color: #10b981; }
.status-msg.error { color: #f43f5e; }
.status-msg.centered { text-align: center; }

.timer-display {
  text-align: right;
  font-size: 0.8rem;
  font-weight: 800;
  color: #f43f5e;
  margin-top: 8px;
  letter-spacing: 0.05em;
}

/* 8. Footer Section */
.footer-section {
  margin-top: 60px;
  text-align: center;
}

.nav-link {
  font-size: 0.9rem;
  color: #94a3b8;
  text-decoration: none;
  transition: color 0.3s;
}

.nav-link:hover {
  color: #7c4dff;
}

/* 9. Utilities & Transitions */
.font-black { font-weight: 950; }
.italic { font-style: italic; }
.tracking-tighter { letter-spacing: -0.06em; }
.mt-24 { margin-top: 24px; }

.gradient-text {
  background: linear-gradient(to right, #1a1a1a, #7c4dff, #38bdf8);
  -webkit-background-clip: text; background-clip: text; color: transparent;
}

.slide-fade-enter-active, .slide-fade-leave-active { transition: all 0.4s ease; }
.slide-fade-enter-from, .slide-fade-leave-to { opacity: 0; transform: translateY(10px); }

@media (max-width: 600px) {
  .main-viewport { padding: 40px 20px; }
  .main-title { font-size: 2.5rem; }
  .cta-submit { padding: 18px; }
}
</style>