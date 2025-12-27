<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import mypageApi from '@/api/mypage';
import { 
  Sparkles, 
  Search, 
  Edit3, 
  Trash2, 
  ArrowRight, 
  FileText, 
  Trophy, 
  Users, 
  X,
  Loader2,
  Inbox
} from 'lucide-vue-next';

const router = useRouter();
const route = useRoute();

/* =========================
   상태 관리 및 테마 설정
========================= */
const loading = ref(false);
const activityMode = ref('ALL');
const mainTab = ref('challenge'); // challenge | community
const showDeleteModal = ref(false);
const itemToDelete = ref(null);

const myChallengePosts = ref([]);
const myCommunityPosts = ref([]);

// 테마 컬러 정의
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
  },
  ALL: {
    primary: "#7c4dff", // 전체일 때는 보라색 포인트
    soft: "rgba(124, 77, 255, 0.1)",
    glow: "rgba(124, 77, 255, 0.05)"
  }
};

// 필터링 모드에 따른 테마 결정
const activeTheme = computed(() => themeColors[activityMode.value] || themeColors.ALL);

// --------------------
// 계산 속성 (필터링)
// --------------------
const filteredList = computed(() => {
  const base = mainTab.value === 'challenge' ? myChallengePosts.value : myCommunityPosts.value;
  if (activityMode.value === 'ALL') return base;
  return base.filter((item) => item.raw?.modeType === activityMode.value);
});

// --------------------
// 데이터 로딩
// --------------------
const loadPosts = async () => {
  loading.value = true;
  try {
    const [chRes, coRes] = await Promise.all([
      mypageApi.getMyChallenges(),
      mypageApi.getMyPosts(),
    ]);

    myChallengePosts.value = (chRes.data || []).map((c) => ({
      id: c.challengeId,
      title: c.title,
      createdAt: c.createdAt?.split('T')[0] || c.createdAt,
      raw: c,
    }));

    myCommunityPosts.value = (coRes.data || []).map((p) => ({
      id: p.postId,
      title: p.title,
      createdAt: p.createdAt?.split('T')[0] || p.createdAt,
      raw: p,
    }));
  } catch (e) {
    console.error('내 게시물 로딩 실패', e);
  } finally {
    loading.value = false;
  }
};

// --------------------
// 액션 로직
// --------------------
const goDetail = (item) => {
  const name = mainTab.value === 'challenge' ? 'my-challenge-detail' : 'community-detail';
  router.push({ name, params: { id: item.id } });
};

const goEdit = (item) => {
  const name = mainTab.value === 'challenge' ? 'challenge-edit' : 'community-edit';
  const modeParam = item.raw?.modeType || (route.query.mode || 'EAT');
  router.push({ 
    name, 
    params: { id: item.id },
    query: { mode: modeParam }
  });
};

const openDeleteConfirm = (item) => {
  itemToDelete.value = item;
  showDeleteModal.value = true;
};

const confirmDelete = async () => {
  if (!itemToDelete.value) return;
  try {
    if (mainTab.value === 'challenge') {
      await mypageApi.deleteChallenge?.(itemToDelete.value.id);
      myChallengePosts.value = myChallengePosts.value.filter(p => p.id !== itemToDelete.value.id);
    } else {
      await mypageApi.deleteCommunity(itemToDelete.value.id);
      myCommunityPosts.value = myCommunityPosts.value.filter(p => p.id !== itemToDelete.value.id);
    }
  } catch (e) {
    console.error('삭제 실패', e);
  } finally {
    showDeleteModal.value = false;
    itemToDelete.value = null;
  }
};

onMounted(loadPosts);
</script>

