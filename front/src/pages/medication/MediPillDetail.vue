<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from '@/api/axios';
import { 
  Sparkles, 
  ArrowLeft, 
  ExternalLink, 
  Plus, 
  Info, 
  AlertTriangle, 
  X,
  FileText,
  ShieldAlert,
  Package,
  Activity,
  History
} from 'lucide-vue-next';

const route = useRoute();
const router = useRouter();

/* =========================
   상태 관리
========================= */
const itemSeq = String(route.query?.itemSeq || '').trim();
const mode = String(route.query?.mode || 'EAT');

const showOriginalModal = ref(false);
const isAtpnOpen = ref(false);

const medicine = ref({
  itemSeq: itemSeq,
  name: '의약품명 정보 없음',
  genericName: '성분 정보 없음',
  company: '제조사 정보 없음',
  shape: '',
  color: '',
  imprint: '',
  image: '',
  efficacy: '',
  usage: '',
  contraindication: '',
  needCaution: '',
  interaction: '',
  sideEffect: '',
  storage: '',
  atpnRaw: '',
  category: '',
  ageRange: '',
});

const normalizeImage = (v) => (v && typeof v === 'string' ? v : '');

// 의약품안전나라 원문 URL
const originalUrl = computed(() => {
  const v = String(medicine.value.itemSeq || '').trim();
  return v ? `https://nedrug.mfds.go.kr/pbp/CCBBB01/getItemDetail?itemSeq=${encodeURIComponent(v)}` : '';
});

/* =========================
   데이터 통신
========================= */
const fetchDrugDetail = async () => {
  const v = String(medicine.value.itemSeq || '').trim();
  if (!v) return;

  try {
    const res = await axios.get(`/drug/detail/${v}`);
    const d = res.data || {};

    medicine.value = {
      ...medicine.value,
      itemSeq: d.itemSeq || medicine.value.itemSeq,
      name: d.itemName || medicine.value.name,
      genericName: d.ingrName || medicine.value.genericName,
      company: d.entpName || medicine.value.company,
      shape: d.drugShape || medicine.value.shape,
      color: d.colorClass1 || medicine.value.color,
      imprint: d.printFront || medicine.value.imprint,
      image: normalizeImage(d.itemImage) || medicine.value.image,
      efficacy: d.efcyQesitm || medicine.value.efficacy,
      usage: d.useMethodQesitm || medicine.value.usage,
      contraindication: d.atpnQesitm || medicine.value.contraindication,
      needCaution: d.atpnWarnQesitm || medicine.value.needCaution,
      interaction: d.intrcQesitm || medicine.value.interaction,
      sideEffect: d.seQesitm || medicine.value.sideEffect,
      storage: d.depositMethodQesitm || medicine.value.storage,
      atpnRaw: d.atpnWarnQesitm || d.atpnQesitm || medicine.value.atpnRaw,
      category: d.className || medicine.value.category,
      ageRange: d.ageRange || medicine.value.ageRange,
    };
  } catch (e) {
    console.error('약 상세 조회 실패:', e);
  }
};

onMounted(fetchDrugDetail);

/* =========================
   헬퍼 함수
========================= */
const nl2br = (text) => String(text || '').replace(/\r\n|\n/g, '<br>');

const renderOrGuide = (text) => {
  const v = String(text || '').trim();
  return v ? nl2br(v) : onlyOriginalGuide();
};

const onlyOriginalGuide = () => {
  return `앱에 요약된 정보가 없습니다.<br>상단 <b>원문 전체보기</b>에서 확인해 주세요.`;
};

const openOriginalModal = () => {
  if (!originalUrl.value) return;
  showOriginalModal.value = true;
  document.body.style.overflow = 'hidden';
};

const closeOriginalModal = () => {
  showOriginalModal.value = false;
  document.body.style.overflow = '';
};

const goScan = () => router.push({ name: 'medi-scan', query: { mode } });

