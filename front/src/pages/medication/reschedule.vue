<script setup>
import { ref, computed } from "vue";
import axios from "@/api/axios";
import { useRoute, useRouter } from "vue-router";
import { 
  Sparkles, 
  Clock, 
  BellRing, 
  ArrowRight, 
  X,
  Check,
  AlertCircle,
  Loader2
} from "lucide-vue-next";

const route = useRoute();
const router = useRouter();

/* ===============================
   상태 관리 및 데이터 수신
================================ */
// service-worker 또는 이전 페이지에서 전달된 데이터
const medicationId = Number(route.query.medicationId);
const doseIndex = Number(route.query.doseIndex);

const delayMinutes = ref(10); // 기본값 10분 설정
const isSubmitting = ref(false);

// 퀵 선택 옵션
const quickOptions = [
  { label: '5분 후', value: 5 },
  { label: '10분 후', value: 10 },
  { label: '30분 후', value: 30 },
  { label: '1시간 후', value: 60 },
];

/* ===============================
   액션 로직
================================ */
const setQuickDelay = (val) => {
  delayMinutes.value = val;
};

const sendReschedule = async () => {
  if (!delayMinutes.value || delayMinutes.value < 1) {
    return;
  }

  isSubmitting.value = true;
  try {
    await axios.post("/medication/reschedule", {
      medicationId,
      doseIndex,
      delayMinutes: delayMinutes.value,
    });
    
    // 성공 시 부드러운 이동
    router.push("/disease?mode=MEDI_EAT");
  } catch (e) {
    console.error("재알림 설정 실패:", e);
  } finally {
    isSubmitting.value = false;
  }
};

const goBack = () => {
  router.back();
};
</script>

<template>
  <div class="intro-container">
    <!-- 배경 장식 (MEDI 전용 유동적인 Blob) -->
    <div class="blob-medi"></div>

    <main class="content-wrapper">
      <div class="reschedule-card glass-card">
        <!-- 닫기 버튼 -->
        <button class="close-btn" @click="goBack"><X :size="24" /></button>

        <header class="page-header">
          <div class="badge">
            <BellRing :size="12" class="icon-purple" />
            <span>SNOOZE NOTIFICATION</span>
          </div>
          <h1 class="hero-title italic">
            REMIND ME<br />
            <span class="gradient-text">LATER.</span>
          </h1>
          <p class="hero-subtext">지금 복용이 어려우신가요? 잠시 후 다시 알려드릴게요.</p>
        </header>

        <div class="form-container mt-12">
          <!-- 1. 퀵 선택 칩 -->
          <div class="section-group">
            <label class="field-label italic">QUICK SELECT</label>
            <div class="chip-grid">
              <button 
                v-for="opt in quickOptions" 
                :key="opt.value"
                class="time-chip"
                :class="{ active: delayMinutes === opt.value }"
                @click="setQuickDelay(opt.value)"
              >
                {{ opt.label }}
              </button>
            </div>
          </div>

          <!-- 2. 직접 입력 -->
          <div class="section-group mt-10">
            <label class="field-label italic">CUSTOM TIME</label>
            <div class="modern-input-box">
              <Clock :size="20" class="input-icon" />
              <input 
                type="number" 
                v-model.number="delayMinutes" 
                placeholder="분 단위 입력"
                min="1"
              />
              <span class="unit-text">MINUTES</span>
            </div>
            <p v-if="delayMinutes < 1" class="error-hint mt-2">
              <AlertCircle :size="14" />
              <span>1분 이상의 시간을 입력해주세요.</span>
            </p>
          </div>
        </div>

        <!-- 액션 버튼 -->
        <footer class="action-footer mt-16">
          <button 
            class="main-start-btn w-full" 
            :disabled="delayMinutes < 1 || isSubmitting"
            @click="sendReschedule"
          >
            <template v-if="!isSubmitting">
              CONFIRM DELAY
              <ArrowRight :size="20" />
            </template>
            <template v-else>
              <Loader2 :size="20" class="animate-spin" />
              SETTING UP...
            </template>
          </button>
          <!-- 버튼 아래 멘트 간격 mt-6 -> mt-12로 대폭 확대 -->
          <p class="guide-note mt-12">
            설정하신 시간 후에 푸시 알림으로 다시 찾아뵐게요.
          </p>
        </footer>
      </div>
    </main>
  </div>
</template>