<template>
  <div class="intro-container" :style="{ '--point-color': activeTheme.primary, '--point-soft': activeTheme.soft }">
    <!-- 배경 장식 (모드에 따라 유동적으로 변화) -->
    <TransitionGroup name="fade-blob">
      <div v-if="activityMode === 'ALL' || activityMode === 'EAT'" key="eat-blob" class="blob blob-eat"></div>
      <div v-if="activityMode === 'ALL' || activityMode === 'MEDI_EAT'" key="medi-blob" class="blob blob-medi"></div>
    </TransitionGroup>

    <main class="content-wrapper">
      <div class="activity-card glass-card">
        <!-- Header 섹션 -->
        <header class="page-header">
          <div class="header-top">
            <div class="badge">
              <Sparkles :size="12" class="icon-purple" />
              <span>MY ACTIVITY</span>
            </div>
            <!-- 모드 필터 칩 -->
            <div class="mode-filter">
              <button
                v-for="m in ['ALL', 'EAT', 'MEDI_EAT']"
                :key="m"
                :class="['mode-chip', { active: activityMode === m }]"
                @click="activityMode = m"
              >
                {{ m }}
              </button>
            </div>
          </div>
          
          <h2 class="hero-title italic">
            MY<br />
            <span class="gradient-text">POSTS.</span>
          </h2>
        </header>

        <!-- 메인 탭 (Challenge / Community) - 크기 축소 적용 -->
        <div class="tab-navigation">
          <button
            :class="['nav-tab', { active: mainTab === 'challenge' }]"
            @click="mainTab = 'challenge'"
          >
            <Trophy :size="16" />
            챌린지
          </button>
          <button
            :class="['nav-tab', { active: mainTab === 'community' }]"
            @click="mainTab = 'community'"
          >
            <Users :size="16" />
            커뮤니티
          </button>
        </div>

        <!-- 게시글 리스트 영역 -->
        <section class="list-container">
          <div v-if="loading" class="state-box">
            <Loader2 class="spinner" :size="32" />
            <p>기록을 불러오고 있어요...</p>
          </div>

          <div v-else-if="filteredList.length === 0" class="state-box empty">
            <div class="empty-icon">
              <Inbox :size="48" />
            </div>
            <h3>작성한 게시글이 없습니다.</h3>
            <p>당신의 건강한 일상을 공유해보세요.</p>
          </div>

          <div v-else class="post-grid">
            <div
              v-for="(item, index) in filteredList"
              :key="item.id"
              class="post-row animate-in"
              :style="{ animationDelay: `${index * 0.05}s` }"
            >
              <div class="post-info" @click="goDetail(item)">
                <div class="post-title-group">
                  <span 
                    class="post-category-tag"
                    :style="{ 
                      color: item.raw?.modeType === 'MEDI_EAT' ? '#38bdf8' : '#a3e635',
                      backgroundColor: item.raw?.modeType === 'MEDI_EAT' ? 'rgba(56, 189, 248, 0.1)' : 'rgba(163, 230, 53, 0.1)'
                    }"
                  >
                    {{ item.raw?.modeType || 'EAT' }}
                  </span>
                  <h4 class="post-title">{{ item.title }}</h4>
                </div>
                <span class="post-date">{{ item.createdAt }}</span>
              </div>

              <div class="post-actions">
                <button class="action-btn view" title="조회" @click="goDetail(item)">
                  <Search :size="18" />
                </button>
                <button class="action-btn edit" title="수정" @click="goEdit(item)">
                  <Edit3 :size="18" />
                </button>
                <button class="action-btn delete" title="삭제" @click="openDeleteConfirm(item)">
                  <Trash2 :size="18" />
                </button>
              </div>
            </div>
          </div>
        </section>
      </div>
    </main>

    <!-- 삭제 확인 커스텀 모달 -->
    <Transition name="fade">
      <div v-if="showDeleteModal" class="modal-overlay" @click.self="showDeleteModal = false">
        <div class="auth-modal-card">
          <button class="close-btn" @click="showDeleteModal = false">
            <X :size="20" />
          </button>
          
          <div class="modal-header">
            <div class="mini-logo italic">MEDEAT</div>
            <h3 class="modal-title italic">DELETE POST</h3>
            <p class="modal-subtitle">정말로 이 게시글을 삭제하시겠습니까?<br/>삭제된 데이터는 복구할 수 없습니다.</p>
          </div>

          <div class="auth-btn-group">
            <button class="auth-btn danger-confirm-btn" @click="confirmDelete">
              삭제 확정
            </button>
            <button class="auth-btn back-btn" @click="showDeleteModal = false">취소</button>
          </div>
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
  padding: 120px 24px 80px;
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
  max-width: 800px;
  position: relative;
  z-index: 10;
}

.glass-card {
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border: 1px solid rgba(255, 255, 255, 0.7);
  border-radius: 50px;
  padding: 60px 48px;
  box-shadow: 0 40px 100px rgba(0, 0, 0, 0.02);
}

/* Header 스타일 */
.page-header { margin-bottom: 40px; }
.header-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }

.badge {
  display: inline-flex; align-items: center; gap: 6px; padding: 6px 12px;
  background: rgba(255, 255, 255, 0.8); border-radius: 999px;
  font-size: 0.65rem; font-weight: 800; color: #7c4dff; border: 1px solid white;
}

.mode-filter { display: flex; gap: 8px; }
.mode-chip {
  padding: 6px 14px; border-radius: 999px; border: 1.5px solid transparent;
  background: rgba(255, 255, 255, 0.6); font-size: 0.7rem; font-weight: 800;
  color: #94a3b8; cursor: pointer; transition: 0.3s;
}
.mode-chip.active {
  background: white; color: var(--point-color); border-color: var(--point-soft);
  box-shadow: 0 8px 16px var(--point-soft);
}

