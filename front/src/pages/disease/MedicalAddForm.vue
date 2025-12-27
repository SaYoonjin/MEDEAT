<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import diseaseApi from "@/api/disease";
import { 
  Sparkles, 
  Stethoscope, 
  Plus, 
  X, 
  Check, 
  HeartPulse,
  Search,
  Save,
  Activity
} from "lucide-vue-next";

const router = useRouter();

const allDiseases = ref([]);    // 전체 질병
const selectedList = ref([]);  // 내 질병 목록
const searchQuery = ref("");   // 검색어 상태

/* ===============================
   데이터 로드 로직
================================ */
const loadAllDiseases = async () => {
  try {
    const res = await diseaseApi.getAllDiseases();
    allDiseases.value = res.data || [];
  } catch (e) {
    console.error("전체 질병 조회 실패", e);
  }
};

const loadMyDiseases = async () => {
  try {
    const res = await diseaseApi.getMyDiseases();
    selectedList.value = res.data || [];
  } catch (e) {
    console.error("내 질병 목록 조회 실패", e);
  }
};

/* ===============================
   검색 필터링 로직
================================ */
const filteredDiseases = computed(() => {
  const query = searchQuery.value.trim().toLowerCase();
  if (!query) return allDiseases.value;
  return allDiseases.value.filter(d => 
    d.diseaseName.toLowerCase().includes(query)
  );
});

/* ===============================
   액션 로직
================================ */
const toggleDisease = async (disease) => {
  const isSelected = selectedList.value.find(s => s.diseaseId === disease.diseaseId);
  
  if (isSelected) {
    // 이미 선택된 경우 삭제
    await removeDisease(isSelected.userDiseaseId);
  } else {
    // 선택되지 않은 경우 추가
    try {
      await diseaseApi.addUserDisease(disease.diseaseId);
      await loadMyDiseases();
    } catch (err) {
      if (err.response?.status !== 409) console.error("질병 추가 실패", err);
    }
  }
};

const removeDisease = async (userDiseaseId) => {
  try {
    await diseaseApi.deleteUserDisease(userDiseaseId);
    selectedList.value = selectedList.value.filter(
      d => d.userDiseaseId !== userDiseaseId
    );
  } catch (e) {
    console.error("질병 삭제 실패", e);
  }
};

const handleSave = () => {
  // 저장 로직 완료 후 이동
  router.push({ path: "/disease", query: { mode: "MEDI_EAT" } });
};

onMounted(async () => {
  await loadMyDiseases();
  await loadAllDiseases();
});
</script>

