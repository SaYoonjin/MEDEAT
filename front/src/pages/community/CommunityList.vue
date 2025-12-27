<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import communityApi from "@/api/community";
import { 
  Sparkles, 
  Search, 
  MessageSquare, 
  Plus, 
  ChevronRight, 
  Heart, 
  Bookmark, 
  ArrowRight, 
  Loader2, 
  Inbox,
  Calendar,
  User,
  TrendingUp,
  LayoutGrid,
  X,
  ExternalLink
} from "lucide-vue-next";

const router = useRouter();
const route = useRoute();
const keyword = ref("");
const isSearchFocused = ref(false);
const posts = ref([]);
const sortKey = ref("latest");
const loading = ref(false);

// 디자인 시스템 컬러 정의
const designSystem = {
  primary: "#7C4DFF",
  secondary: "#FF8A65",
  eat: "#a3e635",
  medi: "#38bdf8"
};

// --------------------
// 데이터 로딩 엔진
// --------------------
const loadList = async () => {
  loading.value = true;
  try {
    const res = await communityApi.getList(null, keyword.value);
    posts.value = res.data || [];
  } catch (e) {
    console.error("데이터 로드 실패:", e);
  } finally {
    loading.value = false;
  }
};

const search = () => loadList();
const clearSearch = () => { keyword.value = ""; loadList(); };

// --------------------
// 계산 속성 (정렬 및 하이라이트)
// --------------------
const sortedPosts = computed(() => {
  const list = [...posts.value];
  if (sortKey.value === "popular") {
    return list.sort((a, b) => {
      const likeDiff = (b.likeCount || 0) - (a.likeCount || 0);
      if (likeDiff !== 0) return likeDiff;
      return new Date(b.createdAt) - new Date(a.createdAt);
    });
  } else {
    return list.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  }
});

const topPost = computed(() => {
  if (posts.value.length === 0) return null;
  return [...posts.value].sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0))[0];
});

const formatDate = (dateStr) => {
  if (!dateStr) return "";
  const date = new Date(dateStr);
  return `${date.getMonth() + 1}월 ${date.getDate()}일`;
};

// --------------------
// 라우터 이동
// --------------------
const goWrite = () => router.push({ name: "community-new" });
const goDetail = (postId) => router.push({ name: "community-detail", params: { id: postId } });

onMounted(loadList);
</script>

