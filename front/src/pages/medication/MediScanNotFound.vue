<script setup>
import { useRouter, useRoute } from 'vue-router';
import { computed } from 'vue';
import { 
  SearchX, 
  Search, 
  Camera, 
  AlertTriangle, 
  Info,
  Check
} from 'lucide-vue-next';

const router = useRouter();
const route = useRoute();

/** 🌟 현재 모드 (MEDI 테마 적용) */
const mode = computed(() => route.query.mode || "MEDI");

const goRescan = () => {
  // MediScan 화면으로 이동 시 사진 촬영 영역 강조를 위한 힌트 전달
  router.push({ 
    name: 'medi-scan', 
    query: { mode: mode.value, focus: 'camera' } 
  });
};

const goSearchByName = () => {
  // MediScan 화면으로 이동 + 검색박스 포커스 및 강조 애니메이션용 힌트
  router.push({
    name: 'medi-scan',
    query: { mode: mode.value, focus: 'search' },
  });
};
</script>

<template>
  <div class="intro-container">
    <!-- 배경 장식 (은은한 MEDI 하늘색 글로우) -->
    <div class="blob-medi"></div>

    <main class="content-wrapper">
      <div class="scan-not-found-card glass-card">
        <!-- 헤더 섹션 -->
        <header class="page-header">
          <div class="badge">
            <SearchX :size="14" class="icon-sky" />
            <span>IDENTIFICATION FAILED</span>
          </div>
          <h1 class="hero-title italic">
            PILL NOT<br />
            <span class="gradient-text">FOUND.</span>
          </h1>
          <p class="hero-subtext">사진만으로는 이 알약을 정확히 식별하기 어려웠어요.</p>
        </header>

        <!-- 이유 섹션 -->
        <section class="info-section">
          <h2 class="section-title italic">POSSIBLE REASONS</h2>
          <ul class="reason-list">
            <li>
              <div class="dot"></div>
              <span>사진이 흐리거나 알약이 일부분만 보이는 경우</span>
            </li>
            <li>
              <div class="dot"></div>
              <span>빛 반사나 그림자로 인해 글자가 잘 보이지 않는 경우</span>
            </li>
            <li>
              <div class="dot"></div>
              <span>국내 허가 의약품이 아니거나, 아직 등록되지 않은 약인 경우</span>
            </li>
          </ul>
        </section>

        <!-- 액션 버튼 섹션 (양옆 배치 및 높이 조절) -->
        <section class="action-section mt-12">
          <h2 class="section-title italic">TRY AGAIN</h2>
          <div class="button-group">
            <button class="main-start-btn" @click="goRescan">
              다시 촬영하기
              <Camera :size="18" />
            </button>
            <button class="btn-glass-outline" @click="goSearchByName">
              이름으로 검색하기
              <Search :size="16" />
            </button>
          </div>
        </section>

        <!-- 촬영 팁 섹션 -->
        <section class="info-section mt-12">
          <div class="section-header-with-icon">
            <Info :size="18" class="icon-sky" />
            <h2 class="section-title italic">PHOTO TIPS</h2>
          </div>
          <ul class="tip-list">
            <li>
              <Check :size="14" class="check-icon" />
              <span>한 번에 한 종류의 알약만, 하얀 바탕 위에 올려 찍어주세요.</span>
            </li>
            <li>
              <Check :size="14" class="check-icon" />
              <span>알약 전체가 또렷하게 나오도록 가까이에서 촬영해 주세요.</span>
            </li>
            <li>
              <Check :size="14" class="check-icon" />
              <span>빛 반사와 그림자를 줄이기 위해 자연광이나 고른 조명을 사용해 주세요.</span>
            </li>
          </ul>
        </section>

        <!-- 안전 안내 (색상 톤 다운 및 은은하게 개선) -->
        <footer class="warn-box-glass mt-12">
          <div class="warn-header">
            <AlertTriangle :size="16" />
            <h2 class="italic">SAFETY NOTICE</h2>
          </div>
          <p class="warn-text">
            어떤 약인지 확실하지 않은 상태에서는 임의로 복용하지 않는 것이 안전합니다.
            처방받지 않은 약이거나, 정체가 불분명한 약일 경우에는 반드시 약국이나 의료기관에 문의해 주세요.
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
  max-width: 800px;
  position: relative;
  z-index: 10;
}

