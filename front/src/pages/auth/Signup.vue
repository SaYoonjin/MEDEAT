<template>
  <div class="page-wrapper">
    <!-- 🌟 배경: 마이페이지 스타일 (연두 + 하늘 공존) -->
    <div class="background-decorations">
      <div class="blob blob-eat"></div>
      <div class="blob blob-medi"></div>
    </div>

    <!-- 전체 화면을 채우는 글래스 레이어 -->
    <main class="main-viewport">
      <div class="content-wrapper">
        <!-- 상단 헤더 영역 -->
        <header class="header-section">
          <div class="brand-tag font-black italic">MEDEAT</div>
          <h1 class="main-title font-black italic tracking-tighter">SIGN UP</h1>
          <p class="description">필수 정보를 입력하고 MEDEAT와 함께 건강한 리듬을 시작해 보세요.</p>
        </header>

        <!-- 회원가입 폼 섹션 -->
        <section class="form-section">
          <form @submit.prevent="submitForm" class="input-stack">
            
            <!-- 아이디 (필수) -->
            <div class="input-group">
              <label>ID <span class="required">*</span></label>
              <input 
                type="text" 
                v-model="form.loginId" 
                placeholder="로그인 아이디를 입력하세요" 
                class="premium-input"
              />
              <Transition name="slide-fade">
                <p class="status-msg error" v-if="errors.loginId">{{ errors.loginId }}</p>
              </Transition>
            </div>

            <!-- 비밀번호 (필수) -->
            <div class="input-group">
              <label>PASSWORD <span class="required">*</span></label>
              <input 
                type="password" 
                v-model="form.password" 
                placeholder="영문/숫자 포함 8자 이상" 
                class="premium-input"
              />
              <Transition name="slide-fade">
                <p class="status-msg error" v-if="errors.password">{{ errors.password }}</p>
              </Transition>
            </div>

            <!-- 이름 (필수) -->
            <div class="input-group">
              <label>NAME <span class="required">*</span></label>
              <input 
                type="text" 
                v-model="form.name" 
                placeholder="성함을 입력하세요" 
                class="premium-input"
              />
              <Transition name="slide-fade">
                <p class="status-msg error" v-if="errors.name">{{ errors.name }}</p>
              </Transition>
            </div>

            <!-- 닉네임 (선택) -->
            <div class="input-group">
              <label>NICKNAME <span class="optional">(선택)</span></label>
              <input 
                type="text" 
                v-model="form.nickname" 
                placeholder="서비스에서 사용할 닉네임" 
                class="premium-input"
              />
            </div>

            <!-- 이메일 (필수) -->
            <div class="input-group">
              <label>EMAIL <span class="required">*</span></label>
              <input 
                type="email" 
                v-model="form.email" 
                placeholder="example@medeat.com" 
                class="premium-input"
              />
              <Transition name="slide-fade">
                <p class="status-msg error" v-if="errors.email">{{ errors.email }}</p>
              </Transition>
            </div>

            <!-- 전화번호 (필수) -->
            <div class="input-group">
              <label>PHONE <span class="required">*</span></label>
              <input 
                type="text" 
                v-model="form.phone" 
                placeholder="'-' 포함 또는 숫자만 입력" 
                class="premium-input"
              />
              <Transition name="slide-fade">
                <p class="status-msg error" v-if="errors.phone">{{ errors.phone }}</p>
              </Transition>
            </div>

            <!-- 성별 & 나이 -->
            <div class="inline-group">
              <div class="input-group flex-1">
                <label>GENDER <span class="required">*</span></label>
                <div class="select-wrapper">
                  <select v-model="form.gender" class="premium-input">
                    <option value="" disabled selected>선택</option>
                    <option value="M">남성</option>
                    <option value="F">여성</option>
                  </select>
                </div>
                <p class="status-msg error" v-if="errors.gender">{{ errors.gender }}</p>
              </div>

              <div class="input-group flex-1">
                <label>AGE <span class="required">*</span></label>
                <input 
                  type="number" 
                  v-model.number="form.age" 
                  min="1" max="120" 
                  placeholder="만 나이" 
                  class="premium-input"
                />
                <p class="status-msg error" v-if="errors.age">{{ errors.age }}</p>
              </div>
            </div>

            <!-- 키 / 몸무게 -->
            <div class="inline-group">
              <div class="input-group flex-1">
                <label>HEIGHT(cm) <span class="required">*</span></label>
                <input 
                  type="number" 
                  step="0.1" 
                  v-model.number="form.height" 
                  placeholder="160.5" 
                  class="premium-input"
                />
                <p class="status-msg error" v-if="errors.height">{{ errors.height }}</p>
              </div>

              <div class="input-group flex-1">
                <label>WEIGHT(kg) <span class="required">*</span></label>
                <input 
                  type="number" 
                  step="0.1" 
                  v-model.number="form.weight" 
                  placeholder="52.3" 
                  class="premium-input"
                />
                <p class="status-msg error" v-if="errors.weight">{{ errors.weight }}</p>
              </div>
            </div>

            <!-- 목표 유형 -->
            <div class="input-group">
              <BaseSelect
                v-model="goalSelect"
                :options="GOAL_OPTIONS"
                label="GOAL TYPE"
                placeholder="목표를 선택해 주세요"
                :required="true"
                class="premium-select-component"
              />
              <Transition name="slide-fade">
                <p class="status-msg error" v-if="errors.goalType">{{ errors.goalType }}</p>
              </Transition>

              <Transition name="slide-fade">
                <input
                  v-if="isCustomGoal"
                  type="text"
                  v-model="goalCustom"
                  placeholder="목표를 직접 입력하세요"
                  class="premium-input custom-goal-input"
                />
              </Transition>
              <p class="status-msg error" v-if="errors.goalCustom">{{ errors.goalCustom }}</p>
            </div>

            <!-- 버튼 영역 -->
            <div class="action-trigger mt-10">
              <button
                type="submit"
                class="cta-submit font-black"
                :disabled="!canSubmit || isSubmitting"
                :class="{ disabled: !canSubmit || isSubmitting }"
              >
                {{ isSubmitting ? '처리 중...' : '회원가입 완료' }}
              </button>

              <Transition name="slide-fade">
                <p v-if="!canSubmit && disabledHint && !isSubmitting" class="status-msg info centered">
                  {{ disabledHint }}
                </p>
              </Transition>
            </div>
          </form>
        </section>

        <!-- 하단 네비게이션 -->
        <footer class="footer-section">
          <router-link to="/login" class="nav-link font-black">이미 계정이 있으신가요? 로그인</router-link>
        </footer>
      </div>
    </main>
  </div>
