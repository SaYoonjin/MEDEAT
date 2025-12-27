<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import challengeApi from '@/api/challenge'
import { 
  Sparkles, 
  Trophy, 
  Type, 
  FileText, 
  LayoutGrid, 
  Calendar, 
  Users, 
  Target, 
  Zap,
  ArrowRight,
  Loader2,
  Info,
  Save,
  X
} from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()

/* =========================
   상태 관리
========================= */
const isEdit = computed(() => !!route.params.id)

const form = ref({
  title: '',
  description: '',
  category: '',
  periodDays: 7,
  difficulty: 'NORMAL',
  maxParticipants: 10,
  modeType: 'EAT', // 기본값
})

const loading = ref(false)
const errorMsg = ref('')

/** 🌟 밖에서 설정한 모드 정보를 실시간으로 감시하여 배경에 즉각 반영 */
watch(() => route.query.mode, (newMode) => {
  if (newMode && !isEdit.value) {
    form.value.modeType = newMode;
  }
}, { immediate: true });

/* =========================
   🌟 테마 설정 (선택된 모드에 따라 위치/색상 변동)
========================= */
const themeConfig = computed(() => {
  if (form.value.modeType === 'MEDI_EAT') {
    return {
      color: "#38bdf8", // 하늘색
      soft: "rgba(56, 189, 248, 0.15)",
    }
  }
  return {
    color: "#a3e635", // 연두색
    soft: "rgba(163, 230, 53, 0.15)",
  }
});

// 버튼 활성 조건
const canSubmit = computed(() => {
  const t = form.value
  if (!t.title.trim()) return false
  if (!t.description.trim()) return false
  if (!t.category.trim()) return false
  if (!t.difficulty.trim()) return false
  if (!t.modeType.trim()) return false
  if (!t.periodDays || t.periodDays < 1) return false
  if (!t.maxParticipants || t.maxParticipants < 1) return false
  return true
})

// 버튼 비활성 이유 안내
const disabledHint = computed(() => {
  const t = form.value
  const missing = []
  if (!t.title.trim()) missing.push('제목')
  if (!t.description.trim()) missing.push('설명')
  if (!t.category.trim()) missing.push('카테고리')
  if (!t.periodDays || t.periodDays < 1) return '기간은 1일 이상이어야 합니다.'
  if (!t.maxParticipants || t.maxParticipants < 1) return '최대 인원은 1명 이상이어야 합니다.'
  if (missing.length > 0) return `필수 항목 입력: ${missing.join(', ')}`
  return ''
})