<template>
  <div class="page-container">
    <!-- 🌟 배경: MyScrap과 100% 동일하게 수정된 하이브리드 블롭 -->
    <div class="background-decorations">
      <div class="blob blob-eat"></div>
      <div class="blob blob-medi"></div>
    </div>

    <main class="content-wrapper">
      <!-- 헤더 (MyScrap과 동일한 폰트 크기 및 스타일) -->
      <header class="page-header">
        <div class="badge-row">
          <div class="badge glass-effect">
            <Sparkles :size="12" class="text-primary" />
            <span class="font-black italic text-primary">COMMUNITY INSIGHT</span>
          </div>
        </div>
        <h2 class="page-title font-black italic tracking-tighter">
          COMMUNITY<br />
          <span class="gradient-text">STREAM.</span>
        </h2>
        <p class="page-desc">건강한 일상을 설계하는 지식의 동기화</p>
      </header>

      <!-- 상단 하이라이트 -->
      <section v-if="topPost && !keyword" class="highlight-section mb-10">
        <article class="highlight-card glass-effect" @click="goDetail(topPost.postId)">
          <div class="highlight-content">
            <div class="tag-row">
              <span class="trending-badge font-black italic">
                <TrendingUp :size="14" /> TOP TRENDING
              </span>
              <div class="author-tag font-black italic text-primary">
                <User :size="12" /> @{{ topPost.writerId.toUpperCase() }}
              </div>
            </div>
            <h3 class="hot-title font-black italic tracking-tighter">{{ topPost.title }}</h3>
            <div class="hot-footer">
              <div class="stat-group">
                <span class="stat-pill"><Heart :size="14" class="text-peach" /> <strong>{{ topPost.likeCount }}</strong></span>
                <span class="stat-pill"><MessageSquare :size="14" /> <strong>{{ topPost.commentCount }}</strong></span>
              </div>
              <div class="action-hint font-black italic">
                VIEW POST <ArrowRight :size="16" />
              </div>
            </div>
          </div>
        </article>
      </section>

      <!-- 컨트롤러 -->
      <div class="control-row mb-8">
        <div class="search-box glass-effect" :class="{ focused: isSearchFocused }">
          <Search :size="18" class="text-primary" />
          <input
            v-model="keyword"
            @keyup.enter="search"
            @focus="isSearchFocused = true"
            @blur="isSearchFocused = false"
            type="text"
            placeholder="Search insights..."
          />
          <button v-if="keyword" class="clear-btn" @click="clearSearch" title="검색어 지우기">
            <X :size="14" />
          </button>
        </div>

        <div class="sort-tabs-wrapper glass-effect">
          <button :class="{ active: sortKey === 'latest' }" @click="sortKey = 'latest'" class="font-black">LATEST</button>
          <button :class="{ active: sortKey === 'popular' }" @click="sortKey = 'popular'" class="font-black">POPULAR</button>
          <div class="tab-slider" :class="sortKey"></div>
        </div>

        <button class="write-btn-circle" @click="goWrite" title="새 글 작성">
          <Plus :size="24" />
        </button>
      </div>

      <!-- 리스트 -->
      <section class="posts-list-section">
        <div v-if="loading" class="state-box">
          <Loader2 class="spinner" :size="32" />
          <p class="font-black italic opacity-30">SYNCING FEED...</p>
        </div>

        <div v-else-if="posts.length === 0" class="state-box empty">
          <div class="empty-icon-circle glass-effect">
            <Inbox :size="48" class="opacity-20" />
          </div>
          <h3 class="font-black italic mt-6">FEED IS EMPTY.</h3>
          <p class="opacity-40">첫 번째 이야기의 주인공이 되어보세요.</p>
          <button class="action-btn-primary mt-8" @click="goWrite">
            START WRITING <ArrowRight :size="16" />
          </button>
        </div>

        <div v-else class="posts-grid">
          <article
            v-for="(p, index) in sortedPosts"
            :key="p.postId"
            class="post-card glass-effect animate-in"
            :style="{ animationDelay: `${index * 0.05}s` }"
            @click="goDetail(p.postId)"
          >
            <div class="card-main">
              <div class="card-meta">
                <div class="author-tag font-black italic text-primary">
                  <User :size="12" /> {{ p.writerId.toUpperCase() }}
                </div>
                <div class="date-text font-black opacity-30">{{ formatDate(p.createdAt) }}</div>
              </div>
              <h3 class="post-title font-black">{{ p.title }}</h3>
              <div class="card-footer">
                <div class="stat-pill"><Heart :size="12" class="text-peach" /> {{ p.likeCount }}</div>
                <div class="stat-pill"><MessageSquare :size="12" /> {{ p.commentCount }}</div>
                <div class="stat-pill"><Bookmark :size="12" /> {{ p.scrapCount || 0 }}</div>
              </div>
            </div>

            <div class="card-action">
              <div class="view-btn-mini">
                <ChevronRight :size="20" />
              </div>
            </div>
          </article>
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped>
/* --- 🌟 가이드라인 및 MyScrap 배경 완벽 동기화 --- */
.page-container {
  position: relative;
  min-height: 100vh;
  background: #F8F9FC;
  padding: 100px 24px 80px;
  overflow-x: hidden;
  color: #1A1A1A;
}

.background-decorations {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%; z-index: 0; pointer-events: none;
}

/* MyScrap과 동일한 수치 적용 */
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

/* --- Glassmorphism --- */
.glass-effect {
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 32px;
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.07);
}

/* --- 헤더 --- */
.page-header { text-align: center; margin-bottom: 60px; }
.badge-row { display: flex; justify-content: center; margin-bottom: 24px; }
.badge { display: inline-flex; align-items: center; gap: 8px; padding: 8px 16px; font-size: 0.65rem; border-radius: 999px; }

