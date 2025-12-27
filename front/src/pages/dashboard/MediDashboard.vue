<template>
  <div class="dashboard-container">
    <!-- 배경 그래픽: MEDI-EAT 테마 (블루 & 퍼플 톤) -->
    <div class="aurora-bg">
      <div class="blob blob-blue"></div>
      <div class="blob blob-cyan"></div>
      <div class="blob blob-purple"></div>
    </div>

    <div class="content-wrapper">
      <!-- 헤더 영역 -->
      <header class="page-header animate-enter">
        <div class="header-left">
          <div class="title-area">
            <span class="title-emoji">💊</span>
            <h2 class="page-title">MEDI-EAT Dashboard</h2>
          </div>
          <p class="page-subtitle">오늘의 식단과 약 복용, 꼼꼼하게 관리하세요!</p>
        </div>

        <div class="header-right">
          <div class="date-capsule">
            <span class="icon">📅</span>
            {{ selectedDate }}
          </div>

          <!-- XP 스트립 (블루 테마) -->
          <div v-if="progress" class="xp-strip">
            <div class="xp-info">
              <div class="xp-icon-box">{{ levelMeta.emoji }}</div>
              <div class="xp-text">
                <div class="xp-level">
                  <b>Lv. {{ progress.level }}</b>
                  <span>{{ progress.levelName || levelMeta.name }}</span>
                </div>
                <div class="xp-bar-container">
                  <div class="xp-bar-bg">
                    <div class="xp-bar-fill" :style="{ width: xpPercent + '%' }"></div>
                  </div>
                  <div class="xp-meta-text">
                    <span>{{ progress.totalXp }} / {{ progress.nextLevelXp }} XP</span>
                    <span class="xp-gain">(+{{ progress.todayXp }})</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </header>

      <!-- ------------------ 벤토 그리드 레이아웃 ------------------ -->
      <main class="bento-grid">
        
        <!-- [A] 메인 히어로: 질병 & 약 관리 (2x2) -->
        <article class="bento-card hero-card animate-enter delay-1">
          <div class="card-bg-mesh blue"></div>

          <div class="card-header">
            <div class="card-title-group">
              <div class="icon-box blue">🩺</div>
              <h3>질병 & 약 관리</h3>
            </div>
            <div class="header-actions">
              <button class="action-btn" @click="goMedication">관리</button>
              <button class="action-btn primary" @click="goMediScan">스캔</button>
            </div>
          </div>

          <!-- 메디 스캔 배너 -->
          <div class="hero-banner" @click="goMediScan">
            <div class="banner-content">
              <span class="banner-icon">📸</span>
              <div class="banner-text">
                <strong>알약 스캔하기</strong>
                <span>사진을 찍어 약을 바로 등록하세요</span>
              </div>
            </div>
            <span class="banner-arrow">➜</span>
          </div>

          <div class="hero-body">
            <!-- 등록된 질병 -->
            <section class="sub-section">
              <div class="sub-head">
                <span>등록된 질병</span>
                <button v-if="diseases.length === 0" class="tiny-link" @click="goMedication">등록 +</button>
              </div>

              <div v-if="diseases.length" class="tag-list">
                <span v-for="d in diseases" :key="d.userDiseaseId" class="tag-item">
                  {{ d.diseaseName }}
                </span>
              </div>
              <div v-else class="empty-state-mini">등록된 질병이 없습니다.</div>
            </section>

            <!-- 오늘의 약 복용 -->
            <section class="sub-section full-height">
              <div class="sub-head">
                <span>오늘의 약 복용</span>
                <div class="dots" v-if="totalMedPages > 1">
                  <span
                    v-for="(_, idx) in totalMedPages"
                    :key="idx"
                    class="dot"
                    :class="{ active: idx === currentMedPage }"
                    @click="setMedPage(idx)"
                  />
                </div>
              </div>

              <div v-if="medications.length" class="med-list-container">
                <div class="med-grid">
                  <div class="med-card" v-for="med in pagedMeds" :key="med.medicationId">
                    <div class="med-info">
                      <div class="med-name">{{ med.drugName }}</div>
                      <div class="med-dose">{{ med.dose }}</div>
                    </div>
                    <div class="med-checks">
                      <button
                        v-for="(label, i) in med.times"
                        :key="i"
                        class="check-pill"
                        :class="{ checked: med.checks[i] }"
                        @click.stop="toggleCheck(med, i)"
                      >
                        {{ label }}
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              <div v-else class="empty-state-box">
                <p>복용 중인 약이 없습니다.</p>
                <button class="outline-btn" @click="goMedication">약 등록하기</button>
              </div>
            </section>
          </div>
        </article>

        <!-- [B] 영양 요약 (Tall Card) -->
        <article class="bento-card summary-card animate-enter delay-2">
          <div class="card-header">
            <div class="card-title-group">
              <div class="icon-box green">🥗</div>
              <h3>영양 요약</h3>
            </div>
            <button class="more-link" @click="goDietAnalysis">상세</button>
          </div>

          <div v-if="dailyTotal.totalCalorie > 0" class="summary-content">
            <div class="chart-wrapper">
              <svg viewBox="0 0 36 36" class="circular-chart">
                <path
                  class="circle-bg"
                  d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                />
                <path
                  class="circle-fg"
                  :stroke-dasharray="kcalPercent + ', 100'"
                  d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                />
              </svg>
              <div class="chart-inner">
                <span class="chart-val">{{ dailyTotal.totalCalorie }}</span>
                <span class="chart-unit">kcal</span>
              </div>
            </div>
            <div class="status-badge" :class="statusClass">{{ statusText }}</div>

            <div class="macro-bars">
              <div class="macro-row">
                <span class="m-label">탄수화물</span>
                <div class="m-track"><div class="m-fill carb" :style="{ width: carbRate + '%' }"></div></div>
                <span class="m-val">{{ carbRate }}%</span>
              </div>
              <div class="macro-row">
                <span class="m-label">단백질</span>
                <div class="m-track"><div class="m-fill protein" :style="{ width: proteinRate + '%' }"></div></div>
                <span class="m-val">{{ proteinRate }}%</span>
              </div>
              <div class="macro-row">
                <span class="m-label">지방</span>
                <div class="m-track"><div class="m-fill fat" :style="{ width: fatRate + '%' }"></div></div>
                <span class="m-val">{{ fatRate }}%</span>
              </div>
            </div>
          </div>

          <div v-else class="empty-state-vertical">
            <div class="empty-icon">🍽️</div>
            <p>오늘 기록이 없어요</p>
            <button class="glass-btn" @click="goDiet">기록하기</button>
          </div>
        </article>

        <!-- [C] 챌린지 (1x1) -->
        <article class="bento-card challenge-card animate-enter delay-3">
          <div class="card-bg-sticker">🎯</div>
          <div class="card-header">
            <div class="card-title-group white-text">
              <h3>Challenge</h3>
            </div>
            <button class="more-link white" @click="goChallenge">More</button>
          </div>

          <div v-if="mediChallenge" class="challenge-body">
            <div class="ch-badge">MEDI-EAT</div>
            <div class="ch-title">{{ mediChallenge.name }}</div>
            <div class="ch-progress-wrap">
              <div class="ch-track">
                <div class="ch-fill" :style="{ width: mediChallenge.progress + '%' }"></div>
              </div>
              <div class="ch-meta">
                <span>진행률</span>
                <strong>{{ mediChallenge.progress }}%</strong>
              </div>
            </div>
          </div>

          <div v-else class="empty-state-white">
            <p>진행 중인 챌린지가 없습니다</p>
            <button class="glass-btn small" @click="goChallenge">둘러보기</button>
          </div>
        </article>

        <!-- [D] Community (1x1) -->
        <article class="bento-card community-card animate-enter delay-4">
          <div class="card-header">
            <div class="card-title-group">
              <div class="icon-box purple">💬</div>
              <h3>Community</h3>
            </div>
          </div>

          <div v-if="topPost" class="community-preview" @click="goCommunity">
            <div class="hot-badge">HOT 🔥</div>
            <div class="post-tit">{{ topPost.title }}</div>
            <div class="post-info">
              <span>{{ displayWriter }}</span>
              <span class="likes">♥ {{ topPost.likes }}</span>
            </div>
          </div>

          <div v-else class="empty-state-mini">
            <p>인기글이 없어요</p>
            <button class="tiny-link" @click="goCommunityWrite">글쓰기</button>
          </div>
        </article>

        <!-- [E] 추천 코너 (Full Width Bottom) -->
        <article class="bento-card recommend-card animate-enter delay-5">
          <div class="card-header">
            <div class="card-title-group">
              <div class="icon-box orange">💡</div>
              <h3>맞춤 건강 추천</h3>
            </div>
            <button class="more-link" @click="goRecommendation">전체보기</button>
          </div>

          <div class="reco-grid">
            <!-- 1. 약 리마인더 -->
            <div class="reco-box">
              <div class="reco-icon blue">💊</div>
              <div class="reco-text">
                <div class="r-head">약 리마인더</div>
                <div class="r-main">{{ medReminderText }}</div>
                <div class="r-sub">{{ medReminderSub }}</div>
              </div>
            </div>

            <!-- 2. 주의사항 (카드형) -->
            <div class="reco-box warn-box">
              <div class="reco-icon red">⚠</div>
              <div class="reco-text full">
                <div class="r-head">주의사항</div>
                
                <div v-if="warningCards.length" class="warn-card-list">
                  <div v-for="w in warningCards" :key="w.key" class="warn-item">
                    <div class="w-top">
                      <span class="w-dot"></span>
                      <span class="w-drug">{{ w.drugName }}</span>
                    </div>
                    <div class="w-desc">{{ w.oneLine }}</div>
                  </div>
                </div>
                <div v-else class="r-desc">특이사항이 없습니다.</div>
              </div>
            </div>

            <!-- 3. 건강 팁 -->
            <div class="reco-box">
              <div class="reco-icon purple">🩺</div>
              <div class="reco-text">
                <div class="r-head">{{ diseaseCoachTitle }}</div>
                <div class="r-desc">{{ diseaseCoachText }}</div>
              </div>
            </div>
          </div>
        </article>

      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";

