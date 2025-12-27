<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import challengeApi from "@/api/challenge";
import api from "@/api/axios.js";
import { 
  Sparkles, 
  MessagesSquare, 
  Users, 
  Calendar, 
  Trophy, 
  ChevronLeft, 
  Plus, 
  Trash2, 
  CheckCircle2, 
  XCircle, 
  ArrowRight,
  Loader2,
  Target,
  Edit,
  ArrowLeft,
  Inbox,
  ChevronRight,
  Zap,
  Activity
} from "lucide-vue-next";

const route = useRoute();
const router = useRouter();

const userChallengeId = Number(route.params.id);
const mode = computed(() => route.query.mode || "EAT");

const challenge = ref(null);
const uc = ref(null);
const logs = ref([]);
const loginUser = ref(null);

const loading = ref(true);
const error = ref("");

/* =========================
   🌟 테마 설정 (모드별 색상 및 블롭 위치 동기화)
========================= */
const themeConfig = computed(() => {
  if (mode.value === 'MEDI_EAT') {
    return {
      color: "#38bdf8", 
      soft: "rgba(56, 189, 248, 0.15)",
    }
  }
  return {
    color: "#a3e635", 
    soft: "rgba(163, 230, 53, 0.15)",
  }
});

// 오늘 날짜
const today = computed(() => {
  const d = new Date();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${d.getFullYear()}-${m}-${day}`;
});

const expectedEndDate = computed(() => {
  if (!uc.value?.startDate || !challenge.value?.periodDays) return null;
  const d = new Date(uc.value.startDate);
  d.setDate(d.getDate() + challenge.value.periodDays - 1);
  return d.toISOString().slice(0, 10);
});

// 신규 로그 폼
const newLog = ref({
  logDate: today.value,
  status: "SUCCESS",
  memo: "",
});

const loadLoginUser = async () => {
  try {
    const res = await api.get("/auth/checkLogin");
    loginUser.value = res.data;
  } catch (e) {
    loginUser.value = null;
  }
};

const isOwner = computed(() => {
  if (!challenge.value || !loginUser.value) return false;
  return challenge.value.userId === loginUser.value.userId;
});

const goChat = () => {
  if (!challenge.value) return;
  router.push({
    path: `/challenge/${challenge.value.challengeId}/chat`,
    query: { mode: mode.value },
  });
};

const loadDetail = async () => {
  loading.value = true;
  error.value = "";
  try {
    const res = await challengeApi.getDetail(userChallengeId, mode.value);
    uc.value = res.data.userChallenge;
    challenge.value = res.data.challenge;
    logs.value = (res.data.logs || []).map((l) => ({
      ...l,
      logDate: l.logDate?.slice(0, 10),
    }));
    if (uc.value?.startDate) {
      newLog.value.logDate = today.value < uc.value.startDate ? uc.value.startDate : today.value;
    }
  } catch (err) {
    error.value = "데이터 동기화 중 오류가 발생했습니다.";
  } finally {
    loading.value = false;
  }
};

const addLog = async () => {
  try {
    if (!newLog.value.logDate) return;
    await api.post("/challenge/log", null, {
      params: {
        userChallengeId,
        status: newLog.value.status,
        memo: newLog.value.memo,
        logDate: newLog.value.logDate,
      },
    });
    newLog.value.status = "SUCCESS";
    newLog.value.memo = "";
    await loadDetail();
  } catch (err) {
    console.error(err);
  }
};

const deleteLog = async (logId) => {
  if (!confirm("기록을 삭제할까요?")) return;
  try {
    await challengeApi.deleteLog(logId);
    await loadDetail();
  } catch (err) {
    console.error(err);
  }
};

const deleteChallenge = async () => {
  if (!confirm("챌린지를 영구 삭제하시겠습니까?")) return;
  try {
    await api.delete(`/challenge/${challenge.value.challengeId}`);
    router.push({ path: "/challenge", query: { mode: mode.value } });
  } catch (err) {
    console.error(err);
  }
};

onMounted(async () => {
  await loadLoginUser();
  await loadDetail();
});
</script>

<template>
  <div class="intro-container" :style="{ '--point-color': themeConfig.color, '--point-soft': themeConfig.soft }">
    <div class="background-decorations">
      <Transition name="fade-blob">
        <div v-if="mode === 'EAT'" class="blob blob-eat" key="eat"></div>
        <div v-else class="blob blob-medi" key="medi"></div>
      </Transition>
    </div>

    <main class="content-wrapper" v-if="!loading && challenge">
      <!-- 헤더 섹션 -->
      <header class="page-header">
        <div class="header-top">
          <button class="back-btn-pill glass-effect" @click="router.push({ path: '/challenge', query: { mode } })">
            <ArrowLeft :size="16" />
            <span>LIST</span>
          </button>

          <div class="status-chip-glass" v-if="uc">
            <span class="label font-black italic">MY STATUS</span>
            <span class="status-pill font-black" :class="uc.status.toLowerCase()">{{ uc.status }}</span>
          </div>
        </div>

        <div class="title-area-centered">
          <div class="badge-row mb-10">
            <div class="badge-premium">
              <Trophy :size="12" />
              <span class="italic">{{ challenge?.category?.toUpperCase() }} MISSION.</span>
            </div>
          </div>
          <h1 class="hero-title italic tracking-tighter">
            {{ challenge?.title?.split(' ')[0] || 'THE' }}<br />
            <span class="gradient-text">{{ challenge?.title?.split(' ').slice(1).join(' ') || 'MISSION.' }}</span>
          </h1>
          <!-- 🌟 수정: 헤더 문구 간격 확대 mt-8 -> mt-16 -->
          <p class="hero-desc mt-16">난이도 {{ challenge?.difficulty }} · {{ challenge?.periodDays }}일간의 건강한 여정</p>
        </div>
      </header>

      <!-- 통합 챌린지 마스터 카드 -->
      <div class="master-challenge-card glass-card mt-16">
        
        <!-- 01. MISSION GUIDE -->
        <section class="challenge-section">
          <div class="panel-header">
            <h3 class="panel-tag font-black italic">01. MISSION GUIDE</h3>
            <div class="panel-line"></div>
          </div>
          
          <div class="content-box mt-10">
            <p class="main-description font-black">{{ challenge.description }}</p>
          </div>

          <!-- 🌟 수정: 설명과 대시보드 사이 간격 확대 mt-14 -> mt-24 -->
          <div class="participation-dashboard mt-10 glass-effect p-12">
            <div class="stat-header">
              <div class="label-group">
                <Users :size="18" class="text-primary" />
                <span class="font-black opacity-30">PARTICIPANTS</span>
              </div>
              <div class="val-group font-black">
                <strong>{{ challenge.currentParticipants }}</strong> / {{ challenge.maxParticipants }}
              </div>
            </div>
            <div class="progress-track mt-6">
              <div class="progress-fill" :style="{ width: (challenge.currentParticipants / challenge.maxParticipants) * 100 + '%' }"></div>
            </div>
            
            <!-- 🌟 수정: 바와 날짜 사이 간격 확대 mt-12 -> mt-20 -->
            <div class="date-period-grid mt-10">
              <div class="date-item">
                <span class="label font-black">START DATE</span>
                <span class="val font-black italic">{{ uc?.startDate || '-' }}</span>
              </div>
              <div class="date-item border-left pl-10">
                <span class="label font-black">EXPECTED END</span>
                <span class="val font-black italic">{{ uc?.endDate || expectedEndDate || "-" }}</span>
              </div>
            </div>
          </div>
        </section>

        <!-- 02. SOCIAL SPACE -->
        <section class="challenge-section mt-16">
          <div class="panel-header">
            <h3 class="panel-tag font-black italic">02. SOCIAL SPACE</h3>
            <div class="panel-line"></div>
          </div>
          <button
            v-if="uc && uc.status === 'PROGRESS'"
            class="chat-banner-compact glass-effect mt-10"
            @click="goChat"
          >
            <div class="chat-icon-orb-mini bg-primary-gradient">
              <Zap :size="20" class="text-white" />
            </div>
            <div class="chat-info-text-mini">
              <span class="font-black italic title">LIVE 오픈채팅</span>
              <span class="sub">실시간 소통하기</span>
            </div>
            <ChevronRight :size="18" class="ml-auto opacity-30" />
          </button>
        </section>

        <!-- 03. PERFORMANCE HISTORY (번호 순서 유지) -->
        <section class="challenge-section mt-16">
          <div class="panel-header mb-10">
            <h3 class="panel-tag font-black italic">03. PERFORMANCE HISTORY</h3>
            <div class="panel-line"></div>
          </div>

          <div v-if="logs.length > 0" class="logs-minimal-grid custom-scroll">
            <div v-for="log in logs" :key="log.challengeLogId" class="log-pill-item glass-effect animate-in">
              <div class="log-status-icon" :class="log.status.toLowerCase()">
                <CheckCircle2 v-if="log.status === 'SUCCESS'" :size="18" />
                <XCircle v-else :size="18" />
              </div>
              <div class="log-meta-info">
                <span class="log-date-label font-black italic">{{ log.logDate }}</span>
                <span class="log-tag-badge font-black" :class="log.status.toLowerCase()">{{ log.status }}</span>
              </div>
              <button v-if="loginUser && uc && loginUser.userId === uc.userId" class="btn-log-trash-mini" @click="deleteLog(log.challengeLogId)">
                <Trash2 :size="14" />
              </button>
            </div>
          </div>

          <div v-else class="empty-log-box">
            <Inbox :size="48" class="opacity-10 mb-4" />
            <p class="font-black italic opacity-20">아직 기록된 로그가 없습니다.</p>
          </div>
        </section>

        <!-- 04. ADD LOG (맨 하단 배치) -->
        <section class="challenge-section mt-16">
          <div class="panel-header">
            <h3 class="panel-tag font-black italic">04. ADD LOG</h3>
            <div class="panel-line"></div>
          </div>
          <div class="record-form-container mt-10 p-12 glass-effect">
            <div class="form-compact-grid">
              <div class="entry-field">
                <label class="font-black italic">DATE / STATUS</label>
                <div class="dual-input-row">
                   <input type="date" v-model="newLog.logDate" class="modern-input-field" :min="uc?.startDate || ''" :max="today" />
                   <select v-model="newLog.status" class="modern-select-field">
                     <option value="SUCCESS">SUCCESS</option>
                     <option value="FAIL">FAILED</option>
                   </select>
                </div>
              </div>
              
              <!-- 🌟 수정: 메모 필드 간격 확대 mt-8 -> mt-10 -->
              <div class="entry-field mt-16">
                <label class="font-black italic">DAILY MEMO (OPTIONAL)</label>
                <div class="memo-input-box-wide">
                  <Edit :size="18" class="text-primary" />
                  <input v-model="newLog.memo" placeholder="오늘의 성취 기록을 간단히 남겨주세요." />
                </div>
              </div>

              <!-- 🌟 수정: 버튼 간격 확대 mt-12 -> mt-24 -->
              <button class="btn-log-action-premium mt-16" @click="addLog">
                <span class="font-black text-white">LOG 추가하기</span>
                <Plus :size="22" class="text-white" />
              </button>
            </div>
          </div>
        </section>
      </div>

      <!-- 챌린지 종료 버튼 -->
      <footer class="page-footer-utility mt-16">
        <button v-if="isOwner" class="auth-btn danger-confirm-btn italic font-black" @click="deleteChallenge">
          <Trash2 :size="14" />
          TERMINATE THIS CHALLENGE
        </button>
      </footer>
    </main>

    <!-- 로딩 스크린 -->
    <div v-else class="loading-full-screen">
      <Loader2 class="spinner-large" :size="40" />
      <p class="font-black italic mt-6 opacity-30">SYNCING DATA...</p>
    </div>
  </div>
</template>

<style scoped>
/* --- 🌟 인트로 레이아웃 스타일 계승 --- */
.intro-container {
  background-color: #f8f9fc;
  color: #1a1a1a;
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
  padding: 120px 24px 100px;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

/* 배경 Blobs */
.blob {
  position: fixed;
  border-radius: 50%;
  filter: blur(160px);
  z-index: 0;
  opacity: 0.2;
  pointer-events: none;
  transition: all 0.8s cubic-bezier(0.23, 1, 0.32, 1);
}
.blob-eat { 
  top: -10%; left: -10%; width: 60vw; height: 60vw; 
  background: radial-gradient(circle, rgba(163, 230, 53, 0.8) 0%, transparent 70%); 
} 
.blob-medi { 
  bottom: -15%; right: -10%; width: 55vw; height: 55vw; 
  background: radial-gradient(circle, rgba(56, 189, 248, 0.7) 0%, transparent 70%); 
}

.fade-blob-enter-active, .fade-blob-leave-active { transition: opacity 0.8s ease; }
.fade-blob-enter-from, .fade-blob-leave-to { opacity: 0; }

.content-wrapper {
  width: 100%;
  max-width: 1000px; 
  position: relative;
  z-index: 10;
}

/* 🌟 통합 챌린지 마스터 카드 디자인 */
.master-challenge-card {
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border: 1px solid rgba(255, 255, 255, 0.7);
  border-radius: 60px;
  padding: 100px 80px; 
  box-shadow: 0 40px 100px rgba(0, 0, 0, 0.02);
}

/* --- Header & Title 중앙 정렬 --- */
.page-header { margin-bottom: 40px; }
.header-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 60px; }

.back-btn-pill {
  display: flex; align-items: center; gap: 10px; padding: 12px 28px;
  border-radius: 999px; border: 1.5px solid white; cursor: pointer;
  font-weight: 850; font-size: 0.85rem; color: #94a3b8;
}
.back-btn-pill:hover { background: white; color: #1a1a1a; transform: translateX(-5px); }

.status-chip-glass { padding: 14px 28px; border-radius: 20px; display: flex; flex-direction: column; align-items: flex-end; gap: 4px; }
.status-chip-glass .label { font-size: 0.7rem; letter-spacing: 0.1em; }
.status-pill { font-size: 1.1rem; color: #10b981; }
.status-pill.progress { color: #7C4DFF; }

/* 🌟 MISSION 뱃지 및 타이틀 중앙 정렬 */
.title-area-centered { text-align: center; }
.badge-row { display: flex; justify-content: center; margin-bottom: 24px; }
.badge-premium {
  display: inline-flex; align-items: center; gap: 8px; padding: 10px 24px;
  background: rgba(255, 255, 255, 0.8); border-radius: 999px;
  font-size: 0.75rem; font-weight: 800; color: #7c4dff; border: 1px solid white;
}

.hero-title { font-size: 3.2rem; line-height: 0.95; font-weight: 950; letter-spacing: -0.05em; }
.gradient-text {
  background: linear-gradient(to right, #1a1a1a, var(--point-color));
  -webkit-background-clip: text; background-clip: text; color: transparent;
  transition: all 0.5s ease;
}
.hero-desc { font-size: 1.4rem; opacity: 0.4; font-weight: 600; text-align: center; letter-spacing: -0.01em; }

/* --- Section 스타일 --- */
.panel-header { display: flex; align-items: center; gap: 24px; margin-bottom: 40px; }
.panel-tag { font-size: 1.2rem; letter-spacing: 0.2em; color: #1a1a1a; }
.panel-line { flex: 1; height: 1.5px; background: rgba(0,0,0,0.06); }

.main-description { font-size: 1.8rem; line-height: 1.5; color: #475569; letter-spacing: -0.02em; }

/* 참여 정보 박스 */
.stat-header { display: flex; justify-content: space-between; align-items: center; }
.label-group { display: flex; align-items: center; gap: 14px; font-size: 1.1rem; }
.progress-track { height: 20px; background: rgba(0,0,0,0.05); border-radius: 999px; overflow: hidden; }
.progress-fill { height: 100%; background: linear-gradient(90deg, #7C4DFF, var(--point-color)); border-radius: 999px; transition: width 1.2s cubic-bezier(0.23, 1, 0.32, 1); }

.date-period-grid { display: grid; grid-template-columns: 1fr 1fr; }
.date-item { display: flex; flex-direction: column; gap: 8px; }
.date-item.border-left { border-left: 1.5px solid rgba(0,0,0,0.05); }
.date-item .label { font-size: 0.8rem; opacity: 0.4; letter-spacing: 0.1em; }
.date-item .val { font-size: 1.4rem; color: #1a1a1a; }

/* 오픈채팅 컴팩트 디자인 */
.chat-banner-compact {
  width: fit-content; min-width: 320px; display: flex; align-items: center; gap: 20px; padding: 20px 32px;
  background: white; border: 1.5px solid #f1f5f9; border-radius: 28px;
  cursor: pointer; transition: 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}
.chat-banner-compact:hover { transform: translateY(-5px); border-color: #7C4DFF; box-shadow: 0 15px 35px rgba(124, 77, 255, 0.1); }
.chat-icon-orb-mini { 
  width: 52px; height: 52px; border-radius: 18px; display: flex; align-items: center; justify-content: center; 
  box-shadow: 0 8px 16px rgba(124, 77, 255, 0.2);
}
.bg-primary-gradient { background: linear-gradient(135deg, #7C4DFF, #38bdf8); }
.chat-info-text-mini { display: flex; flex-direction: column; text-align: left; }
.chat-info-text-mini .title { font-size: 1.2rem; line-height: 1; margin-bottom: 6px; color: #1a1a1a; }
.chat-info-text-mini .sub { font-size: 0.85rem; font-weight: 800; opacity: 0.4; letter-spacing: -0.01em; }

/* ADD LOG 폼 디자인 */
.record-form-container { border-radius: 40px; }
.entry-field label { display: block; font-size: 0.85rem; letter-spacing: 0.15em; color: #1a1a1a; margin-bottom: 12px; margin-left: 12px; }
.dual-input-row { display: flex; gap: 16px; }
.modern-input-field, .modern-select-field { flex: 1; padding: 20px 28px; border-radius: 20px; border: 1.5px solid #f1f5f9; background: white; outline: none; font-family: inherit; font-weight: 950; font-size: 1.1rem; color: #1a1a1a; transition: 0.3s; }
.modern-input-field:focus, .modern-select-field:focus { border-color: var(--point-color); transform: translateY(-2px); }

.memo-input-box-wide { display: flex; align-items: center; gap: 18px; padding: 20px 28px; border-radius: 20px; border: 1.5px solid #f1f5f9; background: white; transition: 0.3s; }
.memo-input-box-wide:focus-within { border-color: var(--point-color); transform: translateY(-2px); }
.memo-input-box-wide input { flex: 1; background: none; border: none; outline: none; font-weight: 950; font-size: 1.1rem; color: #1a1a1a; }

.btn-log-action-premium { 
  width: 100%; display: flex; align-items: center; justify-content: center; gap: 16px; padding: 24px; border-radius: 999px; border: none; cursor: pointer; transition: 0.4s;
  background: #1a1a1a; box-shadow: 0 20px 40px rgba(0,0,0,0.15);
}
.btn-log-action-premium:hover { background: var(--point-color); transform: translateY(-5px); }
.btn-log-action-premium:hover .text-white { color: #1a1a1a !important; }

/* 히스토리 타임라인 (성공여부 중심) */
.logs-minimal-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px; max-height: 500px; overflow-y: auto; padding: 10px; }
.log-pill-item { display: flex; align-items: center; gap: 20px; padding: 22px 28px; transition: 0.4s cubic-bezier(0.23, 1, 0.32, 1); border-radius: 28px; }
.log-pill-item:hover { background: rgba(255,255,255,0.8); transform: translateY(-3px); }

.log-status-icon.success { color: #10b981; }
.log-status-icon.fail { color: #f43f5e; }

.log-meta-info { flex: 1; display: flex; justify-content: space-between; align-items: center; }
.log-date-label { font-size: 1.1rem; color: #1a1a1a; }
.log-tag-badge { font-size: 0.65rem; padding: 4px 12px; border-radius: 8px; color: white; text-transform: uppercase; }
.log-tag-badge.success { background: #10b981; }
.log-tag-badge.fail { background: #f43f5e; }

.btn-log-trash-mini { width: 36px; height: 36px; border-radius: 10px; border: none; background: #fef2f2; color: #f43f5e; cursor: pointer; opacity: 0; transition: 0.2s; }
.log-pill-item:hover .btn-log-trash-mini { opacity: 1; }

/* 종료 버튼 스타일 (Intro 가이드) */
.danger-confirm-btn { 
  background: #1a1a1a; color: white; border: none; border-radius: 20px; 
  padding: 22px 50px; font-size: 1rem; cursor: pointer; transition: 0.3s;
  display: flex; align-items: center; gap: 12px; margin: 0 auto;
}
.danger-confirm-btn:hover { background: #ef4444; transform: translateY(-5px); box-shadow: 0 15px 30px rgba(239, 68, 68, 0.3); }

/* Utilities */
.font-black { font-weight: 950; }
.italic { font-style: italic; }
.tracking-tighter { letter-spacing: -0.06em; }
.text-primary { color: var(--point-color); transition: color 0.5s; }
.text-white { color: #FFFFFF !important; }

/* 간격 클래스 고도화 */
.mt-10 { margin-top: 2.5rem; }
.mt-16 { margin-top: 4rem; }
.mt-20 { margin-top: 5rem; }
.mt-24 { margin-top: 6rem; }
.mt-28 { margin-top: 7rem; }
.mt-32 { margin-top: 10rem; }
.mb-10 { margin-bottom: 2.5rem; }

.animate-in { animation: slideUp 0.8s cubic-bezier(0.23, 1, 0.32, 1) forwards; opacity: 0; }
@keyframes slideUp { from { opacity: 0; transform: translateY(50px); } to { opacity: 1; transform: translateY(0); } }

.custom-scroll::-webkit-scrollbar { width: 6px; }
.custom-scroll::-webkit-scrollbar-thumb { background: rgba(0,0,0,0.08); border-radius: 10px; }

@media (max-width: 900px) {
  .master-challenge-card { padding: 40px 24px; border-radius: 40px; }
  .hero-title { font-size: 2.8rem; }
}
</style>