<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import { useRoute, useRouter } from "vue-router";
import authApi from "@/api/auth";
import NotificationBell from "@/components/notification/NotificationBell.vue";
import NotificationDropdown from "@/components/notification/NotificationDropdown.vue";

const route = useRoute();
const router = useRouter();

const nickname = ref("Guest");
const showNotifications = ref(false);
const bellRef = ref(null);
const hoveredIndex = ref(null);

/* ================= Notification Logic ================= */
const handleOutside = (e) => {
  if (
    !e.target.closest(".notif-wrapper") &&
    !e.target.closest(".bell")
  ) {
    showNotifications.value = false;
  }
};

const toggleNotifications = () => {
  showNotifications.value = !showNotifications.value;
};

const refreshUnread = () => {
  bellRef.value?.loadCount();
};

/* ================= Navigation & Mode Switch ================= */
const currentMode = computed(() => route.query.mode || "EAT");

const switchMode = (newMode) => {
  if (currentMode.value === newMode) return;
  router.push({ 
    path: route.path, 
    query: { ...route.query, mode: newMode } 
  });
};

const isMediMode = computed(() => 
  ["MEDEAT", "MEDI_EAT", "MEDI"].includes(currentMode.value)
);

const themeClass = computed(() => isMediMode.value ? 'theme-blue' : 'theme-green');

const isActive = (path) => {
  return route.path === path || route.path.startsWith(path + "/");
};

const navigateTo = (path) => {
  router.push({ path, query: { mode: currentMode.value } });
};

const logout = async () => {
  try {
    await authApi.logout();
    router.push("/");
  } catch (e) {
    console.error(e);
  }
};

/* ================= English Menu Data ================= */
const dietSubMenus = [
  { label: "DIET LIST", path: "/diet" },
  { label: "ADD DIET", path: "/diet/form" },
  { label: "NUTRITION ANALYSIS", path: "/diet/analysis" },
];

const challengeSubMenus = [
  { label: "MY CHALLENGES", path: "/my/challenges" },
  { label: "EXPLORE LIST", path: "/challenge" },
  { label: "CREATE MISSION", path: "/challenge/form" },
];

const communitySubMenus = [
  { label: "POPULAR", path: "/community" },
  { label: "SCRAPPED", path: "/community/scrap" },
  { label: "LIKED", path: "/community/like" },
  { label: "WRITE POST", path: "/community/new" },
];

const myPageSubMenus = [
  { label: "PROFILE SETTINGS", path: "/mypage/edit" },
  { label: "MY ACTIVITIES", path: "/mypage/posts" },
];

const eatMenus = [
  { label: "DIET", path: "/diet", subMenus: dietSubMenus },
  { label: "CHALLENGE", path: "/challenge", subMenus: challengeSubMenus },
  { label: "COMMUNITY", path: "/community", subMenus: communitySubMenus },
  { label: "MY PAGE", path: "/mypage", subMenus: myPageSubMenus },
];

const mediMenus = [
  { label: "DIET", path: "/diet", subMenus: dietSubMenus },
  {
    label: "MEDICAL",
    path: "/disease",
    subMenus: [
      { label: "CONDITION DASHBOARD", path: "/disease" },
      { label: "ADD CONDITION", path: "/disease/MedicalAddForm" },
      { label: "ADD MEDICATION", path: "/medication/add" },
    ],
  },
  { label: "CHALLENGE", path: "/challenge", subMenus: challengeSubMenus },
  { label: "COMMUNITY", path: "/community", subMenus: communitySubMenus },
  { label: "MY PAGE", path: "/mypage", subMenus: myPageSubMenus },
];

const currentMenus = computed(() =>
  isMediMode.value ? mediMenus : eatMenus
);

