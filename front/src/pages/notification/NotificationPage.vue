<script setup>
import { ref, onMounted, inject, computed, watch } from "vue";
import { useRoute } from "vue-router";
import {
  fetchNotifications,
  markAllAsRead,
} from "@/api/notification"; 
import NotificationItem from "@/components/notification/NotificationItem.vue";
import { Bell, BellOff, Loader2, Check } from "lucide-vue-next";

const route = useRoute();
const notifications = ref([]);
const loading = ref(true);
const refreshBell = inject("refreshBell", null);

// 현재 모드 계산 (EAT 또는 MEDI_EAT)
const currentMode = computed(() => route.query.mode || "EAT");

// 모드별 포인트 컬러 설정 (연두색 vs 하늘색)
const themeColors = {
  EAT: {
    primary: "#a3e635", // 연두색 (Lime-400)
    primarySoft: "rgba(163, 230, 53, 0.1)",
    primaryGlow: "rgba(163, 230, 53, 0.08)"
  },
  MEDI_EAT: {
    primary: "#38bdf8", // 하늘색 (Sky-400)
    primarySoft: "rgba(56, 189, 248, 0.1)",
    primaryGlow: "rgba(56, 189, 248, 0.08)"
  }
};

const activeTheme = computed(() => themeColors[currentMode.value] || themeColors.EAT);

// 전체 알림 불러오기
const loadAll = async () => {
  try {
    const res = await fetchNotifications();
    notifications.value = res.data;
  } catch (error) {
    console.error("알림 로드 실패:", error);
  }
};

onMounted(async () => {
  loading.value = true;
  try {
    await loadAll();
    try {
      await markAllAsRead();
    } catch (e) {
      console.warn("전체 읽음 처리 실패 (무시 가능):", e);
    }
    if (refreshBell) refreshBell();
  } finally {
    loading.value = false;
  }
});

const handleReadAll = async () => {
  try {
    await markAllAsRead();
    if (refreshBell) refreshBell();
    await loadAll();
  } catch (e) {
    console.error("모두 읽기 오류:", e);
  }
};
</script>

<template>
  <!-- 모드에 따라 스타일 변수를 바인딩 -->
  <div class="notification-page" :style="{ '--point-color': activeTheme.primary, '--point-glow': activeTheme.primaryGlow, '--point-soft': activeTheme.primarySoft }">
    <!-- 배경 장식 (모드별 색상 적용) -->
    <div class="bg-glow"></div>
    
    <div class="notification-container">
      <header class="page-header">
        <div class="header-top">
          <div class="title-group">
            <h1 class="page-title italic">NOTIFICATIONS</h1>
            <div v-if="notifications.length > 0" class="pulse-badge">
              {{ notifications.length }}
            </div>
          </div>
          <button v-if="notifications.length > 0" class="read-all-btn" @click="handleReadAll">
            <Check :size="14" class="icon-margin" />
            모두 읽음
          </button>
        </div>
        <p class="page-subtitle">중요한 건강 소식과 활동 알림을 확인하세요.</p>
      </header>

      <main class="notification-content">
        <!-- 로딩 상태 UI -->
        <div v-if="loading" class="state-box">
          <Loader2 class="spinner-icon" :size="40" />
          <p class="state-text">새로운 소식을 확인하고 있어요...</p>
        </div>

        <!-- 알림이 없을 때의 UI -->
        <div v-else-if="notifications.length === 0" class="state-box empty-state">
          <div class="empty-icon-wrapper">
            <BellOff :size="48" stroke-width="1.5" />
          </div>
          <h2 class="empty-title">알림이 없어요.</h2>
          <p class="empty-desc">새로운 소식이 생기면 바로 알려드릴게요!</p>
        </div>

        <!-- 알림 리스트 UI -->
        <div v-else class="notification-list">
          <div 
            v-for="(n, index) in notifications" 
            :key="n.notificationId"
            class="list-item-wrapper"
            :style="{ animationDelay: `${index * 0.05}s` }"
          >
            <NotificationItem :notification="n" />
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<style scoped>
.notification-page {
  min-height: 100vh;
  background-color: #f8f9fc;
  position: relative;
  overflow-x: hidden;
  padding: 140px 24px 60px;
  display: flex;
  justify-content: center;
  transition: all 0.5s ease;
}