const loadForEdit = async () => {
  if (!isEdit.value) return
  try {
    loading.value = true
    errorMsg.value = ''
    const id = route.params.id
    const res = await challengeApi.getChallengeInfo(id)
    const c = res.data.challenge || res.data
    form.value = {
      title: c.title ?? '',
      description: c.description ?? '',
      category: c.category ?? '',
      periodDays: c.periodDays ?? 7,
      difficulty: c.difficulty ?? 'NORMAL',
      maxParticipants: c.maxParticipants ?? 10,
      modeType: c.modeType ?? (route.query.mode || 'EAT'),
    }
  } catch (e) {
    console.error('불러오기 실패', e)
    errorMsg.value = '기존 챌린지 정보를 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
}

const submitForm = async () => {
  if (!canSubmit.value) return
  try {
    loading.value = true
    errorMsg.value = ''
    const payload = { ...form.value }
    if (isEdit.value) {
      await challengeApi.updateChallenge(route.params.id, payload)
    } else {
      await challengeApi.createChallenge(payload)
    }
    router.push(`/challenge?mode=${form.value.modeType}`)
  } catch (e) {
    console.error('저장 실패', e)
    errorMsg.value = '저장 중 오류가 발생했습니다.'
  } finally {
    loading.value = false
  }
}

const goBack = () => router.back();

onMounted(loadForEdit)
</script>

<template>
  <div class="intro-container" :style="{ '--point-color': themeConfig.color, '--point-soft': themeConfig.soft }">
    <!-- 🌟 배경: 모드에 따라 위치와 색상이 변하는 오로라 (진입 시 즉시 반응) -->
    <div class="background-decorations">
      <Transition name="fade-blob" mode="out-in">
        <div v-if="form.modeType === 'EAT'" class="blob blob-eat" key="eat"></div>
        <div v-else class="blob blob-medi" key="medi"></div>
      </Transition>
    </div>

    <main class="content-wrapper">
      <div class="form-card glass-card">
        <!-- Header 섹션 (중앙 정렬) -->
        <header class="page-header">
          <div class="badge-centered">
            <div class="badge glass-effect">
              <Sparkles :size="12" class="text-primary" />
              <span class="font-black italic text-primary">CHALLENGE EDITOR</span>
            </div>
          </div>
          <h2 class="hero-title italic centered mt-6">
            {{ isEdit ? 'EDIT' : 'CREATE' }}<br />
            <span class="gradient-text">CHALLENGE.</span>
          </h2>
          <p class="hero-subtext centered mt-4">건강한 습관을 함께 만들어갈 챌린지를 설계하세요.</p>
        </header>

        <!-- 로딩 상태 -->
        <div v-if="loading && isEdit" class="loading-state">
          <Loader2 class="spinner" :size="48" />
          <p class="font-black italic opacity-30 mt-6">SYNCING INFO...</p>
        </div>

        <form v-else @submit.prevent="submitForm" class="form-container">
          <div v-if="errorMsg" class="error-banner">{{ errorMsg }}</div>

          <!-- 01. BASIC INFO -->
          <section class="form-section">
            <div class="section-header">
              <h3 class="section-title font-black italic">01. BASIC INFO</h3>
              <div class="panel-line"></div>
            </div>

            <div class="input-group mt-10">
              <label>챌린지 제목 <span class="required">*</span></label>
              <div class="modern-input-box glass-effect">
                <Type :size="18" class="input-icon" />
                <input v-model="form.title" type="text" placeholder="예: 저녁 8시 이후 금식하기" />
              </div>
            </div>

            <div class="input-group mt-10">
              <label>상세 설명 <span class="required">*</span></label>
              <div class="modern-textarea-box glass-effect">
                <FileText :size="18" class="textarea-icon" />
                <textarea v-model="form.description" rows="4" placeholder="챌린지의 구체적인 목표와 규칙을 설명해 주세요."></textarea>
              </div>
            </div>

            <div class="input-group mt-10">
              <label>카테고리 <span class="required">*</span></label>
              <div class="modern-input-box glass-effect">
                <LayoutGrid :size="18" class="input-icon" />
                <input v-model="form.category" type="text" placeholder="예: 식습관, 운동, 복약" />
              </div>
            </div>
          </section>

          <!-- 02. CHALLENGE SETTINGS -->
          <section class="form-section mt-24">
            <div class="section-header">
              <h3 class="section-title font-black italic">02. SETTINGS</h3>
              <div class="panel-line"></div>
            </div>

            <div class="input-row mt-10">
              <div class="input-group-half">
                <label>진행 기간 (일) <span class="required">*</span></label>
                <div class="modern-input-box glass-effect">
                  <Calendar :size="18" class="input-icon" />
                  <input v-model.number="form.periodDays" type="number" min="1" />
                </div>
              </div>
              <div class="input-group-half">
                <label>최대 참여 인원 <span class="required">*</span></label>
                <div class="modern-input-box glass-effect">
                  <Users :size="18" class="input-icon" />
                  <input v-model.number="form.maxParticipants" type="number" min="1" />
                </div>
              </div>
            </div>

            <div class="input-row mt-10">
              <div class="input-group-half">
                <label>난이도 설정 <span class="required">*</span></label>
                <div class="modern-input-box glass-effect">
                  <Target :size="18" class="input-icon" />
                  <select v-model="form.difficulty">
                    <option value="EASY">EASY</option>
                    <option value="NORMAL">NORMAL</option>
                    <option value="HARD">HARD</option>
                  </select>
                </div>
              </div>
              <div class="input-group-half">
                <label>운영 모드 <span class="required">*</span></label>
                <div class="modern-input-box glass-effect">
                  <Zap :size="18" class="input-icon" />
                  <select v-model="form.modeType">
                    <option value="EAT">EAT</option>
                    <option value="MEDI_EAT">MEDI_EAT</option>
                  </select>
                </div>
              </div>
            </div>
          </section>

          <!-- Footer Actions -->
          <footer class="footer-actions mt-24">
            <div v-if="!canSubmit" class="validation-hint mb-8">
              <Info :size="14" />
              <span>{{ disabledHint }}</span>
            </div>

            <div class="button-group">
              <button type="button" class="btn-cancel" @click="goBack">취소</button>
              <button 
                type="submit" 
                class="main-start-btn" 
                :disabled="!canSubmit || loading"
              >
                <template v-if="loading">
                  <Loader2 class="spinner mr-2" :size="18" />
                  <span>처리 중...</span>
                </template>
                <template v-else>
                  <span class="btn-text-white">{{ isEdit ? '수정 완료' : '챌린지 등록하기' }}</span>
                </template>
              </button>
            </div>
          </footer>
        </form>
      </div>
    </main>
  </div>
</template>

<style scoped>
/* --- 🌟 인트로 배경 및 파노라마 레이아웃 --- */
.intro-container {
  background-color: #f8f9fc;
  color: #1a1a1a;
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
  padding: 120px 24px 100px;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.background-decorations {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%; z-index: 0; pointer-events: none;
}

/* 🌟 블롭 위치 이원화 및 색상 실시간 전환 */
.blob {
  position: fixed; border-radius: 50%; filter: blur(160px); z-index: 0; opacity: 0.25; pointer-events: none;
  transition: all 1s cubic-bezier(0.23, 1, 0.32, 1);
}
.blob-eat { 
  top: -10%; left: -10%; width: 65vw; height: 65vw; 
  background: radial-gradient(circle, rgba(163, 230, 53, 0.8) 0%, transparent 70%); 
} 
.blob-medi { 
  bottom: -15%; right: -10%; width: 65vw; height: 65vw; 
  background: radial-gradient(circle, rgba(56, 189, 248, 0.7) 0%, transparent 70%); 
}

.fade-blob-enter-active, .fade-blob-leave-active { transition: opacity 1s ease; }
.fade-blob-enter-from, .fade-blob-leave-to { opacity: 0; }

/* 🌟 카드 가로 크기 확장 (1000px) */
.content-wrapper { position: relative; z-index: 10; max-width: 1000px; margin: 0 auto; width: 100%; }

/* --- 🌟 디자인 시스템: Glassmorphism --- */
.glass-card {
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 60px;
  padding: 80px 60px;
  box-shadow: 0 40px 120px rgba(0, 0, 0, 0.03);
  width: 100%;
  box-sizing: border-box;
}

.glass-effect {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 24px;
}

/* --- 🌟 헤더 (중앙 정렬) --- */
.page-header { margin-bottom: 60px; text-align: center; }
.badge-centered { display: flex; justify-content: center; }
.badge {
  display: inline-flex; align-items: center; gap: 8px; padding: 10px 24px;
  background: white; border-radius: 999px;
  font-size: 0.75rem; font-weight: 800; color: #7c4dff; border: 1.5px solid #f1f5f9;
}

.hero-title { font-size: 3.2rem; line-height: 0.95; font-weight: 950; letter-spacing: -0.05em; }
.centered { text-align: center; }
.gradient-text {
  background: linear-gradient(to right, #1A1A1A, var(--point-color));
  -webkit-background-clip: text; background-clip: text; color: transparent;
  transition: all 0.5s ease;
}
.hero-subtext { font-size: 1.2rem; opacity: 0.5; font-weight: 600; letter-spacing: -0.01em; }

/* --- 🌟 섹션 스타일 --- */
.section-header { display: flex; align-items: center; gap: 24px; margin-bottom: 20px; }
.section-title { font-size: 1.2rem; letter-spacing: 0.15em; color: #1a1a1a; }
.panel-line { flex: 1; height: 1.5px; background: rgba(0,0,0,0.06); }

/* --- 🌟 입력 요소 (오버플로우 방지) --- */
.input-group label, .input-group-half label {
  display: block; font-size: 0.85rem; font-weight: 900; color: #1a1a1a;
  opacity: 0.5; margin-bottom: 12px; margin-left: 12px;
}
.required { color: var(--point-color); font-weight: 900; }

.input-row { display: flex; gap: 24px; width: 100%; box-sizing: border-box; }
.input-group-half { flex: 1; min-width: 0; } /* 🌟 텍스트 삐져나감 방지 */

.modern-input-box, .modern-textarea-box {
  display: flex; align-items: center; gap: 16px; padding: 22px 28px; border-radius: 24px;
  background: white; border: 1.5px solid #f1f5f9; transition: 0.4s;
  width: 100%; box-sizing: border-box; /* 🌟 박스 사이즈 고정 */
}
.modern-textarea-box { align-items: flex-start; }

.modern-input-box input, .modern-input-box select, .modern-textarea-box textarea {
  flex: 1; border: none; outline: none; background: none; font-family: inherit;
  font-weight: 950; font-size: 1.1rem; color: #1a1a1a; width: 100%; min-width: 0;
}
.modern-textarea-box textarea { resize: none; line-height: 1.6; min-height: 120px; }

.modern-input-box:focus-within, .modern-textarea-box:focus-within {
  border-color: var(--point-color); transform: translateY(-4px);
  box-shadow: 0 15px 40px var(--point-soft);
}

.input-icon, .textarea-icon { color: #cbd5e1; transition: 0.3s; flex-shrink: 0; }
.modern-input-box:focus-within .input-icon { color: var(--point-color); }

/* --- 🌟 액션 버튼 --- */
.footer-actions { display: flex; flex-direction: column; align-items: center; }

.button-group { display: flex; gap: 16px; width: 100%; }

.btn-cancel {
  flex: 1; padding: 24px; border-radius: 999px; border: 1.5px solid #1a1a1a;
  background: transparent; font-weight: 900; font-size: 1.1rem; cursor: pointer; transition: 0.3s;
}
.btn-cancel:hover { background: #f1f5f9; }

.main-start-btn {
  flex: 2; display: flex; align-items: center; justify-content: center;
  padding: 24px; background: #1a1a1a; color: white; border: none; border-radius: 999px;
  cursor: pointer; transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}
.main-start-btn:hover:not(:disabled) { 
  background: var(--point-color); transform: translateY(-5px); 
  box-shadow: 0 30px 60px var(--point-soft); 
}
.main-start-btn:hover:not(:disabled) .btn-text-white,
.main-start-btn:hover:not(:disabled) .text-white {
  color: #1a1a1a !important;
}

.btn-text-white { color: #ffffff !important; font-size: 1.2rem; font-weight: 950; transition: 0.3s; }
.text-white { color: #ffffff !important; transition: 0.3s; }

.main-start-btn:disabled { opacity: 0.2; cursor: not-allowed; }

.validation-hint {
  display: flex; align-items: center; gap: 8px; font-size: 0.9rem;
  font-weight: 800; color: var(--point-color); opacity: 0.8;
}

.error-banner {
  background: #fff1f2; color: #e11d48; padding: 20px; border-radius: 20px;
  margin-bottom: 32px; font-weight: 800; text-align: center; border: 1px solid rgba(225, 29, 72, 0.1);
}

.loading-state { text-align: center; padding: 100px 0; }
.spinner { animation: spin 1.5s linear infinite; color: var(--point-color); margin: 0 auto; }
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }

/* Utilities */
.font-black { font-weight: 950; }
.italic { font-style: italic; }
.tracking-tighter { letter-spacing: -0.06em; }
.mt-6 { margin-top: 1.5rem; }
.mt-10 { margin-top: 2.5rem; }
.mt-24 { margin-top: 8rem; }
.mb-8 { margin-bottom: 2rem; }
.ml-2 { margin-left: 0.5rem; }

@media (max-width: 1100px) {
  .hero-title { font-size: 2.5rem; }
  .glass-card { padding: 40px 24px; border-radius: 40px; }
  .input-row { flex-direction: column; gap: 10px; }
  .button-group { flex-direction: column; }
}
</style>