</template>

<script setup>
import { reactive, ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import authApi from '@/api/auth'
import BaseSelect from '@/components/common/BaseSelect.vue'

const router = useRouter()

const form = reactive({
  loginId: '',
  password: '',
  name: '',
  nickname: '',
  email: '',
  phone: '',
  gender: '',
  age: null,
  height: null,
  weight: null,
})

const errors = reactive({})
const isSubmitting = ref(false)

const trim = (val) => (typeof val === 'string' ? val.trim() : val)

const GOAL_OPTIONS = [
  { value: 'CUT', label: '체중 감량' },
  { value: 'BULK', label: '근육 증량' },
  { value: 'HEALTH', label: '건강 관리' },
  { value: 'HABIT', label: '식습관 개선' },
  { value: 'FIT', label: '체력 증진' },
  { value: 'DISEASE', label: '질환 관리' },
  { value: 'CUSTOM', label: '기타(직접 입력)' },
]

const goalSelect = ref('') 
const goalCustom = ref('')
const isCustomGoal = computed(() => goalSelect.value === 'CUSTOM')

watch(goalSelect, (v) => {
  if (v !== 'CUSTOM') goalCustom.value = ''
})

const getGoalTypeForSave = () => {
  if (!goalSelect.value) return ''
  if (goalSelect.value === 'CUSTOM') return goalCustom.value.trim()
  const opt = GOAL_OPTIONS.find(o => o.value === goalSelect.value)
  return opt ? opt.label : ''
}

const canSubmit = computed(() => {
  if (!trim(form.loginId)) return false
  if (!trim(form.password) || form.password.length < 8) return false
  if (!trim(form.name)) return false
  if (!trim(form.email) || !form.email.includes('@')) return false
  if (!trim(form.phone)) return false
  if (!trim(form.gender)) return false
  if (!form.age || Number.isNaN(form.age)) return false
  if (!form.height || Number.isNaN(form.height)) return false
  if (!form.weight || Number.isNaN(form.weight)) return false

  const goal = getGoalTypeForSave()
  if (!goal) return false
  if (isCustomGoal.value && !goalCustom.value.trim()) return false

  return true
})

const disabledHint = computed(() => {
  const missing = []

  if (!trim(form.loginId)) missing.push('아이디')
  if (!trim(form.password)) missing.push('비밀번호')
  else if (form.password.length < 8) return '비밀번호는 8자 이상 입력해 주세요.'
  if (!trim(form.name)) missing.push('이름')

  if (!trim(form.email)) missing.push('이메일')
  else if (!form.email.includes('@')) return '이메일 형식을 확인해 주세요.'

  if (!trim(form.phone)) missing.push('전화번호')
  if (!trim(form.gender)) missing.push('성별')
  if (!form.age || Number.isNaN(form.age)) missing.push('나이')
  if (!form.height || Number.isNaN(form.height)) missing.push('키')
  if (!form.weight || Number.isNaN(form.weight)) missing.push('몸무게')

  const goal = getGoalTypeForSave()
  if (!goal) missing.push('목표')

  return missing.length ? `필수 항목: ${missing.join(', ')}` : ''
})

const validateForm = () => {
  Object.keys(errors).forEach((k) => delete errors[k])

  if (!trim(form.loginId)) errors.loginId = '아이디를 입력해 주세요.'
  if (!trim(form.password)) errors.password = '비밀번호를 입력해 주세요.'
  else if (form.password.length < 8) errors.password = '비밀번호는 8자 이상이어야 합니다.'
  if (!trim(form.name)) errors.name = '이름을 입력해 주세요.'

  if (!trim(form.email)) errors.email = '이메일을 입력해 주세요.'
  else {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(form.email)) errors.email = '올바른 이메일 형식이 아닙니다.'
  }

  if (!trim(form.phone)) errors.phone = '전화번호를 입력해 주세요.'
  else {
    const phoneRegex = /^[0-9\-]+$/
    if (!phoneRegex.test(form.phone)) errors.phone = '숫자와 - 만 입력 가능합니다.'
  }

  if (!trim(form.gender)) errors.gender = '성별을 선택해 주세요.'
  if (form.age === null || form.age === '' || Number.isNaN(form.age)) errors.age = '나이를 입력해 주세요.'
  if (form.height === null || form.height === '' || Number.isNaN(form.height)) errors.height = '키를 입력해 주세요.'
  if (form.weight === null || form.weight === '' || Number.isNaN(form.weight)) errors.weight = '몸무게를 입력해 주세요.'

  const goal = getGoalTypeForSave()
  if (!goal) errors.goalType = '목표 유형을 선택해 주세요.'
  if (isCustomGoal.value && !goalCustom.value.trim()) errors.goalCustom = '내용을 작성해 주세요.'

  return Object.keys(errors).length === 0
}

