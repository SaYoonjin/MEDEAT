<script setup>
import { ref, onMounted, computed } from 'vue';
import userApi from '@/api/user';
import { useRouter, useRoute } from 'vue-router';
import axios from '@/api/axios';
import { 
  Sparkles, 
  User, 
  Phone, 
  Activity, 
  Target, 
  Bell, 
  ArrowRight, 
  Mail, 
  Baby,
  ChevronRight
} from 'lucide-vue-next';

const router = useRouter();
const route = useRoute();

/* =========================
   목표 옵션 (라벨 변환용)
========================= */
const GOAL_OPTIONS = [
  { value: "CUT", label: "체중 감량" },
  { value: "BULK", label: "근육 증량" },
  { value: "HEALTH", label: "건강 관리" },
  { value: "HABIT", label: "식습관 개선" },
  { value: "FIT", label: "체력 증진" },
  { value: "DISEASE", label: "질환 관리" },
  { value: "CUSTOM", label: "기타(직접 입력)" },
];

/* =========================
   상태 관리
========================= */
const currentMode = computed(() => route.query.mode || "EAT");
const user = ref({});
const pushEnabled = ref(false);
const profileImage = '/img/profile.jpg';

// 목표 라벨 계산
const displayGoal = computed(() => {
  const goal = user.value.goalType;
  if (!goal) return "-";
  const matched = GOAL_OPTIONS.find(o => o.value === goal || o.label === goal);
  return matched ? matched.label : goal;
});

const loadUserInfo = async () => {
  try {
    const res = await userApi.getMyInfo();
    user.value = res.data;
    pushEnabled.value = !!res.data.pushEnabled;
  } catch (error) {
    console.error("데이터 로드 실패:", error);
  }
};

const togglePush = async () => {
  try {
    await axios.post('/auth/push-enabled', { enabled: pushEnabled.value });
  } catch (error) {
    console.error("알림 설정 변경 실패");
  }
};

// 페이지 이동 함수 (name 기반으로 수정)
const goToEditForm = () => {
  router.push({ name: 'mypage-edit', query: { mode: currentMode.value } });
};

onMounted(loadUserInfo);
</script>

<template>
  <div class="intro-container">
    <!-- 배경 장식 (은은한 하이브리드 광채) -->
    <div class="blob blob-eat"></div>
    <div class="blob blob-medi"></div>

    <main class="content-wrapper">
      <div class="profile-card glass-card">
        <!-- Header 섹션: 프로필 이미지 및 기본 정보 -->
        <header class="profile-header">
          <div class="badge large-badge">
            <Sparkles :size="14" class="icon-purple" />
            <span>MY INFO</span>
          </div>
          
          <div class="profile-avatar-section">
            <div class="avatar-wrapper">
              <img :src="profileImage" class="profile-img" alt="Profile" />
              <div class="status-indicator" :class="{ 'is-active': user.pregnantStatus === 'yes' }"></div>
            </div>
            <h2 class="profile-name italic">
              {{ user.nickname || user.name }}<span class="gradient-dot">.</span>
            </h2>
            <div class="email-tag">
              <Mail :size="14" />
              <span>{{ user.email }}</span>
            </div>
          </div>
        </header>

        <!-- Info Grid: 상세 신체 정보 -->
        <div class="info-section">
          <h3 class="section-title italic">BODY STATS</h3>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-icon"><User :size="18" /></div>
              <div class="stat-content">
                <span class="stat-label">이름</span>
                <span class="stat-value">{{ user.name }}</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon"><Phone :size="18" /></div>
              <div class="stat-content">
                <span class="stat-label">연락처</span>
                <span class="stat-value">{{ user.phone }}</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon"><Activity :size="18" /></div>
              <div class="stat-content">
                <span class="stat-label">신체 조건</span>
                <span class="stat-value">{{ user.gender }} · {{ user.age }}세 · {{ user.height }}cm</span>
              </div>
            </div>
            <div class="stat-item highlight-item">
              <div class="stat-icon"><Target :size="18" /></div>
              <div class="stat-content">
                <span class="stat-label">현재 목표</span>
                <span class="stat-value gradient-text">{{ displayGoal.toUpperCase() }}</span>
              </div>
            </div>
          </div>

        </div>

        <!-- Action Section: 설정 및 이동 -->
        <footer class="action-section">
          <div class="setting-card">
            <div class="setting-info">
              <div class="setting-icon"><Bell :size="18" /></div>
              <div class="setting-text">
                <span class="setting-label">푸시 알림</span>
                <span class="setting-desc">건강 리포트 및 복약 알림</span>
              </div>
            </div>
            <label class="modern-switch">
              <input type="checkbox" v-model="pushEnabled" @change="togglePush" />
              <span class="switch-slider"></span>
            </label>
          </div>

          <button class="main-start-btn" @click="goToEditForm">
            내 정보 수정하기
            <ArrowRight :size="18" />
          </button>
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
  padding: 100px 24px 60px;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

/* 배경 Blobs */
.blob {
  position: fixed;
  border-radius: 50%;
  filter: blur(160px);
  z-index: 0;
  opacity: 0.25;
  pointer-events: none;
}
.blob-eat { 
  top: -10%; left: -10%; width: 55vw; height: 55vw; 
  background: radial-gradient(circle, rgba(163, 230, 53, 0.8) 0%, transparent 70%); 
} 
.blob-medi { 
  bottom: -15%; right: -10%; width: 50vw; height: 50vw; 
  background: radial-gradient(circle, rgba(56, 189, 248, 0.7) 0%, transparent 70%); 
}

