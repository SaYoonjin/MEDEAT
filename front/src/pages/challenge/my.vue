<script setup>
import { ref, onMounted, computed, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import challengeApi from "@/api/challenge";
import { 
  Sparkles, 
  Trophy, 
  Target, 
  ArrowRight, 
  Loader2, 
  Activity, 
  ChevronRight,
  Flame,
  CheckCircle2,
  XCircle,
  Inbox,
  Rocket
} from "lucide-vue-next";

const router = useRouter();
const route = useRoute();

const challenges = ref([]);
const isLoading = ref(true);

const currentMode = computed(() => route.query.mode || "EAT");

/* =========================
   🌟 테마 설정 (모드별 배경 및 포인트 컬러)
========================= */
const theme = computed(() => {
  return currentMode.value === "MEDI_EAT" 
    ? { 
        primary: "#38bdf8",    // 하늘색
        soft: "rgba(56, 189, 248, 0.15)",
        text: "MEDI SCAN", 
        emoji: "💊" 
      }
    : { 
        primary: "#a3e635",    // 연두색
        soft: "rgba(163, 230, 53, 0.15)",
        text: "EAT", 
        emoji: "🍽️" 
      };
});

const calcDday = (startDate, periodDays) => {
  if (!startDate || !periodDays) return null;
  const start = new Date(startDate);
  start.setHours(0, 0, 0, 0);
  const end = new Date(start);
  end.setDate(end.getDate() + periodDays - 1);
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const diff = Math.ceil((end.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
  return diff >= 0 ? `D-${diff}` : "종료";
};

const fetchMyChallenges = async () => {
  try {
    isLoading.value = true;
    const res = await challengeApi.getList(currentMode.value);
    const list = res.data?.ongoing || [];

    challenges.value = list.map((uc) => ({
      id: uc.userChallengeId,
      title: uc.title ?? "제목 없음",
      successRate: uc.successRate ?? 0,
      successCount: uc.successCount ?? 0,
      failCount: uc.failCount ?? 0,
      dday: calcDday(uc.startDate, uc.periodDays),
    }));
  } catch (e) {
    console.error(`${theme.value.text} 로드 실패:`, e);
  } finally {
    isLoading.value = false;
  }
};

watch(() => route.query.mode, fetchMyChallenges);

const goDetail = (id) => {
  router.push({ path: `/challenge/${id}`, query: { mode: currentMode.value } });
};

onMounted(fetchMyChallenges);
</script>

<template>
  <div class="intro-container" :style="{ '--point-color': theme.primary, '--point-soft': theme.soft }">
    <!-- 🌟 배경: 모드별 색상 및 위치 자동 전환 -->
    <div class="background-decorations">
      <Transition name="fade-blob" mode="out-in">
        <div v-if="currentMode === 'EAT'" class="blob blob-eat" key="eat"></div>
        <div v-else class="blob blob-medi" key="medi"></div>
      </Transition>
    </div>

    <main class="content-wrapper">
      <!-- 🌟 헤더: 중앙 정렬 & 시네마틱 타이포그래피 -->
      <header class="page-header">
        <div class="badge-centered">
          <div class="badge glass-effect">
            <Trophy :size="12" class="text-primary" />
            <span class="font-black italic text-primary">{{ theme.text }} VERSION</span>
          </div>
        </div>
        <h1 class="hero-title font-black italic tracking-tighter centered mt-6">
          MY {{ currentMode === 'EAT' ? 'EAT' : 'MEDI' }}<br />
          <span class="gradient-text">CHALLENGES.</span>
        </h1>
        <p v-if="challenges.length > 0" class="hero-subtext centered mt-4">
          현재 <strong class="text-primary">{{ challenges.length }}개</strong>의 도전을 완주하고 있어요!
        </p>
      </header>

      <!-- 로딩 상태 -->
      <div v-if="isLoading" class="state-box-full">
        <Loader2 class="spinner" :size="48" />
        <p class="font-black italic mt-6 opacity-30">SYNCING MISSIONS...</p>
      </div>

      <!-- 빈 상태 -->
      <div v-else-if="challenges.length === 0" class="empty-state-container animate-in">
        <div class="empty-card glass-card">
          <div class="empty-icon-box bg-primary-gradient">
            <Sparkles :size="48" class="text-white" />
          </div>
          <h3 class="font-black italic mt-10">진행 중인 챌린지가 없습니다.</h3>
          <p class="mt-4 opacity-50">새로운 목표를 설정하고 건강한 변화를 시작해보세요!</p>
          <button class="main-start-btn mt-10" @click="router.push('/challenge/create')">
            <span>EXPLORE CHALLENGES</span>
            <Rocket :size="20" />
          </button>
        </div>
      </div>

      <!-- 챌린지 그리드 -->
      <div v-else class="challenge-grid">
        <article
          v-for="(c, index) in challenges"
          :key="c.id"
          class="challenge-premium-card glass-card animate-in"
          :style="{ animationDelay: `${index * 0.1}s` }"
          @click="goDetail(c.id)"
        >
          <div class="card-glow-inner"></div>
          
          <div class="card-top-row">
            <span class="d-day-pill font-black italic" :class="{ 'is-urgent': c.dday === 'D-1' || c.dday === 'D-0' }">
              {{ c.dday }}
            </span>
            <div class="card-icon-mini text-primary"><Activity :size="20" /></div>
          </div>

          <h3 class="challenge-title font-black italic mt-8">{{ c.title }}</h3>

          <!-- 프로그레스 섹션 -->
          <div class="progress-section mt-10">
            <div class="progress-header">
              <span class="label font-black opacity-30">SUCCESS RATE</span>
              <span class="value font-black text-primary">{{ c.successRate }}%</span>
            </div>
            <div class="progress-track-premium mt-3">
              <div
                class="progress-fill-gradient"
                :style="{ width: c.successRate + '%' }"
              ></div>
            </div>
          </div>

          <!-- 하단 스탯 -->
          <div class="stats-grid mt-10">
            <div class="stat-unit">
              <div class="dot bg-success"></div>
              <span class="stat-label font-black">성공</span>
              <strong class="stat-val font-black">{{ c.successCount }}</strong>
            </div>
            <div class="stat-unit">
              <div class="dot bg-fail"></div>
              <span class="stat-label font-black">실패</span>
              <strong class="stat-val font-black">{{ c.failCount }}</strong>
            </div>
          </div>

          <footer class="card-action-footer mt-10">
            <span class="action-text font-black italic text-primary">기록 확인하기</span>
            <ChevronRight :size="18" class="text-primary arrow-anim" />
          </footer>
        </article>
      </div>
    </main>
  </div>
</template>

<style scoped>
/* --- 🌟 인트로 배경 및 파노라마 레이아웃 --- */
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

.background-decorations {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%; z-index: 0; pointer-events: none;
}

/* 블롭 위치 및 색상 전환 (디자인 시스템 동기화) */
.blob {
  position: fixed; border-radius: 50%; filter: blur(160px); z-index: 0; opacity: 0.25; pointer-events: none;
  transition: all 1s cubic-bezier(0.23, 1, 0.32, 1);
}
.blob-eat { top: -10%; left: -10%; width: 65vw; height: 65vw; background: radial-gradient(circle, rgba(163, 230, 53, 0.8) 0%, transparent 70%); }
.blob-medi { bottom: -15%; right: -10%; width: 65vw; height: 65vw; background: radial-gradient(circle, rgba(56, 189, 248, 0.7) 0%, transparent 70%); }

.fade-blob-enter-active, .fade-blob-leave-active { transition: opacity 1s ease; }
.fade-blob-enter-from, .fade-blob-leave-to { opacity: 0; }

.content-wrapper { position: relative; z-index: 10; max-width: 1100px; margin: 0 auto; width: 100%; }

/* --- 🌟 Glassmorphism 카드 --- */
.glass-card {
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 40px;
  box-shadow: 0 40px 120px rgba(0, 0, 0, 0.03);
}

/* --- 🌟 헤더 --- */
.page-header { margin-bottom: 80px; text-align: center; }
.badge-centered { display: flex; justify-content: center; }
.badge {
  display: inline-flex; align-items: center; gap: 8px; padding: 10px 24px;
  background: white; border-radius: 999px;
  font-size: 0.75rem; font-weight: 800; border: 1.5px solid #f1f5f9;
}

.hero-title { font-size: 3.2rem; line-height: 0.95; font-weight: 950; letter-spacing: -0.05em; }
.centered { text-align: center; }
.gradient-text {
  background: linear-gradient(to right, #1A1A1A, var(--point-color));
  -webkit-background-clip: text; background-clip: text; color: transparent;
  transition: all 0.5s ease;
}
.hero-subtext { font-size: 1.2rem; opacity: 0.5; font-weight: 600; letter-spacing: -0.01em; }

/* --- 🌟 챌린지 그리드 & 카드 --- */
.challenge-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 32px;
}

.challenge-premium-card {
  padding: 48px 40px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.challenge-premium-card:hover {
  transform: translateY(-12px);
  background: white;
  border-color: var(--point-color);
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.08);
}

.card-glow-inner {
  position: absolute; top: -10%; right: -10%; width: 150px; height: 150px;
  background: radial-gradient(circle, var(--point-soft) 0%, transparent 70%);
  pointer-events: none;
}

.card-top-row { display: flex; justify-content: space-between; align-items: center; }
.d-day-pill {
  padding: 8px 18px; border-radius: 12px; font-size: 0.85rem;
  background: #f1f5f9; color: #1a1a1a;
}
.d-day-pill.is-urgent { background: #fff1f2; color: #f43f5e; box-shadow: 0 4px 12px rgba(244, 63, 94, 0.1); }

.challenge-title { font-size: 1.5rem; line-height: 1.2; color: #1a1a1a; height: 3.6rem; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }

/* 프로그레스 */
.progress-header { display: flex; justify-content: space-between; align-items: flex-end; }
.progress-header .label { font-size: 0.7rem; letter-spacing: 0.1em; }
.progress-header .value { font-size: 1.4rem; }

.progress-track-premium { height: 14px; background: rgba(0,0,0,0.04); border-radius: 20px; overflow: hidden; }
.progress-fill-gradient { 
  height: 100%; 
  background: linear-gradient(90deg, #1a1a1a, var(--point-color)); 
  border-radius: 20px;
  transition: width 1.5s cubic-bezier(0.23, 1, 0.32, 1);
}

/* 스탯 */
.stats-grid { display: flex; gap: 24px; padding-top: 24px; border-top: 1px solid rgba(0,0,0,0.04); }
.stat-unit { display: flex; align-items: center; gap: 10px; font-size: 0.95rem; }
.dot { width: 8px; height: 8px; border-radius: 50%; }
.bg-success { background: #10b981; }
.bg-fail { background: #f43f5e; }
.stat-label { opacity: 0.4; }
.stat-val { color: #1a1a1a; }

.card-action-footer { display: flex; align-items: center; gap: 8px; opacity: 0; transform: translateX(-10px); transition: 0.3s; }
.challenge-premium-card:hover .card-action-footer { opacity: 1; transform: translateX(0); }
.card-action-footer span { font-size: 0.85rem; }

/* --- 🌟 빈 상태 --- */
.empty-state-container { text-align: center; padding: 40px 0; }
.empty-card { padding: 80px 60px; display: flex; flex-direction: column; align-items: center; }
.empty-icon-box { width: 100px; height: 100px; border-radius: 35px; display: flex; align-items: center; justify-content: center; box-shadow: 0 20px 40px var(--point-soft); }
.bg-primary-gradient { background: linear-gradient(135deg, #1a1a1a, var(--point-color)); }

.main-start-btn {
  background: #1a1a1a; color: #FFF; border: none; padding: 22px 48px; border-radius: 999px;
  font-size: 1.1rem; font-weight: 950; cursor: pointer; transition: 0.4s;
  display: flex; align-items: center; gap: 16px;
}
.main-start-btn:hover { background: var(--point-color); color: #1a1a1a; transform: translateY(-5px); box-shadow: 0 20px 40px var(--point-soft); }

/* --- 🌟 기타 상태 --- */
.state-box-full { text-align: center; padding: 120px 0; }
.spinner { animation: spin 1.5s linear infinite; color: var(--point-color); margin: 0 auto; }
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }

/* Utilities */
.font-black { font-weight: 950; }
.italic { font-style: italic; }
.tracking-tighter { letter-spacing: -0.06em; }
.text-primary { color: var(--point-color); }
.mt-6 { margin-top: 1.5rem; }
.mt-8 { margin-top: 2rem; }
.mt-10 { margin-top: 2.5rem; }
.mt-12 { margin-top: 3rem; }

.animate-in { animation: slideUp 0.8s cubic-bezier(0.23, 1, 0.32, 1) forwards; opacity: 0; }
@keyframes slideUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }

@media (max-width: 768px) {
  .hero-title { font-size: 2.5rem; }
  .challenge-grid { grid-template-columns: 1fr; }
  .empty-card { padding: 60px 24px; }
}
</style>