const goAddMedication = () => {
  router.push({
    name: 'medication-new',
    params: { id: medicine.value.itemSeq || 'new' },
    query: {
      itemSeq: medicine.value.itemSeq,
      itemName: medicine.value.name,
      entpName: medicine.value.company,
    },
  });
};
</script>

<template>
  <div class="intro-container">
    <!-- 배경 장식 (MEDI 전용 하늘색 블롭) -->
    <div class="blob blob-medi"></div>

    <main class="content-wrapper">
      <!-- 상단 액션 바 -->
      <nav class="top-nav-bar">
        <button class="back-btn" @click="goScan">
          <ArrowLeft :size="18" />
          <span>BACK TO SCAN</span>
        </button>
        <div class="action-group">
          <button v-if="originalUrl" class="btn-glass" @click="openOriginalModal">
            <ExternalLink :size="16" />
            원문 전체보기
          </button>
          <button class="main-add-btn" @click="goAddMedication">
            <Plus :size="18" />
            복용 중인 약 추가
          </button>
        </div>
      </nav>

      <!-- 헤더 카드 섹션 -->
      <header class="pill-header-grid">
        <div class="pill-visual-section">
          <!-- 인트로 스타일이 적용된 Visual Card -->
          <div class="visual-card glass-card">
            <div class="pill-image-wrapper">
              <div class="inner-glow"></div>
              <img v-if="medicine.image" :src="medicine.image" class="floating-img" alt="Pill Image" />
              <div v-else class="pill-placeholder floating-img">💊</div>
            </div>
            <div class="pill-tags">
              <span v-if="medicine.shape" class="info-tag">{{ medicine.shape }}</span>
              <span v-if="medicine.color" class="info-tag">{{ medicine.color }}</span>
            </div>
            <div class="pill-id-tag italic">IDENTIFICATION CODE: {{ medicine.itemSeq }}</div>
          </div>
        </div>

        <div class="pill-summary-section">
          <div class="badge">
            <Sparkles :size="12" class="icon-purple" />
            <span>MEDEAT ANALYSIS</span>
          </div>
          <h1 class="pill-main-name italic">{{ medicine.name }}</h1>

          <div class="summary-content-card glass-card">
            <h3 class="summary-label italic">MEDICATION INSIGHT</h3>
            
            <div class="quick-info-grid mb-6">
              <div class="info-box">
                <span class="label">성분</span>
                <span class="val">{{ medicine.genericName || '정보 없음' }}</span>
              </div>
              <div class="info-box">
                <span class="label">제조사</span>
                <span class="val">{{ medicine.company || '정보 없음' }}</span>
              </div>
              <div class="info-box">
                <span class="label">분류</span>
                <span class="val">{{ medicine.category || '참고문헌 확인' }}</span>
              </div>
              <div class="info-box">
                <span class="label">복용 연령</span>
                <span class="val">{{ medicine.ageRange || '전 연령' }}</span>
              </div>
            </div>

            <h3 class="summary-label italic mt-6">EFFICACY SUMMARY</h3>
            <div class="summary-scroll-area body-scroll">
              <p class="summary-text" v-html="renderOrGuide(medicine.efficacy)"></p>
            </div>
          </div>
        </div>
      </header>

      <!-- 상세 정보 섹션들 -->
      <div class="detail-sections-grid">
        <section class="info-card glass-card full-width">
          <div class="card-head">
            <History :size="20" class="icon-sky" />
            <h2 class="italic">USAGE & DOSAGE</h2>
          </div>
          <div class="card-body body-scroll" v-html="renderOrGuide(medicine.usage)"></div>
        </section>

        <section class="info-card glass-card">
          <div class="card-head">
            <ShieldAlert :size="20" class="icon-red" />
            <h2 class="italic">CONTRAINDICATION</h2>
          </div>
          <div class="card-body body-scroll" v-html="renderOrGuide(medicine.contraindication)"></div>
        </section>

        <section class="info-card glass-card">
          <div class="card-head">
            <AlertTriangle :size="20" class="icon-orange" />
            <h2 class="italic">PRECAUTIONS</h2>
          </div>
          <div class="card-body body-scroll" v-html="renderOrGuide(medicine.needCaution)"></div>
        </section>

        <section class="info-card glass-card full-width">
          <div class="card-head">
            <Activity :size="20" class="icon-sky" />
            <h2 class="italic">DRUG INTERACTIONS</h2>
          </div>
          <div class="card-body body-scroll" v-html="renderOrGuide(medicine.interaction)"></div>
        </section>

        <section class="info-card glass-card highlight-red">
          <div class="card-head">
            <Info :size="20" />
            <h2 class="italic">SIDE EFFECTS</h2>
          </div>
          <div class="card-body body-scroll" v-html="renderOrGuide(medicine.sideEffect)"></div>
        </section>

        <section class="info-card glass-card warning-card">
          <div class="card-head">
            <AlertTriangle :size="20" />
            <h2 class="italic">EMERGENCY GUIDE</h2>
          </div>
          <div class="card-body body-scroll">
            <ul class="emergency-list">
              <li>호흡곤란, 얼굴/혀 부종 (알레르기)</li>
              <li>심한 발진, 물집, 피부 벗겨짐</li>
              <li>의식 저하, 심한 어지러움</li>
              <li>심한 흉통, 지속되는 구토</li>
            </ul>
            <p class="emergency-hint">위 증상 발생 시 즉시 복용을 중단하고 내원하세요.</p>
          </div>
        </section>

        <section class="info-card glass-card full-width">
          <div class="card-head">
            <Package :size="20" class="icon-sky" />
            <h2 class="italic">STORAGE METHOD</h2>
          </div>
          <div class="card-body body-scroll" v-html="renderOrGuide(medicine.storage)"></div>
        </section>

        <section class="info-card glass-card full-width fold-section">
          <div class="fold-header" @click="isAtpnOpen = !isAtpnOpen">
            <div class="card-head">
              <FileText :size="20" class="icon-sky" />
              <h2 class="italic">DETAILED PRECAUTIONS (SUMMARY)</h2>
            </div>
            <button class="toggle-btn">{{ isAtpnOpen ? 'CLOSE' : 'EXPAND' }}</button>
          </div>
          <Transition name="slide">
            <div v-if="isAtpnOpen" class="card-body mt-4 body-scroll" v-html="renderOrGuide(medicine.atpnRaw)"></div>
          </Transition>
        </section>
      </div>

      <!-- 하단 주의사항 -->
      <footer class="page-footer-note">
        <AlertTriangle :size="24" class="footer-icon" />
        <div class="footer-text">
          <p>본 정보는 의약품안전나라 데이터를 기반으로 한 <strong>참고용</strong> 정보입니다.</p>
          <p>정확한 복용 결정 및 처방 변경은 반드시 전문의 또는 약사와 상의하시기 바랍니다.</p>
        </div>
      </footer>
    </main>

    <!-- 원문 전체보기 모달 -->
    <Transition name="fade">
      <div v-if="showOriginalModal" class="modal-overlay" @click.self="closeOriginalModal">
        <div class="original-modal-card">
          <header class="modal-head">
            <div class="mini-logo italic">MEDEAT WEBVIEW</div>
            <button class="close-btn" @click="closeOriginalModal"><X :size="24" /></button>
          </header>
          <div class="iframe-container">
            <iframe
              v-if="originalUrl"
              :src="originalUrl"
              title="K-Drug Source"
              loading="lazy"
              referrerpolicy="no-referrer"
            ></iframe>
          </div>
          <footer class="modal-foot">
            <a :href="originalUrl" target="_blank" class="external-link-btn">
              새 탭에서 열기 <ExternalLink :size="14" />
            </a>
          </footer>
        </div>
      </div>
    </Transition>
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

