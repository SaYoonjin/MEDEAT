<script setup>
import { ref, onMounted, computed, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "@/api/axios";
import medicationApi from "@/api/medication";
import { 
  Sparkles, 
  Search, 
  Plus, 
  Clock, 
  AlertCircle, 
  ArrowRight, 
  X, 
  Check, 
  Loader2, 
  Pill,
  ChevronRight,
  AlarmClock
} from "lucide-vue-next";

const route = useRoute();
const router = useRouter();

/* =========================
   상태 관리 및 설정
========================= */
const customTimes = ref({
  "아침": "",
  "점심": "",
  "저녁": "",
  "취침 전": "",
});
const searchResults = ref(false);
const searching = ref(false);
const timeTags = ["아침", "점심", "저녁", "취침 전"];

const form = ref({
  mediId: route.params.id ? Number(route.params.id) : null,
  itemSeq: route.query.itemSeq ? Number(route.query.itemSeq) : null,
  itemName: route.query.itemName || route.query.name || "",
  ingrName: route.query.ingrName || route.query.genericName || "",
  dose: route.query.dose || "",
  daily_count: route.query.daily_count ? Number(route.query.daily_count) : null,
  intake_list: route.query.intakeTime
    ? String(route.query.intakeTime).split(",").map((s) => s.trim()).filter(Boolean)
    : [],
  intakeTime: "",
  recommended: route.query.recommended || route.query.useMethodQesitm || "",
  intervalHour: route.query.intervalHour || "",
  memo: route.query.memo || "",
  drugName: route.query.itemName || route.query.name || "",
  company: route.query.company || "",
  pillCode: route.query.pillCode || "",
  genericName: route.query.genericName || route.query.ingrName || "",
  drugShape: route.query.drugShape || "",
  colorClass1: route.query.colorClass1 || "",
  printFront: route.query.printFront || "",
  itemImage: route.query.itemImage || "",
});

const tagToTime = {
  "아침": "08:00",
  "점심": "12:00",
  "저녁": "18:00",
  "취침 전": "22:00",
};

/* -----------------------------
   유효성 검사 및 안내 문구
----------------------------- */
const canSave = computed(() => {
  const f = form.value;
  return f.itemName?.trim() && f.dose?.trim() && f.intake_list?.length > 0;
});

const disabledHint = computed(() => {
  const f = form.value;
  if (!f.itemName?.trim()) return "약 이름을 입력하거나 검색해 주세요.";
  if (!f.dose?.trim()) return "정확한 복용 용량을 입력해 주세요.";
  if (f.intake_list.length === 0) return "복용 시간대를 최소 하나 이상 선택해 주세요.";
  if (f.daily_count && f.intake_list.length !== f.daily_count) {
    return `복용 시간대를 총 ${f.daily_count}개 선택해 주세요.`;
  }
  return "";
});

/* -----------------------------
   액션 로직
----------------------------- */
const searchDrug = async () => {
  const keyword = form.value.itemName?.trim();
  if (!keyword || keyword.length < 2) return;

  searching.value = true;
  try {
    const res = await axios.get("/drug/search", { params: { keyword } });
    searchResults.value = res.data || [];
  } catch (e) {
    console.error("약 검색 실패:", e);
    searchResults.value = [];
  } finally {
    searching.value = false;
  }
};

const selectDrug = (item) => {
  form.value.itemName = item.itemName;
  form.value.drugName = item.itemName;
  form.value.itemSeq = item.itemSeq ? Number(item.itemSeq) : null;
  form.value.ingrName = item.ingrName || form.value.ingrName;
  form.value.company = item.entpName || form.value.company;
  if (item.useMethodQesitm) form.value.recommended = item.useMethodQesitm;
  searchResults.value = false;
};

// 🌟 복용 횟수 변경 시 기존 선택된 태그 정리 로직 추가
const setDailyCount = (num) => { 
  form.value.daily_count = num; 
  if (form.value.intake_list.length > num) {
    form.value.intake_list = form.value.intake_list.slice(0, num);
  }
};

// 🌟 복용 횟수 제한 로직 반영
const toggleTimeTag = (tag) => {
  const list = form.value.intake_list;
  if (list.includes(tag)) {
    form.value.intake_list = list.filter((t) => t !== tag);
  } else {
    // 복용 횟수가 지정되어 있고, 이미 한계치만큼 선택했다면 더이상 추가 불가
    if (form.value.daily_count && list.length >= form.value.daily_count) {
      return; 
    }
    form.value.intake_list = [...list, tag];
  }
};

const saveMedication = async () => {
  if (!canSave.value) return;
  form.value.drugName = form.value.itemName;
  form.value.dailyCount = form.value.intake_list.length;
  form.value.intakeTime = form.value.intake_list
    .map((tag) => (customTimes.value[tag]?.trim() || tagToTime[tag]))
    .join(", ");

  try {
    await medicationApi.save(form.value);
    router.push({ path: "/disease", query: { mode: "MEDI_EAT" } });
  } catch (err) {
    console.error("저장 실패");
  }
};

onMounted(async () => {
  if (!route.params.id) return;
  try {
    const res = await medicationApi.getMedication(route.params.id);
    const m = res.data || {};
    form.value.mediId = m.medicationId;
    form.value.itemName = m.drugName || "";
    form.value.dose = m.dose || "";
    form.value.memo = m.memo || "";
    form.value.daily_count = m.dailyCount || 1;
    if (m.intakeTime) {
      const times = m.intakeTime.split(",").map(s => s.trim());
      form.value.intake_list = Object.keys(tagToTime).filter(tag => {
          return times.some(t => t.startsWith(tagToTime[tag]));
      });
      times.forEach((t, i) => {
          const tag = form.value.intake_list[i];
          if(tag) customTimes.value[tag] = t;
      });
    }
    form.value.recommended = m.recommended || form.value.recommended;
  } catch (e) {
    console.error("데이터 로드 실패");
  }
});
</script>

<template>
  <div class="intro-container">
    <div class="blob blob-medi"></div>

    <main class="content-wrapper">
      <div class="form-card glass-card">
        <header class="page-header">
          <div class="badge">
            <Sparkles :size="12" class="icon-purple" />
            <span>MEDI-EAT CARE</span>
          </div>
          <h2 class="hero-title italic">
            ADD<br />
            <span class="gradient-text">MEDICATION.</span>
          </h2>
          <p class="hero-subtext">복용하는 약의 정보와 시간을 정교하게 설정하세요.</p>
        </header>

        <form @submit.prevent="saveMedication" class="form-container">
          <!-- 1. 기본 정보 섹션 -->
          <div class="form-section section-gap">
            <!-- 🌟 번호 디자인 아까 버전으로 복구 -->
            <h3 class="section-title italic">1. BASIC INFO</h3>
            
            <div class="input-group">
              <label>약 이름 <span class="required">*</span></label>
              <div class="search-input-wrapper">
                <div class="modern-input-box">
                  <Pill :size="18" class="input-icon" />
                  <input 
                    type="text" 
                    v-model="form.itemName" 
                    placeholder="약 이름을 입력하거나 검색하세요" 
                    @keyup.enter="searchDrug"
                  />
                </div>
                <button type="button" class="search-btn" @click="searchDrug">
                  <Loader2 v-if="searching" :size="18" class="spinner" />
                  <Search v-else :size="18" />
                </button>
              </div>

              <Transition name="expand">
                <div v-if="searchResults && searchResults.length" class="search-results-inline">
                  <div class="results-header">SEARCH RESULTS ({{ searchResults.length }})</div>
                  <div class="mini-result-list">
                    <div 
                      v-for="item in searchResults" 
                      :key="item.itemSeq" 
                      class="mini-item-inline" 
                      @click="selectDrug(item)"
                    >
                      <div class="item-icon">
                        <img v-if="item.itemImage" :src="item.itemImage" class="mini-img" />
                        <span v-else>💊</span>
                      </div>
                      <div class="item-info">
                        <span class="name">{{ item.itemName }}</span>
                        <span class="corp">{{ item.entpName }}</span>
                      </div>
                      <ChevronRight :size="14" class="opacity-30" />
                    </div>
                  </div>
                </div>
              </Transition>
            </div>

            <div class="input-group mt-10">
              <label>복용 용량 <span class="required">*</span></label>
              <div class="modern-input-box">
                <Plus :size="18" class="input-icon" />
                <input type="text" v-model="form.dose" placeholder="예: 500mg, 1정" />
              </div>
            </div>
          </div>

          <!-- 2. 스케줄 섹션 -->
          <div class="form-section section-gap mt-16">
            <h3 class="section-title italic">2. SCHEDULE</h3>
            
            <div class="input-group">
              <label>하루 총 복용 횟수 <span class="required">*</span></label>
              <div class="chip-group">
                <button 
                  v-for="num in [1, 2, 3, 4]" 
                  :key="num" 
                  type="button"
                  class="mode-chip" 
                  :class="{ active: form.daily_count === num }"
                  @click="setDailyCount(num)"
                >
                  {{ num }}회
                </button>
              </div>
            </div>

            <div class="input-group mt-12">
              <label>복용 시간대 <span class="required">*</span> <span class="limit-hint" v-if="form.daily_count">(최대 {{ form.daily_count }}개 선택 가능)</span></label>
              <div class="chip-group">
                <button 
                  v-for="tag in timeTags" 
                  :key="tag" 
                  type="button"
                  class="mode-chip" 
                  :class="{ active: form.intake_list.includes(tag) }"
                  @click="toggleTimeTag(tag)"
                >
                  <Check v-if="form.intake_list.includes(tag)" :size="12" class="check-icon" />
                  {{ tag }}
                </button>
              </div>
            </div>

            <Transition name="expand">
              <div v-if="form.intake_list.length > 0" class="time-detail-wrapper mt-12">
                <div class="time-detail-grid">
                  <div v-for="tag in form.intake_list" :key="tag" class="time-setting-card">
                    <div class="time-card-icon">
                      <AlarmClock :size="18" />
                    </div>
                    <div class="time-card-content">
                      <span class="time-tag-label">{{ tag }}</span>
                      <input type="time" v-model="customTimes[tag]" class="time-field" />
                    </div>
                    <span class="default-hint">기본 {{ tagToTime[tag] }}</span>
                  </div>
                </div>
              </div>
            </Transition>
          </div>

          <!-- 3. 추가 정보 섹션 -->
          <div class="form-section section-gap mt-16">
            <h3 class="section-title italic">3. OPTIONAL INFO</h3>
            
            <div class="input-group">
              <label>권장사항</label>
              <textarea 
                v-model="form.recommended" 
                placeholder="복용 시 주의사항이나 전문가 권고를 입력하세요"
                rows="3"
                class="modern-textarea"
              ></textarea>
            </div>
            <div class="input-group mt-10">
              <label>메모</label>
              <input 
                type="text" 
                v-model="form.memo" 
                class="modern-input-box-flat" 
                placeholder="나만의 메모를 남겨주세요"
              />
            </div>
          </div>

          <div class="footer-actions mt-16">
            <div v-if="!canSave || (form.daily_count && form.intake_list.length !== form.daily_count)" class="validation-hint mb-8">
              <AlertCircle :size="16" />
              <span>{{ disabledHint }}</span>
            </div>
            <button type="submit" class="main-start-btn" :disabled="!canSave || (form.daily_count && form.intake_list.length !== form.daily_count)">
              SAVE MEDICATION
              <ArrowRight :size="20" />
            </button>
          </div>
        </form>
      </div>
    </main>
  </div>
</template>

<style scoped>
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

.blob-medi {
  position: fixed;
  bottom: -15%;
  right: -10%;
  width: 60vw;
  height: 60vw;
  background: radial-gradient(circle, rgba(56, 189, 248, 0.25) 0%, transparent 70%);
  border-radius: 50%;
  filter: blur(140px);
  z-index: 0;
  pointer-events: none;
}

.content-wrapper {
  width: 100%;
  max-width: 860px;
  position: relative;
  z-index: 10;
}

.glass-card {
  background: rgba(255, 255, 255, 0.45);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 40px;
  padding: 60px 50px;
  box-shadow: 0 40px 100px rgba(0, 0, 0, 0.02);
}

.page-header { text-align: center; margin-bottom: 50px; }
.badge {
  display: inline-flex; align-items: center; gap: 6px; padding: 6px 12px;
  background: white; border-radius: 999px; margin-bottom: 24px;
  font-size: 0.65rem; font-weight: 850; color: #7c4dff; border: 1.5px solid #f1f5f9;
}
.hero-title { font-size: 3.5rem; line-height: 0.95; font-weight: 950; letter-spacing: -0.05em; margin-bottom: 20px; }
.gradient-text {
  background: linear-gradient(to bottom right, #1a1a1a, #38bdf8);
  -webkit-background-clip: text; background-clip: text; color: transparent;
}
.hero-subtext { font-size: 1.1rem; opacity: 0.6; font-weight: 500; }

.section-title {
  font-size: 1.1rem; font-weight: 900; letter-spacing: 0.05em;
  color: #1a1a1a; margin-bottom: 30px; display: flex; align-items: center; gap: 12px;
}
.section-title::after { content: ''; flex: 1; height: 1px; background: rgba(0,0,0,0.05); }

.input-group label {
  display: block; font-size: 0.85rem; font-weight: 800; color: #1a1a1a;
  opacity: 0.5; margin-bottom: 12px; margin-left: 8px;
}
.limit-hint { font-size: 0.75rem; color: #38bdf8; font-weight: 700; margin-left: 6px; }
.required { color: #38bdf8; margin-left: 2px; }

.search-input-wrapper { display: flex; gap: 12px; }
.modern-input-box, .modern-input-box-flat {
  flex: 1; background: rgba(255, 255, 255, 0.6); border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 20px; padding: 18px 24px; font-weight: 700;
  display: flex; align-items: center; gap: 12px; transition: 0.3s;
}
.modern-input-box input, .modern-input-box-flat input {
  background: none; border: none; outline: none; width: 100%; font-weight: 700; color: #1a1a1a; font-size: 1rem;
}
.modern-input-box:focus-within {
  background: white; border-color: #38bdf8; transform: translateY(-2px);
  box-shadow: 0 15px 35px rgba(56, 189, 248, 0.1);
}

.search-btn {
  width: 60px; height: 60px; border-radius: 20px; border: none;
  background: #1a1a1a; color: white; cursor: pointer;
  display: flex; align-items: center; justify-content: center; transition: 0.3s;
}
.search-btn:hover { background: #38bdf8; transform: scale(1.05); }

/* --- 인라인 검색 결과 --- */
.search-results-inline {
  width: 100%; margin-top: 20px; max-height: 400px; 
  overflow-y: auto; background: rgba(255, 255, 255, 0.3); 
  border-radius: 28px; padding: 20px;
  border: 1px dashed rgba(56, 189, 248, 0.4); 
}
.results-header { font-size: 0.75rem; font-weight: 900; color: #38bdf8; padding: 0 8px 14px; letter-spacing: 0.1em; }
.mini-result-list { display: flex; flex-direction: column; gap: 10px; }
.mini-item-inline {
  display: flex; align-items: center; gap: 14px; padding: 16px; border-radius: 22px;
  background: white; cursor: pointer; transition: 0.3s; box-shadow: 0 4px 12px rgba(0,0,0,0.02);
}
.mini-item-inline:hover { background: #f0f9ff; transform: scale(1.01); box-shadow: 0 10px 25px rgba(56, 189, 248, 0.1); }
.item-icon { width: 52px; height: 52px; border-radius: 16px; background: #f8fafc; display: flex; align-items: center; justify-content: center; font-size: 1.4rem; flex-shrink: 0; overflow: hidden; }
.mini-img { width: 100%; height: 100%; object-fit: cover; }
.item-info { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
.item-info .name { font-size: 1.05rem; font-weight: 850; color: #1a1a1a; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.item-info .corp { font-size: 0.85rem; font-weight: 600; color: #94a3b8; }

/* --- 칩 그룹 --- */
.chip-group { display: flex; flex-wrap: wrap; gap: 14px; }
.mode-chip {
  padding: 14px 24px; border-radius: 999px; border: 2px solid transparent;
  background: rgba(0, 0, 0, 0.03); font-size: 0.95rem; font-weight: 850;
  color: #94a3b8; cursor: pointer; transition: 0.3s; display: flex; align-items: center; gap: 8px;
}
.mode-chip:hover { background: rgba(0, 0, 0, 0.06); }
.mode-chip.active {
  background: white; color: #1a1a1a; border-color: #38bdf8;
  box-shadow: 0 10px 20px rgba(56, 189, 248, 0.15);
}

/* --- 시간 설정 영역 (Upgraded) --- */
.time-detail-wrapper {
  padding: 32px; background: rgba(0,0,0,0.02); border-radius: 35px;
  border: 1px solid rgba(0,0,0,0.03);
}
.time-detail-grid {
  display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 20px;
}
.time-setting-card {
  background: white; border-radius: 24px; padding: 20px;
  display: flex; flex-direction: column; gap: 12px; position: relative;
  box-shadow: 0 8px 20px rgba(0,0,0,0.04); transition: 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}
.time-setting-card:hover { transform: translateY(-5px); box-shadow: 0 15px 30px rgba(56, 189, 248, 0.1); }

.time-card-icon { color: #38bdf8; opacity: 0.7; margin-bottom: -4px; }
.time-card-content { display: flex; flex-direction: column; gap: 6px; }
.time-tag-label { font-size: 0.8rem; font-weight: 950; color: #94a3b8; letter-spacing: 0.05em; }
.time-field {
  background: #f8fafc; border: 2px solid #f1f5f9; border-radius: 12px;
  padding: 10px 14px; font-weight: 800; font-size: 1.1rem; color: #1a1a1a; outline: none; transition: 0.3s;
}
.time-field:focus { border-color: #38bdf8; background: white; }
.default-hint { font-size: 0.7rem; color: #cbd5e1; font-weight: 800; text-align: right; }

/* --- 기타 요소 --- */
.modern-textarea {
  width: 100%; background: rgba(255, 255, 255, 0.6); border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 24px; padding: 24px; font-weight: 700; font-size: 1rem; outline: none;
  transition: 0.3s; resize: none;
}
.modern-textarea:focus { background: white; border-color: #38bdf8; box-shadow: 0 15px 35px rgba(56, 189, 248, 0.1); }

.footer-actions { text-align: center; }
.validation-hint {
  display: flex; align-items: center; justify-content: center; gap: 10px;
  font-size: 0.95rem; font-weight: 800; color: #38bdf8;
}
.main-start-btn {
  width: 100%; display: flex; align-items: center; justify-content: center; gap: 16px;
  padding: 24px; background: #1a1a1a; color: white; border: none; border-radius: 999px;
  font-size: 1.2rem; font-weight: 800; cursor: pointer; transition: all 0.4s;
}
.main-start-btn:hover:not(:disabled) { background: #38bdf8; transform: translateY(-5px); box-shadow: 0 25px 50px rgba(56, 189, 248, 0.3); }
.main-start-btn:disabled { opacity: 0.2; cursor: not-allowed; }

/* --- 애니메이션 --- */
.expand-enter-active, .expand-leave-active { transition: all 0.5s cubic-bezier(0.23, 1, 0.32, 1); max-height: 800px; overflow: hidden; }
.expand-enter-from, .expand-leave-to { opacity: 0; max-height: 0; transform: translateY(-10px); }

.spinner { animation: spin 1s linear infinite; }
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
.italic { font-style: italic; }

.mt-10 { margin-top: 2.5rem; }
.mt-12 { margin-top: 3rem; }
.mt-16 { margin-top: 4rem; }
.mb-8 { margin-bottom: 2rem; }

@media (max-width: 860px) {
  .hero-title { font-size: 3rem; }
  .glass-card { padding: 45px 30px; border-radius: 40px; }
  .time-detail-grid { grid-template-columns: 1fr 1fr; }
}
</style>