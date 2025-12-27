<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import mypageApi from '@/api/mypage';
import { 
  Sparkles, 
  Bookmark, 
  Trash2, 
  ArrowRight, 
  X,
  Loader2,
  Inbox,
  Calendar,
  Heart,
  ExternalLink,
  MessageSquare,
  User,
  LayoutGrid
} from 'lucide-vue-next';

const router = useRouter();
const route = useRoute();

/* =========================
   상태 관리
========================= */
const scrapPosts = ref([]);
const loading = ref(false);
const showCancelModal = ref(false);
const itemToCancel = ref(null);

// 디자인 시스템 컬러 (보라색 포인트 통일)
const designSystem = {
  primary: "#7C4DFF",   // Lavender-Blue
  secondary: "#FF8A65", // Soft Peach
  text: "#1A1A1A",
  bg: "#F8F9FC"
};

// --------------------
// 데이터 로딩
// --------------------
const loadScraps = async () => {
  loading.value = true;
  try {
    const res = await mypageApi.getMyScraps();
    scrapPosts.value = (res.data || []).map((p) => ({
      id: p.postId,
      title: p.title,
      createdAt: p.createdAt?.split('T')[0] || p.createdAt,
      writer: p.nickname || p.writerId || '익명',
      likes: p.likeCount || 0
    }));
  } catch (e) {
    console.error('스크랩 목록 로딩 실패', e);
  } finally {
    loading.value = false;
  }
};

// --------------------
// 액션 로직
// --------------------
const goDetail = (post) => {
  router.push({
    name: 'community-detail',
    params: { id: post.id },
    query: { mode: route.query.mode } 
  });
};

const openCancelModal = (post) => {
  itemToCancel.value = post;
  showCancelModal.value = true;
};

const confirmCancel = async () => {
  if (!itemToCancel.value) return;
  try {
    await mypageApi.toggleCommunityScrap(itemToCancel.value.id);
    scrapPosts.value = scrapPosts.value.filter(p => p.id !== itemToCancel.value.id);
  } catch (e) {
    console.error('스크랩 해제 실패', e);
  } finally {
    showCancelModal.value = false;
    itemToCancel.value = null;
  }
};

onMounted(loadScraps);
</script>

