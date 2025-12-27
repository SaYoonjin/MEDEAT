<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import axios from "@/api/axios";
import BaseSelect from "@/components/common/BaseSelect.vue";
import { 
  Sparkles, 
  Camera, 
  Search, 
  History, 
  Info, 
  X, 
  Loader2, 
  ArrowRight,
  ChevronRight,
  AlertCircle,
  Check
} from "lucide-vue-next";

const router = useRouter();
const route = useRoute();

/** 🌟 현재 모드 */
const mode = computed(() => route.query.mode || "MEDI");

/* ===============================
   상태 관리 및 하이라이트 제어
================================ */
const scanResults = ref([]);
const recentMeds = ref([]);
const highlightTarget = ref(""); 
const scanCardRef = ref(null);
const searchCardRef = ref(null);

// 분석 결과 모달 제어
const showResultModal = ref(false);

/* ===============================
   파일 업로드 핸들링
================================ */
const frontFile = ref(null);
const backFile = ref(null);
const frontPreviewUrl = ref("");
const backPreviewUrl = ref("");
const frontInput = ref(null);
const backInput = ref(null);

const isScanning = ref(false);

const triggerFront = () => { if (frontInput.value) frontInput.value.click(); };
const triggerBack = () => { if (backInput.value) backInput.value.click(); };

const onFrontChange = (e) => {
  const file = e.target.files?.[0];
  if (!file) return;
  frontFile.value = file;
  if (frontPreviewUrl.value) URL.revokeObjectURL(frontPreviewUrl.value);
  frontPreviewUrl.value = URL.createObjectURL(file);
};

const onBackChange = (e) => {
  const file = e.target.files?.[0];
  if (!file) return;
  backFile.value = file;
  if (backPreviewUrl.value) URL.revokeObjectURL(backPreviewUrl.value);
  backPreviewUrl.value = URL.createObjectURL(file);
};

const removeFront = () => {
  frontFile.value = null;
  if (frontPreviewUrl.value) { URL.revokeObjectURL(frontPreviewUrl.value); frontPreviewUrl.value = ""; }
  if (frontInput.value) frontInput.value.value = "";
};

const removeBack = () => {
  backFile.value = null;
  if (backPreviewUrl.value) { URL.revokeObjectURL(backPreviewUrl.value); backPreviewUrl.value = ""; }
  if (backInput.value) backInput.value.value = "";
};

/* ===============================
   폼 필터 상태 (15종 색상 옵션 적용)
================================ */
const dosageForm = ref("");
const frontColor = ref("");
const backColor = ref("");
const scoreLine = ref("");
const hasImprint = ref("none");
const imprintFront = ref("");
const imprintBack = ref("");

const colorOptions = [
  { label: "하양", value: "하양" }, { label: "노랑", value: "노랑" }, { label: "주황", value: "주황" },
  { label: "분홍", value: "분홍" }, { label: "빨강", value: "빨강" }, { label: "갈색", value: "갈색" },
  { label: "연두", value: "연두" }, { label: "초록", value: "초록" }, { label: "청록", value: "청록" },
  { label: "파랑", value: "파랑" }, { label: "보라", value: "보라" }, { label: "자주", value: "자주" },
  { label: "회색", value: "회색" }, { label: "검정", value: "검정" }, { label: "투명", value: "투명" },
];

const isCapsule = computed(() => dosageForm.value === "캡슐" || dosageForm.value === "연질캡슐");
const frontColorLabel = computed(() => isCapsule.value ? "왼쪽 색상" : "앞면 색상");
const backColorLabel = computed(() => isCapsule.value ? "오른쪽 색상" : "뒷면 색상");

/* ===============================
   검색 및 기록 로직
================================ */
const searchKeyword = ref("");
const searchResults = ref([]);
const isSearching = ref(false);
const searchError = ref("");

const LS_RECENT = "mediScanRecentMeds";

const canScan = computed(() => !!frontFile.value && !!backFile.value && dosageForm.value && frontColor.value && backColor.value);