import dietApi from "@/api/diet";
import diseaseApi from "@/api/disease";
import medicationApi from "@/api/medication";
import challengeApi from "@/api/challenge";
import communityApi from "@/api/community";
import authApi from "@/api/auth";
import gamificationApi from "@/api/gamification";

const router = useRouter();
const selectedDate = ref(new Date().toISOString().substring(0, 10));

/* =========================
   ✅ XP (EAT와 동일 로직)
========================= */
const progress = ref(null);

const LEVELS = [
  { level: 1, name: "시작 접시", emoji: "🌱", minXp: 0 },
  { level: 2, name: "균형 한입", emoji: "🌿", minXp: 150 },
  { level: 3, name: "데일리 플레이트", emoji: "🍽️", minXp: 400 },
  { level: 4, name: "루틴 메이커", emoji: "⚡", minXp: 800 },
  { level: 5, name: "영양 밸런서", emoji: "✨", minXp: 1400 },
  { level: 6, name: "컨디션 코치", emoji: "🧠", minXp: 2200 },
  { level: 7, name: "MEDEAT 마스터", emoji: "👑", minXp: 3200 },
];

const levelMeta = computed(() => {
  if (!progress.value) return LEVELS[0];
  const lv = Number(progress.value.level || 1);
  return LEVELS.find((x) => x.level === lv) || LEVELS[0];
});

