<template>
  <div class="page-wrapper">
    <!-- 🌟 배경: 마이페이지/회원가입/로그인 스타일 완벽 동기화 (연두 + 하늘 공존) -->
    <div class="background-decorations">
      <div class="blob blob-eat"></div>
      <div class="blob blob-medi"></div>
    </div>

    <!-- 전체 화면을 덮는 투명 유리 오버레이 -->
    <main class="main-viewport">
      <div class="content-container">
        <header class="header-area">
          <!-- 🌟 브랜드 태그 보라색으로 변경 -->
          <div class="brand-tag font-black italic">MEDEAT</div>
          <h1 class="page-title font-black italic tracking-tighter">FIND ID</h1>
          <p class="page-subtitle">회원정보에 등록된 이름과 이메일을 입력해주세요.</p>
        </header>

        <div class="form-container">
          <!-- 1) 이름 -->
          <div class="form-group">
            <label>NAME</label>
            <input
              v-model="name"
              type="text"
              placeholder="가입한 이름을 입력하세요"
              class="glass-input"
            />
          </div>

          <!-- 2) 이메일 + 코드 발송 -->
          <div class="form-group">
            <label>EMAIL</label>
            <div class="input-with-btn">
              <input
                v-model="email"
                type="email"
                placeholder="example@medeat.com"
                class="glass-input"
              />
              <button class="util-btn font-black" @click="sendCode" :disabled="sending">
                {{ sending ? "..." : "SEND" }}
              </button>
            </div>

            <p v-if="sendMessage" class="msg info">{{ sendMessage }}</p>

            <p v-if="timer > 0" class="timer-display">
              남은 시간: {{ Math.floor(timer / 60) }}:{{ (timer % 60).toString().padStart(2, "0") }}
            </p>
          </div>

          <!-- 3) 인증코드 입력 -->
          <Transition name="slide-fade">
            <div class="form-group" v-if="codeSent">
              <label>VERIFICATION CODE</label>
              <div class="input-with-btn">
                <input v-model="code" placeholder="6자리 인증코드" class="glass-input" />
                <button class="util-btn check-btn font-black" @click="verifyCode">CHECK</button>
              </div>

              <p v-if="verifyMessage" :class="verified ? 'msg success' : 'msg error'">
                {{ verifyMessage }}
              </p>
            </div>
          </Transition>

          <!-- 4) 아이디 조회 -->
          <Transition name="slide-fade">
            <div class="form-group action-area mt-10" v-if="verified">
              <button class="main-action-btn font-black" @click="findId">아이디 조회하기</button>

              <div v-if="resultIds.length" class="result-box animate-in">
                <p class="mb-3">회원님의 아이디입니다 🎉</p>
                <ul>
                  <li v-for="id in resultIds" :key="id">
                    <strong>{{ id }}</strong>
                  </li>
                </ul>
              </div>


              <p v-if="notFound" class="msg error centered">
                일치하는 회원이 없습니다.
              </p>
            </div>
          </Transition>
        </div>

        <footer class="footer-area">
          <router-link class="back-link font-black" to="/login">로그인 하러가기</router-link>
        </footer>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from "vue";
import authApi from "@/api/auth";

const name = ref("");
const email = ref("");
const code = ref("");

const sending = ref(false);
const sendMessage = ref("");
const codeSent = ref(false);

const verified = ref(false);
const verifyMessage = ref("");

const resultIds = ref([]);
const notFound = ref(false);

const timer = ref(0);
let timerId = null;

const sendCode = async () => {
  if (!name.value || !email.value) {
    sendMessage.value = "이름과 이메일을 모두 입력해주세요.";
    return;
  }
  sending.value = true;
  sendMessage.value = "";
  try {
    await authApi.sendEmailCode(email.value);
    codeSent.value = true;
    sendMessage.value = "인증코드를 이메일로 전송했습니다.";
    startTimer();
  } catch {
    sendMessage.value = "이메일 발송 실패. 다시 시도해주세요.";
  } finally {
    sending.value = false;
  }
};