.page-title { font-size: 3.5rem; line-height: 1; margin-bottom: 16px; }
.gradient-text {
  background: linear-gradient(to right, #1A1A1A, #7C4DFF, #FF8A65);
  -webkit-background-clip: text; background-clip: text; color: transparent;
}
.page-desc { font-size: 1.1rem; opacity: 0.4; font-weight: 600; letter-spacing: -0.01em; }

/* --- 하이라이트 --- */
.highlight-card {
  padding: 40px 50px; cursor: pointer; transition: all 0.4s ease;
  position: relative; overflow: hidden;
}
.highlight-card:hover { transform: translateY(-5px); background: rgba(255,255,255,0.6); }

.tag-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.trending-badge { background: #FF8A65; color: white; padding: 5px 14px; border-radius: 999px; font-size: 0.75rem; display: flex; align-items: center; gap: 6px; }
.hot-title-text { font-size: 1.8rem; line-height: 1.3; margin-bottom: 32px; word-break: keep-all; }

.hot-footer { display: flex; justify-content: space-between; align-items: center; }
.stat-group { display: flex; gap: 12px; }
.stat-pill { padding: 6px 14px; background: rgba(0,0,0,0.03); border-radius: 999px; font-size: 0.8rem; display: flex; align-items: center; gap: 8px; color: #64748b; }
.action-hint { font-size: 0.9rem; color: #1A1A1A; display: flex; align-items: center; gap: 8px; }

/* --- 컨트롤러 --- */
.control-row { display: flex; gap: 16px; align-items: center; }

.search-box { flex: 1; display: flex; align-items: center; gap: 12px; padding: 0 20px; border-radius: 999px; height: 56px; }
.search-box input { flex: 1; background: none; border: none; outline: none; font-size: 1rem; font-weight: 700; color: #1A1A1A; }
.search-box.focused { background: white; border-color: #7C4DFF; box-shadow: 0 10px 25px rgba(124, 77, 255, 0.1); }

.clear-btn {
  width: 28px; height: 28px; border-radius: 50%; border: none; background: rgba(0, 0, 0, 0.05); color: #94a3b8;
  display: flex; align-items: center; justify-content: center; cursor: pointer; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); margin-right: 4px;
}
.clear-btn:hover { background: #f1f5f9; color: #1a1a1a; transform: rotate(90deg); }

.sort-tabs-wrapper { display: flex; padding: 4px; border-radius: 999px; position: relative; height: 56px; align-items: center; width: 240px; }
.sort-tabs-wrapper button { flex: 1; border: none; background: none; cursor: pointer; position: relative; z-index: 2; font-size: 0.85rem; color: #94a3b8; transition: 0.3s; }
.sort-tabs-wrapper button.active { color: white; }
.tab-slider { position: absolute; top: 4px; bottom: 4px; left: 4px; width: calc(50% - 4px); background: #1A1A1A; border-radius: 999px; transition: 0.4s cubic-bezier(0.23, 1, 0.32, 1); z-index: 1; }
.tab-slider.popular { transform: translateX(100%); }

.write-btn-circle { width: 56px; height: 56px; border-radius: 50%; border: none; background: #1A1A1A; color: white; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: 0.3s; box-shadow: 0 10px 20px rgba(0,0,0,0.1); }
.write-btn-circle:hover { background: #7C4DFF; transform: scale(1.1) rotate(90deg); }

/* --- 리스트 --- */
.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(420px, 1fr));
  gap: 20px;
}

.post-card {
  padding: 28px; display: flex; justify-content: space-between; align-items: center;
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
  cursor: pointer;
}
.post-card:hover { transform: translateY(-5px) scale(1.01); background: rgba(255, 255, 255, 0.6); border-color: #7C4DFF; }

.card-main { flex: 1; overflow: hidden; }
.card-meta { display: flex; align-items: center; gap: 16px; margin-bottom: 10px; }
.author-tag { display: flex; align-items: center; gap: 6px; font-size: 0.7rem; letter-spacing: 0.05em; color: #7C4DFF; }
.date-text { font-size: 0.7rem; }

.post-title { font-size: 1.15rem; line-height: 1.4; margin-bottom: 16px; word-break: keep-all; color: #1A1A1A; }

.card-footer { display: flex; gap: 10px; }
.stat-pill { padding: 4px 10px; background: rgba(0,0,0,0.03); border-radius: 999px; font-size: 0.65rem; font-weight: 900; display: flex; align-items: center; gap: 6px; color: #64748b; }
.text-peach { color: #FF8A65; }

.card-action { margin-left: 20px; }
.view-btn-mini { width: 40px; height: 40px; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #cbd5e1; transition: 0.3s; }
.post-card:hover .view-btn-mini { color: #7C4DFF; transform: translateX(5px); }

/* --- 상태 --- */
.state-box { padding: 80px 40px; text-align: center; }
.spinner { animation: rotate 1.5s linear infinite; color: #7C4DFF; margin-bottom: 20px; }
.empty-icon-circle { width: 80px; height: 80px; border-radius: 50%; margin: 0 auto; display: flex; align-items: center; justify-content: center; }

/* --- Utilities --- */
.font-black { font-weight: 950; }
.italic { font-style: italic; }
.tracking-tighter { letter-spacing: -0.06em; }
.text-primary { color: #7C4DFF; }
.mb-8 { margin-bottom: 2rem; }
.mb-10 { margin-bottom: 3rem; }

.action-btn-primary {
  background: #1A1A1A; color: white; border: none; padding: 14px 32px; border-radius: 999px;
  font-weight: 950; cursor: pointer; transition: 0.3s; display: flex; align-items: center; gap: 10px; margin: 0 auto;
}
.action-btn-primary:hover { transform: translateY(-3px); background: #7C4DFF; }

@keyframes rotate { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
.animate-in { animation: slideUp 0.6s cubic-bezier(0.23, 1, 0.32, 1) forwards; opacity: 0; }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

@media (max-width: 900px) {
  .posts-grid { grid-template-columns: 1fr; }
  .page-title { font-size: 2.8rem; }
  .control-row { flex-direction: column; align-items: stretch; }
  .sort-tabs-wrapper { width: 100%; }
}
</style>