.content-wrapper {
  width: 100%;
  max-width: 600px;
  position: relative;
  z-index: 10;
}

.glass-card {
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 50px;
  padding: 50px 40px;
  box-shadow: 0 40px 100px rgba(0, 0, 0, 0.02);
}

/* Header 스타일 */
.profile-header { text-align: center; margin-bottom: 40px; }

/* 배지 크기 확대 및 멘트 변경 반영 */
.large-badge {
  display: inline-flex; align-items: center; gap: 8px; padding: 8px 18px;
  background: rgba(255, 255, 255, 0.8); border-radius: 999px; margin-bottom: 24px;
  font-size: 0.8rem; font-weight: 850; color: #7c4dff; border: 1.5px solid white;
  box-shadow: 0 4px 12px rgba(124, 77, 255, 0.1);
}

.profile-avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-wrapper {
  position: relative;
  margin-bottom: 20px;
}

/* 프로필 사진 크기 확대 */
.profile-img {
  width: 160px;
  height: 160px;
  border-radius: 40px;
  object-fit: cover;
  border: 5px solid white;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.status-indicator {
  position: absolute;
  bottom: 0px;
  right: 0px;
  width: 26px;
  height: 26px;
  background: #cbd5e1;
  border: 4px solid white;
  border-radius: 50%;
}
.status-indicator.is-active { background: #f43f5e; }

/* 닉네임 크기 소폭 축소 */
.profile-name {
  font-size: 1.6rem;
  font-weight: 950;
  letter-spacing: -0.04em;
  margin-bottom: 8px;
}
.gradient-dot { color: #7c4dff; }

.email-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(0, 0, 0, 0.03);
  padding: 6px 14px;
  border-radius: 100px;
  font-size: 0.85rem;
  font-weight: 600;
  color: #64748b;
}

/* Info Section 스타일 */
.section-title {
  font-size: 1rem; font-weight: 900; letter-spacing: 0.1em;
  color: #94a3b8; margin-bottom: 24px; display: flex; align-items: center; gap: 12px;
}
.section-title::after { content: ''; flex: 1; height: 1px; background: rgba(0,0,0,0.05); }

.stats-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 20px;
  transition: 0.3s;
}
.stat-item:hover { transform: scale(1.02); background: white; }

.stat-icon {
  width: 40px; height: 40px; border-radius: 12px;
  background: #f1f5f9; display: flex; align-items: center; justify-content: center;
  color: #64748b;
}

.stat-content { display: flex; flex-direction: column; }
.stat-label { font-size: 0.7rem; font-weight: 800; color: #94a3b8; text-transform: uppercase; }
.stat-value { font-size: 1rem; font-weight: 700; color: #1a1a1a; }

.highlight-item { border-left: 4px solid #7c4dff; }
.gradient-text {
  background: linear-gradient(to right, #7c4dff, #38bdf8);
  -webkit-background-clip: text; background-clip: text; color: transparent;
}

/* Care Plus 배너 */
.care-plus-banner {
  margin-top: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  background: linear-gradient(135deg, #fff1f2 0%, #fff 100%);
  border: 1px dashed #fda4af;
  border-radius: 20px;
}
.banner-icon { color: #f43f5e; }
.banner-title { display: block; font-size: 0.75rem; font-weight: 900; color: #f43f5e; }
.banner-desc { font-size: 0.9rem; font-weight: 700; color: #1a1a1a; }

/* Action Section */
.action-section { margin-top: 40px; display: flex; flex-direction: column; gap: 16px; }

.setting-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f8fafc;
  border-radius: 24px;
  border: 1px solid #f1f5f9;
}

.setting-info { display: flex; align-items: center; gap: 12px; }
.setting-icon { color: #7c4dff; }
.setting-label { display: block; font-size: 0.95rem; font-weight: 800; }
.setting-desc { font-size: 0.75rem; font-weight: 600; color: #94a3b8; }

/* Modern Switch */
.modern-switch {
  position: relative; width: 48px; height: 26px;
}
.modern-switch input { opacity: 0; width: 0; height: 0; }
.switch-slider {
  position: absolute; cursor: pointer; inset: 0;
  background: #e2e8f0; border-radius: 30px; transition: 0.4s;
}
.switch-slider:before {
  position: absolute; content: ""; height: 18px; width: 18px;
  left: 4px; bottom: 4px; background: white; border-radius: 50%; transition: 0.4s;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
input:checked + .switch-slider { background: #1a1a1a; }
input:checked + .switch-slider:before { transform: translateX(22px); }

/* Main Button 스타일 계승 */
.main-start-btn {
  display: flex; align-items: center; justify-content: center; gap: 12px;
  padding: 18px; background: #1a1a1a; color: white; border: none; border-radius: 999px;
  font-size: 1rem; font-weight: 800; cursor: pointer; transition: all 0.4s;
}
.main-start-btn:hover { background: #7c4dff; transform: translateY(-5px); box-shadow: 0 20px 40px rgba(124, 77, 255, 0.3); }

.italic { font-style: italic; }

/* 애니메이션 */
.slide-enter-active, .slide-leave-active { transition: all 0.4s; max-height: 100px; }
.slide-enter-from, .slide-leave-to { opacity: 0; transform: translateY(-10px); max-height: 0; }

@media (max-width: 640px) {
  .profile-name { font-size: 1.6rem; }
  .glass-card { padding: 40px 24px; border-radius: 40px; }
  .profile-img { width: 110px; height: 110px; }
}
</style>