const xpPercent = computed(() => {
  if (!progress.value) return 0;
  const next = Number(progress.value.nextLevelXp ?? 0);
  const total = Number(progress.value.totalXp ?? 0);
  if (next <= 0) return 0;
  return Math.max(0, Math.min(100, Math.round((total / next) * 100)));
});

const loadProgress = async () => {
  try {
    const userRes = await authApi.getUser();
    const userId = userRes.data?.userId;
    if (!userId) {
      progress.value = null;
      return;
    }
    const res = await gamificationApi.getProgress(userId, selectedDate.value);
    progress.value = res.data;
  } catch (e) {
    progress.value = null;
  }
};

/* =========================
   Router
========================= */
const goDiet = () => router.push({ name: "diet-list", query: { mode: "MEDI_EAT" } });
const goDietAnalysis = () => router.push({ name: "diet-analysis", query: { mode: "MEDI_EAT" } });
const goChallenge = () => router.push({ name: "challenge-list", query: { mode: "MEDI_EAT" } });
const goCommunity = () => router.push({ name: "community-list", query: { mode: "MEDI_EAT" } });
const goCommunityWrite = () => router.push({ name: "community-new", query: { mode: "MEDI_EAT" } });
const goMediScan = () => router.push({ path: "/medication/scan", query: { mode: "MEDI_EAT" } });
const goMedication = () => router.push({ path: "/disease", query: { mode: "MEDI_EAT" } });
const goRecommendation = () => router.push({ path: "/recommendation", query: { mode: "MEDI_EAT" } });

/* =========================
   1) Diet Summary
========================= */
const dailyTotal = ref({ totalCalorie: 0, totalCarb: 0, totalProtein: 0, totalFat: 0 });
const goal = { kcal: 2000 };

const kcalPercent = computed(() => {
  if (!goal.kcal) return 0;
  return Math.min(100, Math.round((dailyTotal.value.totalCalorie / goal.kcal) * 100));
});

const carbRate = computed(() => {
  const sum = dailyTotal.value.totalCarb + dailyTotal.value.totalProtein + dailyTotal.value.totalFat || 1;
  return Math.round((dailyTotal.value.totalCarb / sum) * 100);
});
const proteinRate = computed(() => {
  const sum = dailyTotal.value.totalCarb + dailyTotal.value.totalProtein + dailyTotal.value.totalFat || 1;
  return Math.round((dailyTotal.value.totalProtein / sum) * 100);
});
const fatRate = computed(() => {
  const sum = dailyTotal.value.totalCarb + dailyTotal.value.totalProtein + dailyTotal.value.totalFat || 1;
  return Math.round((dailyTotal.value.totalFat / sum) * 100);
});

const statusText = computed(() => {
  if (dailyTotal.value.totalCalorie === 0) return "기록 없음";
  if (dailyTotal.value.totalCalorie < goal.kcal * 0.7) return "부족";
  if (dailyTotal.value.totalCalorie > goal.kcal * 1.1) return "과잉";
  return "적정";
});
const statusClass = computed(() => {
  if (dailyTotal.value.totalCalorie === 0) return "s-none";
  if (dailyTotal.value.totalCalorie < goal.kcal * 0.7) return "s-low";
  if (dailyTotal.value.totalCalorie > goal.kcal * 1.1) return "s-high";
  return "s-good";
});

