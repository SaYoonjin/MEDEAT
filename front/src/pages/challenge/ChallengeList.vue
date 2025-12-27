<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import api from "@/api/axios.js";
import { 
  Sparkles, 
  Trophy, 
  Activity, 
  Plus, 
  ChevronRight, 
  Flame, 
  Target, 
  Users, 
  ArrowRight, 
  Loader2, 
  Search,
  X,
  Zap,
  LayoutGrid,
  Clock,
  Inbox,
  Heart,
  Calendar,
  TrendingUp
} from "lucide-vue-next";

const router = useRouter();
const route = useRoute();

/* =========================
   상태 관리
========================= */
const mode = ref(route.query.mode === "MEDI_EAT" ? "MEDI_EAT" : "EAT");
const popular = ref([]);
const ongoing = ref([]);
const available = ref([]);
const loading = ref(true);
const errorMsg = ref("");

// 검색 및 필터 상태
const searchKeyword = ref("");
const isSearchFocused = ref(false);

/* =========================
   🌟 테마 설정 (모드별 배경 및 포인트 컬러)
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

// 챌린지 목록 가져오기
const loadChallenges = async () => {
  loading.value = true;
  errorMsg.value = "";
  try {
    const res = await api.get("/challenge", { params: { mode: mode.value } });
    popular.value = res.data.popular || [];
    ongoing.value = res.data.ongoing || [];
    available.value = res.data.available || [];
  } catch (e) {
    console.error("챌린지 로드 실패:", e);
    errorMsg.value = e.response?.data?.message || "챌린지를 불러오는 중 오류가 발생했습니다.";
  } finally {
    loading.value = false;
  }
};

watch(
  () => route.query.mode,
  (newMode) => {
    const nextMode = newMode === "MEDI_EAT" ? "MEDI_EAT" : "EAT";
    if (nextMode !== mode.value) {
      mode.value = nextMode;
      loadChallenges();
    }
  }
);

// 특정 챌린지에 이미 참여 중인지 확인하고 userChallengeId 반환
const getOngoingUserChallengeId = (challengeId) => {
  const found = ongoing.value.find(uc => uc.challengeId === challengeId);
  return found ? found.userChallengeId : null;
};

// 인기 챌린지 클릭 핸들러 (참여 중이면 상세로, 아니면 참여하기)
const handlePopularClick = (c) => {
  const ucId = getOngoingUserChallengeId(c.challengeId);
  if (ucId) {
    goDetail(ucId);
  } else {
    joinChallenge(c.challengeId);
  }
};

// 검색 필터링 로직
const filteredOngoing = computed(() => {
  if (!searchKeyword.value.trim()) return ongoing.value;
  return ongoing.value.filter(c => 
    c.title.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
    c.category?.toLowerCase().includes(searchKeyword.value.toLowerCase())
  );
});

const filteredAvailable = computed(() => {
  if (!searchKeyword.value.trim()) return available.value;
  return available.value.filter(c => 
    c.title.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
    c.category?.toLowerCase().includes(searchKeyword.value.toLowerCase())
  );
});

const totalCount = computed(() => {
  const ids = new Set();
  popular.value.forEach((c) => c?.challengeId != null && ids.add(c.challengeId));
  ongoing.value.forEach((uc) => uc?.challengeId != null && ids.add(uc.challengeId));
  available.value.forEach((c) => c?.challengeId != null && ids.add(c.challengeId));
  return ids.size;
});

// 참여하기
const joinChallenge = async (challengeId) => {
  if (!confirm("이 챌린지에 참여할까요?")) return;
  try {
    const res = await api.post("/challenge/join", null, {
      params: { challengeId },
    });
    alert(res.data?.message || "참여 완료!");
    await loadChallenges();
  } catch (e) {
    const msg = e.response?.data?.message || e.response?.data;
    alert(msg || "챌린지 참여 중 오류가 발생했습니다.");
  }
};

// 포기하기
const giveUp = async (userChallengeId) => {
  if (!confirm("정말 포기하시겠습니까?")) return;
  try {
    const res = await api.post("/challenge/giveup", null, {
      params: { userChallengeId },
    });
    alert(res.data?.message || "챌린지를 포기했습니다.");
    await loadChallenges();
  } catch (e) {
    alert(e.response?.data?.message || "챌린지 포기 중 오류가 발생했습니다.");
  }
};

const goDetail = (userChallengeId) => {
  router.push({
    name: "challenge-detail",
    params: { id: userChallengeId },
    query: { mode: mode.value },
  });
};

const goForm = () => {
  router.push({ path: '/challenge/form', query: { mode: mode.value } });
};

onMounted(loadChallenges);
</script>

<template>
  <div class="page-container" :style="{ '--point-color': themeConfig.color }">
    <div class="background-decorations">
      <Transition name="fade-blob" mode="out-in">
        <!-- 🌟 모드별 블롭 위치 분리 (EAT: 좌상단 / MEDI_EAT: 우하단) -->
        <div v-if="mode === 'EAT'" class="blob blob-eat" key="eat"></div>
        <div v-else class="blob blob-medi" key="medi"></div>
      </Transition>
    </div>

    <main class="content-wrapper">
      <!-- 🌟 헤더: 중앙 정렬 & 시네마틱 타이포그래피 -->
      <header class="page-header">
        <div class="badge-row">
          <div class="badge glass-effect">
            <Trophy :size="12" class="text-primary" />
            <span class="font-black italic text-primary">CHALLENGE HUB.</span>
          </div>
        </div>
        <h2 class="page-title font-black italic tracking-tighter">
          CHALLENGE<br />
          <!-- 🌟 포인트 컬러 동적 적용되는 텍스트 -->
          <span class="gradient-text">DASHBOARD.</span>
        </h2>
        <p class="page-desc">건강한 습관을 만들어가는 당신의 전용 대시보드</p>
      </header>

      <!-- 🌟 상단 요약 HUD -->
      <section class="summary-hud mb-10">
        <div class="summary-card glass-effect">
          <div class="summary-icon bg-black"><Clock :size="18" class="text-white" /></div>
          <div class="summary-info">
            <span class="label font-black opacity-30">ONGOING</span>
            <span class="value font-black">{{ ongoing.length }}</span>
          </div>
        </div>
        <div class="summary-card glass-effect">
          <div class="summary-icon bg-black"><Zap :size="18" class="text-white" /></div>
          <div class="summary-info">
            <span class="label font-black opacity-30">AVAILABLE</span>
            <span class="value font-black">{{ available.length }}</span>
          </div>
        </div>
        <div class="summary-card glass-effect">
          <div class="summary-icon bg-black"><LayoutGrid :size="18" class="text-white" /></div>
          <div class="summary-info">
            <span class="label font-black opacity-30">TOTAL</span>
            <span class="value font-black">{{ totalCount }}</span>
          </div>
        </div>
      </section>

      <!-- 🌟 인기 챌린지 (하이라이트 카드 스타일) -->
      <section v-if="popular.length > 0 && !searchKeyword" class="highlight-section mb-12">
        <div class="section-header-mini mb-6">
           <h3 class="font-black italic opacity-20">🔥 HOT TRENDING</h3>
        </div>
        <article 
          class="highlight-card glass-effect" 
          @click="handlePopularClick(popular[0])"
        >
          <div class="highlight-content">
            <div class="tag-row">
              <span class="trending-badge font-black italic">TOP CHOICE</span>
              <div class="category-tag font-black italic text-primary">
                {{ popular[0].category.toUpperCase() }}
              </div>
            </div>
            <h3 class="hot-title font-black italic tracking-tighter">{{ popular[0].title }}</h3>
            <div class="hot-footer">
              <div class="stat-group">
                <span class="stat-pill"><Users :size="14" /> <strong>{{ popular[0].currentParticipants }}</strong></span>
                <span class="stat-pill"><Calendar :size="14" /> <strong>{{ popular[0].periodDays }}D</strong></span>
              </div>
              <div class="action-hint font-black italic">
                EXPLORE NOW <ArrowRight :size="16" />
              </div>
            </div>
          </div>
        </article>
      </section>

      <!-- 🌟 컨트롤러: 검색 및 추가 버튼 -->
      <div class="control-row mb-10">
        <div class="search-box glass-effect" :class="{ focused: isSearchFocused }">
          <Search :size="18" class="text-primary" />
          <input
            v-model="searchKeyword"
            @focus="isSearchFocused = true"
            @blur="isSearchFocused = false"
            type="text"
            placeholder="Search missions..."
          />
          <button v-if="searchKeyword" class="clear-btn" @click="searchKeyword = ''">
            <X :size="14" />
          </button>
        </div>

        <!-- 🌟 챌린지 추가 버튼 (커뮤니티 스타일) -->
        <button class="write-btn-circle" @click="goForm" title="챌린지 생성">
          <Plus :size="24" />
        </button>
      </div>

      <div v-if="loading" class="state-box">
        <Loader2 class="spinner" :size="32" />
        <p class="font-black italic opacity-30">SYNCING FEED...</p>
      </div>

      <div v-else-if="errorMsg" class="error-banner">{{ errorMsg }}</div>

      <template v-else>
        <!-- 🌟 메인 리스트 그리드 (진행 중) -->
        <section class="challenge-grid-section mb-14">
          <div class="section-header-row mb-6">
            <h3 class="section-title font-black italic">⏳ ACTIVE MISSIONS</h3>
            <span class="count-pill font-black">{{ filteredOngoing.length }}</span>
          </div>

          <div v-if="filteredOngoing.length === 0" class="empty-state-box glass-effect">
            <Inbox :size="40" class="opacity-10" />
            <p class="font-black italic opacity-20">진행 중인 챌린지가 없습니다.</p>
          </div>

          <div v-else class="posts-grid">
            <article
              v-for="uc in filteredOngoing"
              :key="uc.userChallengeId"
              class="post-card glass-effect animate-in"
              @click="goDetail(uc.userChallengeId)"
            >
              <div class="card-main">
                <div class="card-meta">
                  <div class="author-tag font-black italic text-primary">
                    <Activity :size="12" /> SYNCING
                  </div>
                  <div class="date-text font-black opacity-30">{{ uc.category }}</div>
                </div>
                <h3 class="post-title font-black">{{ uc.title }}</h3>
                <div class="card-footer">
                  <div class="stat-pill"><Clock :size="12" /> {{ uc.periodDays }}일</div>
                  <div class="stat-pill"><Target :size="12" /> LV.{{ uc.difficulty }}</div>
                </div>
              </div>
              <div class="card-actions-row">
                <button class="circle-btn-mini delete" @click.stop="giveUp(uc.userChallengeId)" title="포기">
                  <X :size="16" />
                </button>
                <div class="view-btn-mini">
                  <ChevronRight :size="20" />
                </div>
              </div>
            </article>
          </div>
        </section>

        <!-- 🌟 메인 리스트 그리드 (참여 가능) -->
        <section class="challenge-grid-section">
          <div class="section-header-row mb-6">
            <h3 class="section-title font-black italic">✅ NEW OPPORTUNITIES</h3>
            <span class="count-pill font-black">{{ filteredAvailable.length }}</span>
          </div>

          <div v-if="filteredAvailable.length === 0" class="empty-state-box glass-effect">
            <Zap :size="40" class="opacity-10" />
            <p class="font-black italic opacity-20">새로운 챌린지가 없습니다.</p>
          </div>

          <div v-else class="posts-grid">
            <article
              v-for="c in filteredAvailable"
              :key="c.challengeId"
              class="post-card glass-effect animate-in"
              @click="joinChallenge(c.challengeId)"
            >
              <div class="card-main">
                <div class="card-meta">
                  <div class="author-tag font-black italic text-primary">
                    <Plus :size="12" /> AVAILABLE
                  </div>
                  <div class="date-text font-black opacity-30">{{ c.category }}</div>
                </div>
                <h3 class="post-title font-black">{{ c.title }}</h3>
                <div class="card-footer">
                  <div class="stat-pill"><Users :size="12" /> {{ c.currentParticipants }}/{{ c.maxParticipants }}</div>
                  <div class="stat-pill"><Clock :size="12" /> {{ c.periodDays }}일</div>
                </div>
              </div>
              <div class="card-actions-row">
                <div class="view-btn-mini join">
                  <Plus :size="20" />
                </div>
              </div>
            </article>
          </div>
        </section>
      </template>
    </main>
  </div>
</template>

<style scoped>
/* --- 🌟 인트로 배경 및 레이아웃 --- */
.page-container {
  background-color: #f8f9fc;
  color: #1a1a1a;
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
  padding: 100px 24px 80px;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.background-decorations {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%; z-index: 0; pointer-events: none;
}

/* MyScrap과 동일 수치 */
.blob { position: fixed; border-radius: 50%; filter: blur(160px); z-index: 0; opacity: 0.25; pointer-events: none; transition: all 0.8s ease; }

/* 🌟 모드별 위치 분리 */
.blob-eat { top: -10%; left: -10%; width: 55vw; height: 55vw; background: radial-gradient(circle, rgba(163, 230, 53, 0.8) 0%, transparent 70%); } 
.blob-medi { bottom: -15%; right: -10%; width: 50vw; height: 50vw; background: radial-gradient(circle, rgba(56, 189, 248, 0.7) 0%, transparent 70%); }

.fade-blob-enter-active, .fade-blob-leave-active { transition: opacity 0.8s ease; }
.fade-blob-enter-from, .fade-blob-leave-to { opacity: 0; }

.content-wrapper { position: relative; z-index: 10; max-width: 1200px; margin: 0 auto; width: 100%; }

/* --- Glassmorphism --- */
.glass-effect {
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 32px;
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.07);
}

