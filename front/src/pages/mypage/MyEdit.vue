<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import userApi from "@/api/user";
import BaseSelect from "@/components/common/BaseSelect.vue";
import { Sparkles, ArrowRight, User, Phone, MapPin, Target, LogOut, X, ChevronRight, AlertTriangle } from "lucide-vue-next";

const router = useRouter();
const route = useRoute();

/* =========================
   상태 관리
========================= */
const currentMode = computed(() => route.query.mode || "EAT");

const user = ref({
  userId: 0,
  name: "",
  nickname: "",
  email: "",
  phone: "",
  gender: "",
  age: 0,
  height: 0,
  weight: 0,
  goalType: "",
  pregnantStatus: "",
  pregnancyWeek: 0,
});

/* =========================
   성별 및 목표 정규화 로직
========================= */
const normalizeGender = (g) => {
  if (!g) return "";
  const raw = String(g).trim().toUpperCase();
  if (["M", "MALE", "남", "남자", "남성"].includes(raw)) return "남성";
  if (["F", "FEMALE", "여", "여자", "여성"].includes(raw)) return "여성";
  return "";
};

const denormalizeGenderForSave = (g) => {
  if (g === "남성") return "M";
  if (g === "여성") return "F";
  return null;
};

const GOAL_OPTIONS = [
  { value: "CUT", label: "체중 감량" },
  { value: "BULK", label: "근육 증량" },
  { value: "HEALTH", label: "건강 관리" },
  { value: "HABIT", label: "식습관 개선" },
  { value: "FIT", label: "체력 증진" },
  { value: "DISEASE", label: "질환 관리" },
  { value: "CUSTOM", label: "기타(직접 입력)" },
];

const goalSelect = ref("");
const goalCustom = ref("");
const isCustomGoal = computed(() => goalSelect.value === "CUSTOM");

const normalizeGoal = (goalType) => {
  const g = (goalType || "").trim();
  const matched = GOAL_OPTIONS.find((o) => o.label === g || o.value === g);
  if (matched) {
    goalSelect.value = matched.value;
  }
};

/* =========================
   데이터 통신
========================= */
const loadUserInfo = async () => {
  try {
    const res = await userApi.getMyInfo();
    const data = res.data || {};
    user.value = { ...user.value, ...data, gender: normalizeGender(data.gender) };
    normalizeGoal(data.goalType);
  } catch (err) {
    console.error("정보 로드 실패", err);
  }
};

const handleSubmit = async () => {
  try {
    const goalTypeFinal = isCustomGoal.value ? goalCustom.value.trim() : GOAL_OPTIONS.find(o => o.value === goalSelect.value)?.label;
    if (!goalTypeFinal) return; 

    const payload = {
      ...user.value,
      goalType: goalTypeFinal,
      gender: denormalizeGenderForSave(user.value.gender),
    };

    await userApi.updateInfo(payload);
    router.push(`/mypage?mode=${currentMode.value}`);
  } catch (err) {
    console.error("수정 실패");
  }
};

const goBack = () => router.push(`/mypage?mode=${currentMode.value}`);

/* =========================
   회원 탈퇴 모달
========================= */
const withdrawOpen = ref(false);
const withdrawAgree = ref(false);
const withdrawLoading = ref(false);

const openWithdraw = () => { withdrawOpen.value = true; withdrawAgree.value = false; };
const closeWithdraw = () => { if (!withdrawLoading.value) withdrawOpen.value = false; };

const confirmWithdraw = async () => {
  if (!withdrawAgree.value) return;
  try {
    withdrawLoading.value = true;
    await userApi.deleteUser();
    router.push("/login");
  } catch (e) {
    console.error("탈퇴 실패");
  } finally {
    withdrawLoading.value = false;
  }
};

onMounted(loadUserInfo);
</script>

