<script setup>
import { ref, onMounted, computed, watch } from "vue";
import { useRouter } from "vue-router";
import diseaseApi from "@/api/disease";
import medicationApi from "@/api/medication";
import { 
  Sparkles, 
  Stethoscope, 
  Pill, 
  Plus, 
  X, 
  Check, 
  ChevronLeft, 
  ChevronRight, 
  Calendar,
  AlertCircle,
  Clock,
  Trash2,
  Edit3
} from "lucide-vue-next";

const router = useRouter();

/* ===============================
   질병 관리 로직
================================ */
const diseases = ref([]);

const loadDiseases = async () => {
  try {
    const res = await diseaseApi.getMyDiseases();
    diseases.value = res.data || [];
  } catch (e) {
    console.error("질병 불러오기 실패", e);
  }
};

const removeDisease = async (id) => {
  if (!confirm("이 질병 정보를 삭제하시겠습니까?")) return;
  try {
    await diseaseApi.remove(id);
    await loadDiseases();
  } catch (e) {
    console.error("질병 삭제 실패", e);
  }
};

/* ===============================
   약 목록 및 복용 로그 로직
================================ */
const medications = ref([]);
const todayLogs = ref([]);

const loadTodayLogs = async () => {
  try {
    const res = await medicationApi.getTodayLogs();
    todayLogs.value = res.data || [];
  } catch (e) {
    console.error("복용 로그 로드 실패", e);
  }
};

const loadMedications = async () => {
  try {
    const res = await medicationApi.getMyMedications();
    medications.value = (res.data || []).map((m) => {
      // 🌟 시간 파싱 시점에 이미 정렬됨
      const times = parseTimes(m.intakeTime);
      const takenStatus = times.map((_, idx) =>
        todayLogs.value.some(
          (log) =>
            log.medicationId === m.medicationId &&
            log.takenIndex === idx + 1
        )
      );
      return { ...m, takenStatus };
    });
  } catch (err) {
    console.error("약 목록 불러오기 실패", err);
  }
};

const toggleTaken = async (med, index) => {
  const originalStatus = med.takenStatus[index];
  med.takenStatus[index] = !originalStatus;

  try {
    await medicationApi.saveLog({
      medicationId: med.medicationId,
      takenIndex: index + 1,
    });
    await loadTodayLogs();
    await loadMedications();
  } catch (e) {
    med.takenStatus[index] = originalStatus;
    console.error("복용 상태 저장 실패");
  }
};

const removeMedication = async (id) => {
  if (!confirm("이 약을 목록에서 삭제하시겠습니까?")) return;
  try {
    await medicationApi.remove(id);
    await loadMedications();
  } catch (e) {
    console.error("약 삭제 실패", e);
  }
};

/* ===============================
   슬라이더 및 헬퍼
================================ */
const pageSize = 2;
const currentMedPage = ref(0);

const medPages = computed(() => {
  const chunks = [];
  for (let i = 0; i < medications.value.length; i += pageSize) {
    chunks.push(medications.value.slice(i, i + pageSize));
  }
  return chunks;
});

const totalMedPages = computed(() => medPages.value.length);
const visibleMeds = computed(() => medPages.value[currentMedPage.value] || []);

const prevMedPage = () => {
  currentMedPage.value = (currentMedPage.value - 1 + totalMedPages.value) % totalMedPages.value;
};
const nextMedPage = () => {
  currentMedPage.value = (currentMedPage.value + 1) % totalMedPages.value;
};

// 🌟 복용 시간대 오름차순 정렬 로직 추가
const parseTimes = (intakeTime) => {
  if (!intakeTime) return [];
  return intakeTime
    .split(",")
    .map((t) => t.trim())
    .filter(Boolean)
    .sort((a, b) => a.localeCompare(b)); // 오름차순 정렬
};

const goAddDisease = () => router.push("/disease/MedicalAddForm");
const goAddMedication = () => router.push("/medication/new");
const goEditMedication = (id) => router.push(`/medication/${id}`);

