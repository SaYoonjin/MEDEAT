<script setup>
import { ref, onMounted, onBeforeUnmount, computed } from "vue";
import { useRouter } from "vue-router";
import authApi from "@/api/auth";
import { 
  Sparkles, 
  ArrowRight, 
  Coffee, 
  Pill, 
  Activity, 
  BarChart3, 
  X, 
  Zap, 
  Target, 
  ChevronRight,
  ShieldCheck,
  Utensils,
  ClipboardCheck,
  Clock,
  HeartPulse,
  Check,
  Heart
} from "lucide-vue-next";

const router = useRouter();
const nickname = ref("Guest");
const scrolled = ref(0);
const showAuthModal = ref(false);

// 🌟 오프닝 및 스플래시 제어 상태
const isOpening = ref(true);
const isSplashActive = ref(false);

// 섹션 참조를 위한 ref (앵커 스크롤용)
const eatSection = ref(null);
const mediSection = ref(null);
const insightSection = ref(null);

const handleScroll = () => {
  const position = window.scrollY;
  const height = document.documentElement.scrollHeight - window.innerHeight;
  scrolled.value = position / height;
};

// 특정 섹션으로 부드럽게 스크롤
const scrollToSection = (sectionRef) => {
  if (sectionRef) {
    sectionRef.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }
};

const handleMainAction = () => {
  if (nickname.value === 'Guest') {
    showAuthModal.value = true;
  } else {
    router.push({ path: "/dashboard", query: { mode: "EAT" } });
  }
};

onMounted(async () => {
  // 🌟 오프닝 시퀀스 타이밍 제어
  setTimeout(() => {
    isOpening.value = false;
    // 레이어가 완전히 올라간 후(1.2s) 배경 Splash 발생
    setTimeout(() => {
      isSplashActive.value = true;
    }, 1200);
  }, 2200);

  try {
    const res = await authApi.checkLogin();
    nickname.value = res.data.nickname;
  } catch {
    nickname.value = "Guest";
  }
  window.addEventListener("scroll", handleScroll);
});

onBeforeUnmount(() => {
  window.removeEventListener("scroll", handleScroll);
});
</script>

