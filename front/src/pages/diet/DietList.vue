<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import dietApi from '@/api/diet'
import { 
  Sparkles, 
  ChevronLeft, 
  ChevronRight, 
  Plus, 
  Calendar as CalIcon,
  Utensils,
  Search,
  Edit3, 
  Trash2, 
  PieChart, 
  Activity, 
  ArrowRight, 
  Sunrise, 
  Sun, 
  Moon, 
  MoonStar, 
  Coffee, 
  Clock
} from 'lucide-vue-next'

const router = useRouter()
const route = useRoute()

/* =======================
   🌟 테마 설정 (MyActivity 스타일 이식)
======================= */
const currentMode = computed(() => {
  const m = (route.query.mode || 'EAT').toString().toUpperCase()
  return m === 'MEDI_EAT' ? 'MEDI_EAT' : 'EAT'
})

const themeColors = {
  EAT: {
    primary: "#a3e635", // 연두색
    soft: "rgba(163, 230, 53, 0.15)",
    glow: "rgba(163, 230, 53, 0.25)"
  },
  MEDI_EAT: {
    primary: "#38bdf8", // 하늘색
    soft: "rgba(56, 189, 248, 0.15)",
    glow: "rgba(56, 189, 248, 0.25)"
  }
};

const activeTheme = computed(() => themeColors[currentMode.value]);

/* =======================
   기존 로직 유지 (건드리지 않음)
======================= */
const MEAL_CONFIG = {
  "아침": { rank: 1, color: "#3b82f6", bg: "rgba(59, 130, 246, 0.1)", icon: Sunrise },
  "점심": { rank: 2, color: "#22c55e", bg: "rgba(34, 197, 94, 0.1)", icon: Sun },
  "저녁": { rank: 3, color: "#ea580c", bg: "rgba(234, 88, 12, 0.1)", icon: Moon },
  "야식": { rank: 4, color: "#6366f1", bg: "rgba(99, 102, 241, 0.1)", icon: MoonStar },
  "간식": { rank: 5, color: "#ec4899", bg: "rgba(236, 72, 153, 0.1)", icon: Coffee },
  "기타": { rank: 6, color: "#94a3b8", bg: "rgba(148, 163, 184, 0.1)", icon: Utensils }
};

const normMeal = (s) => (s ?? "").toString().trim();
const getMealConfig = (m) => MEAL_CONFIG[normMeal(m)] || MEAL_CONFIG["기타"];

const formatNum = (num) => {
  if (num === null || num === undefined || isNaN(num)) return 0;
  return parseFloat(Number(num).toFixed(2));
};

const sortMeals = (arr = []) =>
  [...arr]
    .map(normMeal)
    .filter(Boolean)
    .filter((v, i, a) => a.indexOf(v) === i)
    .sort((a, b) => getMealConfig(a).rank - getMealConfig(b).rank);

const selectedDate = ref(new Date().toISOString().substring(0, 10))
const dietList = ref([])

const groupedDietList = computed(() => {
  const groups = {};
  dietList.value.forEach(item => {
    const time = item.mealTime || '기타';
    if (!groups[time]) {
      groups[time] = {
        mealTime: time,
        totalCalorie: 0,
        totalCarb: 0,
        totalProtein: 0,
        totalFat: 0,
        records: []
      };
    }
    groups[time].totalCalorie += (item.totalCalorie || 0);
    groups[time].totalCarb += (item.totalCarb || 0);
    groups[time].totalProtein += (item.totalProtein || 0);
    groups[time].totalFat += (item.totalFat || 0);
    groups[time].records.push(item);
  });

  return Object.values(groups).map(g => ({
    ...g,
    totalCalorie: formatNum(g.totalCalorie),
    totalCarb: formatNum(g.totalCarb),
    totalProtein: formatNum(g.totalProtein),
    totalFat: formatNum(g.totalFat)
  })).sort((a, b) => getMealConfig(a.mealTime).rank - getMealConfig(b.mealTime).rank);
});

const loadDiet = async () => {
  try {
    const res = await dietApi.getList(selectedDate.value)
    dietList.value = res.data || []
  } catch (e) {
    console.error('식단 조회 오류:', e)
  }
}

const deleteDiet = async (dietId) => {
  if (!confirm('식단 기록을 삭제할까요?')) return
  try {
    await dietApi.deleteDiet(dietId)
    loadDiet()
  } catch (e) {
    console.error('삭제 오류:', e)
  }
}