/* 배경 글로우: 포인트 컬러 변수 사용 */
.bg-glow {
  position: fixed;
  top: -10%;
  right: -10%;
  width: 60vw;
  height: 60vw;
  background: radial-gradient(circle, var(--point-glow) 0%, transparent 70%);
  z-index: 0;
  pointer-events: none;
  transition: background 0.5s ease;
}

.notification-container {
  width: 100%;
  max-width: 720px;
  position: relative;
  z-index: 1;
}

/* 헤더 섹션 */
.page-header {
  margin-bottom: 40px;
  animation: fadeInDown 0.8s cubic-bezier(0.23, 1, 0.32, 1);
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.title-group {
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-title {
  font-size: 2.8rem;
  font-weight: 950;
  letter-spacing: -2.5px;
  color: #1a1a1a;
  margin: 0;
}

.pulse-badge {
  background: var(--point-color);
  color: white;
  font-size: 0.85rem;
  font-weight: 800;
  padding: 4px 12px;
  border-radius: 999px;
  box-shadow: 0 0 0 4px var(--point-soft);
  animation: pulse 2s infinite;
  transition: background 0.5s ease;
}

.page-subtitle {
  font-size: 1rem;
  color: #64748b;
  font-weight: 500;
}

/* 버튼 디자인: 포인트 컬러 적용 */
.read-all-btn {
  display: flex;
  align-items: center;
  font-size: 0.85rem;
  font-weight: 700;
  color: var(--point-color);
  background: white;
  border: 1px solid var(--point-soft);
  padding: 8px 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
}

.read-all-btn:hover {
  background: var(--point-color);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px var(--point-soft);
}

.icon-margin {
  margin-right: 6px;
}

/* 알림 리스트 레이아웃 */
.notification-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.list-item-wrapper {
  opacity: 0;
  animation: slideInUp 0.6s cubic-bezier(0.23, 1, 0.32, 1) forwards;
}

/* 상태 박스 (Glassmorphism 스타일) */
.state-box {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 40px;
  padding: 80px 40px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.03);
}

.spinner-icon {
  color: var(--point-color);
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

.empty-icon-wrapper {
  width: 100px;
  height: 100px;
  background: #f1f5f9;
  border-radius: 35% 65% 70% 30% / 30% 30% 70% 70%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  color: #94a3b8;
  animation: blobby 10s infinite ease-in-out;
}

.empty-title {
  font-size: 1.5rem;
  font-weight: 800;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.empty-desc {
  font-size: 1rem;
  color: #64748b;
  font-weight: 500;
}

/* 애니메이션 */
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes fadeInDown {
  from { opacity: 0; transform: translateY(-20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes slideInUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes pulse {
  0% { transform: scale(1); box-shadow: 0 0 0 0 var(--point-soft); }
  70% { transform: scale(1.05); box-shadow: 0 0 0 10px rgba(255, 255, 255, 0); }
  100% { transform: scale(1); box-shadow: 0 0 0 0 rgba(255, 255, 255, 0); }
}

@keyframes blobby {
  0%, 100% { border-radius: 35% 65% 70% 30% / 30% 30% 70% 70%; }
  50% { border-radius: 50% 50% 30% 70% / 50% 60% 40% 50%; }
}

/* 모바일 최적화 */
@media (max-width: 640px) {
  .notification-page { padding: 100px 16px 40px; }
  .page-title { font-size: 2rem; }
  .state-box { padding: 60px 24px; border-radius: 30px; }
  .header-top { flex-direction: column; align-items: flex-start; gap: 16px; }
  .read-all-btn { width: 100%; justify-content: center; }
}

.italic { font-style: italic; }
</style>