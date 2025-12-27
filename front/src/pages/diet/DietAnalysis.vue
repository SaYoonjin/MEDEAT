<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import dietApi from "@/api/diet";
import api from "@/api/axios";

// Chart.js 컴포넌트 및 등록
import { Line, Doughnut, Bar } from "vue-chartjs";
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  Filler,
  LineElement,
  BarElement,
  PointElement,
  CategoryScale,
  LinearScale,
  ArcElement,
} from "chart.js";

// Lucide 아이콘
import { 
  Sparkles, 
  Download, 
  Calendar as CalIcon, 
  ChevronLeft, 
  ChevronRight, 
  Activity, 
  PieChart, 
  TrendingUp, 
  Zap, 
  Droplets, 
  Flame, 
  AlertTriangle,
  Info,
  ChevronDown,
  ChefHat,
  Heart,
  Loader2,
  ArrowRight,
  BarChart3,
  Utensils,
  FileDown,
  Check,
  ShieldAlert
} from 'lucide-vue-next';

ChartJS.register(
  Title, Tooltip, Legend, Filler, LineElement, BarElement, PointElement, CategoryScale, LinearScale, ArcElement
);

const router = useRouter();
const route = useRoute();

/* ==========================
   공통 유틸
   ========================== */
const formatDate = (d) => {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${y}-${m}-${day}`;
};

/* --------------------------
   상태값
   -------------------------- */
const analysisArea = ref(null);
const mode = computed(() => route.query.mode || "EAT");

/* 🌟 테마 설정 (모드별 포인트 컬러 동기화) */
const themeConfig = computed(() => {
  if (mode.value === 'MEDI_EAT') {
    return {
      color: "#38bdf8", // 하늘색
      soft: "rgba(56, 189, 248, 0.15)",
    }
  }
  return {
    color: "#a3e635", // 연두색
    soft: "rgba(163, 230, 53, 0.15)",
  }
});

const todayStr = formatDate(new Date());
const period = ref(route.query.period || "day");
const baseDate = ref(route.query.baseDate || todayStr);
const startDate = ref(route.query.startDate || todayStr);
const endDate = ref(route.query.endDate || todayStr);

const calendarMonth = ref(new Date(startDate.value));
const days = ref([]);

const loading = ref(false);
const itResult = ref(null);
const medResult = ref(null);

const snackChartRef = ref(null);
const dailyChartRef = ref(null);

/* --------------------------
   추천 음식 로직
   -------------------------- */
const recommendTab = ref("lowCalorie"); 
const recommendLoading = ref(false);
const recommendData = ref({
  lowCalorie: [],
  substitutes: [],
  highProteinLowFat: [],
  lowFat: [],
});

const recommendRaw = ref({
  remainingKcalMeals: [],
  goalBasedMeals: [],
  lowerKcalAlternatives: [],
  macroBalanceMeals: [],
  healthyFoodCandidates: [],
  cookingAdvices: [],
  messages: [],
});

const getRecommendDate = () => {
  if (period.value === "custom") {
    return endDate.value || startDate.value || todayStr;
  } else {
    return baseDate.value || todayStr;
  }
};

const loadRecommendations = async () => {
  recommendLoading.value = true;
  try {
    const date = getRecommendDate();
    let userId = Number(localStorage.getItem("userId")) || 1;

    const res = await dietApi.getRecommendations(userId, date);
    const data = res.data || {};

    recommendRaw.value = {
      remainingKcalMeals: data.remainingKcalMeals || [],
      goalBasedMeals: data.goalBasedMeals || [],
      lowerKcalAlternatives: data.lowerKcalAlternatives || [],
      macroBalanceMeals: data.macroBalanceMeals || [],
      healthyFoodCandidates: data.healthyFoodCandidates || [],
      cookingAdvices: data.cookingAdvices || [],
      messages: data.messages || [],
    };

    recommendData.value = {
      lowCalorie: data.remainingKcalMeals?.[0]?.foods || [],
      substitutes: (data.lowerKcalAlternatives || []).map(alt => alt.alternativeFood).filter(f => !!f),
      highProteinLowFat: data.goalBasedMeals?.[0]?.foods || [],
      lowFat: (data.macroBalanceMeals || []).find(m => (m.title || "").includes("지방"))?.foods || [],
    };
  } catch (e) {
    console.error("추천 로드 실패:", e);
  } finally {
    recommendLoading.value = false;
  }
};

/* --------------------------
   달력 로직
   -------------------------- */
const generateCalendar = () => {
  const year = calendarMonth.value.getFullYear();
  const month = calendarMonth.value.getMonth();
  const first = new Date(year, month, 1);
  const last = new Date(year, month + 1, 0);

  const arr = [];
  let cur = new Date(first);
  cur.setDate(cur.getDate() - cur.getDay());

  while (cur <= last || cur.getDay() !== 0) {
    arr.push({ date: new Date(cur), dateStr: formatDate(cur) });
    cur.setDate(cur.getDate() + 1);
  }
  days.value = arr;
};

const moveMonth = (offset) => {
  calendarMonth.value = new Date(calendarMonth.value.getFullYear(), calendarMonth.value.getMonth() + offset, 1);
  generateCalendar();
};

const onClickDate = (ds) => {
  if (period.value === "custom") {
    if (!startDate.value || (startDate.value && endDate.value)) {
      startDate.value = ds; endDate.value = "";
    } else {
      if (ds < startDate.value) { endDate.value = startDate.value; startDate.value = ds; }
      else endDate.value = ds;
    }
  } else {
    baseDate.value = ds;
    calendarMonth.value = new Date(ds);
  }
  syncUrl();
};

const setPeriod = (p) => {
  period.value = p;
  calendarMonth.value = new Date(p === "custom" ? startDate.value : baseDate.value);
  generateCalendar();
  syncUrl();
};

const syncUrl = () => {
  const query = { mode: mode.value, period: period.value };
  if (period.value === "custom") { query.startDate = startDate.value; query.endDate = endDate.value || startDate.value; }
  else { query.baseDate = baseDate.value; }
  router.replace({ path: "/diet/analysis", query });
};

const computeRange = () => {
  if (period.value === "custom") return { startDate: startDate.value, endDate: endDate.value || startDate.value };
  const base = new Date(baseDate.value);
  const s = new Date(base);
  if (period.value === "week") s.setDate(s.getDate() - 6);
  if (period.value === "month") s.setDate(s.getDate() - 29);
  return { startDate: formatDate(s), endDate: baseDate.value };
};

const runAnalysis = async () => {
  loading.value = true;
  await loadRecommendations();
  const range = computeRange();
  try {
    const itRes = await dietApi.analyzeIt(range.startDate, range.endDate);
    itResult.value = itRes.data;
    if (mode.value === "MEDI_EAT") {
      const medRes = await dietApi.analyzeMed(range.startDate, range.endDate);
      medResult.value = medRes.data;
    }
  } catch (e) {
    console.error("분석 실패:", e);
  } finally {
    loading.value = false;
  }
};

/* --------------------------
   차트 옵션 및 데이터
   -------------------------- */
const macroChartData = computed(() => {
  if (!itResult.value?.periodSummary) return null;
  const { carbRatio, proteinRatio, fatRatio } = itResult.value.periodSummary;
  return {
    labels: ["탄수화물", "단백질", "지방"],
    datasets: [{
      data: [carbRatio, proteinRatio, fatRatio],
      backgroundColor: ["#fbbf24", "#60a5fa", "#c084fc"],
      borderWidth: 0,
      hoverOffset: 15
    }]
  };
});

const snackChartData = computed(() => {
  if (!itResult.value?.pattern) return null;
  const { nightCount, snackCount } = itResult.value.pattern;
  if (nightCount === 0 && snackCount === 0) return null;
  return {
    labels: ["야식", "간식"],
    datasets: [{ 
      data: [nightCount, snackCount], 
      backgroundColor: ["#ffb347", "#6a8bff"],
      borderWidth: 0
    }],
  };
});

const dailyChartData = computed(() => {
  if (!itResult.value?.dailyChart) return null;
  const rows = itResult.value.dailyChart;
  const isSingleDay = rows.length === 1;

  return {
    labels: rows.map(r => r.date.substring(5)),
    datasets: [
      { 
        label: "칼로리(kcal)", 
        data: rows.map(r => r.totalKcal), 
        borderColor: "rgba(248, 113, 113, 1)", 
        backgroundColor: isSingleDay ? "rgba(248, 113, 113, 0.6)" : "rgba(248, 113, 113, 0.1)", 
        fill: true, 
        tension: 0.4,
        yAxisID: 'y'
      },
      { 
        label: "탄수화물(g)", 
        data: rows.map(r => r.totalCarb), 
        borderColor: "rgba(251, 191, 36, 1)", 
        backgroundColor: isSingleDay ? "rgba(251, 191, 36, 0.6)" : "rgba(251, 191, 36, 0.1)",
        tension: 0.4,
        yAxisID: 'y1'
      },
      { 
        label: "단백질(g)", 
        data: rows.map(r => r.totalProtein), 
        borderColor: "rgba(96, 165, 250, 1)", 
        backgroundColor: isSingleDay ? "rgba(96, 165, 250, 0.6)" : "rgba(96, 165, 250, 0.1)",
        tension: 0.4,
        yAxisID: 'y1' 
      },
      { 
        label: "지방(g)", 
        data: rows.map(r => r.totalFat), 
        borderColor: "rgba(192, 132, 252, 1)", 
        backgroundColor: isSingleDay ? "rgba(192, 132, 252, 0.6)" : "rgba(192, 132, 252, 0.1)",
        tension: 0.4,
        yAxisID: 'y1' 
      },
    ],
  };
});

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: { 
    legend: { 
      display: true, 
      position: 'bottom', 
      labels: { 
        boxWidth: 20, 
        padding: 30,
        font: { 
          weight: '900', 
          size: 14 
        } 
      } 
    },
    tooltip: { mode: 'index', intersect: false }
  },
  scales: { 
    y: { 
      type: 'linear',
      display: true,
      position: 'left',
      beginAtZero: true, 
      grid: { color: "rgba(0,0,0,0.03)" },
      title: { display: true, text: 'kcal', font: { weight: '900', size: 12 } }
    }, 
    y1: {
      type: 'linear',
      display: true,
      position: 'right',
      beginAtZero: true,
      grid: { drawOnChartArea: false },
      title: { display: true, text: 'grams (g)', font: { weight: '900', size: 12 } }
    },
    x: { 
      grid: { display: false },
      offset: true 
    } 
  }
};

const exportPdf = async () => {
  if (!itResult.value) return alert("분석을 먼저 진행해주세요.");
  try {
    const range = computeRange();
    let userId = Number(localStorage.getItem("userId")) || 1;
    const url = mode.value === "EAT" ? "/pdf/diet-analysis" : "/pdf/medi-report";
    const res = await api.get(url, { 
      params: { userId, startDate: range.startDate, endDate: range.endDate }, 
      responseType: "blob" 
    });
    const blob = new Blob([res.data], { type: "application/pdf" });
    const link = document.createElement("a");
    link.href = window.URL.createObjectURL(blob);
    link.download = `Report_${mode.value}_${range.startDate}.pdf`;
    link.click();
  } catch (e) {
    console.error("PDF 다운로드 실패:", e);
  }
};

onMounted(() => { generateCalendar(); loadRecommendations(); });
</script>

<template>
  <!-- 🌟 최상단 컨테이너에 포인트 컬러 바인딩 -->
  <div class="intro-container" :style="{ '--point-color': themeConfig.color, '--point-soft': themeConfig.soft }">
    <!-- 모드별 배경 블롭 분리 -->
    <div v-if="mode === 'EAT'" class="blob blob-eat"></div>
    <div v-if="mode === 'MEDI_EAT'" class="blob blob-medi"></div>

    <main class="content-wrapper" ref="analysisArea">
      <!-- 헤더 중앙 정렬 -->
      <header class="page-header-centered">
        <div class="badge-row">
          <div class="badge">
            <PieChart :size="12" class="icon-purple" />
            <span>AI NUTRITION ANALYSIS</span>
          </div>
        </div>
        
        <h1 class="hero-title italic">
          DIET<br />
          <!-- 🌟 포인트 컬러 적용 -->
          <span class="gradient-text">ANALYSIS.</span>
        </h1>
        <p class="hero-subtext">데이터로 보는 당신의 건강한 식습관 리포트</p>
      </header>

      <!-- 와이드 캘린더 -->
      <section class="wide-setting-section mt-28">
        <div class="glass-card panel-card-wide">
          <div class="setting-top-row">
             <h3 class="section-title italic">1. PERIOD & DATE SETTING</h3>
             <div class="period-selector-modern">
                <button v-for="p in [['day','1일'],['week','1주'],['month','1달'],['custom','직접 설정']]" 
                  :key="p[0]" :class="['period-btn', { active: period === p[0] }]" @click="setPeriod(p[0])">
                  {{ p[1] }}
                </button>
              </div>
          </div>

          <div class="calendar-module-wide mt-14">
            <div class="cal-nav-bar">
              <button class="nav-arrow" @click="moveMonth(-1)"><ChevronLeft :size="24" /></button>
              <span class="cal-display-title">{{ calendarMonth.getFullYear() }}년 {{ calendarMonth.getMonth() + 1 }}월</span>
              <button class="nav-arrow" @click="moveMonth(1)"><ChevronRight :size="24" /></button>
            </div>
            
            <div class="cal-body-wide mt-20">
              <div class="week-days-wide">
                <span v-for="w in ['SUN','MON','TUE','WED','THU','FRI','SAT']" :key="w">{{ w }}</span>
              </div>
              <div class="cal-grid-wide">
                <div v-for="d in days" :key="d.dateStr" class="day-cell-wide" @click="onClickDate(d.dateStr)">
                  <div :class="['day-inner-wide', { 
                    'selected': period === 'custom' ? (d.dateStr === startDate || d.dateStr === endDate) : d.dateStr === baseDate,
                    'in-range': period === 'custom' && d.dateStr > startDate && d.dateStr < endDate
                  }]">
                    <span class="day-num">{{ d.date.getDate() }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- PDF 다운로드 버튼 재배치 -->
          <div class="run-action-group-centered mt-14">
             <button class="run-btn-premium" @click="runAnalysis" :disabled="loading">
                <div class="btn-glow"></div>
                <Loader2 v-if="loading" class="animate-spin mr-3" :size="20" />
                <BarChart3 v-else class="mr-3" :size="20" />
                <span class="btn-text">{{ loading ? '분석 중...' : '분석 리포트 생성' }}</span>
             </button>
             <button v-if="itResult" class="pdf-download-btn-secondary" @click="exportPdf">
                <FileDown :size="20" />
                <span>PDF 저장</span>
             </button>
          </div>
        </div>
      </section>

      <!-- 분석 결과 섹션 -->
      <Transition name="fade-up">
        <div v-if="itResult" class="analysis-results-stack mt-28">
          
          <!-- 상단 대시보드 카드 -->
          <div class="dashboard-grid">
            <div class="dash-card glass-card">
              <div class="dash-info">
                <span class="dash-label">TOTAL CALORIES</span>
                <span class="dash-value">{{ itResult.periodSummary.totalKcal.toLocaleString() }} <small>kcal</small></span>
              </div>
              <Flame :size="48" class="dash-icon text-orange" />
            </div>
            <div class="dash-card glass-card">
              <div class="dash-info">
                <span class="dash-label">DAILY AVERAGE</span>
                <span class="dash-value">{{ itResult.periodSummary.avgKcal.toFixed(0) }} <small>kcal</small></span>
              </div>
              <Activity :size="48" class="dash-icon text-lime" />
            </div>
            <div class="dash-card glass-card score-highlight">
              <div class="dash-info">
                <span class="dash-label">DIET SCORE</span>
                <span class="dash-value">{{ itResult.score.value }}<small>/100</small></span>
              </div>
              <BarChart3 :size="48" class="dash-icon text-purple" />
            </div>
          </div>

          <!-- 탄단지 비율 및 야식 패턴 -->
          <div class="mid-analysis-row mt-20">
            <section class="macro-section glass-card flex-2">
              <h4 class="card-title italic">MACRO NUTRIENT BALANCE</h4>
              <div class="macro-visual-box-donut mt-20">
                <div class="donut-chart-container">
                  <Doughnut v-if="macroChartData" :data="macroChartData" :options="{ cutout: '70%', plugins: { legend: { display: false } } }" />
                </div>
              </div>
            </section>

            <section class="pattern-section glass-card flex-1">
              <h4 class="card-title italic">MEAL PATTERN</h4>
              <div class="pattern-visual mt-20">
                 <div class="chart-box-mini">
                    <Doughnut v-if="snackChartData" :data="snackChartData" :options="{ cutout: '82%', plugins: { legend: { display: false } } }" />
                    <div class="chart-center-val">{{ itResult.pattern.nightCount + itResult.pattern.snackCount }}</div>
                 </div>
              </div>
            </section>
          </div>

          <!-- 메딧모드 전용 약물 위험 섹션 -->
          <Transition name="fade">
            <section v-if="mode === 'MEDI_EAT' && medResult" class="medi-interaction-section glass-card mt-20">
              <div class="section-header-row">
                <h3 class="section-title italic">MEDICAL INTERACTION ANALYSIS</h3>
                <div class="interaction-summary-pills mt-20">
                  <div class="int-pill danger">위험 {{ medResult.dashboard.dangerCount }}</div>
                  <div class="int-pill warning">주의 {{ medResult.dashboard.warningCount }}</div>
                  <div class="int-pill safe">안전 {{ medResult.dashboard.safeCount }}</div>
                </div>
              </div>
              <div class="interaction-content mt-20">
                <div class="risk-info-box">
                  <div class="risk-icon-box"><ShieldAlert :size="32" class="text-orange" /></div>
                  <div class="risk-text-box" mt-20>
                    <h4>약물-식단 상호작용 분석 결과</h4>
                    <p>현재 복용 중인 약물과 오늘의 식단 성분을 대조한 결과입니다. 질환 관리에 주의가 필요한 성분을 확인하세요.</p>
                  </div>
                </div>
              </div>
            </section>
          </Transition>

          <!-- 추이 차트 -->
          <section class="trend-section glass-card mt-28">
             <h4 class="card-title italic">NUTRITION TREND</h4>
             <div class="trend-chart-box mt-20">
                <Bar v-if="dailyChartData && dailyChartData.labels.length === 1" :data="dailyChartData" :options="chartOptions" />
                <Line v-else-if="dailyChartData" :data="dailyChartData" :options="chartOptions" />
                <div v-else class="empty-chart">데이터가 없습니다.</div>
             </div>
          </section>

          <!-- 음식 추천 섹션 -->
          <section class="recommendation-section-renew mt-28" :class="mode">
            <div class="rec-glass-container glass-card">
              <div class="rec-side-nav">
                <div class="rec-nav-info">
                  <h3 class="section-title italic">2. FOOD<br/>INSIGHTS</h3>
                  <p class="rec-sub-desc">분석 데이터를 기반으로 당신만을 위해 제안하는 맞춤 식단 정보입니다.</p>
                </div>
                <nav class="rec-tab-list mt-20">
                  <button 
                    v-for="t in [['lowCalorie','저칼로리'],['substitutes','대체 음식'],['highProteinLowFat','고단백'],['lowFat','저지방']]"
                    :key="t[0]" 
                    :class="['rec-sidebar-btn', { active: recommendTab === t[0] }]" 
                    @click="recommendTab = t[0]"
                  >
                    <div class="active-indicator"></div>
                    <span class="btn-label">{{ t[1] }}</span>
                    <ChevronRight :size="16" class="arrow" />
                  </button>
                </nav>
              </div>

              <div class="rec-main-content">
                <div v-if="recommendLoading" class="loading-state-full">
                  <Loader2 class="animate-spin" :size="32" :class="mode === 'MEDI_EAT' ? 'text-sky' : 'text-lime'" />
                  <p class="mt-4">분석 결과를 토대로 최적의 음식을 찾는 중...</p>
                </div>
                
                <div v-else class="rec-view-area">
                  <div class="rec-view-header">
                    <h4 class="view-category-name italic" 
                        :style="{ color: mode === 'EAT' ? '#a3e635' : '#38bdf8' }">
                      {{ recommendTab.toUpperCase() }} RECOMMENDATIONS
                    </h4>
                  </div>
                  
                  <div class="rec-scroll-grid mt-20 custom-scrollbar">
                    <div v-for="f in recommendData[recommendTab]" :key="f.foodId" class="rec-premium-card">
                      <div class="food-icon-box"><Utensils :size="18" /></div>
                      <div class="food-info-box">
                        <div class="food-name-row">
                          <span class="f-name">{{ f.name }}</span>
                          <span class="f-kcal-pill ml-6">{{ f.calorie }} <small>kcal</small></span>
                        </div>
                        <div class="f-macros mt-4">
                          <div class="macro-tag"><span>C</span> {{ f.carb }}g</div>
                          <div class="macro-tag"><span>P</span> {{ f.protein }}g</div>
                          <div class="macro-tag"><span>F</span> {{ f.fat }}g</div>
                        </div>
                      </div>
                    </div>
                    
                    <div v-if="!recommendData[recommendTab].length" class="rec-empty-state">
                      <Heart :size="48" class="opacity-10" />
                      <p>기록이 쌓이면 맞춤 음식을 추천해 드릴게요.</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </section>
        </div>

        <div v-else class="empty-results-box mt-28">
          <div class="empty-visual">
             <BarChart3 :size="64" class="opacity-10" />
          </div>
          <h3>분석 리포트가 아직 준비되지 않았습니다.</h3>
          <p>상단의 기간을 설정하고 분석 버튼을 눌러 리포트를 생성하세요.</p>
        </div>
      </Transition>

      <footer class="safety-footer mt-48">
        ※ 본 분석 리포트는 참고용이며, 전문적인 의료 진단이나 치료를 대신할 수 없습니다.
      </footer>
    </main>
  </div>
</template>

<style scoped>
/* 인트로 레이아웃 기반 공통 */
.intro-container {
  background-color: #f8f9fc;
  color: #1a1a1a;
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
  padding: 140px 24px 100px;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.blob {
  position: fixed; border-radius: 50%; filter: blur(160px); z-index: 0; opacity: 0.15; pointer-events: none;
}
.blob-eat { top: -10%; left: -10%; width: 65vw; height: 65vw; background: radial-gradient(circle, #a3e635 0%, transparent 70%); }
.blob-medi { top: -10%; left: -10%; width: 65vw; height: 65vw; background: radial-gradient(circle, #38bdf8 0%, transparent 70%); }

.content-wrapper { width: 100%; max-width: 1200px; position: relative; z-index: 10; }

.glass-card {
  background: rgba(255, 255, 255, 0.45);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 50px;
  box-shadow: 0 40px 100px rgba(0, 0, 0, 0.02);
}

/* 헤더 중앙 정렬 */
.page-header-centered { text-align: center; margin-bottom: 100px; }
.badge-row { display: flex; justify-content: center; margin-bottom: 28px; }
.badge {
  display: inline-flex; align-items: center; gap: 8px; padding: 8px 16px;
  background: white; border-radius: 999px; font-size: 0.7rem; font-weight: 850; color: #7c4dff; border: 1.5px solid #f1f5f9;
}
.hero-title { font-size: 4.2rem; line-height: 0.9; font-weight: 950; letter-spacing: -0.05em; margin-bottom: 28px; }

/* 🌟 포인트 컬러 동적 적용 */
.gradient-text { 
  background: linear-gradient(to right, #1a1a1a, var(--point-color)); 
  -webkit-background-clip: text; 
  background-clip: text; 
  color: transparent; 
  transition: all 0.5s ease;
}

.hero-subtext { font-size: 1.25rem; opacity: 0.6; font-weight: 600; }

/* 캘린더 패널 */
.panel-card-wide { padding: 60px; }
.setting-top-row { display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid rgba(0,0,0,0.05); padding-bottom: 40px; }
.section-title { font-size: 1.2rem; font-weight: 950; color: #1a1a1a; letter-spacing: 0.05em; }

.period-selector-modern { display: flex; background: #f1f5f9; padding: 6px; border-radius: 20px; gap: 8px; }
.period-btn {
  padding: 12px 28px; border: none; background: transparent; border-radius: 16px;
  font-size: 0.85rem; font-weight: 850; color: #94a3b8; cursor: pointer; transition: 0.3s;
}
.period-btn.active { background: white; color: #1a1a1a; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }

.calendar-module-wide { background: rgba(255,255,255,0.4); border-radius: 40px; padding: 50px; border: 1px solid rgba(255,255,255,0.6); }
.cal-nav-bar { display: flex; justify-content: space-between; align-items: center; }
.cal-display-title { font-size: 1.8rem; font-weight: 950; color: #1a1a1a; }
.nav-arrow {
  width: 52px; height: 52px; border-radius: 50%; border: none; background: white; 
  color: #1a1a1a; cursor: pointer; display: flex; align-items: center; justify-content: center;
  box-shadow: 0 6px 20px rgba(0,0,0,0.06); transition: 0.3s;
}
/* 🌟 화살표 호버 컬러 동적 적용 */
.nav-arrow:hover { background: var(--point-color); transform: scale(1.1); }

.week-days-wide { display: grid; grid-template-columns: repeat(7, 1fr); text-align: center; margin-bottom: 30px; }
.week-days-wide span { font-size: 0.85rem; font-weight: 900; color: #cbd5e1; letter-spacing: 0.15em; }

.cal-grid-wide { display: grid; grid-template-columns: repeat(7, 1fr); gap: 12px; }
.day-cell-wide { aspect-ratio: 1.8; display: flex; align-items: center; justify-content: center; cursor: pointer; }
.day-inner-wide {
  width: 85%; height: 100%; display: flex; align-items: center; justify-content: center;
  border-radius: 24px; transition: 0.3s cubic-bezier(0.23, 1, 0.32, 1);
  background: rgba(0,0,0,0.02);
}
.day-num { font-size: 1.15rem; font-weight: 850; color: #64748b; }

.day-inner-wide.selected { background: #1a1a1a; transform: scale(1.05); box-shadow: 0 15px 35px rgba(0,0,0,0.15); }
.day-inner-wide.selected .day-num { color: white; }
/* 🌟 범위 선택 컬러 동적 적용 */
.day-inner-wide.in-range { background: var(--point-soft); }

/* 분석 및 PDF 버튼 */
.run-action-group-centered { display: flex; justify-content: center; gap: 16px; align-items: center; }
.run-btn-premium {
  position: relative; overflow: hidden;
  padding: 24px 64px; border-radius: 999px; border: none; background: #1a1a1a;
  color: #ffffff !important; font-weight: 950; font-size: 1.2rem; cursor: pointer; transition: 0.4s;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.15);
}
.run-btn-premium .btn-text { color: #ffffff !important; }
/* 🌟 분석 실행 버튼 호버 컬러 동적 적용 */
.run-btn-premium:hover:not(:disabled) { background: var(--point-color); color: #1a1a1a !important; transform: translateY(-5px); box-shadow: 0 35px 70px var(--point-soft); }
.run-btn-premium:hover .btn-text { color: #1a1a1a !important; }

.pdf-download-btn-secondary {
  display: flex; align-items: center; gap: 10px; padding: 22px 32px; border-radius: 999px;
  background: white; border: 2px solid #1a1a1a; color: #1a1a1a; font-weight: 900;
  font-size: 1rem; cursor: pointer; transition: 0.3s;
}
.pdf-download-btn-secondary:hover { background: #f1f5f9; transform: translateY(-3px); }

/* 대시보드 카드 */
.dashboard-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 30px; }
.dash-card { padding: 48px; display: flex; justify-content: space-between; align-items: center; }
.dash-label { font-size: 0.8rem; font-weight: 900; color: #94a3b8; display: block; margin-bottom: 12px; letter-spacing: 0.15em; }
.dash-value { font-size: 3.2rem; font-weight: 950; line-height: 1; color: #1a1a1a; }
.dash-value small { font-size: 1.2rem; opacity: 0.5; font-weight: 800; margin-left: 6px; }
.dash-icon { opacity: 0.15; transform: rotate(-10deg); }

.text-orange { color: #f97316; }
.text-lime { color: #a3e635; }
.text-purple { color: #a855f7; }

.mid-analysis-row { display: flex; gap: 30px; }
.flex-2 { flex: 2; }
.flex-1 { flex: 1; }

.macro-section { padding: 48px; display: flex; flex-direction: column; }
.card-title { font-size: 1.05rem; font-weight: 950; color: #cbd5e1; letter-spacing: 0.12em; }

.macro-visual-box-donut { display: flex; align-items: center; justify-content: center; }
.donut-chart-container { width: 220px; height: 220px; }
.macro-legend-row-vertical { display: flex; flex-direction: column; gap: 16px; }
.legend-item-v { display: flex; align-items: center; gap: 12px; font-size: 1rem; font-weight: 800; color: #475569; }
.dot { width: 14px; height: 14px; border-radius: 50%; }
.bg-carb { background: #fbbf24; }
.bg-protein { background: #60a5fa; }
.bg-fat { background: #c084fc; }

/* 메딧모드 전용 섹션 */
.medi-interaction-section { padding: 48px; }
.interaction-summary-pills { display: flex; gap: 10px; }
.int-pill { padding: 6px 16px; border-radius: 999px; font-size: 0.8rem; font-weight: 900; }
.int-pill.danger { background: #fff1f2; color: #f43f5e; }
.int-pill.warning { background: #fffbeb; color: #f59e0b; }
.int-pill.safe { background: #f0fdf4; color: #22c55e; }

.risk-info-box { display: flex; gap: 24px; padding: 24px; background: #f8fafc; border-radius: 24px; align-items: center; }
.risk-text-box h4 { font-size: 1.1rem; font-weight: 950; color: #1a1a1a; margin-bottom: 4px; }
.risk-text-box p { font-size: 0.95rem; font-weight: 600; color: #64748b; line-height: 1.6; }

.pattern-section { padding: 48px; }
.pattern-visual { display: flex; flex-direction: column; align-items: center; }
.chart-box-mini { width: 180px; height: 180px; position: relative; }
.chart-center-val { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); font-size: 2.8rem; font-weight: 950; color: #1a1a1a; }

/* 추이 차트 */
.trend-section { padding: 48px; }
.trend-chart-box { height: 450px; position: relative; }

/* 음식 추천 섹션 */
.rec-glass-container { display: grid; grid-template-columns: 320px 1fr; overflow: hidden; border-radius: 60px; padding: 0 !important; }

.rec-side-nav { background: rgba(0,0,0,0.03); padding: 60px 40px; border-right: 1px solid rgba(0,0,0,0.05); }
.rec-nav-info .section-title { font-size: 1.6rem; line-height: 1.1; margin-bottom: 16px; }
.rec-sub-desc { font-size: 0.9rem; color: #94a3b8; font-weight: 600; line-height: 1.6; }

.rec-tab-list { display: flex; flex-direction: column; gap: 16px; } 
.rec-sidebar-btn {
  display: flex; align-items: center; gap: 16px; padding: 20px 24px; border-radius: 24px;
  border: 1px solid transparent; background: transparent; cursor: pointer; transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
  width: 100%; text-align: left;
}
.rec-sidebar-btn:hover { background: rgba(255,255,255,0.7); }

/* 🌟 추천 탭 활성화 컬러 동적 적용 */
.rec-sidebar-btn.active { 
  background: var(--point-color); 
  border-color: var(--point-color); 
  box-shadow: 0 10px 30px var(--point-soft); 
}

.rec-sidebar-btn.active .btn-label, .rec-sidebar-btn.active .arrow { color: #1a1a1a !important; font-weight: 950; }
.rec-sidebar-btn .btn-label { font-size: 1rem; font-weight: 850; color: #94a3b8; flex: 1; }
.rec-sidebar-btn .arrow { color: #cbd5e1; opacity: 0; transition: 0.3s; }
.rec-sidebar-btn.active .arrow { opacity: 1; transform: translateX(5px); }

.rec-main-content { padding: 60px; background: rgba(255,255,255,0.25); flex: 1; }
.view-category-name { font-size: 1.1rem; font-weight: 950; letter-spacing: 0.2em; }

.rec-scroll-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px; max-height: 550px; overflow-y: auto; padding-right: 15px; }
.rec-premium-card {
  background: white; border-radius: 32px; padding: 28px; display: flex; gap: 20px;
  border: 1.5px solid #f1f5f9; transition: 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}
.rec-premium-card:hover { transform: translateY(-8px) scale(1.02); box-shadow: 0 20px 40px rgba(0,0,0,0.04); }

.food-icon-box { width: 52px; height: 52px; border-radius: 18px; background: #f8fafc; display: flex; align-items: center; justify-content: center; color: #cbd5e1; flex-shrink: 0; }
.food-info-box { flex: 1; display: flex; flex-direction: column; justify-content: center; }

/* 음식 명과 칼로리 사이 확실한 여백 확보 */
.food-name-row { display: flex; justify-content: space-between; align-items: center; gap: 24px; }
.f-name { font-size: 1.15rem; font-weight: 950; color: #1a1a1a; word-break: keep-all; }
.f-kcal-pill { font-size: 1.05rem; font-weight: 950; color: #f87171; white-space: nowrap; padding: 4px 10px; background: #fff1f2; border-radius: 8px; }

.f-macros { display: flex; gap: 12px; }
.macro-tag { font-size: 0.75rem; font-weight: 900; color: #94a3b8; background: #f1f5f9; padding: 5px 12px; border-radius: 12px; }

.safety-footer { text-align: center; font-size: 1rem; color: #cbd5e1; font-weight: 750; line-height: 1.7; }
.italic { font-style: italic; }

.mt-20 { margin-top: 3rem; }
.mt-28 { margin-top: 8rem; }
.mt-48 { margin-top: 15rem; }

.fade-up-enter-active { animation: fadeIn 1s ease-out forwards; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(50px); } to { opacity: 1; transform: translateY(0); } }

.custom-scrollbar::-webkit-scrollbar { width: 6px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }

@media (max-width: 1000px) {
  .dashboard-grid, .mid-analysis-row, .rec-glass-container { grid-template-columns: 1fr; }
}
</style>