/* MEDI 전용 배경 Blobs */
.blob-medi {
  position: fixed;
  top: -10%;
  right: -10%;
  width: 70vw;
  height: 70vw;
  background: radial-gradient(circle, rgba(56, 189, 248, 0.15) 0%, transparent 70%);
  border-radius: 50%;
  filter: blur(140px);
  z-index: 0;
  pointer-events: none;
}

.content-wrapper {
  width: 100%;
  max-width: 1000px; 
  position: relative;
  z-index: 10;
}

.glass-card {
  background: rgba(255, 255, 255, 0.45);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 40px;
  box-shadow: 0 40px 100px rgba(0, 0, 0, 0.02);
}

/* 상단 내비게이션 */
.top-nav-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  background: none;
  border: none;
  font-weight: 850;
  font-size: 0.85rem;
  color: #94a3b8;
  cursor: pointer;
  transition: 0.3s;
}
.back-btn:hover { color: #1a1a1a; transform: translateX(-5px); }

.action-group { display: flex; gap: 12px; }

.btn-glass {
  padding: 12px 20px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid white;
  font-weight: 800;
  font-size: 0.85rem;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: 0.3s;
}
.btn-glass:hover { background: white; box-shadow: 0 10px 20px rgba(0,0,0,0.05); }

.main-add-btn {
  padding: 12px 24px;
  border-radius: 999px;
  background: #1a1a1a;
  color: white;
  border: none;
  font-weight: 800;
  font-size: 0.85rem;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: 0.4s;
}
.main-add-btn:hover { background: #38bdf8; transform: translateY(-3px); box-shadow: 0 10px 25px rgba(56, 189, 248, 0.3); }

/* 헤더 그리드 */
.pill-header-grid {
  display: grid;
  grid-template-columns: 340px 1fr;
  gap: 40px;
  margin-bottom: 40px;
}

.visual-card { padding: 30px 24px; text-align: center; }

/* 인트로 스타일 약 이미지 래퍼 */
.pill-image-wrapper {
  width: 100%;
  height: 300px;
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 35px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.05);
  position: relative;
}

.inner-glow {
  position: absolute;
  width: 80%;
  height: 80%;
  background: radial-gradient(circle, rgba(56, 189, 248, 0.15) 0%, transparent 70%);
  z-index: 0;
}

.floating-img {
  width: 85%;
  height: 85%;
  object-fit: contain;
  z-index: 1;
  filter: drop-shadow(0 15px 30px rgba(0,0,0,0.1));
  animation: floating 6s ease-in-out infinite;
}

@keyframes floating {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-15px); }
}