const loadDietSummary = async () => {
  try {
    const today = selectedDate.value;
    const res = await dietApi.getList(today);
    let cal = 0,
      carb = 0,
      pro = 0,
      fat = 0;
    (res.data || []).forEach((d) => {
      cal += d.totalCalorie || 0;
      carb += d.totalCarb || 0;
      pro += d.totalProtein || 0;
      fat += d.totalFat || 0;
    });
    dailyTotal.value = {
      totalCalorie: Math.round(cal),
      totalCarb: Math.round(carb),
      totalProtein: Math.round(pro),
      totalFat: Math.round(fat),
    };
  } catch (e) {}
};

/* =========================
   2) Diseases
========================= */
const diseases = ref([]);
const loadDiseases = async () => {
  try {
    const res = await diseaseApi.getMyDiseases();
    diseases.value = res.data || [];
  } catch (e) {}
};

/* =========================
   3) Medications
========================= */
const medications = ref([]);
const medsPerPage = 2;
const currentMedPage = ref(0);
const todayLogs = ref([]);


const totalMedPages = computed(() => Math.ceil(medications.value.length / medsPerPage));
const pagedMeds = computed(() => {
  const start = currentMedPage.value * medsPerPage;
  return medications.value.slice(start, start + medsPerPage);
});
const setMedPage = (idx) => (currentMedPage.value = idx);

const loadMedication = async () => {
  try {
    const medRes = await medicationApi.getMyMedication();
    const meds = medRes.data || [];

    medications.value = meds.map((med) => {
      
      const times = med.intakeTime
        ? med.intakeTime.split(",").map((t) => t.trim())
        : [];

      // ⭐ 오늘 로그 기반으로 체크 복원
      const checks = times.map((_, idx) =>
        todayLogs.value.some(
          (log) =>
            log.medicationId === med.medicationId &&
            log.takenIndex === idx + 1 // DB는 1-based
        )
      );

      return {
        ...med,
        times,
        checks,
      };
    });
  } catch (e) {}
};


const loadTodayLogs = async () => {
  const res = await medicationApi.getTodayLogs();
  todayLogs.value = res.data || [];
};

const toggleCheck = async (med, i) => {
  if (med.checks[i]) return; // 이미 체크된 건 무시

  med.checks[i] = true;

  try {
    await medicationApi.saveLog({
      medicationId: med.medicationId,
      takenIndex: i + 1, // ⭐ DB는 1-based
    });
    await loadTodayLogs();
  } catch (e) {
    // 실패 시 롤백
    med.checks[i] = false;
  }
};



/* =========================
   4) Challenge
========================= */
const mediChallenge = ref(null);
const loadMediChallenge = async () => {
  try {
    const res = await challengeApi.getList("MEDI_EAT");
    const list = res.data?.ongoing || res.data?.ongoingChallenges || [];
    if (!list.length) {
      mediChallenge.value = null;
      return;
    }

    const c = list[0];
    const detail = await challengeApi
      .getDetail(c.userChallengeId || c.id, "MEDI_EAT")
      .then((r) => r.data)
      .catch(() => null);

    if (!detail) {
      mediChallenge.value = null;
      return;
    }

    const total = Number(detail.challenge?.periodDays || 30);
    const success = (detail.logs || []).filter((l) => l.status === "SUCCESS").length;
    mediChallenge.value = {
      name: detail.challenge?.title || "진행 중 챌린지",
      progress: total > 0 ? Math.round((success / total) * 100) : 0,
    };
  } catch (e) {
    mediChallenge.value = null;
  }
};

/* =========================
   5) Community
========================= */
const topPost = ref(null);
const displayWriter = computed(() => topPost.value?.nickname || "익명");

const loadTopPost = async () => {
  try {
    const res = await communityApi.getTopPost();
    topPost.value = res.data || null;
  } catch (e) {
    topPost.value = null;
  }
};

/* =========================
   6) Recommendations
========================= */
const medStats = computed(() => {
  let total = 0,
    taken = 0;
  medications.value.forEach((m) => {
    total += m.times.length;
    taken += m.checks.filter(Boolean).length;
  });
  return { total, taken, remaining: Math.max(total - taken, 0) };
});

const medReminderText = computed(() => {
  const { total, remaining } = medStats.value;
  if (total === 0) return "복용 일정 없음";
  if (remaining === 0) return "모두 복용 완료! 🎉";
  return `${remaining}회 더 복용 필요`;
});
const medReminderSub = computed(() => {
  const { total, taken } = medStats.value;
  return total ? `${taken}/${total} 완료` : "";
});

/* =========================
   ✅ 주의사항 1문장만
========================= */
const DRUG_WARN_RULES = [
  { match: /(타이레놀|아세트아미노펜)/i, text: "아세트아미노펜 계열은 음주를 피하고, 감기약 등 중복 성분을 확인해 주세요." },
  { match: /(이부프로펜|나프록센|NSAID|록소)/i, text: "소염진통제(NSAIDs)는 공복 복용을 피하고 위장장애가 있으면 주의가 필요해요." },
  { match: /(항생제|아목시|세파|시프로|클라리|독시)/i, text: "항생제는 처방 기간을 지켜 복용하고 임의로 중단하지 않는 것이 좋아요." },
  { match: /(항히스타민|감기약|졸림|수면)/i, text: "졸림을 유발할 수 있어 운전/기계 조작 전 복용은 피하는 게 좋아요." },
  { match: /(와파린|아스피린|항응고)/i, text: "항응고/항혈소판 계열은 출혈 위험에 유의하고, 복용 중인 약/영양제 병용을 꼭 확인해 주세요." },
];