/* --- Header --- */
.page-header { text-align: center; margin-bottom: 60px; }
.badge-row { display: flex; justify-content: center; margin-bottom: 24px; }
.badge { display: inline-flex; align-items: center; gap: 8px; padding: 8px 16px; font-size: 0.65rem; border-radius: 999px; }

.page-title { font-size: 3.5rem; line-height: 1; margin-bottom: 16px; }

/* 🌟 DASHBOARD. 텍스트 컬러 실시간 추적 */
.gradient-text {
  background: linear-gradient(to right, #1A1A1A, var(--point-color));
  -webkit-background-clip: text; background-clip: text; color: transparent;
  transition: all 0.5s ease;
}
.page-desc { font-size: 1.1rem; opacity: 0.4; font-weight: 600; letter-spacing: -0.01em; }

/* --- Summary HUD --- */
.summary-hud { display: flex; gap: 16px; justify-content: center; }
.summary-card { flex: 1; display: flex; align-items: center; gap: 20px; padding: 20px 32px; max-width: 320px; }
.summary-icon { width: 44px; height: 44px; border-radius: 12px; display: flex; align-items: center; justify-content: center; }
.summary-info { display: flex; flex-direction: column; }
.summary-info .label { font-size: 0.65rem; letter-spacing: 0.1em; margin-bottom: 4px; }
.summary-info .value { font-size: 1.8rem; line-height: 1; }

/* --- 하이라이트 --- */
.highlight-card { padding: 40px 50px; cursor: pointer; transition: all 0.4s ease; position: relative; overflow: hidden; }
.highlight-card:hover { transform: translateY(-5px); background: rgba(255,255,255,0.6); }
.tag-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.trending-badge { background: #FF8A65; color: white; padding: 5px 14px; border-radius: 999px; font-size: 0.75rem; }
.hot-title { font-size: 2.2rem; line-height: 1.1; margin-bottom: 32px; color: #1a1a1a; }
.hot-footer { display: flex; justify-content: space-between; align-items: center; }
.stat-group { display: flex; gap: 12px; }
.stat-pill { padding: 6px 14px; background: rgba(0,0,0,0.03); border-radius: 999px; font-size: 0.8rem; display: flex; align-items: center; gap: 8px; color: #64748b; }
.action-hint { font-size: 0.9rem; color: #1A1A1A; display: flex; align-items: center; gap: 8px; }

/* --- 컨트롤러 --- */
.control-row { display: flex; gap: 16px; align-items: center; justify-content: center; }
.search-box { flex: 1; max-width: 600px; display: flex; align-items: center; gap: 12px; padding: 0 24px; border-radius: 999px; height: 56px; }
.search-box input { flex: 1; background: none; border: none; outline: none; font-size: 1rem; font-weight: 700; color: #1A1A1A; }
.search-box.focused { background: white; border-color: #7C4DFF; box-shadow: 0 10px 25px rgba(124, 77, 255, 0.1); }
.clear-btn { width: 28px; height: 28px; border-radius: 50%; border: none; background: rgba(0, 0, 0, 0.05); color: #94a3b8; display: flex; align-items: center; justify-content: center; cursor: pointer; }

.write-btn-circle { width: 56px; height: 56px; border-radius: 50%; border: none; background: #1A1A1A; color: white; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: 0.3s; box-shadow: 0 10px 20px rgba(0,0,0,0.1); }
.write-btn-circle:hover { background: #7C4DFF; transform: scale(1.1) rotate(90deg); }

/* --- 리스트 그리드 --- */
.section-header-row { display: flex; justify-content: space-between; align-items: center; padding: 0 10px; }
.section-title { font-size: 1.1rem; opacity: 0.3; letter-spacing: 0.1em; }
.count-pill { background: #1a1a1a; color: white; padding: 4px 12px; border-radius: 8px; font-size: 0.75rem; }

.posts-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(380px, 1fr)); gap: 20px; }
.post-card { padding: 28px; display: flex; justify-content: space-between; align-items: center; transition: 0.3s cubic-bezier(0.23, 1, 0.32, 1); cursor: pointer; }
.post-card:hover { transform: translateY(-5px); background: rgba(255, 255, 255, 0.6); border-color: #7C4DFF; }

.card-main { flex: 1; overflow: hidden; }
.card-meta { display: flex; align-items: center; gap: 16px; margin-bottom: 10px; }
.author-tag { display: flex; align-items: center; gap: 6px; font-size: 0.7rem; color: #7C4DFF; }
.date-text { font-size: 0.7rem; }
.post-title { font-size: 1.15rem; line-height: 1.4; margin-bottom: 16px; color: #1A1A1A; }

.card-footer { display: flex; gap: 10px; }
.stat-pill { padding: 4px 10px; background: rgba(0,0,0,0.03); border-radius: 999px; font-size: 0.65rem; font-weight: 900; display: flex; align-items: center; gap: 6px; color: #64748b; }

.card-actions-row { display: flex; align-items: center; gap: 12px; margin-left: 20px; }
.circle-btn-mini { width: 36px; height: 36px; border-radius: 50%; border: none; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: 0.2s; }
.circle-btn-mini.delete { background: #f1f5f9; color: #94a3b8; }
.circle-btn-mini.delete:hover { background: #fee2e2; color: #f43f5e; }

.view-btn-mini { width: 40px; height: 40px; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #cbd5e1; transition: 0.3s; }
.view-btn-mini.join { background: #1a1a1a; color: white; }
.post-card:hover .view-btn-mini { color: #7C4DFF; transform: translateX(5px); }
.post-card:hover .view-btn-mini.join { background: #7C4DFF; color: white; transform: scale(1.1); }

/* --- 상태 박스 --- */
.state-box { padding: 80px 40px; text-align: center; }
.empty-state-box { padding: 60px 40px; text-align: center; display: flex; flex-direction: column; align-items: center; gap: 16px; border-radius: 32px; }
.spinner { animation: rotate 1.5s linear infinite; color: #7C4DFF; }
.error-banner { background: #fff1f2; color: #e11d48; padding: 20px; border-radius: 20px; text-align: center; font-weight: 800; }

/* --- Utilities --- */
.font-black { font-weight: 950; }
.italic { font-style: italic; }
.tracking-tighter { letter-spacing: -0.06em; }
.text-primary { color: #7C4DFF; }
.mb-10 { margin-bottom: 2.5rem; }
.mb-12 { margin-bottom: 3rem; }
.mb-14 { margin-bottom: 4rem; }

@keyframes rotate { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
.animate-in { animation: slideUp 0.6s cubic-bezier(0.23, 1, 0.32, 1) forwards; opacity: 0; }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

@media (max-width: 900px) {
  .posts-grid { grid-template-columns: 1fr; }
  .summary-hud { flex-direction: column; align-items: center; }
  .summary-card { width: 100%; max-width: none; }
  .control-row { flex-direction: column; align-items: stretch; }
  .search-box { max-width: none; }
  .popular-grid { grid-template-columns: 1fr; }
}
</style>