<template>
  <div class="page-wrapper" :class="{ 'is-puddled': !isSplashActive }">
    
    <!-- 🌟 00. Opening Sequence (최신 컬러 드로잉 버전) -->
    <Transition name="opening-exit">
      <div v-if="isOpening" class="opening-sequence">
        <div class="opening-content">
          <!-- 중앙 로고 드로잉 (처음부터 컬러) -->
          <div class="logo-drawing-wrapper">
            <svg class="heart-svg" viewBox="-2 -2 28 28" fill="none">
              <defs>
                <linearGradient id="opening-heart-grad" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stop-color="#a3e635" />
                  <stop offset="100%" stop-color="#38bdf8" />
                </linearGradient>
              </defs>
              <path class="heart-path" d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" />
            </svg>
          </div>

          <!-- 브랜드 네임 -->
          <h2 class="opening-brand font-black tracking-tighter uppercase italic gradient-text-logo">
            MEDEAT
          </h2>
          
          <div class="loading-dots mt-6">
            <span v-for="i in 3" :key="i" class="dot" :style="{ animationDelay: `${(i-1)*0.2}s` }"></span>
          </div>
        </div>
      </div>
    </Transition>

    <!-- 🌟 배경 장식 (Splash 연출 포함) -->
    <div class="background-decorations">
      <div class="blob blob-eat"></div>
      <div class="blob blob-medi"></div>
    </div>

    <!-- 🌟 HUD 내비게이션 (Header.vue 메뉴 스타일 적용) -->
    <header class="glass-nav">
      <div class="nav-content">
        <h1 class="logo italic" @click="router.push('/')">MEDEAT</h1>
        <!-- 🌟 font-black italic 제거 및 Header.vue 스타일 동기화 -->
        <div class="nav-links">
          <span class="nav-anchor" @click="scrollToSection(eatSection)">EAT</span>
          <span class="dot"></span>
          <span class="nav-anchor" @click="scrollToSection(mediSection)">MEDI</span>
          <span class="dot"></span>
          <span class="nav-anchor" @click="scrollToSection(insightSection)">INSIGHT</span>
        </div>
        <div class="nav-user">
          <template v-if="nickname !== 'Guest'">
            <span class="user-nickname font-black">{{ nickname }}님</span>
            <button class="nav-start-btn font-black" @click="handleMainAction">DASHBOARD</button>
          </template>
          <template v-else>
            <button class="nav-start-btn outline font-black" @click="showAuthModal = true">JOIN NOW</button>
          </template>
        </div>
      </div>
    </header>

    <!-- 🌟 01. Hero Section -->
    <section class="hero-section">
      <div 
        class="hero-content"
        :style="{ 
          opacity: 1 - scrolled * 5,
          transform: `scale(${1 - scrolled * 0.05}) translateY(${-scrolled * 120}px)` 
        }"
      >
        <div class="badge-premium">
          <Sparkles :size="14" class="text-primary" />
          <span class="font-black italic">SYNC YOUR HEALTH RHYTHM</span>
        </div>
        <h2 class="hero-title font-black italic tracking-tighter">
          YOUR LIFE,<br />
          <span class="gradient-text">SYNCHRONIZED.</span>
        </h2>
        <p class="hero-subtext font-black">
          식단, 복약, 그리고 컨디션 관리.<br />
          가장 세련된 방식으로 당신의 건강 데이터를 동기화하세요.
        </p>

        <div class="hero-cta mt-12">
          <button class="main-start-btn font-black" @click="handleMainAction">
            <span class="text-white">GET STARTED</span>
            <ArrowRight :size="22" class="text-white" />
          </button>
        </div>
      </div>
      
      <div class="scroll-indicator" :style="{ opacity: 1 - scrolled * 15 }">
        <div class="line"></div>
      </div>
    </section>

    <!-- 🌟 02. Feature Module Cards -->
    <section class="feature-section">
      <div class="section-label-row centered mb-16">
        <h3 class="section-tag font-black italic">CORE MODULES</h3>
      </div>
      <div class="cards-grid">
        <div class="feature-premium-card glass-effect animate-in" @click="scrollToSection(eatSection)">
          <div class="card-glow eat"></div>
          <div class="icon-orb eat"><Coffee :size="24" /></div>
          <h3 class="card-title font-black italic tracking-tighter">EAT</h3>
          <p class="card-desc font-black">영양과 미식의 정밀한 기록</p>
          <div class="card-action-hint"><ChevronRight :size="18" /></div>
        </div>

        <div class="feature-premium-card glass-effect animate-in" style="animation-delay: 0.1s" @click="scrollToSection(mediSection)">
          <div class="card-glow medi"></div>
          <div class="icon-orb medi"><Pill :size="24" /></div>
          <h3 class="card-title font-black italic tracking-tighter">MEDI</h3>
          <p class="card-desc font-black">놓치지 않는 완벽한 복약 스케줄</p>
          <div class="card-action-hint"><ChevronRight :size="18" /></div>
        </div>

        <div class="feature-premium-card glass-effect animate-in" style="animation-delay: 0.2s" @click="scrollToSection(insightSection)">
          <div class="card-glow purple"></div>
          <div class="icon-orb purple"><BarChart3 :size="24" /></div>
          <h3 class="card-title font-black italic tracking-tighter">INSIGHT</h3>
          <p class="card-desc font-black">데이터가 알려주는 몸의 언어</p>
          <div class="card-action-hint"><ChevronRight :size="18" /></div>
        </div>
      </div>
    </section>

    <!-- 🌟 03. Detailed Section: EAT -->
    <section ref="eatSection" class="detail-explain-section">
      <div class="detail-container glass-effect">
        <div class="detail-visual">
          <div class="floating-mockup eat-theme">
            <Utensils :size="80" class="icon-main" />
            <div class="data-particles">
              <div class="p-item kcal">540 kcal</div>
              <div class="p-item carb">Carb 45%</div>
            </div>
          </div>
        </div>
        <div class="detail-text-content">
          <span class="detail-badge eat">PRECISION NUTRITION</span>
          <h3 class="detail-title font-black italic tracking-tighter">당신의 식사를<br/>데이터로 정의하다.</h3>
          <p class="detail-desc font-black">
            단순한 칼로리 기록을 넘어 탄단지 밸런스와 AI 분석을 통해<br/>
            최적의 식단 가이드를 제안합니다. 한 입의 가치를 숫자로 확인하세요.
          </p>
          <ul class="detail-feature-list font-black">
            <li class="feature-chip glass-effect">
              <div class="check-icon-box eat"><Check :size="14" /></div>
              <span>실시간 영양 성분 해체 분석</span>
            </li>
            <li class="feature-chip glass-effect">
              <div class="check-icon-box eat"><Check :size="14" /></div>
              <span>AI 기반 맞춤형 식단 큐레이션</span>
            </li>
          </ul>
        </div>
      </div>
    </section>

    <!-- 🌟 04. Detailed Section: MEDI -->
    <section ref="mediSection" class="detail-explain-section reverse">
      <div class="detail-container glass-effect">
        <div class="detail-text-content">
          <span class="detail-badge medi">SMART MEDICATION</span>
          <h3 class="detail-title font-black italic tracking-tighter">정교한 복약,<br/>빈틈없는 케어.</h3>
          <p class="detail-desc font-black">
            약물의 상호작용 분석과 스마트 알림 시스템으로<br/>
            당신의 건강 루틴에 오차 없는 정확함을 더합니다.
          </p>
          <ul class="detail-feature-list font-black">
            <li class="feature-chip glass-effect">
              <div class="check-icon-box medi"><Check :size="14" /></div>
              <span>의약품 안전 정보 실시간 대조</span>
            </li>
            <li class="feature-chip glass-effect">
              <div class="check-icon-box medi"><Check :size="14" /></div>
              <span>다중 복용 스케줄 통합 관리</span>
            </li>
          </ul>
        </div>
        <div class="detail-visual">
          <div class="floating-mockup medi-theme">
            <HeartPulse :size="80" class="icon-main" />
            <div class="data-particles">
              <div class="p-item medi">08:00 AM</div>
              <div class="p-item safe">Interaction Safe</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 🌟 05. Analysis Section: INSIGHT -->
    <section ref="insightSection" class="insight-section">
      <div class="insight-glass-box glass-effect">
        <div class="insight-content">
          <span class="small-label font-black italic">THE ANALYSIS</span>
          <h4 class="section-title font-black italic tracking-tighter">SMART<br />REPORTING.</h4>
          <p class="section-desc font-black">
            자동으로 수집된 당신의 하루는<br />
            AI를 통해 매주 아름다운 분석 리포트로 재탄생합니다.
          </p>
          <button class="btn-text-link font-black italic" @click="handleMainAction">
            EXPLORE INSIGHTS <ArrowRight :size="16" />
          </button>
        </div>
        <div class="insight-visual">
          <div class="mockup-container glass-effect">
            <div class="mockup-header">
              <div class="mock-dot"></div>
              <div class="mock-bar"></div>
            </div>
            <div class="mockup-body">
              <div class="chart-visual-mock bg-primary-gradient"></div>
              <div class="lines-mock mt-6">
                <div class="line-m"></div>
                <div class="line-m w-2/3"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 🌟 Minimal Footer -->
    <footer class="minimal-footer mt-48">
      <div class="footer-content">
        <span class="footer-logo font-black italic">MEDEAT</span>
        <div class="footer-links-row font-black">
          <span>PRIVACY</span>
          <span>TERMS</span>
          <span>CONTACT</span>
        </div>
        <span class="footer-copy font-black opacity-20">© 2025 ALL RIGHTS RESERVED.</span>
      </div>
    </footer>

    <!-- 🌟 Auth Choice Modal -->
    <Transition name="fade">
      <div v-if="showAuthModal" class="modal-overlay" @click.self="showAuthModal = false">
        <div class="auth-modal-card glass-card">
          <button class="close-btn" @click="showAuthModal = false">
            <X :size="24" />
          </button>
          <div class="modal-header">
            <div class="mini-logo font-black italic">MEDEAT</div>
            <h3 class="modal-title font-black italic">GET<br/>STARTED.</h3>
            <p class="modal-subtitle font-black">건강한 리듬을 동기화할 준비가 되셨나요?</p>
          </div>
          <div class="auth-btn-group mt-10">
            <button class="auth-btn login-btn font-black" @click="router.push('/login')">LOGIN</button>
            <button class="auth-btn signup-btn font-black" @click="router.push('/signup')">CREATE ACCOUNT</button>
          </div>
          <p class="modal-footer-text font-black"><ShieldCheck :size="12" /> SECURE AUTHENTICATION</p>
        </div>
      </div>
    </Transition>
  </div>