<template>
  <div class="intro-container">
    <!-- 배경 장식 (EAT & MEDI 하이브리드) -->
    <div class="blob blob-eat"></div>
    <div class="blob blob-medi"></div>

    <main class="content-wrapper">
      <div class="edit-card glass-card">
        <!-- Header 섹션 -->
        <header class="page-header">
          <div class="badge">
            <Sparkles :size="12" class="icon-purple" />
            <span>MY PROFILE SETTINGS</span>
          </div>
          <h2 class="hero-title italic">
            YOUR BODY,<br />
            <span class="gradient-text">DEFINED.</span>
          </h2>
          <p class="hero-subtext">더 정확한 맞춤 건강 분석을 위해 정보를 업데이트하세요.</p>
        </header>

        <form @submit.prevent="handleSubmit" class="form-container">
          <!-- Account Info Section -->
          <div class="form-section">
            <h3 class="section-title italic">ACCOUNT INFO</h3>
            <div class="form-grid">
              <div class="input-group full">
                <label>이름</label>
                <div class="input-wrapper">
                  <User :size="16" class="input-icon" />
                  <input type="text" v-model="user.name" placeholder="이름을 입력하세요" />
                </div>
              </div>
              <div class="input-group">
                <label>닉네임</label>
                <input type="text" v-model="user.nickname" class="modern-input" placeholder="닉네임" />
              </div>
              <div class="input-group">
                <label>연락처</label>
                <div class="input-wrapper">
                  <Phone :size="16" class="input-icon" />
                  <input type="tel" v-model="user.phone" placeholder="010-0000-0000" />
                </div>
              </div>
            </div>
          </div>

          <!-- Physical Data Section -->
          <div class="form-section mt-10">
            <h3 class="section-title italic">PHYSICAL DATA</h3>
            <div class="form-grid">
              <div class="input-group">
                <label>성별</label>
                <div class="toggle-group">
                  <button type="button" :class="{active: user.gender === '남성'}" @click="user.gender = '남성'">남성</button>
                  <button type="button" :class="{active: user.gender === '여성'}" @click="user.gender = '여성'">여성</button>
                </div>
              </div>
              <div class="input-group">
                <label>나이</label>
                <div class="unit-input">
                  <input type="number" v-model="user.age" />
                  <span class="unit">세</span>
                </div>
              </div>
              <div class="input-group">
                <label>신장</label>
                <div class="unit-input">
                  <input type="number" v-model="user.height" step="0.1" />
                  <span class="unit">cm</span>
                </div>
              </div>
              <div class="input-group">
                <label>체중</label>
                <div class="unit-input">
                  <input type="number" v-model="user.weight" step="0.1" />
                  <span class="unit">kg</span>
                </div>
              </div>
              <div class="input-group full">
                <label>건강 목표</label>
                <BaseSelect
                  v-model="goalSelect"
                  :options="GOAL_OPTIONS"
                  class="modern-select"
                />
                <Transition name="fade">
                  <input
                    v-if="isCustomGoal"
                    type="text"
                    v-model="goalCustom"
                    class="modern-input mt-3"
                    placeholder="목표를 직접 입력하세요"
                  />
                </Transition>
              </div>
            </div>
          </div>

          <!-- Women Care Plus Section -->
          <Transition name="slide">
            <div class="form-section care-plus-section mt-10" v-if="user.gender === '여성'">
              <h3 class="section-title italic pink">CARE PLUS+</h3>
              <div class="form-grid">
                <div class="input-group">
                  <label>임신 여부</label>
                  <div class="toggle-group pink-accent">
                    <button type="button" :class="{active: user.pregnantStatus === 'yes'}" @click="user.pregnantStatus = 'yes'">예</button>
                    <button type="button" :class="{active: user.pregnantStatus === 'no'}" @click="user.pregnantStatus = 'no'">아니오</button>
                  </div>
                </div>
                <div class="input-group" v-if="user.pregnantStatus === 'yes'">
                  <label>임신 주차</label>
                  <div class="unit-input">
                    <input type="number" v-model="user.pregnancyWeek" />
                    <span class="unit">주</span>
                  </div>
                </div>
              </div>
            </div>
          </Transition>

          <!-- Footer Actions -->
          <div class="footer-actions">
            <button type="button" class="btn-outline" @click="goBack">CANCEL</button>
            <button type="submit" class="main-start-btn">
              SAVE CHANGES
              <ArrowRight :size="18" />
            </button>
          </div>
        </form>

        <!-- 회원 탈퇴 영역 오른쪽 정렬 -->
        <div class="withdraw-container">
          <button type="button" class="withdraw-btn" @click="openWithdraw">
            회원 탈퇴를 원하시나요?
          </button>
        </div>
      </div>
    </main>

    <!-- Withdrawal Modal (Intro 모달 스타일 적용) -->
    <Transition name="fade">
      <div v-if="withdrawOpen" class="modal-overlay" @click.self="closeWithdraw">
        <div class="auth-modal-card">
          <button class="close-btn" @click="closeWithdraw">
            <X :size="20" />
          </button>
          
          <div class="modal-header">
            <div class="mini-logo italic">MEDEAT</div>
            <h3 class="modal-title italic">EXIT SERVICE</h3>
            <p class="modal-subtitle">탈퇴 시 모든 데이터가 영구 삭제됩니다.<br/>정말 떠나시겠어요?</p>
          </div>

          <!-- 커스텀 체크박스 스타일 적용 -->
          <div class="withdraw-agree-box">
             <div class="checkbox-wrapper">
                <input type="checkbox" id="withdrawAgree" v-model="withdrawAgree" />
                <label for="withdrawAgree" class="checkbox-label">
                  <div class="custom-check">
                    <X v-if="withdrawAgree" :size="12" class="check-icon" />
                  </div>
                  <span class="agree-text">모든 내용을 확인했으며 동의합니다.</span>
                </label>
             </div>
          </div>

          <div class="auth-btn-group">
            <button 
              class="auth-btn danger-confirm-btn" 
              :disabled="!withdrawAgree || withdrawLoading" 
              @click="confirmWithdraw"
            >
              {{ withdrawLoading ? '처리 중...' : '탈퇴 확정' }}
            </button>
            <button class="auth-btn back-btn" @click="closeWithdraw">돌아가기</button>
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
  padding: 120px 24px 60px;
  display: flex;
  justify-content: center;
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
  top: -10%; 
  left: -10%; 
  width: 55vw; 
  height: 55vw; 
  background: radial-gradient(circle, rgba(163, 230, 53, 0.8) 0%, transparent 70%); 
} 
.blob-medi { 
  bottom: -15%; 
  right: -10%; 
  width: 50vw; 
  height: 50vw; 
  background: radial-gradient(circle, rgba(56, 189, 248, 0.7) 0%, transparent 70%); 
}