<template>
  <div class="intro-container">
    <!-- 🌟 배경 장식 (MyPage.vue와 동일한 하이브리드 광채 적용) -->
    <div class="blob blob-eat"></div>
    <div class="blob blob-medi"></div>

    <main class="content-wrapper">
      <!-- 🌟 헤더 (통합 멘트 및 보라색 포인트) -->
      <header class="page-header">
        <div class="badge-row">
          <div class="badge glass-effect">
            <Sparkles :size="12" class="text-primary" />
            <span class="font-black italic text-primary">COMMUNITY INSIGHT</span>
          </div>
        </div>
        <h2 class="page-title font-black italic tracking-tighter">
          SAVED <br /> <span class="gradient-text">POSTS.</span>
        </h2>
        <p class="page-desc">당신이 영감을 얻은 커뮤니티의 소중한 기록들</p>
      </header>

      <!-- 리스트 섹션 -->
      <section class="scrap-list-section">
        <div v-if="loading" class="state-box">
          <Loader2 class="spinner" :size="32" />
          <p class="font-black italic opacity-30">SYNCING ARCHIVE...</p>
        </div>

        <div v-else-if="scrapPosts.length === 0" class="state-box empty">
          <div class="empty-icon-circle glass-effect">
            <Inbox :size="48" class="opacity-20" />
          </div>
          <h3 class="font-black italic mt-6">ARCHIVE IS EMPTY.</h3>
          <p class="opacity-40">나중에 다시 보고 싶은 글을 저장해 보세요.</p>
          <button class="action-btn-primary mt-8" @click="router.push('/community')">
            EXPLORE FEED <ArrowRight :size="16" />
          </button>
        </div>

        <!-- 🌟 게시글 그리드 (카드 크기 최적화 및 보라색 포인트) -->
        <div v-else class="scrap-grid">
          <article
            v-for="(post, index) in scrapPosts"
            :key="post.id"
            class="scrap-card glass-effect animate-in"
            :style="{ animationDelay: `${index * 0.05}s` }"
          >
            <div class="card-main" @click="goDetail(post)">
              <div class="card-meta">
                <div class="author-tag font-black italic">
                  <User :size="12" /> {{ post.writer.toUpperCase() }}
                </div>
                <div class="date-text font-black opacity-30">{{ post.createdAt }}</div>
              </div>
              <h3 class="post-title font-black">{{ post.title }}</h3>
              <div class="card-footer">
                <div class="stat-pill"><Heart :size="12" class="text-peach" /> {{ post.likes }}</div>
                <div class="stat-pill"><MessageSquare :size="12" /> VIEW</div>
              </div>
            </div>

            <div class="card-actions">
              <button class="circle-btn view" @click="goDetail(post)" title="상세보기">
                <ExternalLink :size="18" />
              </button>
              <button class="circle-btn delete" @click="openCancelModal(post)" title="삭제">
                <Trash2 :size="18" />
              </button>
            </div>
          </article>
        </div>
      </section>
    </main>

    <!-- 🌟 삭제 확인 모달 -->
    <Transition name="fade">
      <div v-if="showCancelModal" class="modal-overlay" @click.self="showCancelModal = false">
        <div class="auth-modal-card glass-effect">
          <button class="close-btn" @click="showCancelModal = false"><X :size="24" /></button>
          
          <div class="modal-header">
            <div class="mini-logo font-black italic">MEDEAT</div>
            <h3 class="modal-title font-black italic">UNSAVE POST.</h3>
            <p class="modal-subtitle">이 게시글을 보관함에서 삭제하시겠습니까?</p>
          </div>

          <div class="modal-actions mt-8">
            <button class="modal-btn confirm font-black" @click="confirmCancel">삭제 확정</button>
            <button class="modal-btn cancel font-black" @click="showCancelModal = false">돌아가기</button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<style scoped>
/* --- 🌟 가이드라인 및 MyPage.vue 배경 완벽 계승 --- */
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

/* 배경 Blobs (MyPage.vue와 동일 수치) */
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

.content-wrapper { position: relative; z-index: 10; max-width: 1200px; margin: 0 auto; width: 100%; }

/* --- 🌟 디자인 시스템: Glassmorphism --- */
.glass-effect {
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 32px;
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.07);
}

/* --- 🌟 헤더 스타일 (가이드라인 폰트 적용) --- */
.page-header { text-align: center; margin-bottom: 60px; }
.badge-row { display: flex; justify-content: center; margin-bottom: 24px; }
.badge { display: inline-flex; align-items: center; gap: 8px; padding: 8px 16px; font-size: 0.65rem; border-radius: 999px; }