<style scoped>
/* 인트로 레이아웃 계승 */
.intro-container {
  background-color: #f8f9fc;
  color: #1a1a1a;
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
  padding: 120px 24px 80px;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

/* MEDI 전용 배경 Blob 애니메이션 */
.blob-medi {
  position: fixed;
  top: -10%;
  right: -10%;
  width: 70vw;
  height: 70vw;
  background: radial-gradient(circle, rgba(56, 189, 248, 0.18) 0%, transparent 70%);
  border-radius: 50%;
  filter: blur(140px);
  z-index: 0;
  pointer-events: none;
  animation: blobFloat 20s infinite alternate ease-in-out;
}

@keyframes blobFloat {
  0% { transform: translate(0, 0) rotate(0deg); }
  100% { transform: translate(5%, 5%) rotate(180deg); }
}

.content-wrapper {
  width: 100%;
  max-width: 560px;
  position: relative;
  z-index: 10;
}

.glass-card {
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 50px;
  padding: 60px 48px;
  box-shadow: 0 40px 100px rgba(0, 0, 0, 0.02);
  position: relative;
}

/* 닫기 버튼 */
.close-btn {
  position: absolute;
  top: 32px;
  right: 32px;
  background: none;
  border: none;
  color: #94a3b8;
  cursor: pointer;
  transition: 0.3s;
}
.close-btn:hover { color: #1a1a1a; transform: rotate(90deg); }

/* Header 스타일 */
.page-header { text-align: center; }
.badge {
  display: inline-flex; align-items: center; gap: 6px; padding: 6px 14px;
  background: white; border-radius: 999px; margin-bottom: 20px;
  font-size: 0.65rem; font-weight: 850; color: #7c4dff; border: 1.5px solid #f1f5f9;
}
.hero-title { font-size: 3rem; line-height: 0.95; font-weight: 950; letter-spacing: -0.05em; margin-bottom: 20px; }
.gradient-text {
  background: linear-gradient(to bottom right, #1a1a1a, #38bdf8);
  -webkit-background-clip: text; background-clip: text; color: transparent;
}
.hero-subtext { font-size: 1rem; opacity: 0.6; font-weight: 600; line-height: 1.5; }

/* Form 스타일 */
.field-label {
  display: block; font-size: 0.8rem; font-weight: 900; color: #cbd5e1;
  margin-bottom: 16px; letter-spacing: 0.05em;
}

/* 퀵 칩 그리드 */
.chip-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
.time-chip {
  padding: 16px;
  border-radius: 20px;
  background: white;
  border: 1.5px solid #f1f5f9;
  font-size: 0.95rem;
  font-weight: 800;
  color: #64748b;
  cursor: pointer;
  transition: 0.3s cubic-bezier(0.23, 1, 0.32, 1);
}
.time-chip:hover {
  transform: translateY(-3px);
  border-color: #38bdf8;
  color: #1a1a1a;
  box-shadow: 0 10px 20px rgba(56, 189, 248, 0.1);
}
.time-chip.active {
  background: #1a1a1a;
  color: white;
  border-color: #1a1a1a;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

/* 입력창 스타일 */
.modern-input-box {
  background: white;
  border: 1.5px solid #f1f5f9;
  border-radius: 20px;
  padding: 18px 24px;
  display: flex;
  align-items: center;
  gap: 14px;
  transition: 0.3s;
}
.modern-input-box:focus-within {
  border-color: #38bdf8;
  box-shadow: 0 15px 30px rgba(56, 189, 248, 0.1);
}
.modern-input-box input {
  flex: 1; border: none; outline: none;
  font-size: 1.2rem; font-weight: 850; color: #1a1a1a;
  background: none;
}
.input-icon { color: #38bdf8; }
.unit-text { font-size: 0.7rem; font-weight: 900; color: #94a3b8; letter-spacing: 0.05em; }

.error-hint {
  display: flex; align-items: center; gap: 6px;
  font-size: 0.75rem; font-weight: 700; color: #f43f5e;
}

/* 액션 버튼 */
.main-start-btn {
  display: flex; align-items: center; justify-content: center; gap: 12px;
  padding: 22px; background: #1a1a1a; color: white; border: none; border-radius: 999px;
  font-size: 1.05rem; font-weight: 850; cursor: pointer; transition: all 0.4s;
}
.main-start-btn:hover:not(:disabled) { 
  background: #2a2a2a; transform: translateY(-5px); 
  box-shadow: 0 20px 40px rgba(56, 189, 248, 0.2); 
}
.main-start-btn:disabled { opacity: 0.2; cursor: not-allowed; }

.guide-note { font-size: 0.8rem; color: #94a3b8; text-align: center; font-weight: 600; }

.italic { font-style: italic; }
.mt-10 { margin-top: 2.5rem; }
.mt-12 { margin-top: 2rem; }
.mt-16 { margin-top: 4rem; }
.w-full { width: 100%; }

.animate-spin { animation: spin 1s linear infinite; }
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }

@media (max-width: 480px) {
  .glass-card { padding: 40px 24px; border-radius: 40px; }
  .hero-title { font-size: 2.4rem; }
}
</style>