const scanDisabledHint = computed(() => {
  const missing = [];
  if (!frontFile.value) missing.push("앞면 이미지");
  if (!backFile.value) missing.push("뒷면 이미지");
  if (!dosageForm.value) missing.push("형태");
  if (!frontColor.value) missing.push("앞면 색상");
  if (!backColor.value) missing.push("뒷면 색상");
  return missing.length > 0 ? `필수 항목: ${missing.join(", ")}` : "";
});

const loadRecent = () => {
  try {
    const raw = localStorage.getItem(LS_RECENT);
    recentMeds.value = raw ? JSON.parse(raw) : [];
  } catch { recentMeds.value = []; }
};

const pushRecent = (drugInfo) => {
  if (!drugInfo) return;
  const item = {
    itemSeq: drugInfo.itemSeq ?? drugInfo.id ?? null,
    itemName: drugInfo.itemName ?? drugInfo.name ?? "",
    entpName: drugInfo.entpName ?? drugInfo.company ?? "",
    itemImage: drugInfo.itemImage ?? "",
    drugShape: drugInfo.drugShape ?? "",
    colorClass1: drugInfo.colorClass1 ?? "",
  };
  const list = [...recentMeds.value];
  const filtered = list.filter(x => (x.itemSeq || x.itemName) !== (item.itemSeq || item.itemName));
  filtered.unshift(item);
  recentMeds.value = filtered.slice(0, 10);
  localStorage.setItem(LS_RECENT, JSON.stringify(recentMeds.value));
};

/* ===============================
   API 호출: 스캔
================================ */
const scanImage = async () => {
  if (!canScan.value) return;
  isScanning.value = true;
  try {
    const formData = new FormData();
    formData.append("front", frontFile.value);
    formData.append("back", backFile.value);
    const meta = {
      drugShape: dosageForm.value,
      colorClass1: frontColor.value,
      colorClass2: backColor.value,
      printFront: hasImprint.value === "exist" ? imprintFront.value : null,
      printBack: hasImprint.value === "exist" ? imprintBack.value : null,
    };
    formData.append("meta", new Blob([JSON.stringify(meta)], { type: "application/json" }));
    
    const res = await axios.post("/drug/scan", formData);
    const results = res.data.results || res.data || [];
    
    if (!results.length) {
      router.push({ name: "medi-scan-not-found", query: { mode: mode.value } });
    } else {
      scanResults.value = results.slice(0, 6); 
      showResultModal.value = true;
      document.body.style.overflow = 'hidden';
    }
  } catch (e) {
    console.error("이미지 스캔 실패", e);
  } finally { isScanning.value = false; }
};

const searchByName = async () => {
  const keyword = searchKeyword.value.trim();
  if (!keyword) return;
  isSearching.value = true;
  searchError.value = "";
  try {
    const res = await axios.get("/drug/search", { params: { keyword } });
    searchResults.value = res.data || [];
    if (!searchResults.value.length) searchError.value = "검색 결과가 없습니다.";
  } catch (e) {
    searchError.value = "검색 중 오류가 발생했습니다.";
  } finally { isSearching.value = false; }
};

const goDetail = (item) => {
  if (!item) return;
  pushRecent(item);
  showResultModal.value = false;
  document.body.style.overflow = '';
  router.push({
    name: "medi-pill-detail",
    query: { mode: mode.value, itemSeq: String(item.itemSeq || item.id) }
  });
};

const closeResultModal = () => {
  showResultModal.value = false;
  document.body.style.overflow = '';
};

/* ===============================
   이동 처리 및 애니메이션 (focus 감지)
================================ */
const applyHighlight = async () => {
  const focus = route.query.focus;
  if (!focus) return;
  await nextTick();
  highlightTarget.value = focus;
  if (focus === 'camera' && scanCardRef.value) {
    scanCardRef.value.scrollIntoView({ behavior: 'smooth', block: 'center' });
  } else if (focus === 'search' && searchCardRef.value) {
    searchCardRef.value.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }
  setTimeout(() => { highlightTarget.value = ""; }, 3000);
};