<template>
  <div class="intro-container">
    <!-- 배경 장식 -->
    <div class="blob-medi"></div>

    <main class="content-wrapper">
      <div class="form-card glass-card">
        <!-- Header 섹션 -->
        <header class="page-header">
          <div class="badge">
            <HeartPulse :size="12" class="icon-purple" />
            <span>CONDITION DEFINED</span>
          </div>
          <h2 class="hero-title italic">
            ADD<br />
            <span class="gradient-text">CONDITION.</span>
          </h2>
          <p class="hero-subtext">관리가 필요한 질병을 검색하고 선택하여 건강 프로필을 완성하세요.</p>
        </header>

        <div class="form-container">
          <!-- 1. 검색 및 선택 섹션 -->
          <section class="form-section">
            <div class="section-header-row">
              <h3 class="section-title italic">1. FIND CONDITIONS</h3>
              <div class="search-box-mini">
                <Search :size="14" class="search-icon" />
                <input v-model="searchQuery" type="text" placeholder="질명 검색..." />
              </div>
            </div>
            
            <div class="selection-grid mt-6">
              <button
                v-for="d in filteredDiseases"
                :key="d.diseaseId"
                class="disease-card-item"
                :class="{ selected: selectedList.some(s => s.diseaseId === d.diseaseId) }"
                @click="toggleDisease(d)"
              >
                <div class="card-status">
                   <div class="check-box">
                      <Check v-if="selectedList.some(s => s.diseaseId === d.diseaseId)" :size="12" />
                   </div>
                </div>
                <span class="disease-label">{{ d.diseaseName }}</span>
              </button>
              
              <div v-if="filteredDiseases.length === 0" class="no-results">
                검색 결과가 없습니다.
              </div>
            </div>
          </section>

          <!-- 2. 내 질병 목록 -->
          <section class="form-section mt-16">
            <h3 class="section-title italic">2. MY LIST</h3>
            <div class="selected-bucket mt-6">
              <div v-if="selectedList.length === 0" class="empty-bucket-state">
                <Activity :size="32" class="opacity-20" />
                <p>현재 선택된 항목이 없습니다.</p>
              </div>

              <div v-else class="selected-tags-grid">
                <TransitionGroup name="tag-pop">
                  <div v-for="d in selectedList" :key="d.diseaseId" class="premium-tag">
                    <span class="tag-name">{{ d.diseaseName }}</span>
                    <button class="tag-remove-btn" @click="removeDisease(d.userDiseaseId)">
                      <X :size="12" />
                    </button>
                  </div>
                </TransitionGroup>
              </div>
            </div>
          </section>

          <!-- 하단 액션 버튼 -->
          <footer class="footer-actions mt-16">
            <button class="main-start-btn w-full" @click="handleSave">
              SAVE & COMPLETE
            </button>
            <!-- 🌟 간격을 mt-10에서 mt-14로 늘려 여유를 주었습니다. -->
            <p class="guide-note mt-14">
              정보가 정확할수록 인공지능이 더 정밀한 건강 리포트를 생성합니다.
            </p>
          </footer>
        </div>
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

.blob-medi {
  position: fixed;
  bottom: -15%;
  right: -10%;
  width: 60vw;
  height: 60vw;
  background: radial-gradient(circle, rgba(56, 189, 248, 0.2) 0%, transparent 70%);
  border-radius: 50%;
  filter: blur(140px);
  z-index: 0;
  pointer-events: none;
  animation: blobFloat 25s infinite alternate ease-in-out;
}

@keyframes blobFloat {
  0% { transform: translate(0, 0) rotate(0deg); }
  100% { transform: translate(5%, 5%) rotate(180deg); }
}

.content-wrapper {
  width: 100%;
  max-width: 720px;
  position: relative;
  z-index: 10;
}

.glass-card {
  background: rgba(255, 255, 255, 0.45);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 50px;
  padding: 60px 50px;
  box-shadow: 0 40px 100px rgba(0, 0, 0, 0.02);
}