</template>

<style scoped>
.page-wrapper {
  background-color: #f8f9fc;
  color: #1a1a1a;
  overflow-x: hidden;
  position: relative;
}

/* 🌟 00. Opening Sequence Styles */
.opening-sequence {
  position: fixed; inset: 0; background-color: rgba(248, 249, 252, 0.4); z-index: 9999;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  backdrop-filter: blur(15px);
}
.opening-content { display: flex; flex-direction: column; align-items: center; }

.logo-drawing-wrapper { position: relative; width: 140px; height: 140px; margin-bottom: 24px; }
.heart-svg { width: 100%; height: 100%; overflow: visible; }
.heart-path {
  stroke-dasharray: 80;
  stroke-dashoffset: 80;
  stroke-width: 1.5;
  stroke: url(#opening-heart-grad);
  fill: rgba(163, 230, 53, 0.05);
  animation: drawHeart 2.2s cubic-bezier(0.445, 0.05, 0.55, 0.95) forwards;
}

@keyframes drawHeart {
  0% { stroke-dashoffset: 80; fill: transparent; }
  100% { stroke-dashoffset: 0; fill: rgba(163, 230, 53, 0.08); }
}

.opening-brand { 
  font-size: 3rem; 
  opacity: 0;
  transform: translateY(20px);
  animation: fadeUpLogo 0.8s ease-out 1.5s forwards;
}
.gradient-text-logo {
  background: linear-gradient(to right, #a3e635, #38bdf8);
  -webkit-background-clip: text; background-clip: text; color: transparent;
}

@keyframes fadeUpLogo {
  to { opacity: 1; transform: translateY(0); }
}

.loading-dots { display: flex; gap: 8px; }
.dot { width: 6px; height: 6px; background-color: #a3e635; border-radius: 50%; animation: pulse 1s infinite alternate; }

.opening-exit-leave-active { transition: transform 1.2s cubic-bezier(0.85, 0, 0.15, 1); }
.opening-exit-leave-to { transform: translateY(-100%); }

@keyframes pulse { from { opacity: 0.2; } to { opacity: 0.6; } }

/* 🌟 배경 장식 Blobs & Splash 연출 (타이밍 핵심) */
.background-decorations { position: fixed; inset: 0; z-index: 0; pointer-events: none; }
.blob { 
  position: fixed; 
  border-radius: 50%; 
  filter: blur(160px); 
  opacity: 0.25; 
  transition: all 1.8s cubic-bezier(0.19, 1, 0.22, 1); 
}

/* is-puddled 상태 (오프닝 중 + 레이어가 아직 다 안 올라갔을 때): 중앙 밀집 */
.is-puddled .blob-eat { 
  top: 50%; left: 50%; 
  width: 75vw; height: 75vw; 
  transform: translate(-50%, -50%) scale(1.1);
  background: radial-gradient(circle, rgba(163, 230, 53, 0.9) 0%, transparent 70%); 
} 
.is-puddled .blob-medi { 
  top: 50%; left: 50%; 
  width: 70vw; height: 70vw; 
  transform: translate(-50%, -50%) scale(0.95);
  background: radial-gradient(circle, rgba(56, 189, 248, 0.9) 0%, transparent 70%); 
}

/* Splash!: 레이어가 다 올라간 시점(isSplashActive: true)에 제자리로 튐 */
.blob-eat { top: -10%; left: -10%; width: 60vw; height: 60vw; background: radial-gradient(circle, rgba(163, 230, 53, 0.8) 0%, transparent 70%); } 
.blob-medi { bottom: -15%; right: -10%; width: 55vw; height: 55vw; background: radial-gradient(circle, rgba(56, 189, 248, 0.7) 0%, transparent 70%); }

.glass-effect { background: rgba(255, 255, 255, 0.45); backdrop-filter: blur(25px); border: 1px solid rgba(255, 255, 255, 0.7); }

/* 🌟 Header: HUD 스타일 리뉴얼 */
.glass-nav {
  position: fixed; top: 24px; left: 50%; transform: translateX(-50%);
  width: 92%; max-width: 1300px; padding: 14px 40px; border-radius: 999px; z-index: 1000;
  background: rgba(255, 255, 255, 0.4); backdrop-filter: blur(20px); border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.05);
}
.nav-content { display: flex; justify-content: space-between; align-items: center; }
.logo { font-weight: 950; font-size: 1.5rem; letter-spacing: -2px; cursor: pointer; }

/* 🌟 nav-links: Header.vue 스타일로 동기화 */
.nav-links { 
  display: flex; 
  align-items: center; 
  gap: 32px; 
}
.nav-anchor { 
  font-size: 0.75rem; 
  font-weight: 800; 
  letter-spacing: 0.18em; 
  opacity: 0.55; 
  cursor: pointer; 
  transition: all 0.3s ease; 
  white-space: nowrap;
}
.nav-anchor:hover { opacity: 1; color: #7c4dff; transform: scale(1.05); }
.dot { width: 4px; height: 4px; background: #cbd5e1; border-radius: 50%; opacity: 0.4; }

.nav-user { display: flex; align-items: center; gap: 16px; }
.user-nickname { font-size: 0.8rem; font-weight: 700; opacity: 0.6; }
.nav-start-btn { background: #1a1a1a; color: white; border: none; padding: 10px 24px; border-radius: 999px; font-size: 0.75rem; cursor: pointer; transition: 0.3s; }
.nav-start-btn.outline { background: transparent; color: #1a1a1a; border: 1.5px solid #1a1a1a; }
.nav-start-btn:hover { background: #7c4dff; transform: translateY(-2px); box-shadow: 0 8px 20px rgba(124, 77, 255, 0.2); }

/* Hero Section */
.hero-section { height: 100vh; display: flex; flex-direction: column; align-items: center; justify-content: center; position: relative; z-index: 10; }
.hero-content { text-align: center; }
.badge-premium { display: inline-flex; align-items: center; gap: 10px; padding: 8px 20px; background: white; border-radius: 999px; margin-bottom: 32px; font-size: 0.75rem; color: #7c4dff; border: 1.5px solid #f1f5f9; }
.hero-title { font-size: clamp(3.5rem, 11vw, 7.5rem); line-height: 0.85; margin-bottom: 32px; color: #1a1a1a; }
.gradient-text { background: linear-gradient(to right, #1a1a1a, #7c4dff, #38bdf8); -webkit-background-clip: text; background-clip: text; color: transparent; }
.hero-subtext { font-size: 1.3rem; opacity: 0.4; line-height: 1.5; letter-spacing: -0.01em; margin-bottom: 50px; }

/* Main Start Button */
.main-start-btn { 
  display: inline-flex; align-items: center; gap: 20px; padding: 24px 60px; 
  background: #1a1a1a; color: #ffffff !important; border: none; border-radius: 999px; 
  font-size: 1.2rem; cursor: pointer; transition: 0.4s cubic-bezier(0.23, 1, 0.32, 1); 
  box-shadow: 0 20px 40px rgba(0,0,0,0.1); 
}
.text-white { color: #ffffff !important; }

.main-start-btn:hover { transform: translateY(-8px) scale(1.03); background: #1a1a1a; box-shadow: 0 30px 60px rgba(0,0,0,0.15); }

/* Feature Cards */
.feature-section { padding: 120px 24px; z-index: 10; max-width: 1300px; margin: 0 auto; }
.section-tag { font-size: 1rem; color: #cbd5e1; letter-spacing: 0.2em; }
.cards-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 32px; }

.feature-premium-card { padding: 50px 40px; border-radius: 50px; position: relative; overflow: hidden; transition: 0.5s; cursor: pointer; }
.feature-premium-card:hover { transform: translateY(-15px); background: white; border-color: #7c4dff; }
.card-glow { position: absolute; top: -20%; right: -20%; width: 150px; height: 150px; border-radius: 50%; opacity: 0.1; filter: blur(40px); }
.card-glow.eat { background: #a3e635; }
.card-glow.medi { background: #38bdf8; }
.card-glow.purple { background: #7c4dff; }
.icon-orb { width: 64px; height: 64px; border-radius: 20px; display: flex; align-items: center; justify-content: center; color: white; margin-bottom: 40px; }
.icon-orb.eat { background: linear-gradient(135deg, #a3e635, #4d7c0f); }
.icon-orb.medi { background: linear-gradient(135deg, #38bdf8, #1d4ed8); }
.icon-orb.purple { background: linear-gradient(135deg, #7c4dff, #6b21ff); }
.card-title { font-size: 2.5rem; margin-bottom: 12px; }
.card-desc { font-size: 1rem; opacity: 0.4; line-height: 1.4; }
.card-action-hint { margin-top: 32px; color: #cbd5e1; transition: 0.3s; }
.feature-premium-card:hover .card-action-hint { color: #7c4dff; transform: translateX(5px); }

/* Detailed Section */
.detail-explain-section { padding: 80px 24px; z-index: 10; max-width: 1300px; margin: 0 auto; }
.detail-container { border-radius: 70px; padding: 100px; display: grid; grid-template-columns: 1fr 1fr; gap: 80px; align-items: center; }
.reverse .detail-container { grid-template-columns: 1fr 1fr; }

.detail-visual { display: flex; justify-content: center; }
.floating-mockup { width: 300px; height: 300px; border-radius: 60px; background: white; display: flex; align-items: center; justify-content: center; position: relative; box-shadow: 0 30px 60px rgba(0,0,0,0.05); animation: float 6s ease-in-out infinite; }
.icon-main { opacity: 0.1; }
.eat-theme .icon-main { color: #a3e635; }
.medi-theme .icon-main { color: #38bdf8; }

.data-particles { position: absolute; inset: 0; }
.p-item { position: absolute; padding: 10px 20px; border-radius: 12px; font-weight: 800; font-size: 0.85rem; background: white; box-shadow: 0 10px 20px rgba(0,0,0,0.05); }
.p-item.kcal { top: 20%; right: -20px; color: #f43f5e; }
.p-item.carb { bottom: 20%; left: -20px; color: #a3e635; }
.p-item.medi { top: 20%; left: -20px; color: #38bdf8; }
.p-item.safe { bottom: 20%; right: -20px; color: #10b981; }

.detail-badge { display: inline-block; padding: 6px 14px; border-radius: 6px; font-size: 0.7rem; font-weight: 900; margin-bottom: 24px; letter-spacing: 0.1em; }
.detail-badge.eat { background: rgba(163, 230, 53, 0.1); color: #4d7c0f; }
.detail-badge.medi { background: rgba(56, 189, 248, 0.1); color: #1d4ed8; }
.detail-title { font-size: 3.2rem; line-height: 1.1; margin-bottom: 32px; }
.detail-desc { font-size: 1.1rem; opacity: 0.5; line-height: 1.6; margin-bottom: 40px; }

.detail-feature-list { list-style: none; padding: 0; display: flex; flex-direction: column; gap: 16px; }
.feature-chip { 
  display: flex; align-items: center; gap: 16px; font-size: 1rem; 
  background: rgba(255,255,255,0.4); padding: 14px 24px; border-radius: 20px;
  border: 1px solid rgba(255,255,255,0.6); transition: 0.3s;
}
.feature-chip:hover { background: white; transform: translateX(10px); }

.check-icon-box { 
  width: 28px; height: 28px; border-radius: 50%; display: flex; align-items: center; justify-content: center;
  color: white; flex-shrink: 0;
}
.check-icon-box.eat { background: #a3e635; box-shadow: 0 4px 10px rgba(163, 230, 53, 0.3); }
.check-icon-box.medi { background: #38bdf8; box-shadow: 0 4px 10px rgba(56, 189, 248, 0.3); }

/* Insight Section */
.insight-glass-box { max-width: 1300px; margin: 0 auto; border-radius: 70px; padding: 100px; display: grid; grid-template-columns: 1fr 1.2fr; gap: 80px; align-items: center; }
.small-label { display: block; font-size: 0.85rem; color: #7c4dff; letter-spacing: 0.2em; margin-bottom: 24px; }
.section-title { font-size: 4.5rem; line-height: 0.9; margin-bottom: 32px; color: #1a1a1a; }
.section-desc { font-size: 1.2rem; opacity: 0.4; line-height: 1.6; margin-bottom: 48px; }

.btn-text-link { display: inline-flex; align-items: center; gap: 12px; font-size: 1.1rem; background: none; border: none; cursor: pointer; color: #1a1a1a; padding: 0; }
.btn-text-link:hover { color: #7c4dff; }

.mockup-container { padding: 32px; border-radius: 40px; background: white; box-shadow: 0 30px 80px rgba(0,0,0,0.05); animation: float 6s ease-in-out infinite; }
.mockup-header { display: flex; gap: 10px; margin-bottom: 48px; }
.mock-dot { width: 10px; height: 10px; border-radius: 50%; background: #f1f5f9; }
.mock-bar { height: 10px; width: 60px; border-radius: 5px; background: #f8fafc; }
.chart-visual-mock { height: 240px; border-radius: 20px; background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%); position: relative; }
.chart-visual-mock::after { content: ''; position: absolute; bottom: 40px; left: 40px; right: 40px; height: 120px; background: linear-gradient(90deg, #7c4dff 0%, #38bdf8 100%); opacity: 0.2; border-radius: 10px; }
.line-m { height: 12px; background: #f8fafc; border-radius: 6px; margin-bottom: 12px; }

@keyframes float { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-25px); } }

/* Auth Modal */
.modal-overlay { position: fixed; inset: 0; background: rgba(0, 0, 0, 0.5); backdrop-filter: blur(15px); z-index: 2000; display: flex; align-items: center; justify-content: center; padding: 24px; }
.auth-modal-card { background: white; width: 100%; max-width: 440px; padding: 70px 50px 60px; border-radius: 60px; box-shadow: 0 50px 100px rgba(0, 0, 0, 0.2); position: relative; text-align: center; }
.close-btn { position: absolute; top: 32px; right: 32px; background: none; border: none; opacity: 0.3; cursor: pointer; transition: 0.3s; }
.close-btn:hover { opacity: 1; transform: rotate(90deg); }
.mini-logo { font-size: 0.9rem; color: #7c4dff; letter-spacing: 4px; margin-bottom: 24px; }
.modal-title { font-size: 3rem; font-weight: 950; margin-bottom: 16px; line-height: 0.9; color: #1a1a1a; }
.modal-subtitle { font-size: 1.1rem; color: #64748b; margin-bottom: 48px; }

.auth-btn-group { display: flex; flex-direction: column; gap: 16px; }
.auth-btn { padding: 20px; border-radius: 20px; font-size: 1.05rem; cursor: pointer; transition: 0.3s; border: none; }
.login-btn { background: #1a1a1a; color: white; }
.login-btn:hover { background: #000; transform: translateY(-3px); box-shadow: 0 10px 25px rgba(0,0,0,0.2); }
.signup-btn { background: #f8fafc; color: #1a1a1a; border: 1.5px solid #f1f5f9; }
.signup-btn:hover { background: white; border-color: #7c4dff; color: #7c4dff; transform: translateY(-3px); }
.modal-footer-text { margin-top: 40px; font-size: 0.7rem; color: #cbd5e1; letter-spacing: 0.1em; display: flex; align-items: center; justify-content: center; gap: 8px; }

/* Minimal Footer */
.minimal-footer { padding: 80px 40px; border-top: 1px solid rgba(0,0,0,0.03); }
.footer-content { max-width: 1300px; margin: 0 auto; display: flex; justify-content: space-between; align-items: center; }
.footer-logo { font-size: 1.3rem; letter-spacing: -1px; opacity: 0.9; }
.footer-links-row { display: flex; gap: 32px; font-size: 0.75rem; color: #94a3b8; }
.footer-copy { font-size: 0.7rem; letter-spacing: 0.1em; }

/* Utilities */
.font-black { font-weight: 950; }
.italic { font-style: italic; }
.tracking-tighter { letter-spacing: -0.06em; }
.text-primary { color: #7c4dff; }
.scroll-indicator { position: absolute; bottom: 40px; height: 70px; width: 1px; left: 50%; transform: translateX(-50%); }
.line { width: 100%; height: 100%; background: linear-gradient(to bottom, #1a1a1a, transparent); }

.animate-in { animation: slideUp 0.8s cubic-bezier(0.23, 1, 0.32, 1) forwards; opacity: 0; }
@keyframes slideUp { from { opacity: 0; transform: translateY(40px); } to { opacity: 1; transform: translateY(0); } }

@media (max-width: 1100px) {
  .hero-title { font-size: 4.5rem; }
  .cards-grid, .detail-container, .insight-glass-box { grid-template-columns: 1fr; gap: 40px; padding: 60px 40px; }
  .nav-links { display: none; }
}
</style>