onMounted(async () => {
  try {
    const res = await authApi.checkLogin();
    nickname.value = res.data.nickname;
  } catch {
    nickname.value = "Guest";
  }
  document.addEventListener("click", handleOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener("click", handleOutside);
});
</script>

<template>
  <header class="glass-nav" :class="themeClass">
    <div class="nav-content">
      <div class="nav-logo-area">
        <h1 class="logo italic" @click="navigateTo('/dashboard')">
          MEDEAT
        </h1>
      </div>

      <nav class="nav-menu">
        <div
          v-for="(menu, index) in currentMenus"
          :key="index"
          class="menu-item-wrapper"
          @mouseenter="hoveredIndex = index"
          @mouseleave="hoveredIndex = null"
        >
          <span
            class="menu-link font-black italic"
            :class="{ active: isActive(menu.path) }"
            @click="navigateTo(menu.path)"
          >
            {{ menu.label }}
          </span>

          <transition name="fade">
            <div
              v-if="menu.subMenus && hoveredIndex === index"
              class="dropdown-menu glass-effect"
            >
              <div
                v-for="(sub, i) in menu.subMenus"
                :key="i"
                class="dropdown-item font-black"
                @click.stop="navigateTo(sub.path)"
              >
                {{ sub.label }}
              </div>
            </div>
          </transition>
        </div>
      </nav>

      <div class="user-area">
        
        <transition name="fade">
          <button
            v-if="isMediMode"
            class="scan-btn"
            @click="navigateTo('/medication/scan')"
          >
            SCAN
          </button>
        </transition>

        <div class="header-mode-toggle glass-effect">
          <button 
            class="h-mode-btn" 
            :class="{ active: !isMediMode }"
            @click="switchMode('EAT')"
          >
            EAT
          </button>
          <button 
            class="h-mode-btn" 
            :class="{ active: isMediMode }"
            @click="switchMode('MEDI_EAT')"
          >
            MEDI
          </button>
        </div>

        <div class="notif-wrapper">
          <NotificationBell ref="bellRef" @toggle="toggleNotifications" />
          <NotificationDropdown
            v-if="showNotifications"
            @refresh="refreshUnread"
            @close="showNotifications = false"
          />
        </div>

        <div class="profile-box" @click="navigateTo('/mypage')">
          <img src="/img/profile.jpg" class="profile-img" />
          <span class="nickname font-black">{{ nickname }}</span>
        </div>

        <button class="eye-exit-btn" @click="logout">
          <span class="eye-exit-btn__cover">
            <span class="btn-label font-black italic">EXIT</span>
            <span class="eye-exit-btn__eyes">
              <span class="eye-exit-btn__eye">
                <span class="eye-exit-btn__pupil"></span>
              </span>
              <span class="eye-exit-btn__eye">
                <span class="eye-exit-btn__pupil"></span>
              </span>
            </span>
          </span>
        </button>
      </div>
    </div>
  </header>
</template>
<style scoped>
/* Theme Variables */
.theme-green {
  --point-color: #a3e635;
  --point-gradient: linear-gradient(135deg, #a3e635, #65a30d);
  --point-light: #f7fee7;
  --point-dark: #4d7c0f;
}
.theme-blue {
  --point-color: #38bdf8;
  --point-gradient: linear-gradient(135deg, #38bdf8, #0284c7);
  --point-light: #f0f9ff;
  --point-dark: #0369a1;
}

.glass-nav {
  position: fixed;
  top: 24px;
  left: 50%;
  transform: translateX(-50%);
  width: 95%;
  max-width: 1600px;
  padding: 12px 40px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.45);
  backdrop-filter: blur(25px);
  -webkit-backdrop-filter: blur(25px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  z-index: 1000;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.08);
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}

.nav-content { 
  display: grid; 
  grid-template-columns: 1fr auto 1fr; 
  align-items: center; 
  width: 100%; 
}

.nav-logo-area { display: flex; align-items: center; justify-content: flex-start; }
.logo { font-weight: 950; font-size: 1.5rem; letter-spacing: -2px; cursor: pointer; color: #1a1a1a; }

.nav-menu { 
  display: flex; 
  gap: 40px; 
  justify-content: center;
  margin: 0 auto;
}

.menu-item-wrapper { position: relative; }
.menu-link {
  font-size: 0.75rem;
  letter-spacing: 0.18em;
  opacity: 0.55;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}
.menu-link.active, .menu-link:hover { opacity: 1; color: var(--point-color); transform: scale(1.05); }

.dropdown-menu {
  position: absolute; 
  top: 48px; 
  left: 50%; 
  transform: translateX(-50%);
  background: rgba(255, 255, 255, 0.95); 
  border-radius: 20px; 
  padding: 12px 0; 
  min-width: 220px;
  box-shadow: 0 16px 40px rgba(0,0,0,0.1);
  border: 1px solid rgba(0,0,0,0.05);
}
.dropdown-item { 
  padding: 12px 24px; 
  font-size: 13px; 
  cursor: pointer; 
  transition: all 0.2s; 
  text-align: center; 
  color: #64748b;
  letter-spacing: 0.05em;
}
.dropdown-item:hover { background: var(--point-light); color: var(--point-color); transform: scale(1.02); }

.user-area { display: flex; align-items: center; gap: 20px; justify-content: flex-end; }

.header-mode-toggle {
  display: flex; background: rgba(0,0,0,0.06); padding: 4px; border-radius: 999px; gap: 4px;
}
.h-mode-btn {
  padding: 6px 16px; border-radius: 999px; border: none; background: transparent;
  font-size: 11px; font-weight: 900; color: #888; cursor: pointer; transition: 0.3s;
}
.h-mode-btn.active {
  background: white; color: var(--point-color); box-shadow: 0 2px 10px rgba(0,0,0,0.08);
}

.scan-btn {
  background: var(--point-gradient); color: white; border: none;
  padding: 8px 18px; border-radius: 999px; font-size: 12px; font-weight: 900; transition: 0.3s;
}
.scan-btn:hover { transform: translateY(-2px); box-shadow: 0 4px 15px var(--point-soft); }

.profile-box { display: flex; align-items: center; gap: 10px; cursor: pointer; }
.profile-img { width: 34px; height: 34px; border-radius: 50%; border: 2px solid white; box-shadow: 0 4px 10px rgba(0,0,0,0.05); }
.nickname { font-size: 13px; color: #1a1a1a; }

/* 🌟 02. Eye-Blink EXIT Button 스타일링 */
.eye-exit-btn {
  background-color: #1a1a1a;
  border-radius: 999px;
  color: white;
  cursor: pointer;
  min-width: 80px;
  border: none;
  padding: 0;
  position: relative;
  overflow: visible;
  transition: transform 0.3s cubic-bezier(0.65, 0, 0.35, 1);
}

.eye-exit-btn__cover {
  display: block;
  background-color: #f1f5f9; /* 기본 비활성 색상 */
  border: 1.5px solid #e2e8f0;
  color: #94a3b8;
  border-radius: inherit;
  padding: 8px 24px;
  position: relative;
  z-index: 2;
  transform-origin: 20% 50%;
  transition: all 0.4s cubic-bezier(0.65, 0, 0.35, 1);
}

.eye-exit-btn__eyes {
  position: absolute;
  display: flex;
  align-items: center;
  gap: 4px;
  right: 12px;
  bottom: 8px;
  height: 12px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.eye-exit-btn__eye {
  animation: eye-blink 3s infinite;
  background-color: white;
  border-radius: 50%;
  width: 12px;
  height: 12px;
  position: relative;
  overflow: hidden;
}

.eye-exit-btn__pupil {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 5px;
  height: 5px;
  background-color: #1a1a1a;
  border-radius: 50%;
  transform: translate(-50%, -50%);
}

/* Hover 시 인터랙션: 커버가 회전하며 눈이 나타남 */
.eye-exit-btn:hover .eye-exit-btn__cover {
  background-color: var(--point-color); /* 모드별 포인트 컬러 적용 */
  color: #1a1a1a;
  border-color: var(--point-color);
  transform: rotate(-12deg);
}

.eye-exit-btn:hover .eye-exit-btn__eyes {
  opacity: 1;
}

.eye-exit-btn:active .eye-exit-btn__cover {
  transform: rotate(0);
}

@keyframes eye-blink {
  0%, 92%, 100% { height: 12px; }
  96% { height: 0; }
}

/* Transitions */
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s, transform 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateX(-50%) translateY(-15px); }

.font-black { font-weight: 950; }
.italic { font-style: italic; }
.notif-wrapper { position: relative; }

@media (max-width: 1200px) {
  .nav-menu { gap: 20px; }
}
@media (max-width: 1024px) {
  .nav-menu { display: none; }
  .nav-content { grid-template-columns: 1fr 1fr; }
}
</style>