const normalizeText = (s) =>
  String(s || "")
    .replace(/\s+/g, " ")
    .replace(/•/g, "")
    .trim();

const firstSentence = (text) => {
  const s = normalizeText(text);
  if (!s) return "";

  // 1) 줄바꿈 기준 먼저 1줄 컷
  const firstLine = s.split(/\r?\n/)[0].trim();

  // 2) 이미 … 로 잘려오면 그 앞까지만
  const ell = firstLine.indexOf("…");
  const t = ell !== -1 ? firstLine.slice(0, ell).trim() + "…" : firstLine;

  // 3) 마침표/물음표/느낌표 기준 첫 문장
  const m = t.match(/^(.+?[.!?])(\s|$)/);
  if (m) return m[1].trim();

  // 4) 그래도 없으면 길이로 1줄 컷
  return t.length > 80 ? t.slice(0, 80).trim() + "…" : t;
};

const interactionTexts = ref([]);

const loadMedicationWarnings = async () => {
  try {
    const res = await medicationApi.getWarnings();
    interactionTexts.value = res.data || [];
  } catch (e) {
    interactionTexts.value = [];
  }
};

const warningCards = computed(() => {
  const meds = medications.value || [];
  if (!meds.length) return [];

  const apiList = (interactionTexts.value || []).map((x) => ({
    drugName: x.drugName || x.itemName || x.name || "",
    oneLine: firstSentence(x.atpnWarnQesitm || x.warn || x.text),
  }));

  return meds
    .map((m) => {
      const drugName = m.drugName || "복용약";
      const key = String(m.medicationId ?? drugName);

      // 1) API에서 매칭되는 1문장
      const apiHit = apiList.find(
        (a) =>
          a.oneLine &&
          a.drugName &&
          (drugName.includes(a.drugName) || a.drugName.includes(drugName))
      )?.oneLine;

      // 2) 없으면 룰 1문장
      const ruleHit = DRUG_WARN_RULES.find((r) => r.match.test(drugName))?.text;

      const oneLine = apiHit || ruleHit || "특이사항 없음";
      return { key, drugName, oneLine };
    })
    // 전부 "특이사항 없음"이면 굳이 카드 여러개 띄우지 않게 하고 싶으면 여기서 필터 가능
    ;
});

/* =========================
   건강 팁
========================= */
const HEALTH_TIPS_BASE = [
  "식후 10분 산책만 해도 컨디션에 도움돼요.",
  "물을 한 번에 많이 말고 자주 나눠 마셔보세요.",
  "카페인은 오후 늦게 줄이면 수면 질이 좋아져요.",
  "식사 시간을 일정하게 맞추면 하루 리듬이 안정돼요.",
  "오늘 탄·단·지 비율을 한 번만 체크해봐요.",
];

const pickTipByDate = (dateStr, tips) => {
  const s = (dateStr || "").replaceAll("-", "");
  let seed = 0;
  for (const ch of s) seed = (seed * 31 + ch.charCodeAt(0)) >>> 0;
  return tips[seed % tips.length];
};

const diseaseCoachTitle = computed(() => "오늘의 건강 팁");
const diseaseCoachText = computed(() => {
  const tip = pickTipByDate(selectedDate.value, HEALTH_TIPS_BASE);
  return diseases.value.length ? `오늘은 ${tip}` : tip;
});

/* =========================
   초기 로드
========================= */
onMounted(async () => {
  loadDietSummary();
  loadDiseases();

  await loadTodayLogs();   // ⭐ 먼저
  await loadMedication();  // ⭐ 그 다음

  loadMediChallenge();
  loadTopPost();
  loadProgress();
  loadMedicationWarnings();
});


</script>

<style scoped>
/* =========================
   GLOBAL RESET
========================= */
* { box-sizing: border-box; }

.dashboard-container {
  position: relative;
  min-height: 100vh;
  background: #F4F9F6; /* EAT 모드와 동일한 베이스 */
  font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, system-ui, Roboto, sans-serif;
  color: #111827;
  padding: 40px;
  overflow-x: hidden;
}

.content-wrapper {
  position: relative;
  z-index: 2;
  max-width: 1240px;
  margin: 0 auto;
}

/* =========================
   AURORA BACKGROUND (MEDI Theme)
========================= */
.aurora-bg {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
}
.blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.6;
  animation: floatBlob 10s infinite ease-in-out;
}
.blob-blue {
  width: 500px; height: 500px;
  background: #BFDBFE;
  top: -10%; right: -10%;
  animation-duration: 12s;
}
.blob-cyan {
  width: 400px; height: 400px;
  background: #A5F3FC;
  bottom: -10%; left: -5%;
  animation-duration: 14s;
  animation-direction: reverse;
}
.blob-purple {
  width: 300px; height: 300px;
  background: #E9D5FF;
  top: 40%; left: 30%;
  opacity: 0.4;
  animation-duration: 16s;
}