onMounted(async () => {
  loadDiseases();
  await loadTodayLogs();
  await loadMedications();
});

watch(() => medications.value.length, () => (currentMedPage.value = 0));
</script>

<template>
  <div class="intro-container">
    <div class="blob-medi"></div>

    <main class="content-wrapper">
      <div class="manage-card glass-card">
        <!-- Header 섹션 -->
        <header class="page-header">
          <div class="badge">
            <Calendar :size="12" class="icon-purple" />
            <span>DAILY CARE RECORD</span>
          </div>
          <h1 class="hero-title italic">
            HEALTH<br />
            <span class="gradient-text">MANAGEMENT.</span>
          </h1>
          <p class="hero-subtext">질병 이력과 복용 중인 약품을 체계적으로 관리하세요.</p>
        </header>

        <!-- 1. 질병 관리 섹션 -->
        <section class="manage-section mt-16">
          <div class="section-header">
            <div class="title-group">
              <h3 class="section-title italic">MY CONDITIONS</h3>
              <p class="section-desc">현재 등록된 건강 상태입니다.</p>
            </div>
            <button class="add-chip-btn" @click="goAddDisease">
              <Plus :size="14" />
              질병 추가
            </button>
          </div>

          <div v-if="diseases.length === 0" class="empty-state">
            <AlertCircle :size="32" class="opacity-20" />
            <p>등록된 질병이 없습니다. <br/><span class="point" @click="goAddDisease">질병 정보를 추가</span>해 보세요.</p>
          </div>

          <!-- 🌟 mt-6 -> mt-10으로 간격 확장 -->
          <div v-else class="disease-list mt-10">
            <div v-for="d in diseases" :key="d.userDiseaseId" class="disease-item-card">
              <div class="disease-icon-box"><Stethoscope :size="18" /></div>
              <div class="disease-info">
                <span class="disease-name">{{ d.diseaseName }}</span>
                <span class="disease-meta">정기 관찰 및 케어 중</span>
              </div>
              <button class="remove-item-btn" @click="removeDisease(d.userDiseaseId)"><Trash2 :size="16" /></button>
            </div>
          </div>
        </section>

        <!-- 구분선 -->
        <div class="divider-line mt-16"></div>

        <!-- 2. 약 복용 관리 섹션 (🌟 mt-16 -> mt-10으로 간격 축소) -->
        <section class="manage-section mt-10">
          <div class="section-header">
            <div class="title-group">
              <h3 class="section-title italic">DAILY MEDICATION</h3>
              <p class="section-desc">오늘 복용해야 할 약품과 스케줄입니다.</p>
            </div>
            <button class="add-chip-btn pill-btn" @click="goAddMedication">
              <Pill :size="14" />
              복용약 추가
            </button>
          </div>

          <div v-if="medications.length === 0" class="empty-state">
            <Pill :size="32" class="opacity-20" />
            <p>복용 중인 약이 없습니다. <br/><span class="point" @click="goAddMedication">스케줄을 등록</span>하고 알림을 받으세요.</p>
          </div>

          <!-- 🌟 mt-8 -> mt-12로 간격 확장 (문구와 슬라이더 사이) -->
          <div v-else class="med-slider-area mt-12">
            <div class="slider-top-info">
              <span class="today-tag">TODAY'S SCHEDULE</span>
              <span class="page-indicator">{{ currentMedPage + 1 }} / {{ totalMedPages }}</span>
            </div>

            <div class="med-card-grid mt-4">
              <article v-for="m in visibleMeds" :key="m.medicationId" class="med-item-card">
                <div class="med-card-head">
                  <div class="pill-icon-mini"><Pill :size="16" /></div>
                  <h4 class="med-name">{{ m.drugName }}</h4>
                </div>

                <div class="med-card-content">
                  <div class="med-spec">
                    <span class="dose">{{ m.dose }}</span>
                    <span class="count">1일 {{ m.dailyCount }}회</span>
                  </div>

                  <div class="intake-timeline">
                    <div 
                      v-for="(t, idx) in parseTimes(m.intakeTime)" 
                      :key="idx" 
                      class="time-check-circle"
                      :class="{ 'is-taken': m.takenStatus?.[idx] }"
                      @click="toggleTaken(m, idx)"
                    >
                      <span class="time-val">{{ t }}</span>
                      <div class="status-marker">
                        <Check v-if="m.takenStatus?.[idx]" :size="10" />
                        <span v-else>OFF</span>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="med-card-actions">
                  <button class="edit-btn" @click="goEditMedication(m.medicationId)">수정</button>
                  <button class="delete-btn" @click="removeMedication(m.medicationId)">삭제</button>
                </div>
              </article>
            </div>

            <div v-if="totalMedPages > 1" class="slider-controls mt-10">
              <button class="arrow-btn" @click="prevMedPage"><ChevronLeft :size="20" /></button>
              <div class="pagination-dots">
                <span 
                  v-for="i in totalMedPages" 
                  :key="i" 
                  class="p-dot" 
                  :class="{ active: i - 1 === currentMedPage }"
                ></span>
              </div>
              <button class="arrow-btn" @click="nextMedPage"><ChevronRight :size="20" /></button>
            </div>
          </div>
        </section>
      </div>
    </main>
  </div>