const submitForm = async () => {
  if (!validateForm()) {
    alert('입력하신 정보를 다시 확인해 주세요.')
    return
  }

  try {
    isSubmitting.value = true
    const payload = {
      ...form,
      loginId: form.loginId.trim(),
      password: form.password.trim(),
      name: form.name.trim(),
      nickname: form.nickname?.trim() || null,
      email: form.email.trim(),
      phone: form.phone.trim(),
      goalType: getGoalTypeForSave(),
    }

    const response = await authApi.signup(payload)
    if (response.status === 200) {
      alert('회원가입 완료! 로그인 해주세요.')
      router.push('/login')
    }
  } catch (error) {
    console.error(error)
    const msg = error?.response?.data?.message || '회원가입 중 오류가 발생했습니다.'
    alert(msg)
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
/* 1. Base Layout */
.page-wrapper {
  width: 100vw;
  min-height: 100vh;
  background-color: #f8f9fc;
  position: relative;
  overflow-x: hidden;
  color: #1a1a1a;
}

/* 2. Dynamic Background - 마이페이지 스타일 동기화 */
.background-decorations {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%; z-index: 0; pointer-events: none;
}

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

/* 3. Main Viewport */
.main-viewport {
  position: relative;
  z-index: 10;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  display: flex;
  justify-content: center;
  overflow-y: auto;
  padding: 80px 40px;
}

.main-viewport::-webkit-scrollbar { width: 6px; }
.main-viewport::-webkit-scrollbar-thumb { background: rgba(0,0,0,0.05); border-radius: 10px; }

.content-wrapper {
  width: 100%;
  max-width: 540px;
  display: flex;
  flex-direction: column;
}

/* 4. Header Section */
.header-section {
  text-align: center;
  margin-bottom: 50px;
}

.brand-tag {
  color: #7c4dff;
  font-weight: 950;
  font-size: 0.85rem;
  letter-spacing: 4px;
  margin-bottom: 20px;
  display: block;
}

.main-title {
  font-size: clamp(2.5rem, 6vw, 3.5rem);
  letter-spacing: -3px;
  line-height: 0.9;
  margin-bottom: 16px;
  color: #1a1a1a;
}

.description {
  font-size: 1rem;
  color: #4b5563;
  font-weight: 600;
  line-height: 1.5;
  opacity: 0.6;
}

/* 5. Form & Inputs */
.form-section {
  width: 100%;
}

.input-stack {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.input-group {
  display: flex;
  flex-direction: column;
}

.inline-group {
  display: flex;
  gap: 16px;
}

.flex-1 { flex: 1; }

label {
  font-size: 0.7rem;
  font-weight: 900;
  letter-spacing: 0.15em;
  color: #1a1a1a;
  opacity: 0.4;
  margin-bottom: 10px;
  padding-left: 8px;
  text-transform: uppercase;
}

.required { color: #ef4444; margin-left: 2px; }
.optional { color: #9ca3af; font-size: 0.6rem; margin-left: 4px; }

.premium-input,
:deep(.premium-select-component) select,
:deep(.premium-select-component) input {
  width: 100% !important;
  height: 60px !important;
  padding: 0 24px !important;
  background: rgba(255, 255, 255, 0.5) !important;
  border: 1.5px solid rgba(255, 255, 255, 0.7) !important;
  border-radius: 20px !important;
  font-size: 1rem !important;
  font-weight: 800 !important;
  color: #1a1a1a !important;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1) !important;
  box-sizing: border-box !important;
  appearance: none !important;
}

.premium-input:focus,
:deep(.premium-select-component) select:focus,
:deep(.premium-select-component) input:focus {
  outline: none !important;
  background: #ffffff !important;
  border-color: #7c4dff !important;
  box-shadow: 0 10px 30px rgba(124, 77, 255, 0.1) !important;
  transform: translateY(-2px) !important;
}

select.premium-input,
:deep(.premium-select-component) select {
  cursor: pointer;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%239ca3af'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M19 9l-7 7-7-7'%3E%3C/path%3E%3C/svg%3E") !important;
  background-repeat: no-repeat !important;
  background-position: right 24px center !important;
  background-size: 16px !important;
  padding-right: 50px !important;
}

:deep(.premium-select-component) label {
  font-size: 0.7rem !important;
  font-weight: 900 !important;
  letter-spacing: 0.15em !important;
  color: #1a1a1a !important;
  opacity: 0.4 !important;
  margin-bottom: 10px !important;
  text-transform: uppercase !important;
  display: block !important;
  padding-left: 8px !important;
}

input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.custom-goal-input { 
  margin-top: 12px; 
}

/* 6. Action Area */
.cta-submit {
  width: 100%;
  padding: 22px;
  background: #111827;
  color: #ffffff;
  border: none;
  border-radius: 22px;
  font-size: 1.1rem;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.cta-submit:hover:not(.disabled) {
  background: #7c4dff;
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(124, 77, 255, 0.2);
}

.cta-submit.disabled {
  opacity: 0.4;
  cursor: not-allowed;
  background: #9ca3af;
  box-shadow: none;
}

/* 7. Status & Messages */
.status-msg {
  font-size: 0.8rem;
  font-weight: 800;
  margin-top: 8px;
}
.status-msg.info { color: #94a3b8; line-height: 1.5; }
.status-msg.error { color: #f43f5e; padding-left: 8px; }
.status-msg.centered { text-align: center; }

/* 8. Footer Section */
.footer-section {
  margin-top: 60px;
  margin-bottom: 40px;
  text-align: center;
}

.nav-link {
  font-size: 0.9rem;
  color: #94a3b8;
  text-decoration: none;
  transition: all 0.3s;
}

.nav-link:hover {
  color: #7c4dff;
}

/* 9. Utilities & Transitions */
.font-black { font-weight: 950; }
.italic { font-style: italic; }
.tracking-tighter { letter-spacing: -0.06em; }
.text-primary { color: #7c4dff; }
.mt-10 { margin-top: 2.5rem; }

.gradient-text {
  background: linear-gradient(to right, #1a1a1a, #7c4dff, #38bdf8);
  -webkit-background-clip: text; background-clip: text; color: transparent;
}

.slide-fade-enter-active, .slide-fade-leave-active { transition: all 0.4s ease; }
.slide-fade-enter-from, .slide-fade-leave-to { opacity: 0; transform: translateY(10px); }

@media (max-width: 600px) {
  .main-viewport { padding: 40px 20px; }
  .main-title { font-size: 2.5rem; }
  .inline-group { flex-direction: column; gap: 28px; }
  .cta-submit { padding: 18px; }
}
</style>