const goToAnalysis = () => {
  router.push({
    name: 'diet-analysis',
    query: { mode: currentMode.value }
  })
}

const year = ref(new Date().getFullYear())
const month = ref(new Date().getMonth() + 1)
const weeks = ref([])
const mealMap = ref({})

const loadCalendar = async () => {
  const monthStr = `${year.value}-${String(month.value).padStart(2, '0')}`
  try {
    const res = await dietApi.getCalendarLogs(monthStr)
    mealMap.value = {}
    res.data.forEach((row) => {
      mealMap.value[row.date] = sortMeals(row.meals);
    });
    buildCalendar()
  } catch (e) {
    buildCalendar()
  }
}

const buildCalendar = () => {
  const y = year.value;
  const m = month.value;
  const first = new Date(y, m - 1, 1).getDay();
  const last = new Date(y, m, 0).getDate();
  const temp = [];
  let week = new Array(7).fill(null);
  let col = first;

  for (let d = 1; d <= last; d++) {
    const dateStr = `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')}`;
    week[col] = { day: d, full: dateStr, meals: mealMap.value[dateStr] || [] };
    if (col === 6) { temp.push(week); week = new Array(7).fill(null); col = 0; } else { col++; }
  }
  if (week.some(v => v !== null)) temp.push(week);
  weeks.value = temp;
}

const moveMonth = (offset) => {
  const d = new Date(year.value, month.value - 1 + offset, 1);
  year.value = d.getFullYear();
  month.value = d.getMonth() + 1;
  loadCalendar();
}

onMounted(() => { loadCalendar(); loadDiet(); });
</script>