const formatSimilarity = (s) => {
  if (typeof s !== 'number') return '';
  return (s * 100).toFixed(1) + '%';
};

onMounted(() => {
  loadRecent();
  applyHighlight();
});

watch(() => route.query.focus, applyHighlight);

onBeforeUnmount(() => {
  if (frontPreviewUrl.value) URL.revokeObjectURL(frontPreviewUrl.value);
  if (backPreviewUrl.value) URL.revokeObjectURL(backPreviewUrl.value);
});
</script>

<template>
  <div class="intro-container">
    <!-- 배경 장식 (역동적으로 개선된 Blob) -->
    <div class="blob-medi" :class="{ 'is-scanning': isScanning }"></div>
    <div class="blob-medi blob-secondary" :class="{ 'is-scanning': isScanning }"></div>

    <main class="content-wrapper">
      <header class="page-header">
        <div class="badge">
          <Sparkles :size="12" class="icon-purple" />
          <span>AI DRUG IDENTIFICATION</span>
        </div>
        <h1 class="hero-title italic">
          MEDI<br />
          <span class="gradient-text">SCAN.</span>
        </h1>
        <p class="hero-subtext">알약을 촬영하거나 이름으로 검색해 어떤 약인지 확인해보세요.</p>
      </header>

      <div class="main-layout-grid">
        <!-- 왼쪽: 메인 스캔 폼 영역 -->
        <div class="scan-form-section" ref="scanCardRef">
          <div class="scan-card glass-card" :class="{ 'highlight-pulse': highlightTarget === 'camera' }">
            <div class="section-container">
              <h3 class="section-title italic">1. UPLOAD PHOTOS</h3>
              <p class="section-desc">알약의 <strong>앞면과 뒷면</strong> 사진 2장이 필요합니다.</p>
              
              <div class="upload-grid">
                <div class="upload-box" :class="{ 'has-file': frontPreviewUrl }" @click="triggerFront">
                  <input ref="frontInput" type="file" accept="image/*" class="hidden-input" @change="onFrontChange" />
                  <div v-if="!frontPreviewUrl" class="upload-placeholder">
                    <Camera :size="32" />
                    <span class="label">FRONT SIDE</span>
                  </div>
                  <template v-else>
                    <img :src="frontPreviewUrl" class="preview-img" alt="알약 앞면" />
                    <button class="remove-btn" @click.stop="removeFront"><X :size="16" /></button>
                  </template>
                </div>

                <div class="upload-box" :class="{ 'has-file': backPreviewUrl }" @click="triggerBack">
                  <input ref="backInput" type="file" accept="image/*" class="hidden-input" @change="onBackChange" />
                  <div v-if="!backPreviewUrl" class="upload-placeholder">
                    <Camera :size="32" />
                    <span class="label">BACK SIDE</span>
                  </div>
                  <template v-else>
                    <img :src="backPreviewUrl" class="preview-img" alt="알약 뒷면" />
                    <button class="remove-btn" @click.stop="removeBack"><X :size="16" /></button>
                  </template>
                </div>
              </div>
            </div>

            <div class="section-container mt-12">
              <div class="section-header-row">
                <h3 class="section-title italic">2. BASIC INFO</h3>
                <span class="required-badge">REQUIRED</span>
              </div>
              <div class="inputs-grid">
                <div class="input-group">
                  <label>알약 형태</label>
                  <BaseSelect v-model="dosageForm" placeholder="형태 선택" :options="[{label:'정제',value:'정제'},{label:'캡슐',value:'캡슐'},{label:'연질캡슐',value:'연질캡슐'},{label:'필름',value:'필름'}]" />
                </div>
                <div class="input-group">
                  <label>{{ frontColorLabel }}</label>
                  <BaseSelect v-model="frontColor" placeholder="색상 선택" :options="colorOptions" />
                </div>
                <div class="input-group">
                  <label>{{ backColorLabel }}</label>
                  <BaseSelect v-model="backColor" placeholder="색상 선택" :options="colorOptions" />
                </div>
              </div>
            </div>

            <div class="section-container mt-12">
              <div class="section-header-row">
                <h3 class="section-title italic">3. OPTIONAL INFO</h3>
                <span class="optional-badge">OPTIONAL</span>
              </div>
              <div class="inputs-grid compact">
                <BaseSelect v-model="scoreLine" label="분할선" placeholder="모름/상관없음" :options="[{label:'없음',value:'없음'},{label:'1줄',value:'1줄'},{label:'2줄',value:'2줄'},{label:'십자',value:'십자'}]" />
                <BaseSelect v-model="hasImprint" label="각인 여부" :options="[{label:'없음',value:'none'},{label:'있음',value:'exist'}]" />
                <div v-if="hasImprint === 'exist'" class="col-span-2">
                  <label class="field-label">각인 텍스트 (앞/뒤)</label>
                  <div class="dual-input">
                    <input v-model="imprintFront" type="text" placeholder="앞면 각인" />
                    <input v-model="imprintBack" type="text" placeholder="뒷면 각인" />
                  </div>
                </div>
              </div>
            </div>

            <div class="action-footer mt-16">
              <!-- 🌟 간격 넓힘: mb-10 -> mb-14 -->
              <div v-if="scanDisabledHint" class="hint-text mb-14">
                <AlertCircle :size="14" />
                <span>{{ scanDisabledHint }}</span>
              </div>
              <button class="main-start-btn w-full" :disabled="!canScan || isScanning" @click="scanImage">
                <template v-if="!isScanning">
                  RUN AI SCAN
                  <ArrowRight :size="18" />
                </template>
                <template v-else>
                  <Loader2 :size="18" class="animate-spin" />
                  SCANNING...
                </template>
              </button>
              <!-- 🌟 간격 넓힘: mt-8 -> mt-12 -->
              <p class="scan-time-note mt-12">
                스캔에는 <strong>1~5분</strong> 정도 걸릴 수 있어요. 창을 닫지 말고 잠시만 기다려 주세요.
              </p>
            </div>
          </div>
        </div>

        <!-- 오른쪽: 서브 패널 -->
        <aside class="side-panel">
          <div class="side-card glass-card">
            <div class="side-header">
              <h3 class="side-title italic">RECENT</h3>
              <History :size="16" class="opacity-30" />
            </div>
            <!-- 🌟 최근 기록 영역 스크롤 적용 -->
            <div v-if="recentMeds.length > 0" class="mini-result-list scroll-container">
              <div v-for="m in recentMeds" :key="m.itemSeq || m.itemName" class="mini-item" @click="goDetail(m)">
                <div class="item-thumb">
                  <img v-if="m.itemImage" :src="m.itemImage" />
                  <span v-else>💊</span>
                </div>
                <div class="item-info">
                  <span class="name">{{ m.itemName }}</span>
                  <span class="meta">{{ m.drugShape || '정보없음' }} · {{ m.colorClass1 || '색상무관' }}</span>
                </div>
              </div>
            </div>
            <div v-else class="empty-side">조회된 최근 약이 없습니다.</div>
          </div>

          <div class="side-card glass-card" ref="searchCardRef" :class="{ 'highlight-pulse': highlightTarget === 'search' }">
            <h3 class="side-title italic">NAME SEARCH</h3>
            <div class="search-bar-modern">
              <input v-model="searchKeyword" type="text" placeholder="약 이름이나 식별문자" @keyup.enter="searchByName" />
              <button @click="searchByName" :disabled="isSearching"><Search :size="18" /></button>
            </div>
            <div v-if="searchResults.length > 0" class="mini-result-list mt-4">
              <div v-for="item in searchResults.slice(0, 5)" :key="item.itemSeq" class="mini-item" @click="goDetail(item)">
                <div class="item-icon">💊</div>
                <div class="item-info">
                  <span class="name">{{ item.itemName }}</span>
                  <span class="corp">{{ item.entpName }}</span>
                </div>
                <ChevronRight :size="14" class="opacity-30" />
              </div>
            </div>
            <p v-if="searchError" class="search-error-text mt-2">{{ searchError }}</p>
          </div>

          <div class="guide-card">
            <div class="guide-head">
              <Info :size="16" />
              <span>SCAN GUIDE</span>
            </div>
            <ul>
              <li>밝은 곳에서 단색 배경 위에 놓고 찍으세요.</li>
              <li>알약이 화면 중앙에 오도록 가까이 촬영하세요.</li>
              <li>글자나 문양이 또렷하게 보여야 정확합니다.</li>
            </ul>
          </div>
        </aside>
      </div>

      <footer class="safety-footer mt-12">
        ※ 본 서비스는 건강 관리를 돕기 위한 참고용 정보이며, 정확한 복약 상담은 반드시 의사·약사와 상의해 주세요.
      </footer>
    </main>

    <!-- 🌟 분석 결과 모달 (인트로 로그인 스타일로 리뉴얼) -->
    <Transition name="intro-pop">
      <div v-if="showResultModal" class="modal-overlay" @click.self="closeResultModal">
        <div class="auth-modal-style result-container">
          <button class="close-btn-top" @click="closeResultModal"><X :size="24" /></button>
          
          <header class="modal-header-centered">
            <div class="badge-mini">AI ANALYSIS</div>
            <h2 class="modal-title-main italic">MATCHED<br/><span class="gradient-text">CANDIDATES.</span></h2>
            <p class="modal-subtitle-main">분석된 결과 중 실제 알약과 일치하는 항목을 선택하세요.</p>
          </header>

          <!-- 세로형 카드 그리드 (사이즈 최적화) -->
          <div class="result-grid-scroll-custom mt-10">
            <div class="candidate-grid-refined">
              <article 
                v-for="c in scanResults" 
                :key="c.itemSeq" 
                class="pill-compact-card"
                @click="goDetail(c)"
              >
                <div class="pill-card-img-zone">
                  <img v-if="c.itemImage" :src="c.itemImage" class="pill-img-fit" />
                  <div v-else class="pill-emoji-fit">💊</div>
                </div>
                <div class="pill-card-info-zone">
                  <div class="name-similarity-row">
                    <h4 class="pill-name-text">{{ c.itemName }}</h4>
                    <span class="similarity-text-only" v-if="c.similarity">{{ formatSimilarity(c.similarity) }}</span>
                  </div>
                  <p class="pill-corp-text">{{ c.entpName }}</p>
                  <div class="pill-tag-row">
                    <span>{{ c.drugShape }}</span>
                    <span>{{ c.colorClass1 }}</span>
                  </div>
                </div>
              </article>
            </div>
          </div>

          <footer class="modal-action-footer">
            <p class="warning-text-footer">※ 분석 결과는 참고용이며 실제 약품과 다를 수 있습니다.</p>
            <button class="main-start-btn w-full mt-8" @click="closeResultModal">
              다시 촬영하기
              <Camera :size="18" class="ml-2" />
            </button>
          </footer>
        </div>
      </div>
    </Transition>
  </div>