.glass-card {
  background: rgba(255, 255, 255, 0.45);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 50px;
  padding: 60px;
  box-shadow: 0 40px 100px rgba(0, 0, 0, 0.02);
}

/* Header 스타일 */
.page-header { text-align: center; margin-bottom: 50px; }
.badge {
  display: inline-flex; align-items: center; gap: 8px; padding: 6px 14px;
  background: white; border-radius: 999px; margin-bottom: 24px;
  font-size: 0.7rem; font-weight: 850; color: #38bdf8; border: 1.5px solid #f1f5f9;
}
.hero-title { font-size: 3.5rem; line-height: 0.95; font-weight: 950; letter-spacing: -0.05em; margin-bottom: 20px; }
.gradient-text {
  background: linear-gradient(to bottom right, #1a1a1a, #38bdf8);
  -webkit-background-clip: text; background-clip: text; color: transparent;
}
.hero-subtext { font-size: 1.1rem; opacity: 0.6; font-weight: 600; line-height: 1.5; }

/* 섹션 타이틀 */
.section-title { font-size: 0.9rem; font-weight: 900; color: #cbd5e1; letter-spacing: 0.1em; margin-bottom: 20px; display: block; }
.section-header-with-icon { display: flex; align-items: center; gap: 10px; margin-bottom: 20px; }
.section-header-with-icon .section-title { margin-bottom: 0; }

/* 리스트 스타일 */
.reason-list, .tip-list { list-style: none; padding: 0; }
.reason-list li {
  display: flex; align-items: center; gap: 14px; margin-bottom: 12px;
  font-size: 0.95rem; font-weight: 600; color: #475569;
}
.reason-list .dot { width: 6px; height: 6px; background: #38bdf8; border-radius: 50%; flex-shrink: 0; }

.tip-list li {
  display: flex; align-items: flex-start; gap: 12px; margin-bottom: 14px;
  font-size: 0.95rem; font-weight: 600; color: #475569; line-height: 1.5;
}
.check-icon { color: #38bdf8; margin-top: 2px; flex-shrink: 0; }

/* 버튼 그룹 (양옆 배치 및 컴팩트한 높이) */
.button-group { display: flex; gap: 12px; }

.main-start-btn {
  flex: 1;
  display: flex; align-items: center; justify-content: center; gap: 10px;
  padding: 14px 24px; background: #1a1a1a; color: white; border: none; border-radius: 999px;
  font-size: 1rem; font-weight: 850; cursor: pointer; transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}

.main-start-btn:hover { 
  background: #2a2a2a; 
  transform: translateY(-3px); 
  box-shadow: 0 10px 25px rgba(56, 189, 248, 0.15);
}

.btn-glass-outline {
  flex: 1;
  display: flex; align-items: center; justify-content: center; gap: 10px;
  padding: 14px 24px; background: rgba(255, 255, 255, 0.6); border: 1.5px solid #e2e8f0;
  border-radius: 999px; font-size: 0.95rem; font-weight: 800; color: #64748b; cursor: pointer; transition: 0.3s;
}
.btn-glass-outline:hover { 
  background: white; 
  border-color: #38bdf8; 
  color: #1a1a1a; 
  transform: translateY(-2px);
}

/* 안전 안내 (은은하게 개선) */
.warn-box-glass {
  background: rgba(241, 245, 249, 0.5); /* 연한 회색/하늘색 톤 배경 */
  border: 1px solid #e2e8f0;
  border-left: 4px solid #94a3b8; /* 차분한 회색 포인트 */
  border-radius: 24px;
  padding: 24px 30px;
}
.warn-header { display: flex; align-items: center; gap: 8px; color: #64748b; margin-bottom: 12px; }
.warn-header h2 { font-size: 0.8rem; font-weight: 900; letter-spacing: 0.1em; }
.warn-text { font-size: 0.85rem; color: #475569; line-height: 1.6; font-weight: 600; }

.italic { font-style: italic; }
.icon-sky { color: #38bdf8; }
.mt-12 { margin-top: 3rem; }

@media (max-width: 640px) {
  .hero-title { font-size: 2.8rem; }
  .glass-card { padding: 40px 24px; border-radius: 40px; }
  .button-group { flex-direction: column; }
}
</style>