<template>
  <!-- 🌟 테마 컬러 바인딩 -->
  <div class="intro-container" :style="{ '--point-color': activeTheme.primary, '--point-soft': activeTheme.soft }">
    
    <!-- 🌟 배경 장식 (MyActivity와 동일한 유동적 블롭) -->
    <TransitionGroup name="fade-blob">
      <div v-if="currentMode === 'EAT'" key="eat-blob" class="blob blob-eat"></div>
      <div v-if="currentMode === 'MEDI_EAT'" key="medi-blob" class="blob blob-medi"></div>
    </TransitionGroup>

    <main class="content-wrapper">
      <header class="page-header">
        <div class="badge">
          <Sparkles :size="12" class="icon-purple" />
          <span>AI DIET IDENTIFICATION</span>
        </div>
        <h1 class="hero-title italic">
          DIET<br />
          <span class="gradient-text">PLANNER.</span>
        </h1>
        <p class="hero-subtext">달력에서 날짜를 선택하여 당신의 소중한 식사 기록을 남기세요.</p>
      </header>

      <div class="main-layout-grid">
        <!-- 메인 캘린더 영역 -->
        <div class="main-content-section">
          <div class="calendar-card glass-card">
            <header class="section-header-row">
              <h3 class="section-title italic">NUTRITION CALENDAR</h3>
              <div class="cal-nav-group">
                <button class="nav-btn" @click="moveMonth(-1)"><ChevronLeft :size="20" /></button>
                <div class="current-month-label">{{ year }}.{{ String(month).padStart(2, '0') }}</div>
                <button class="nav-btn" @click="moveMonth(1)"><ChevronRight :size="20" /></button>
              </div>
            </header>

            <div class="calendar-wrapper mt-8">
              <div class="week-names">
                <div v-for="d in ['SUN','MON','TUE','WED','THU','FRI','SAT']" :key="d" class="w-name">{{ d }}</div>
              </div>
              <div class="calendar-body">
                <div v-for="(w, wi) in weeks" :key="wi" class="week-row">
                  <div
                    v-for="(d, di) in w"
                    :key="di"
                    class="day-cell"
                    :class="{ 'is-active': d && d.full === selectedDate, 'empty': !d }"
                    @click="d && (selectedDate = d.full, loadDiet())"
                  >
                    <div v-if="d" class="day-content">
                      <span class="day-num">{{ d.day }}</span>
                      <div class="meal-indicator-dots">
                        <span 
                          v-for="m in d.meals" 
                          :key="m" 
                          class="meal-dot" 
                          :style="{ backgroundColor: getMealConfig(m).color }"
                        ></span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 중앙 분석 레포트 버튼 (🌟 완전한 중앙 정렬) -->
            <div class="calendar-footer mt-24">
              <button class="analysis-premium-btn" @click="goToAnalysis">
                <div class="btn-glow-layer"></div>
                <PieChart :size="22" class="btn-icon" />
                <span class="btn-text">영양 분석 레포트 보기</span>
                <ArrowRight :size="20" class="btn-arrow" />
              </button>
            </div>
          </div>
        </div>

        <!-- 사이드 식단 리스트 영역 -->
        <aside class="side-panel-section">
          <div class="side-card glass-card list-card">
            <div class="side-header">
              <h3 class="side-title italic">TODAY'S LOG</h3>
              <div class="selected-date-badge">{{ selectedDate }}</div>
            </div>

            <button
              class="add-meal-btn w-full mb-10"
              @click="router.push({ path: '/diet/form', query: { date: selectedDate } })"
            >
              <Plus :size="20" />
              식단 기록 추가
            </button>

            <!-- 그룹 리스트 영역 -->
            <div v-if="groupedDietList.length > 0" class="side-log-container body-scroll">
              <div 
                v-for="group in groupedDietList" 
                :key="group.mealTime" 
                class="meal-group-item"
              >
                <div class="meal-group-header">
                  <div class="meal-type" :style="{ color: getMealConfig(group.mealTime).color }">
                    <component :is="getMealConfig(group.mealTime).icon" :size="18" class="mr-4" />
                    <span class="type-text">{{ group.mealTime }}</span>
                  </div>
                  <div class="group-kcal">{{ group.totalCalorie }} kcal</div>
                </div>

                <div class="menu-pill-stack mt-5 gap-3">
                  <div 
                    v-for="record in group.records" 
                    :key="record.dietId" 
                    class="menu-mini-card"
                    @click="router.push({ name: 'diet-detail', params: { id: record.dietId } })"
                  >
                    <span class="menu-name">{{ record.title || '식단 정보' }}</span>
                    <div class="menu-actions">
                      <button class="mini-icon" @click.stop="router.push({ name: 'diet-edit', params: { id: record.dietId } })"><Edit3 :size="14" /></button>
                      <button class="mini-icon danger" @click.stop="deleteDiet(record.dietId)"><Trash2 :size="14" /></button>
                    </div>
                  </div>
                </div>

                <div class="meal-group-footer mt-5">
                  <div class="m-tag"><span class="label">C</span> {{ group.totalCarb }}g</div>
                  <div class="m-tag"><span class="label">P</span> {{ group.totalProtein }}g</div>
                  <div class="m-tag"><span class="label">F</span> {{ group.totalFat }}g</div>
                </div>
              </div>
            </div>

            <div v-else class="empty-side-state">
              <Utensils :size="32" class="opacity-20 mb-4" />
              <p>기록된 식단이 없습니다.</p>
              <span class="sub">건강한 하루를 기록해보세요!</span>
            </div>
          </div>

          <div class="guide-box">
            <div class="guide-head">
              <Clock :size="16" class="icon-lime" />
              <span>RECORD GUIDE</span>
            </div>
            <ul>
              <li>식사 직후 기록하면 더 정확한 분석이 가능합니다.</li>
              <li>상세 분석 레포트에서 주간 영양 균형을 확인하세요.</li>
            </ul>
          </div>
        </aside>
      </div>

      <!-- 최하단 안내 문구 -->
      <footer class="safety-footer mt-48">
        ※ 본 정보는 참고용이며, 정확한 식이 요법은 전문의의 진단이 필요합니다.
      </footer>
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
  padding: 100px 24px 80px;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

/* 🌟 배경 Blobs (MyActivity 스타일로 수정) */
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

/* 블롭 전환 애니메이션 */
.fade-blob-enter-active, .fade-blob-leave-active { transition: opacity 0.8s ease; }
.fade-blob-enter-from, .fade-blob-leave-to { opacity: 0; }

.content-wrapper {
  width: 100%;
  max-width: 1240px;
  position: relative;
  z-index: 10;
}

.page-header { text-align: center; margin-bottom: 60px; }
.badge {
  display: inline-flex; align-items: center; gap: 6px; padding: 6px 12px;
  background: white; border-radius: 999px; 
  font-size: 0.65rem; font-weight: 850; color: #7c4dff; border: 1.5px solid #f1f5f9;
  margin-bottom: 20px;
}
.hero-title { font-size: 3.5rem; line-height: 0.95; font-weight: 950; letter-spacing: -0.05em; margin-bottom: 20px; }