</template>

<style scoped>
/* 인트로 레이아웃 스타일 계승 */
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

/* 🌟 배경 Blob 애니메이션 강화 */
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
  animation: blobMotion 20s infinite alternate ease-in-out;
}
.blob-secondary {
  top: 40%;
  left: -20%;
  width: 55vw;
  height: 55vw;
  background: radial-gradient(circle, rgba(124, 77, 255, 0.1) 0%, transparent 70%);
  animation: blobMotion 25s infinite alternate-reverse ease-in-out;
}

@keyframes blobMotion {
  0% { transform: translate(0, 0) rotate(0deg) scale(1); }
  50% { transform: translate(8%, 4%) rotate(180deg) scale(1.15); }
  100% { transform: translate(-5%, -2%) rotate(360deg) scale(1); }
}

/* 스캔 시 강조 */
.is-scanning {
  animation-duration: 4s !important;
  background: radial-gradient(circle, rgba(56, 189, 248, 0.3) 0%, transparent 70%) !important;
}

.content-wrapper {
  width: 100%;
  max-width: 1200px;
  position: relative;
  z-index: 10;
}

.page-header { text-align: center; margin-bottom: 60px; }
.badge {
  display: inline-flex; align-items: center; gap: 6px; padding: 6px 12px;
  background: white; border-radius: 999px; 
  font-size: 0.65rem; font-weight: 850; color: #7c4dff; border: 1.5px solid #f1f5f9;
}
.hero-title { font-size: 3.5rem; line-height: 0.95; font-weight: 950; letter-spacing: -0.05em; margin-bottom: 20px; }
.gradient-text {
  background: linear-gradient(to bottom right, #1a1a1a, #38bdf8);
  -webkit-background-clip: text; background-clip: text; color: transparent;
}
.hero-subtext { font-size: 1.1rem; opacity: 0.6; font-weight: 500; }

.main-layout-grid {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 30px;
}

.glass-card {
  background: rgba(255, 255, 255, 0.45);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 40px;
  box-shadow: 0 40px 100px rgba(0, 0, 0, 0.02);
  transition: box-shadow 0.5s ease;
}

.highlight-pulse {
  animation: pulseHighlight 1.5s infinite alternate;
}

@keyframes pulseHighlight {
  from { box-shadow: 0 0 0px rgba(56, 189, 248, 0); border-color: rgba(255, 255, 255, 0.8); }
  to { box-shadow: 0 0 30px rgba(56, 189, 248, 0.4); border-color: #38bdf8; }
}

.scan-card { padding: 50px; }

.section-container { margin-bottom: 20px; }
.section-title { font-size: 1.1rem; font-weight: 900; color: #1a1a1a; letter-spacing: 0.05em; }
.section-desc { font-size: 0.9rem; color: #94a3b8; margin-bottom: 24px; }
.section-header-row { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }

.required-badge { font-size: 0.6rem; font-weight: 900; color: #38bdf8; background: rgba(56, 189, 248, 0.1); padding: 4px 10px; border-radius: 6px; }
.optional-badge { font-size: 0.6rem; font-weight: 900; color: #94a3b8; background: rgba(0, 0, 0, 0.05); padding: 4px 10px; border-radius: 6px; }

.upload-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.upload-box {
  min-height: 220px; 
  height: auto;
  max-height: 400px;
  background: rgba(255, 255, 255, 0.5); 
  border: 2px dashed #e2e8f0;
  border-radius: 28px; 
  display: flex; 
  align-items: center; 
  justify-content: center;
  cursor: pointer; 
  transition: 0.3s; 
  position: relative; 
  overflow: hidden;
}
.upload-box:hover { border-color: #38bdf8; background: white; }
.upload-box.has-file { border-style: solid; border-color: #38bdf8; }
.preview-img { width: 100%; height: auto; max-height: 380px; object-fit: contain; }
.remove-btn {
  position: absolute; top: 12px; right: 12px; width: 28px; height: 28px;
  background: rgba(0,0,0,0.7); color: white; border: none; border-radius: 50%;
  display: flex; align-items: center; justify-content: center; z-index: 10;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  gap: 12px; 
}
.upload-placeholder .label {
  font-size: 0.7rem;
  font-weight: 900;
  letter-spacing: 0.1em;
}

.inputs-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.inputs-grid.compact { grid-template-columns: 1fr 1fr; }
.input-group label, .field-label { display: block; font-size: 0.75rem; font-weight: 800; color: #94a3b8; margin-bottom: 8px; margin-left: 10px; }

.dual-input { display: flex; gap: 10px; }
.dual-input input {
  flex: 1; padding: 12px 18px; border-radius: 14px; border: 1.5px solid #f1f5f9;
  background: white; font-weight: 700; font-size: 0.9rem; outline: none; transition: 0.3s;
}
.dual-input input:focus { border-color: #38bdf8; box-shadow: 0 8px 20px rgba(56, 189, 248, 0.1); }

.side-panel { display: flex; flex-direction: column; gap: 24px; }
.side-card { padding: 24px; }
.side-title { font-size: 0.85rem; font-weight: 900; color: #94a3b8; letter-spacing: 0.1em; margin-bottom: 16px; }
.side-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }

.search-bar-modern {
  display: flex; background: white; border-radius: 14px; padding: 4px;
  border: 1.5px solid #f1f5f9; transition: 0.3s;
}
.search-bar-modern:focus-within { border-color: #38bdf8; box-shadow: 0 10px 25px rgba(56, 189, 248, 0.1); }
.search-bar-modern input { flex: 1; border: none; outline: none; padding: 10px 14px; font-weight: 700; font-size: 0.9rem; width: 100%; }
.search-bar-modern button { width: 40px; height: 40px; border-radius: 10px; border: none; background: #1a1a1a; color: white; cursor: pointer; transition: 0.3s; flex-shrink: 0; }

.mini-result-list { display: flex; flex-direction: column; gap: 8px; }

/* 🌟 스크롤 영역 스타일 개선 */
.scroll-container {
  max-height: 280px;
  overflow-y: auto;
  padding-right: 4px;
}
.scroll-container::-webkit-scrollbar { width: 4px; }
.scroll-container::-webkit-scrollbar-track { background: transparent; }
.scroll-container::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }

.mini-item {
  display: flex; align-items: center; gap: 12px; padding: 10px; border-radius: 12px;
  background: rgba(255, 255, 255, 0.4); cursor: pointer; transition: 0.3s;
}
.mini-item:hover { background: white; transform: translateX(5px); }

.item-thumb, .item-icon {
  width: 36px; height: 36px; border-radius: 10px; background: white;
  display: flex; align-items: center; justify-content: center; font-size: 1.2rem; flex-shrink: 0;
}
.item-thumb img { width: 100%; height: 100%; object-fit: cover; border-radius: 10px; }
.item-info { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
.item-info .name { font-size: 0.85rem; font-weight: 800; color: #1a1a1a; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.item-info .corp, .item-info .meta { font-size: 0.7rem; font-weight: 600; color: #94a3b8; }

.empty-side { padding: 20px; text-align: center; font-size: 0.8rem; color: #cbd5e1; font-weight: 700; }

.guide-card { 
  padding: 24px 24px 24px 32px; 
  background: #f8fafc; 
  border-radius: 30px; 
  border: 1px solid #f1f5f9; 
}
.guide-head { 
  display: flex; align-items: center; gap: 10px; font-size: 0.75rem; font-weight: 900; color: #1a1a1a; margin-bottom: 16px; margin-left: -8px; 
}
.guide-card li { font-size: 0.75rem; color: #64748b; line-height: 1.7; margin-bottom: 8px; font-weight: 500; }

.action-footer { text-align: center; }

/* 🌟 간격 넓힘 조절 */
.hint-text { 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  gap: 8px; 
  font-size: 0.8rem; 
  font-weight: 850; 
  color: #38bdf8; 
}
.mb-14 { margin-bottom: 1rem; } /* 버튼 위 경고 문구 여백 확장 */
.mt-12 { margin-top: 1.1rem; }    /* 버튼 아래 안내 멘트 여백 확장 */

.main-start-btn {
  display: flex; align-items: center; justify-content: center; gap: 12px;
  padding: 20px; background: #1a1a1a; color: white; border: none; border-radius: 999px;
  font-size: 1rem; font-weight: 850; cursor: pointer; transition: all 0.4s;
}
.main-start-btn:hover:not(:disabled) { 
  background: #2a2a2a; transform: translateY(-5px); box-shadow: 0 20px 40px rgba(56, 189, 248, 0.2); 
}

.scan-time-note { font-size: 0.75rem; font-weight: 600; color: #94a3b8; line-height: 1.5; text-align: center; }

/* 🌟 인트로 스타일 모달 리뉴얼 (화이트 배경 + 블러) */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.5); 
  backdrop-filter: blur(25px); -webkit-backdrop-filter: blur(25px);
  z-index: 2000; display: flex; align-items: center; justify-content: center; padding: 24px;
}

.auth-modal-style {
  width: 100%; max-width: 860px; 
  background: #ffffff; 
  padding: 60px 48px; position: relative; max-height: 90vh; overflow: hidden;
  display: flex; flex-direction: column;
  border-radius: 50px;
  box-shadow: 0 50px 100px rgba(0, 0, 0, 0.15);
}

.intro-pop-enter-active, .intro-pop-leave-active { transition: all 0.6s cubic-bezier(0.23, 1, 0.32, 1); }
.intro-pop-enter-from { opacity: 0; transform: scale(0.95) translateY(40px); }

.close-btn-top { 
  position: absolute; top: 32px; right: 32px; background: none; border: none; 
  color: #1a1a1a; cursor: pointer; opacity: 0.3; transition: 0.3s;
}
.close-btn-top:hover { opacity: 1; transform: rotate(90deg); }

.modal-header-centered { text-align: center; margin-bottom: 10px; }
.badge-mini {
  display: inline-block; padding: 4px 12px; background: #f1f5f9; 
  border-radius: 999px; font-size: 0.65rem; font-weight: 900; color: #7c4dff; letter-spacing: 0.1em;
}
.modal-title-main { font-size: 3rem; font-weight: 950; margin-top: 16px; line-height: 0.95; }
.modal-subtitle-main { font-size: 1rem; color: #64748b; font-weight: 600; margin-top: 12px; }

/* 🌟 결과 그리드 & 카드 (사이즈 최적화 및 스크롤바 수정) */
.result-grid-scroll-custom { 
  flex: 1; overflow-y: auto; padding: 10px 8px 20px; 
}
.result-grid-scroll-custom::-webkit-scrollbar { width: 5px; }
.result-grid-scroll-custom::-webkit-scrollbar-track { background: #f1f5f9; border-radius: 10px; }
.result-grid-scroll-custom::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }

.candidate-grid-refined {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}

.pill-compact-card {
  background: white;
  border-radius: 30px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 0 10px 25px rgba(56, 189, 248, 0.08); 
  border: 1.5px solid #f8fafc;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  height: 300px; 
}

.pill-compact-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(56, 189, 248, 0.15);
  border-color: rgba(56, 189, 248, 0.2);
}

.pill-card-img-zone {
  height: 140px; 
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.pill-img-fit { width: 75%; height: 75%; object-fit: contain; }
.pill-emoji-fit { font-size: 2.5rem; }

.pill-card-info-zone { padding: 20px; flex: 1; display: flex; flex-direction: column; }

.name-similarity-row { display: flex; justify-content: space-between; align-items: flex-start; gap: 8px; margin-bottom: 4px; }
.pill-name-text { font-size: 1rem; font-weight: 900; color: #1a1a1a; line-height: 1.2; flex: 1; }

.similarity-text-only {
  font-size: 0.75rem; font-weight: 900; color: #38bdf8; letter-spacing: -0.02em; flex-shrink: 0;
}

.pill-corp-text { font-size: 0.75rem; font-weight: 600; color: #94a3b8; margin-bottom: 12px; }

.pill-tag-row { display: flex; gap: 5px; flex-wrap: wrap; margin-top: auto; }
.pill-tag-row span {
  font-size: 0.6rem; font-weight: 850; background: #f1f5f9; color: #64748b; padding: 3px 8px; border-radius: 6px;
}

.modal-action-footer { margin-top: 30px; text-align: center; }
.warning-text-footer { font-size: 0.8rem; color: #94a3b8; font-weight: 600; }

.safety-footer { text-align: center; font-size: 0.8rem; color: #cbd5e1; font-weight: 700; line-height: 1.6; }
.hidden-input { display: none; }
.italic { font-style: italic; }
.mt-12 { margin-top: 1rem; }
.mt-16 { margin-top: 4rem; }
.w-full { width: 100%; }

@media (max-width: 980px) {
  .main-layout-grid { grid-template-columns: 1fr; }
  .inputs-grid { grid-template-columns: 1fr; }
  .auth-modal-style { padding: 40px 24px; border-radius: 40px; }
  .candidate-grid-refined { grid-template-columns: 1fr 1fr; }
}
</style>