</template>

<style scoped>
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
  top: -10%;
  right: -10%;
  width: 70vw;
  height: 70vw;
  background: radial-gradient(circle, rgba(56, 189, 248, 0.15) 0%, transparent 70%);
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
  max-width: 860px;
  position: relative;
  z-index: 10;
}

.glass-card {
  background: rgba(255, 255, 255, 0.55);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 50px;
  padding: 60px 50px;
  box-shadow: 0 40px 100px rgba(0, 0, 0, 0.02);
}

.page-header { text-align: center; margin-bottom: 40px; }
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
.hero-subtext { font-size: 1.1rem; opacity: 0.6; font-weight: 600; }

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}
.section-title { font-size: 1.2rem; font-weight: 900; color: #1a1a1a; letter-spacing: 0.05em; }
.section-desc { font-size: 0.9rem; color: #94a3b8; font-weight: 600; margin-top: 4px; }

.add-chip-btn {
  display: flex; align-items: center; gap: 8px;
  padding: 10px 20px; border-radius: 999px; border: none;
  background: #f1f5f9; color: #64748b; font-size: 0.85rem; font-weight: 850;
  cursor: pointer; transition: 0.3s;
}
.add-chip-btn:hover { background: #38bdf8; color: white; transform: translateY(-3px); box-shadow: 0 10px 20px rgba(56, 189, 248, 0.2); }
.pill-btn:hover { background: #7c4dff; }

.empty-state {
  margin-top: 24px;
  padding: 40px; border-radius: 30px;
  background: rgba(0,0,0,0.02); border: 1.5px dashed #e2e8f0;
  text-align: center; display: flex; flex-direction: column; align-items: center; gap: 12px;
}
.empty-state p { font-size: 0.95rem; color: #94a3b8; font-weight: 600; line-height: 1.6; }
.empty-state .point { color: #38bdf8; cursor: pointer; text-decoration: underline; }

.disease-list { display: flex; flex-direction: column; gap: 12px; }
.disease-item-card {
  display: flex; align-items: center; gap: 16px; padding: 18px 24px;
  background: white; border-radius: 24px; border: 1px solid #f1f5f9;
  box-shadow: 0 4px 15px rgba(0,0,0,0.02); transition: 0.3s;
}
.disease-item-card:hover { transform: scale(1.01); box-shadow: 0 10px 30px rgba(56, 189, 248, 0.08); }

.disease-icon-box {
  width: 44px; height: 44px; border-radius: 14px;
  background: rgba(56, 189, 248, 0.1); color: #38bdf8;
  display: flex; align-items: center; justify-content: center;
}
.disease-info { flex: 1; display: flex; flex-direction: column; }
.disease-name { font-size: 1.05rem; font-weight: 850; color: #1a1a1a; }
.disease-meta { font-size: 0.75rem; color: #94a3b8; font-weight: 600; }

.remove-item-btn {
  background: none; border: none; color: #cbd5e1; cursor: pointer; transition: 0.3s;
}
.remove-item-btn:hover { color: #f43f5e; }

.divider-line { height: 1px; background: rgba(0,0,0,0.05); }

.med-card-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}
.med-item-card {
  background: white; border-radius: 35px; padding: 28px;
  border: 1px solid #f1f5f9; box-shadow: 0 15px 35px rgba(0,0,0,0.03);
  display: flex; flex-direction: column; gap: 20px;
}

.med-card-head { display: flex; align-items: center; gap: 10px; }
.pill-icon-mini { width: 32px; height: 32px; border-radius: 10px; background: #f8fafc; display: flex; align-items: center; justify-content: center; color: #7c4dff; }
.med-name { font-size: 1.15rem; font-weight: 900; color: #1a1a1a; }

.med-spec { display: flex; flex-direction: column; gap: 4px; }
.med-spec .dose { font-size: 0.95rem; font-weight: 800; color: #1a1a1a; }
.med-spec .count { font-size: 0.75rem; color: #94a3b8; font-weight: 600; }

.intake-timeline { display: flex; gap: 10px; margin-top: 10px; flex-wrap: wrap; }
.time-check-circle {
  flex: 1; min-width: 70px; padding: 12px 8px; border-radius: 20px;
  background: #f8fafc; border: 1.5px solid #f1f5f9;
  display: flex; flex-direction: column; align-items: center; gap: 6px;
  cursor: pointer; transition: 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}
.time-val { font-size: 0.85rem; font-weight: 900; color: #64748b; }
.status-marker { font-size: 0.6rem; font-weight: 950; color: #cbd5e1; }

.time-check-circle.is-taken {
  background: white; border-color: #22c55e;
  box-shadow: 0 10px 20px rgba(34, 197, 94, 0.15);
}
.time-check-circle.is-taken .time-val { color: #16a34a; }
.time-check-circle.is-taken .status-marker { color: #22c55e; }

.med-card-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: auto; }
.edit-btn, .delete-btn {
  background: none; border: none; font-size: 0.75rem; font-weight: 850; cursor: pointer; transition: 0.3s;
}
.edit-btn { color: #38bdf8; }
.delete-btn { color: #cbd5e1; }
.delete-btn:hover { color: #f43f5e; }

.slider-top-info { display: flex; justify-content: space-between; align-items: center; }
.today-tag { font-size: 0.7rem; font-weight: 900; color: #38bdf8; letter-spacing: 0.1em; }
.page-indicator { font-size: 0.75rem; font-weight: 800; color: #cbd5e1; }

.slider-controls { display: flex; align-items: center; justify-content: center; gap: 20px; }
.arrow-btn {
  width: 44px; height: 44px; border-radius: 50%; border: 1.5px solid #f1f5f9;
  background: white; display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: 0.3s; color: #1a1a1a;
}
.arrow-btn:hover { border-color: #38bdf8; color: #38bdf8; transform: scale(1.1); }

.pagination-dots { display: flex; gap: 8px; }
.p-dot { width: 6px; height: 6px; border-radius: 50%; background: #e2e8f0; transition: 0.3s; }
.p-dot.active { width: 20px; border-radius: 10px; background: #1a1a1a; }

.italic { font-style: italic; }
.mt-10 { margin-top: 2.5rem; }
.mt-12 { margin-top: 2rem; }
.mt-16 { margin-top: 4rem; }
.opacity-20 { opacity: 0.2; }

@media (max-width: 820px) {
  .med-card-grid { grid-template-columns: 1fr; }
  .hero-title { font-size: 2.8rem; }
  .glass-card { padding: 40px 24px; border-radius: 40px; }
}
</style>