/* 🌟 그라데이션 텍스트 (포인트 컬러 연동) */
.gradient-text {
  background: linear-gradient(to bottom right, #1a1a1a, var(--point-color));
  -webkit-background-clip: text; background-clip: text; color: transparent;
  transition: all 0.5s ease;
}
.hero-subtext { font-size: 1.1rem; opacity: 0.6; font-weight: 500; }

.main-layout-grid {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 30px;
}

.glass-card {
  background: rgba(255, 255, 255, 0.45);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 40px;
  box-shadow: 0 40px 100px rgba(0, 0, 0, 0.02);
  transition: all 0.3s ease;
}

.main-content-section .calendar-card { padding: 50px; }

.section-header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
.section-title { font-size: 1.1rem; font-weight: 900; color: #1a1a1a; letter-spacing: 0.05em; }

.cal-nav-group { display: flex; align-items: center; gap: 16px; }
.current-month-label { font-size: 1.6rem; font-weight: 950; color: #1a1a1a; }
.nav-btn {
  width: 44px; height: 44px; border-radius: 50%; border: 1.5px solid #f1f5f9;
  background: white; cursor: pointer; display: flex; align-items: center; justify-content: center;
  transition: 0.3s;
}
/* 🌟 호버 시 포인트 컬러 연동 */
.nav-btn:hover { border-color: var(--point-color); color: var(--point-color); }

.week-names { display: grid; grid-template-columns: repeat(7, 1fr); text-align: center; margin-bottom: 15px; }
.w-name { font-size: 0.75rem; font-weight: 900; color: #cbd5e1; }

.calendar-body { display: flex; flex-direction: column; gap: 8px; }
.week-row { display: grid; grid-template-columns: repeat(7, 1fr); gap: 8px; }

.day-cell {
  aspect-ratio: 1.1; border-radius: 20px; cursor: pointer; transition: 0.3s;
  background: rgba(248, 250, 252, 0.5); display: flex; align-items: center; justify-content: center;
  position: relative; border: 2px solid transparent;
}
.day-cell:hover:not(.empty) { background: white; transform: translateY(-3px); border-color: var(--point-soft); }

/* 🌟 활성 날짜 컬러 연동 */
.day-cell.is-active {
  background: white !important;
  border-color: var(--point-color) !important;
  box-shadow: 0 10px 30px var(--point-soft);
}
.day-cell.is-active .day-num { color: #1a1a1a; font-weight: 950; }
.day-cell.empty { background: transparent; cursor: default; }

.day-content { display: flex; flex-direction: column; align-items: center; gap: 6px; }
.day-num { font-size: 1.05rem; font-weight: 850; color: #334155; }

.meal-indicator-dots { display: flex; gap: 3px; flex-wrap: wrap; justify-content: center; }
.meal-dot { width: 5px; height: 5px; border-radius: 50%; }

/* 🌟 분석 레포트 버튼 (완벽한 중앙 정렬) */
.calendar-footer { 
  display: flex; 
  justify-content: center; 
  width: 100%; 
}
.mt-24 { margin-top: 3rem; }

.analysis-premium-btn {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  padding: 22px 64px;
  background: linear-gradient(135deg, #1a1a1a 0%, #2a2a2a 100%);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 999px;
  cursor: pointer;
  transition: all 0.5s cubic-bezier(0.23, 1, 0.32, 1);
  overflow: hidden;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  color: #ffffff !important; 
}

.btn-text, .btn-icon, .btn-arrow { position: relative; z-index: 2; color: #ffffff !important; transition: 0.4s; }

/* 🌟 버튼 호버 포인트 컬러 연동 */
.analysis-premium-btn:hover {
  transform: translateY(-6px);
  background: var(--point-color);
  box-shadow: 0 30px 60px var(--point-soft);
  border-color: transparent;
  color: #000000 !important; 
}
.analysis-premium-btn:hover .btn-text,
.analysis-premium-btn:hover .btn-icon,
.analysis-premium-btn:hover .btn-arrow {
  color: #000000 !important;
}
.btn-glow-layer { position: absolute; inset: 0; background: radial-gradient(circle at center, rgba(255,255,255, 0.3) 0%, transparent 80%); opacity: 0; transition: 0.5s; z-index: 1; }
.analysis-premium-btn:hover .btn-glow-layer { opacity: 1; }
.analysis-premium-btn:hover .btn-arrow { transform: translateX(8px); }

/* 🌟 사이드 패널 섹션 (간격 조정: gap 48px) */
.side-panel-section { 
  display: flex; 
  flex-direction: column; 
  gap: 48px; 
}
.side-card { padding: 40px; display: flex; flex-direction: column; max-height: 700px; }
.side-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 35px; }
.side-title { font-size: 0.9rem; font-weight: 900; color: #94a3b8; letter-spacing: 0.1em; }

/* 🌟 선택 날짜 배지 컬러 연동 */
.selected-date-badge { font-size: 0.8rem; font-weight: 900; color: var(--point-color); background: var(--point-soft); padding: 5px 12px; border-radius: 10px; }

.add-meal-btn {
  padding: 18px; background: rgba(0, 0, 0, 0.05); color: #1a1a1a; border: 1.5px dashed #cbd5e1;
  border-radius: 18px; font-weight: 850; font-size: 1rem; cursor: pointer; transition: 0.3s;
  display: flex; align-items: center; justify-content: center; gap: 10px;
}
.add-meal-btn:hover { background: white; border-color: var(--point-color); color: var(--point-color); }

.side-log-container { flex: 1; overflow-y: auto; display: flex; flex-direction: column; gap: 40px; padding-right: 12px; }
.side-log-container::-webkit-scrollbar { width: 4px; }
.side-log-container::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }

.meal-group-item { position: relative; padding-left: 20px; border-left: 2px solid #f1f5f9; }
.meal-group-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }

.meal-type { font-size: 1.05rem; font-weight: 950; display: flex; align-items: center; }
.mr-4 { margin-right: 1rem; }
.group-kcal { font-size: 1rem; font-weight: 850; color: #1a1a1a; opacity: 0.9; }

.menu-mini-card {
  background: white; padding: 16px 20px; border-radius: 18px;
  display: flex; justify-content: space-between; align-items: center;
  box-shadow: 0 4px 15px rgba(0,0,0,0.03); cursor: pointer; transition: 0.3s;
  border: 1.5px solid #f8fafc;
}
/* 🌟 메뉴 카드 호버 보더 컬러 연동 */
.menu-mini-card:hover { transform: translateX(8px); border-color: var(--point-color); }
.menu-name { font-size: 1rem; font-weight: 850; color: #475569; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.menu-actions { display: flex; gap: 8px; opacity: 0.2; transition: 0.2s; }
.menu-mini-card:hover .menu-actions { opacity: 1; }
.mini-icon { width: 32px; height: 32px; border-radius: 8px; border: none; background: #f8fafc; color: #94a3b8; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: 0.2s; }
.mini-icon:hover { background: #f1f5f9; color: #1a1a1a; transform: scale(1.1); }
.mini-icon.danger:hover { color: #f43f5e; background: #fff1f2; }

.meal-group-footer { display: flex; gap: 16px; }
.m-tag { font-size: 0.95rem; font-weight: 900; color: #334155; background: #f1f5f9; padding: 6px 14px; border-radius: 10px; }
.m-tag .label { color: #94a3b8; font-size: 0.75rem; margin-right: 6px; }

.guide-box { padding: 28px; background: #f8fafc; border-radius: 35px; border: 1px solid #f1f5f9; }
.guide-head { display: flex; align-items: center; gap: 10px; font-size: 0.8rem; font-weight: 900; color: #1a1a1a; margin-bottom: 20px; }
.guide-box li { font-size: 0.8rem; color: #64748b; line-height: 1.8; margin-bottom: 10px; font-weight: 600; }

.empty-side-state { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; text-align: center; color: #cbd5e1; }
.empty-side-state p { font-size: 1.1rem; font-weight: 900; color: #1a1a1a; margin: 0; }
.empty-side-state .sub { font-size: 0.9rem; font-weight: 600; margin-top: 8px; }

.safety-footer { text-align: center; font-size: 0.85rem; color: #cbd5e1; font-weight: 700; line-height: 1.6; }
.mt-48 { margin-top: 12rem; } 

.italic { font-style: italic; }
/* 🌟 가이드 아이콘 컬러 연동 */
.icon-lime { color: var(--point-color); }
.mb-10 { margin-bottom: 2.5rem; }
.mt-5 { margin-top: 1.25rem; }
.w-full { width: 100%; }

@media (max-width: 1100px) {
  .main-layout-grid { grid-template-columns: 1fr; }
  .hero-title { font-size: 2.8rem; }
  .side-card { max-height: none; }
}
</style>