.page-header { text-align: center; margin-bottom: 50px; }
.badge {
  display: inline-flex; align-items: center; gap: 6px; padding: 6px 14px;
  background: white; border-radius: 999px; margin-bottom: 24px;
  font-size: 0.65rem; font-weight: 850; color: #7c4dff; border: 1.5px solid #f1f5f9;
}
.hero-title { font-size: 3.5rem; line-height: 0.95; font-weight: 950; letter-spacing: -0.05em; margin-bottom: 20px; }
.gradient-text {
  background: linear-gradient(to bottom right, #1a1a1a, #38bdf8);
  -webkit-background-clip: text; background-clip: text; color: transparent;
}
.hero-subtext { font-size: 1.1rem; opacity: 0.6; font-weight: 500; }

.section-header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.section-title {
  font-size: 1.1rem; font-weight: 900; letter-spacing: 0.05em;
  color: #1a1a1a; display: flex; align-items: center; gap: 12px;
}
.section-title::after { content: ''; width: 60px; height: 1px; background: rgba(0,0,0,0.05); }

/* 검색창 */
.search-box-mini {
  display: flex; align-items: center; gap: 8px;
  background: rgba(255, 255, 255, 0.6); border: 1px solid #f1f5f9;
  padding: 8px 16px; border-radius: 999px; width: 200px;
  transition: 0.3s;
}
.search-box-mini:focus-within { border-color: #38bdf8; width: 240px; background: white; }
.search-box-mini input { border: none; outline: none; background: transparent; font-size: 0.8rem; font-weight: 700; width: 100%; }
.search-icon { color: #cbd5e1; }

/* 새로운 선택 방식: 카드 그리드 */
.selection-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 12px;
  max-height: 320px;
  overflow-y: auto;
  padding-right: 8px;
}
.selection-grid::-webkit-scrollbar { width: 4px; }
.selection-grid::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }

.disease-card-item {
  background: white; border: 1.5px solid #f1f5f9; border-radius: 20px;
  padding: 16px; display: flex; flex-direction: column; align-items: flex-start;
  gap: 12px; cursor: pointer; transition: 0.3s cubic-bezier(0.23, 1, 0.32, 1);
  text-align: left;
}
.disease-card-item:hover { transform: translateY(-3px); border-color: #38bdf8; box-shadow: 0 10px 20px rgba(56, 189, 248, 0.08); }
.disease-card-item.selected { background: #f0f9ff; border-color: #38bdf8; }

.card-status { width: 24px; height: 24px; }
.check-box {
  width: 22px; height: 22px; border-radius: 7px; border: 2px solid #e2e8f0;
  display: flex; align-items: center; justify-content: center; transition: 0.3s;
}
.selected .check-box { background: #38bdf8; border-color: #38bdf8; color: white; }

.disease-label { font-size: 0.9rem; font-weight: 850; color: #1a1a1a; line-height: 1.2; }

/* 내 질병 버킷 */
.selected-bucket {
  background: rgba(0,0,0,0.02); border-radius: 35px;
  padding: 30px; min-height: 120px; border: 1px dashed rgba(0,0,0,0.05);
}
.empty-bucket-state {
  height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 12px;
  color: #cbd5e1; font-weight: 700; font-size: 0.9rem;
}

.selected-tags-grid { display: flex; flex-wrap: wrap; gap: 10px; }

.premium-tag {
  display: inline-flex; align-items: center; gap: 10px;
  padding: 10px 18px; border-radius: 16px;
  background: white;
  border: 1px solid #f1f5f9;
  box-shadow: 0 4px 15px rgba(56, 189, 248, 0.1);
  animation: tagPop 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}
.tag-name { font-size: 0.9rem; font-weight: 850; color: #1a1a1a; }

.tag-remove-btn {
  background: #f1f5f9; border: none; color: #94a3b8;
  width: 20px; height: 20px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: 0.2s;
}
.tag-remove-btn:hover { background: #f43f5e; color: white; }

/* 하단 액션 버튼 */
.footer-actions { text-align: center; }
.main-start-btn {
  display: flex; align-items: center; justify-content: center; gap: 12px;
  padding: 22px; background: #1a1a1a; color: white; border: none; border-radius: 999px;
  font-size: 1.1rem; font-weight: 850; cursor: pointer; transition: all 0.4s;
}
.main-start-btn:hover {
  background: #2a2a2a; transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(56, 189, 248, 0.2);
}

.guide-note { font-size: 0.8rem; color: #94a3b8; text-align: center; font-weight: 600; line-height: 1.5; }

.no-results { grid-column: 1 / -1; padding: 40px; text-align: center; color: #cbd5e1; font-weight: 700; font-size: 0.9rem; }

/* 애니메이션 */
@keyframes tagPop {
  0% { opacity: 0; transform: scale(0.5) translateY(10px); }
  100% { opacity: 1; transform: scale(1) translateY(0); }
}

.italic { font-style: italic; }
.mt-14 { margin-top: 2rem; }
.mt-16 { margin-top: 4rem; }
.ml-2 { margin-left: 0.5rem; }
.opacity-20 { opacity: 0.2; }
.w-full { width: 100%; }

@media (max-width: 768px) {
  .hero-title { font-size: 2.8rem; }
  .glass-card { padding: 40px 24px; border-radius: 40px; }
  .search-box-mini { width: 100%; margin-top: 10px; }
  .section-header-row { flex-direction: column; align-items: flex-start; }
}
</style>