@keyframes floatBlob {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(20px, -20px); }
}

/* =========================
   HEADER
========================= */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
  gap: 20px;
}
.header-left { flex: 1; }
.title-area { display: flex; align-items: center; gap: 10px; margin-bottom: 4px; }
.title-emoji { font-size: 32px; animation: bounce 2s infinite; }
@keyframes bounce { 0%,100%{transform:translateY(0);} 50%{transform:translateY(-5px);} }

.page-title {
  font-size: 32px;
  font-weight: 800;
  color: #1E3A8A; /* MEDI Theme Blue */
  margin: 0;
  letter-spacing: -0.5px;
}
.page-subtitle {
  margin: 0;
  font-size: 15px;
  color: #64748B;
  font-weight: 500;
}

.header-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
  min-width: 300px;
}

.date-capsule {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  color: #334155;
  display: flex;
  align-items: center;
  gap: 6px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.03);
}

/* XP STRIP (Blue Theme) */
.xp-strip {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  padding: 10px 16px;
  border-radius: 20px;
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.08); /* Blue shadow */
  width: 100%;
  max-width: 380px;
}
.xp-info { display: flex; align-items: center; gap: 12px; }
.xp-icon-box {
  width: 42px; height: 42px;
  background: #EFF6FF; /* Blue tint */
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  font-size: 20px;
  border: 1px solid #DBEAFE;
}
.xp-text { flex: 1; display: flex; flex-direction: column; gap: 6px; }
.xp-level { display: flex; justify-content: space-between; font-size: 13px; color: #1E40AF; }
.xp-level b { font-weight: 800; }
.xp-bar-container { width: 100%; }
.xp-bar-bg {
  height: 8px;
  background: #E2E8F0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 4px;
}
.xp-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #3B82F6, #2563EB); /* Blue Gradient */
  border-radius: 4px;
  transition: width 0.5s ease;
}
.xp-meta-text {
  display: flex; justify-content: space-between; font-size: 11px; color: #64748B; font-weight: 600;
}
.xp-gain { color: #2563EB; }

/* =========================
   BENTO GRID
========================= */
.bento-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  grid-auto-rows: minmax(180px, auto);
}

/* Card Common */
.bento-card {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 28px;
  padding: 24px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
}
.bento-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.08);
}

.hero-card { grid-column: span 2; grid-row: span 2; }
.summary-card { grid-column: span 1; grid-row: span 2; }
.challenge-card { grid-column: span 1; grid-row: span 1; }
.community-card { grid-column: span 1; grid-row: span 1; }
.recommend-card { grid-column: span 4; }