.hero-title { font-size: 3.2rem; line-height: 0.9; font-weight: 950; letter-spacing: -0.05em; }
.gradient-text {
  background: linear-gradient(to bottom right, #1a1a1a, var(--point-color));
  -webkit-background-clip: text; background-clip: text; color: transparent;
  transition: all 0.5s ease;
}

/* Tab Navigation - 수정됨: 컴팩트한 레이아웃 */
.tab-navigation {
  display: flex; gap: 8px; margin-bottom: 24px;
  padding: 4px; background: rgba(0,0,0,0.03); border-radius: 18px;
}
.nav-tab {
  flex: 1; display: flex; align-items: center; justify-content: center; gap: 6px;
  padding: 10px 14px; border-radius: 14px; border: none;
  font-size: 0.85rem; font-weight: 850; color: #94a3b8; cursor: pointer; transition: 0.3s;
}
.nav-tab.active {
  background: white; color: #1a1a1a; box-shadow: 0 4px 15px rgba(0,0,0,0.05);
}

/* Post List 스타일 */
.post-grid { display: flex; flex-direction: column; gap: 16px; }
.post-row {
  display: flex; justify-content: space-between; align-items: center;
  padding: 24px; border-radius: 24px; background: rgba(255, 255, 255, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.6); transition: 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}
.post-row:hover {
  background: white; transform: translateY(-3px);
  box-shadow: 0 12px 30px rgba(0,0,0,0.04);
}

.post-info { flex: 1; cursor: pointer; }
.post-title-group { display: flex; align-items: center; gap: 12px; margin-bottom: 6px; }
.post-category-tag {
  font-size: 0.65rem; font-weight: 900; 
  padding: 2px 8px; border-radius: 6px;
  text-transform: uppercase;
}
.post-title { font-size: 1.1rem; font-weight: 700; color: #1a1a1a; }
.post-date { font-size: 0.8rem; font-weight: 600; color: #94a3b8; }

.post-actions { display: flex; gap: 8px; }
.action-btn {
  width: 40px; height: 40px; border-radius: 12px; border: none;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: 0.3s; color: #94a3b8; background: #f8fafc;
}
.action-btn:hover { color: #1a1a1a; background: #f1f5f9; transform: scale(1.1); }
.action-btn.delete:hover { color: #ef4444; background: #fef2f2; }

/* State Boxes */
.state-box {
  padding: 80px 40px; text-align: center; color: #94a3b8;
  display: flex; flex-direction: column; align-items: center; gap: 16px;
}
.spinner { animation: spin 1s linear infinite; color: var(--point-color); }
.empty-icon { color: #e2e8f0; margin-bottom: 12px; }
.state-box.empty h3 { color: #1a1a1a; font-size: 1.3rem; font-weight: 800; }

/* Modal 스타일 */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(10px); z-index: 2000; display: flex; align-items: center; justify-content: center; padding: 24px;
}
.auth-modal-card {
  background: white; width: 100%; max-width: 420px; padding: 50px 40px;
  border-radius: 40px; box-shadow: 0 40px 80px rgba(0,0,0,0.2); position: relative; text-align: center;
}
.close-btn { position: absolute; top: 24px; right: 24px; background: none; border: none; cursor: pointer; opacity: 0.3; }
.mini-logo { font-weight: 900; color: #7c4dff; font-size: 0.8rem; letter-spacing: 2px; margin-bottom: 20px; }
.modal-title { font-size: 2.2rem; font-weight: 900; margin-bottom: 12px; letter-spacing: -1px; }
.modal-subtitle { font-size: 1rem; color: #64748b; font-weight: 600; margin-bottom: 32px; }

.auth-btn-group { display: flex; flex-direction: column; gap: 12px; }
.auth-btn { padding: 16px; border-radius: 16px; font-weight: 800; cursor: pointer; transition: 0.3s; border: none; font-size: 1rem; }
.danger-confirm-btn { background: #1a1a1a; color: white; }
.danger-confirm-btn:hover { background: #ef4444; }
.back-btn { background: #f1f5f9; color: #64748b; }

/* Animations */
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
.animate-in { animation: slideUp 0.5s ease-out forwards; opacity: 0; }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

.italic { font-style: italic; }

@media (max-width: 768px) {
  .hero-title { font-size: 2.5rem; }
  .glass-card { padding: 40px 20px; border-radius: 40px; }
  .post-row { flex-direction: column; align-items: flex-start; gap: 10px; }
  .post-actions { width: 100%; justify-content: flex-end; }
}
</style>