.page-title { font-size: 3.5rem; line-height: 1; margin-bottom: 16px; }
.gradient-text {
  background: linear-gradient(to right, #1A1A1A, #7C4DFF, #FF8A65);
  -webkit-background-clip: text; background-clip: text; color: transparent;
}
.page-desc { font-size: 1.1rem; opacity: 0.4; font-weight: 600; letter-spacing: -0.01em; }

/* --- 🌟 스크랩 리스트 (식단 리스트 스타일 참고) --- */
.scrap-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.scrap-card {
  padding: 28px; display: flex; justify-content: space-between; align-items: center;
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
}
.scrap-card:hover {
  transform: translateY(-5px);
  background: rgba(255, 255, 255, 0.6);
  border-color: #7C4DFF;
}

.card-main { flex: 1; cursor: pointer; overflow: hidden; }
.card-meta { display: flex; align-items: center; gap: 16px; margin-bottom: 10px; }

/* 글쓴이 부분 보라색(#7C4DFF) 적용 */
.author-tag { 
  display: flex; align-items: center; gap: 6px; 
  font-size: 0.7rem; letter-spacing: 0.05em;
  color: #7C4DFF;
}

.date-text { font-size: 0.7rem; }

.post-title { 
  font-size: 1.15rem; 
  line-height: 1.4; 
  margin-bottom: 16px; 
  word-break: keep-all; 
  color: #1A1A1A;
}

.card-footer { display: flex; gap: 10px; }
.stat-pill {
  padding: 4px 10px; background: rgba(0,0,0,0.03); border-radius: 999px;
  font-size: 0.65rem; font-weight: 900; display: flex; align-items: center; gap: 6px; color: #64748b;
}
.text-peach { color: #FF8A65; }

.card-actions { display: flex; flex-direction: column; gap: 8px; margin-left: 20px; }
.circle-btn {
  width: 44px; height: 44px; border-radius: 50%; border: none; background: #1A1A1A; color: white;
  display: flex; align-items: center; justify-content: center; cursor: pointer; transition: 0.2s;
}
.circle-btn.delete { background: #f1f5f9; color: #94a3b8; }
.circle-btn:hover { transform: scale(1.1); }
.circle-btn.view:hover { background: #7C4DFF; box-shadow: 0 8px 16px rgba(124, 77, 255, 0.2); }
.circle-btn.delete:hover { background: #FF8A65; color: white; box-shadow: 0 8px 16px rgba(255, 138, 101, 0.2); }

/* --- 🌟 상태 박스 --- */
.state-box { padding: 80px 40px; text-align: center; }
.spinner { animation: rotate 1.5s linear infinite; color: #7C4DFF; margin-bottom: 20px; }
.empty-icon-circle { width: 80px; height: 80px; border-radius: 50%; margin: 0 auto; display: flex; align-items: center; justify-content: center; }

/* --- 🌟 모달 디자인 (Intro 스타일) --- */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.5); 
  backdrop-filter: blur(10px); -webkit-backdrop-filter: blur(10px);
  z-index: 2000; display: flex; align-items: center; justify-content: center;
}
.auth-modal-card { width: 100%; max-width: 420px; padding: 50px 40px; text-align: center; position: relative; background: #fff; border-radius: 40px; }
.close-btn { position: absolute; top: 20px; right: 20px; background: none; border: none; opacity: 0.3; cursor: pointer; }
.mini-logo { font-weight: 950; color: #7C4DFF; font-size: 0.8rem; letter-spacing: 2px; margin-bottom: 16px; }
.modal-title { font-size: 2.2rem; margin-bottom: 12px; }
.modal-subtitle { font-size: 0.95rem; opacity: 0.6; font-weight: 600; line-height: 1.5; }

.modal-actions { display: flex; flex-direction: column; gap: 10px; }
.modal-btn { padding: 18px; border-radius: 16px; border: none; cursor: pointer; transition: 0.2s; font-size: 1rem; }
.confirm { background: #1A1A1A; color: white; }
.confirm:hover { background: #FF8A65; transform: translateY(-2px); }
.cancel { background: #f1f5f9; color: #64748b; }

/* --- Utilities --- */
.font-black { font-weight: 950; }
.italic { font-style: italic; }
.tracking-tighter { letter-spacing: -0.06em; }
.text-primary { color: #7C4DFF; }
.mt-8 { margin-top: 2rem; }

.action-btn-primary {
  background: #1A1A1A; color: white; border: none; padding: 14px 32px; border-radius: 999px;
  font-weight: 950; cursor: pointer; transition: 0.3s; display: flex; align-items: center; gap: 10px; margin: 0 auto;
}
.action-btn-primary:hover { transform: translateY(-3px); background: #7C4DFF; }

@keyframes rotate { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
.animate-in { animation: slideUp 0.6s cubic-bezier(0.23, 1, 0.32, 1) forwards; opacity: 0; }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

@media (max-width: 900px) {
  .scrap-grid { grid-template-columns: 1fr; }
  .page-title { font-size: 2.8rem; }
}
</style>