const startTimer = () => {
  if(timerId) clearInterval(timerId);
  timer.value = 180;
  timerId = setInterval(() => {
    if (timer.value > 0) timer.value--;
    else clearInterval(timerId);
  }, 1000);
};

const verifyCode = async () => {
  const res = await authApi.verifyEmailCode(email.value, code.value);
  if (res.data.valid) {
    verifyMessage.value = "인증 완료!";
    verified.value = true;
  } else {
    verifyMessage.value = "인증코드가 올바르지 않습니다.";
    verified.value = false;
  }
};

const findId = async () => {
  try {
    const res = await authApi.findId(email.value);
    resultIds.value = res.data.loginIds;
    notFound.value = false;
  } catch {
    notFound.value = true;
  }
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

/* 2. Dynamic Background - 타 페이지 스타일 동기화 */
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

.content-container {
  width: 100%;
  max-width: 520px;
  display: flex;
  flex-direction: column;
}

/* 4. Header Section */
.header-area {
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

.page-title {
  font-size: clamp(3rem, 7vw, 4.2rem);
  letter-spacing: -3px;
  line-height: 0.85;
  margin-bottom: 20px;
  color: #1a1a1a;
}

.page-subtitle {
  font-size: 1.05rem;
  color: #4b5563;
  font-weight: 600;
  line-height: 1.5;
  opacity: 0.6;
}

/* 5. Form & Inputs */
.form-group {
  margin-bottom: 32px;
}

label {
  display: block;
  font-size: 0.7rem;
  font-weight: 900;
  letter-spacing: 0.15em;
  color: #1a1a1a;
  opacity: 0.4;
  margin-bottom: 10px;
  padding-left: 8px;
  text-transform: uppercase;
}

.glass-input {
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

.glass-input:focus {
  outline: none;
  background: #ffffff;
  border-color: #7c4dff;
  box-shadow: 0 10px 30px rgba(124, 77, 255, 0.1);
  transform: translateY(-2px);
}

.input-with-btn {
  display: flex;
  gap: 12px;
}

/* 버튼 디자인 */
.util-btn {
  padding: 0 24px;
  background: #111827;
  border: none;
  border-radius: 18px;
  color: #ffffff;
  font-size: 0.75rem;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
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

.main-action-btn {
  width: 100%;
  padding: 22px;
  background: #111827;
  color: #ffffff;
  border: none;
  border-radius: 22px;
  font-size: 1.15rem;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}

.main-action-btn:hover {
  background: #7c4dff;
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(124, 77, 255, 0.2);
}

/* 메시지 디자인 */
.msg { font-size: 0.8rem; font-weight: 800; margin-top: 10px; }
.timer-display { font-size: 0.8rem; font-weight: 800; color: #f43f5e; margin-top: 8px; text-align: right; }
.success { color: #10b981; }
.error { color: #f43f5e; }
.info { color: #94a3b8; }

.result-box {
  margin-top: 40px;
  padding: 32px;
  background: white;
  border-radius: 32px;
  text-align: center;
  font-size: 1.2rem;
  font-weight: 800;
  box-shadow: 0 20px 40px rgba(0,0,0,0.03);
  border: 1px solid rgba(0,0,0,0.05);
}

.footer-area {
  margin-top: 60px;
  text-align: center;
}

.back-link {
  font-size: 0.9rem;
  color: #94a3b8;
  text-decoration: none;
  transition: color 0.3s;
}

.back-link:hover {
  color: #7c4dff;
}

/* 9. Utilities & Transitions */
.font-black { font-weight: 950; }
.italic { font-style: italic; }
.tracking-tighter { letter-spacing: -0.06em; }
.mt-10 { margin-top: 2.5rem; }

.slide-fade-enter-active, .slide-fade-leave-active { transition: all 0.4s ease; }
.slide-fade-enter-from, .slide-fade-leave-to { opacity: 0; transform: translateY(10px); }

.animate-in { animation: slideUp 0.6s cubic-bezier(0.23, 1, 0.32, 1) forwards; }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

@media (max-width: 600px) {
  .main-viewport { padding: 40px 20px; }
  .page-title { font-size: 2.8rem; }
  .header-area { margin-bottom: 40px; }
}
</style>