.pill-placeholder { font-size: 80px; display: flex; align-items: center; justify-content: center; }

.pill-tags { display: flex; justify-content: center; gap: 8px; margin-bottom: 16px; }
.info-tag {
  background: white;
  padding: 8px 16px;
  border-radius: 14px;
  font-size: 0.75rem;
  font-weight: 800;
  color: #38bdf8;
  box-shadow: 0 4px 12px rgba(56, 189, 248, 0.08);
  border: 1px solid rgba(56, 189, 248, 0.1);
}

.pill-id-tag { font-size: 0.65rem; font-weight: 900; color: #cbd5e1; letter-spacing: 0.1em; }

/* 약 이름 크기 최적화 */
.pill-main-name { font-size: 2rem; font-weight: 950; letter-spacing: -0.05em; margin-bottom: 24px; line-height: 1.1; }

.summary-content-card { padding: 24px; }
.summary-label { font-size: 0.85rem; font-weight: 900; color: #38bdf8; margin-bottom: 12px; letter-spacing: 0.1em; }

/* 요약 스크롤 영역 */
.body-scroll {
  max-height: 140px;
  overflow-y: auto;
  padding-right: 12px;
}
.body-scroll::-webkit-scrollbar { width: 4px; }
.body-scroll::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }

.summary-text { font-size: 0.95rem; line-height: 1.6; font-weight: 500; color: #444; }

/* 퀵 인포 그리드 */
.quick-info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.info-box { display: flex; flex-direction: column; gap: 4px; padding: 12px; background: rgba(255,255,255,0.4); border-radius: 16px; border: 1px solid rgba(255,255,255,0.6); }
.info-box .label { font-size: 0.65rem; font-weight: 850; color: #94a3b8; text-transform: uppercase; }
.info-box .val { font-size: 0.85rem; font-weight: 700; color: #1a1a1a; word-break: break-word; }

/* 상세 정보 카드 그리드 */
.detail-sections-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}
.full-width { grid-column: span 2; }

.info-card { padding: 28px; transition: 0.3s; }
.info-card:hover { transform: translateY(-5px); background: white; }

.card-head { display: flex; align-items: center; gap: 10px; margin-bottom: 16px; }
.card-head h2 { font-size: 1rem; font-weight: 900; letter-spacing: 0.05em; color: #1a1a1a; }

.card-body { font-size: 0.95rem; line-height: 1.7; color: #4b5563; font-weight: 500; }

.icon-sky { color: #38bdf8; }
.icon-red { color: #f43f5e; }
.icon-orange { color: #fb923c; }

.highlight-red { border-left: 5px solid #f43f5e; }
.warning-card { background: linear-gradient(135deg, #fff1f2 0%, #ffffff 100%); border: 1px solid #fecaca; }
.warning-card h2 { color: #b91c1c; }

.emergency-list { margin-bottom: 12px; padding-left: 20px; color: #b91c1c; font-weight: 700; }
.emergency-hint { font-size: 0.8rem; font-weight: 600; color: #ef4444; opacity: 0.8; }

.fold-header { display: flex; justify-content: space-between; align-items: center; cursor: pointer; }
.toggle-btn {
  background: #f1f5f9; border: none; padding: 6px 16px; border-radius: 999px;
  font-size: 0.75rem; font-weight: 900; color: #64748b; cursor: pointer;
}

/* 푸터 노트 */
.page-footer-note {
  margin-top: 50px; padding: 24px; border-radius: 30px;
  background: #f8fafc; border: 1px solid #e2e8f0;
  display: flex; align-items: center; gap: 16px;
}
.footer-icon { color: #94a3b8; }
.footer-text { font-size: 0.85rem; color: #64748b; line-height: 1.5; }
.footer-text strong { color: #1a1a1a; }

/* 모달 스타일 */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(15px); z-index: 2000; display: flex; align-items: center; justify-content: center; padding: 40px;
}
.original-modal-card {
  background: white; width: 100%; max-width: 1100px; height: 85vh;
  border-radius: 40px; overflow: hidden; display: flex; flex-direction: column;
  box-shadow: 0 50px 100px rgba(0,0,0,0.3);
}
.modal-head {
  padding: 16px 28px; display: flex; justify-content: space-between; align-items: center;
  background: #f8fafc; border-bottom: 1px solid #e2e8f0;
}
.mini-logo { font-size: 0.8rem; font-weight: 900; color: #38bdf8; letter-spacing: 0.1em; }
.iframe-container { flex: 1; background: white; }
.iframe-container iframe { width: 100%; height: 100%; border: none; }
.modal-foot { padding: 12px 28px; background: white; display: flex; justify-content: flex-end; }
.external-link-btn {
  font-size: 0.8rem; font-weight: 800; color: #38bdf8; text-decoration: none;
  display: flex; align-items: center; gap: 6px;
}

.italic { font-style: italic; }
.mt-4 { margin-top: 1rem; }
.mb-6 { margin-bottom: 1.5rem; }

/* 애니메이션 */
.slide-enter-active, .slide-leave-active { transition: all 0.4s ease; max-height: 400px; }
.slide-enter-from, .slide-leave-to { opacity: 0; max-height: 0; transform: translateY(-10px); }

@media (max-width: 1000px) {
  .pill-header-grid { grid-template-columns: 1fr; }
  .detail-sections-grid { grid-template-columns: 1fr; }
  .full-width { grid-column: auto; }
}
</style>