.content-wrapper {
  width: 100%;
  max-width: 720px;
  position: relative;
  z-index: 10;
}

.glass-card {
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 50px;
  padding: 60px 48px;
  box-shadow: 0 40px 100px rgba(0, 0, 0, 0.02);
}

/* Header 스타일 */
.page-header { text-align: center; margin-bottom: 40px; }
.badge {
  display: inline-flex; align-items: center; gap: 6px; padding: 6px 12px;
  background: rgba(255, 255, 255, 0.8); border-radius: 999px; margin-bottom: 16px;
  font-size: 0.65rem; font-weight: 800; color: #7c4dff; border: 1px solid white;
}
.hero-title { font-size: 2.8rem; line-height: 0.95; font-weight: 950; letter-spacing: -0.05em; margin-bottom: 20px; }
.gradient-text {
  background: linear-gradient(to bottom right, #1a1a1a, #7c4dff, #38bdf8);
  -webkit-background-clip: text; background-clip: text; color: transparent;
}
.hero-subtext { font-size: 1rem; opacity: 0.6; font-weight: 500; }

/* Form 스타일 */
.section-title {
  font-size: 1.1rem; font-weight: 900; letter-spacing: 0.1em;
  color: #94a3b8; margin-bottom: 20px; display: flex; align-items: center; gap: 12px;
}
.section-title::after { content: ''; flex: 1; height: 1px; background: rgba(0,0,0,0.05); }
.section-title.pink { color: #f43f5e; }

.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.input-group.full { grid-column: span 2; }
.input-group label {
  display: block; font-size: 0.7rem; font-weight: 800; color: #1a1a1a;
  opacity: 0.4; margin-bottom: 8px; margin-left: 8px;
}

/* Modern Input 스타일 */
.input-wrapper, .modern-input, .unit-input, .modern-select {
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  padding: 14px 20px;
  font-weight: 700;
  font-size: 0.95rem;
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
  display: flex; align-items: center; gap: 10px;
}
.input-wrapper input, .unit-input input, .modern-input {
  background: none; border: none; outline: none; width: 100%; font-weight: 700;
}
.input-wrapper:focus-within, .modern-input:focus, .unit-input:focus-within {
  background: white; border-color: #7c4dff; transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(124, 77, 255, 0.1);
}

.unit { font-size: 0.8rem; font-weight: 800; color: #94a3b8; }

/* Toggle Group */
.toggle-group {
  display: flex; background: #f1f5f9; padding: 5px; border-radius: 14px; gap: 5px;
}
.toggle-group button {
  flex: 1; padding: 10px; border-radius: 10px; border: none; font-size: 0.85rem;
  font-weight: 800; color: #94a3b8; cursor: pointer; transition: 0.3s; background: transparent;
}
.toggle-group button.active {
  background: white; color: #1a1a1a; box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

/* Actions */
.footer-actions { display: flex; gap: 16px; margin-top: 48px; }
.btn-outline {
  flex: 1; padding: 16px; border-radius: 999px; border: 1.5px solid #1a1a1a;
  background: transparent; font-weight: 800; font-size: 0.95rem; cursor: pointer; transition: 0.3s;
}
.main-start-btn {
  flex: 2; display: flex; align-items: center; justify-content: center; gap: 12px;
  padding: 16px; background: #1a1a1a; color: white; border: none; border-radius: 999px;
  font-size: 0.95rem; font-weight: 800; cursor: pointer; transition: all 0.4s;
}

/* 회원 탈퇴 컨테이너: 오른쪽 정렬 */
.withdraw-container { text-align: right; margin-top: 32px; }
.withdraw-btn {
  background: none; border: none; color: #94a3b8; font-size: 0.8rem; font-weight: 700;
  text-decoration: underline; text-underline-offset: 4px; cursor: pointer; opacity: 0.6; transition: 0.3s;
}
.withdraw-btn:hover { color: #f43f5e; opacity: 1; }

/* 모달 애니메이션 및 오버레이 */
.modal-overlay {
  position: fixed; top: 0; left: 0; width: 100vw; height: 100vh;
  background: rgba(0, 0, 0, 0.5); backdrop-filter: blur(10px);
  z-index: 2000; display: flex; align-items: center; justify-content: center; padding: 24px;
}

/* 인트로 스타일 모달 카드 */
.auth-modal-card {
  background: #ffffff; width: 100%; max-width: 440px; padding: 60px 40px 50px;
  border-radius: 40px; box-shadow: 0 40px 80px rgba(0, 0, 0, 0.2); position: relative; text-align: center;
  animation: modalPop 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}

@keyframes modalPop {
  from { opacity: 0; transform: scale(0.95) translateY(10px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}

.close-btn {
  position: absolute; top: 24px; right: 24px; background: none; border: none;
  cursor: pointer; opacity: 0.4; transition: 0.3s; color: #1a1a1a;
}
.close-btn:hover { opacity: 1; transform: rotate(90deg); }

.mini-logo { font-weight: 900; color: #7c4dff; font-size: 0.8rem; letter-spacing: 2px; margin-bottom: 20px; }
.modal-title { font-size: 2.5rem; font-weight: 900; margin-bottom: 12px; letter-spacing: -1.5px; line-height: 1.1; color: #000; }
.modal-subtitle { font-size: 1rem; color: #64748b; font-weight: 600; margin-bottom: 32px; line-height: 1.5; }

/* 커스텀 체크박스 섹션 */
.withdraw-agree-box { 
  margin-bottom: 32px; padding: 24px; background: #fef2f2; border-radius: 24px;
  border: 1px solid rgba(239, 68, 68, 0.1);
}
.checkbox-wrapper { display: flex; align-items: center; justify-content: center; }
.checkbox-wrapper input { display: none; }
.checkbox-label { display: flex; align-items: center; gap: 12px; cursor: pointer; }
.custom-check {
  width: 22px; height: 22px; border: 2px solid #ef4444; border-radius: 6px;
  display: flex; align-items: center; justify-content: center; transition: 0.2s;
  background: white;
}
input:checked + .checkbox-label .custom-check { background: #ef4444; }
.check-icon { color: white; }
.agree-text { font-size: 0.95rem; font-weight: 800; color: #1a1a1a; }

/* 모달 버튼 그룹 */
.auth-btn-group { display: flex; flex-direction: column; gap: 12px; }
.auth-btn { padding: 18px; border-radius: 16px; font-weight: 800; cursor: pointer; transition: 0.3s; border: none; font-size: 1rem; }

.danger-confirm-btn { background: #1a1a1a; color: white; }
.danger-confirm-btn:not(:disabled):hover { background: #ef4444; transform: translateY(-2px); box-shadow: 0 10px 20px rgba(239, 68, 68, 0.2); }
.danger-confirm-btn:disabled { opacity: 0.2; cursor: not-allowed; }

.back-btn { background: #f1f5f9; color: #64748b; }
.back-btn:hover { background: #e2e8f0; color: #1a1a1a; }

.mt-10 { margin-top: 2.5rem; }
.italic { font-style: italic; }

@media (max-width: 768px) {
  .hero-title { font-size: 2.2rem; }
  .glass-card { padding: 40px 20px; border-radius: 30px; }
  .form-grid { grid-template-columns: 1fr; }
  .auth-modal-card { padding: 50px 24px 40px; }
}
</style>