/* Headers inside cards */
.card-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; z-index: 2; position: relative;
}
.card-title-group { display: flex; align-items: center; gap: 10px; }
.card-title-group h3 { font-size: 18px; font-weight: 800; margin: 0; color: #1E293B; }
.card-title-group.white-text h3 { color: #fff; }

.icon-box {
  width: 36px; height: 36px;
  border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  font-size: 18px;
}
.icon-box.blue { background: #DBEAFE; color: #1E40AF; }
.icon-box.green { background: #DCFCE7; color: #15803D; }
.icon-box.purple { background: #F3E8FF; color: #7E22CE; }
.icon-box.orange { background: #FFEDD5; color: #C2410C; }

/* Buttons */
.more-link { background: none; border: none; font-size: 13px; font-weight: 600; color: #64748B; cursor: pointer; }
.more-link:hover { color: #334155; }
.more-link.white { color: rgba(255,255,255,0.8); }
.more-link.white:hover { color: #fff; }

/* [A] HERO CARD Style */
.card-bg-mesh {
  position: absolute; inset: 0; opacity: 0.1; pointer-events: none; z-index: 0;
}
.card-bg-mesh.blue {
  background: radial-gradient(circle at 80% 20%, #2563EB, transparent 60%);
}

.header-actions { display: flex; gap: 8px; }
.action-btn {
  border: 1px solid rgba(0,0,0,0.05);
  background: rgba(255,255,255,0.5);
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  color: #475569;
  cursor: pointer;
  transition: 0.2s;
}
.action-btn:hover { background: #fff; }
.action-btn.primary {
  background: #3B82F6; color: #fff; border: none;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}
.action-btn.primary:hover { background: #2563EB; }

.hero-banner {
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  padding: 12px 16px;
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 20px;
  cursor: pointer;
  transition: 0.2s;
  position: relative; z-index: 1;
}
.hero-banner:hover { background: #fff; transform: scale(1.01); }
.banner-content { display: flex; align-items: center; gap: 12px; }
.banner-icon { font-size: 24px; }
.banner-text { display: flex; flex-direction: column; }
.banner-text strong { font-size: 14px; font-weight: 700; color: #1E3A8A; }
.banner-text span { font-size: 12px; color: #64748B; }
.banner-arrow { color: #3B82F6; font-weight: 700; }

.hero-body { display: flex; flex-direction: column; gap: 20px; flex: 1; z-index: 1; }
.sub-section { background: rgba(255,255,255,0.5); border-radius: 16px; padding: 14px; flex: 1; display: flex; flex-direction: column; }
.sub-section.full-height { flex: 2; }

.sub-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; font-size: 13px; font-weight: 700; color: #475569; }
.tiny-link { background: none; border: none; color: #3B82F6; font-size: 12px; font-weight: 600; cursor: pointer; }

.tag-list { display: flex; flex-wrap: wrap; gap: 6px; }
.tag-item { background: #fff; padding: 4px 10px; border-radius: 8px; font-size: 12px; font-weight: 600; color: #334155; border: 1px solid #E2E8F0; }

.dots { display: flex; gap: 4px; }
.dot { width: 6px; height: 6px; background: #CBD5E1; border-radius: 50%; cursor: pointer; }
.dot.active { background: #3B82F6; width: 12px; border-radius: 4px; transition: 0.3s; }

.med-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
.med-card {
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  display: flex; flex-direction: column; justify-content: space-between;
  border: 1px solid rgba(0,0,0,0.03);
  box-shadow: 0 2px 8px rgba(0,0,0,0.03);
  min-height: 80px;
}
.med-name { font-weight: 700; font-size: 14px; color: #1E293B; margin-bottom: 2px; }
.med-dose { font-size: 11px; color: #94A3B8; margin-bottom: 8px; }
.med-checks { display: flex; gap: 4px; flex-wrap: wrap; }
.check-pill {
  border: 1px solid #E2E8F0;
  background: #F8FAFC;
  color: #64748B;
  font-size: 11px;
  padding: 4px 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: 0.2s;
}
.check-pill:hover { background: #F1F5F9; }
.check-pill.checked {
  background: #3B82F6; color: #fff; border-color: #3B82F6;
  box-shadow: 0 2px 6px rgba(59, 130, 246, 0.3);
}

.empty-state-box {
  flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center;
  border: 2px dashed rgba(0,0,0,0.05); border-radius: 12px; gap: 8px; color: #94A3B8; font-size: 13px;
}
.outline-btn {
  background: transparent; border: 1px solid #CBD5E1; padding: 6px 12px; border-radius: 20px; font-size: 12px; color: #64748B; cursor: pointer;
}

/* [B] SUMMARY CARD */
.summary-content { display: flex; flex-direction: column; align-items: center; flex: 1; justify-content: center; }
.chart-wrapper { width: 140px; height: 140px; position: relative; margin-bottom: 12px; }
.circular-chart { width: 100%; height: 100%; }
.circle-bg { fill: none; stroke: #E2E8F0; stroke-width: 2.5; }
.circle-fg { fill: none; stroke: #10B981; stroke-width: 2.5; stroke-linecap: round; transition: stroke-dasharray 1s ease; }
.chart-inner { position: absolute; inset: 0; display: flex; flex-direction: column; align-items: center; justify-content: center; }
.chart-val { font-size: 26px; font-weight: 800; color: #1E293B; line-height: 1; }
.chart-unit { font-size: 12px; color: #94A3B8; font-weight: 600; margin-top: 2px; }

.status-badge {
  font-size: 12px; font-weight: 700; padding: 4px 10px; border-radius: 20px; margin-bottom: 24px;
}
.status-badge.s-good { background: #DCFCE7; color: #15803D; }
.status-badge.s-low { background: #FEF3C7; color: #B45309; }
.status-badge.s-high { background: #FEE2E2; color: #B91C1C; }

.macro-bars { width: 100%; display: flex; flex-direction: column; gap: 12px; }
.macro-row { display: flex; align-items: center; font-size: 12px; font-weight: 600; color: #64748B; }
.m-label { width: 44px; }
.m-track { flex: 1; height: 6px; background: #F1F5F9; border-radius: 4px; margin: 0 10px; overflow: hidden; }
.m-fill { height: 100%; border-radius: 4px; }
.m-fill.carb { background: #FBBF24; }
.m-fill.protein { background: #34D399; }
.m-fill.fat { background: #F87171; }
.m-val { width: 32px; text-align: right; color: #334155; }

.empty-state-vertical { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 12px; color: #94A3B8; font-size: 14px; font-weight: 600; }
.empty-icon { font-size: 40px; opacity: 0.5; }
.glass-btn { background: rgba(59,130,246,0.1); color: #2563EB; border: none; padding: 8px 16px; border-radius: 20px; font-weight: 600; font-size: 13px; cursor: pointer; }

/* [C] CHALLENGE */
.challenge-card {
  background: linear-gradient(135deg, #3B82F6, #2563EB);
  color: #fff; border: none;
}
.card-bg-sticker { position: absolute; right: -20px; bottom: -20px; font-size: 120px; opacity: 0.15; transform: rotate(-10deg); pointer-events: none; }
.challenge-body { margin-top: auto; z-index: 1; }
.ch-badge { display: inline-block; background: rgba(255,255,255,0.2); padding: 4px 8px; border-radius: 6px; font-size: 11px; font-weight: 700; margin-bottom: 8px; border: 1px solid rgba(255,255,255,0.2); }
.ch-title { font-size: 18px; font-weight: 800; margin-bottom: 16px; line-height: 1.3; text-shadow: 0 2px 4px rgba(0,0,0,0.1); }
.ch-progress-wrap { background: rgba(0,0,0,0.15); border-radius: 12px; padding: 12px; backdrop-filter: blur(4px); }
.ch-track { height: 6px; background: rgba(255,255,255,0.2); border-radius: 3px; overflow: hidden; margin-bottom: 8px; }
.ch-fill { height: 100%; background: #fff; border-radius: 3px; }
.ch-meta { display: flex; justify-content: space-between; font-size: 12px; opacity: 0.9; }

.empty-state-white {
  margin-top: auto; display: flex; flex-direction: column; align-items: center; gap: 10px; color: rgba(255,255,255,0.9); font-size: 13px;
}
.glass-btn.small { padding: 6px 12px; font-size: 12px; background: rgba(255,255,255,0.2); color: #fff; }

/* [D] COMMUNITY */
.community-preview {
  background: #F8FAFC; border-radius: 16px; padding: 16px; cursor: pointer; border: 1px solid rgba(0,0,0,0.03); transition: 0.2s;
  margin-top: auto;
}
.community-preview:hover { transform: scale(1.02); background: #F1F5F9; }
.hot-badge { display: inline-block; background: #FEE2E2; color: #EF4444; font-size: 10px; font-weight: 800; padding: 2px 6px; border-radius: 4px; margin-bottom: 8px; }
.post-tit { font-size: 14px; font-weight: 700; color: #1E293B; margin-bottom: 10px; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.post-info { display: flex; justify-content: space-between; font-size: 11px; color: #94A3B8; }
.empty-state-mini { text-align: center; color: #94A3B8; font-size: 13px; margin-top: auto; display: flex; flex-direction: column; gap: 8px; }

/* [E] RECOMMENDATION */
.reco-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.reco-box {
  background: rgba(255,255,255,0.6); border-radius: 16px; padding: 16px; display: flex; gap: 14px; align-items: flex-start;
  border: 1px solid rgba(0,0,0,0.03);
}
.reco-box.warn-box { background: #FEF2F2; border-color: #FECACA; }

.reco-icon {
  width: 40px; height: 40px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 20px; flex-shrink: 0;
}
.reco-icon.blue { background: #EFF6FF; color: #3B82F6; }
.reco-icon.red { background: #FEF2F2; color: #EF4444; border: 1px solid #FEE2E2; }
.reco-icon.purple { background: #F3E8FF; color: #7E22CE; }

.reco-text { flex: 1; }
.reco-text.full { width: 100%; }
.r-head { font-size: 12px; font-weight: 700; color: #64748B; margin-bottom: 4px; }
.r-main { font-size: 15px; font-weight: 800; color: #2563EB; margin-bottom: 2px; }
.r-sub { font-size: 12px; color: #94A3B8; }
.r-desc { font-size: 13px; color: #334155; line-height: 1.4; font-weight: 500; }

.warn-card-list { display: flex; flex-direction: column; gap: 8px; }
.warn-item { background: #fff; padding: 10px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.03); border: 1px solid #FEE2E2; }
.w-top { display: flex; align-items: center; gap: 6px; margin-bottom: 4px; }
.w-dot { width: 6px; height: 6px; background: #EF4444; border-radius: 50%; }
.w-drug { font-size: 12px; font-weight: 700; color: #B91C1C; }
.w-desc { font-size: 12px; color: #4B5563; line-height: 1.3; }

/* ANIMATION */
.animate-enter { animation: fadeUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards; opacity: 0; transform: translateY(20px); }
.delay-1 { animation-delay: 0.1s; }
.delay-2 { animation-delay: 0.2s; }
.delay-3 { animation-delay: 0.3s; }
.delay-4 { animation-delay: 0.4s; }
.delay-5 { animation-delay: 0.5s; }
@keyframes fadeUp { to { opacity: 1; transform: translateY(0); } }

/* RESPONSIVE */
@media (max-width: 1024px) {
  .bento-grid { grid-template-columns: repeat(2, 1fr); }
  .hero-card { grid-column: span 2; }
  .recommend-card { grid-column: span 2; }
  .reco-grid { grid-template-columns: 1fr; }
}
@media (max-width: 768px) {
  .dashboard-container { padding: 20px; }
  .page-header { flex-direction: column; align-items: stretch; }
  .header-right { align-items: stretch; min-width: 0; }
  .xp-strip { max-width: 100%; }
  .bento-grid { display: flex; flex-direction: column; }
  .med-grid { grid-template-columns: 1fr; }
}
</style>