<template>
  <div class="dashboard-container">
    <!-- 배경 데코레이션 -->
    <div class="bg-decoration">
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
    </div>

    <div class="dashboard-content">
      <!-- [Header] 타이틀 & XP/날짜 요약 -->
      <header class="header-section">
        <div class="header-title-group">
          <div class="emoji-icon">🥑</div>
          <div>
            <h1 class="page-title">Today's Health</h1>
            <p class="page-subtitle">건강한 하루를 위한 맛있는 기록</p>
          </div>
        </div>

        <div class="header-stats-group">
          <!-- 날짜 -->
          <div class="date-badge">
            <span class="calendar-icon">📅</span>
            {{ selectedDate }}
          </div>

          <!-- XP / Level 정보 -->
          <div class="xp-stat-card" v-if="progress">
            <div class="xp-info">
              <span class="level-badge">Lv.{{ progress.level }}</span>
              <span class="level-name">{{ levelMeta.name }}</span>
            </div>
            <div class="xp-bar-container">
              <div class="xp-bar">
                <div class="xp-fill" :style="{ width: xpPercent + '%' }"></div>
              </div>
              <div class="xp-text">
                <span>{{ levelXpInThisLevel }} / {{ levelCap }} XP</span>
                <span class="next-xp">(다음 레벨까지 {{ remainToNext }})</span>
              </div>
            </div>
            <div class="streak-badge">
              🔥 {{ progress.currentStreak }}일 연속
            </div>
          </div>
        </div>
      </header>

      <!-- [Main Grid] -->
      <div class="main-grid">
        
        <!-- [Left Column] 메인 데이터 (영양, 식단) -->
        <div class="col-left">
          
          <!-- A. 영양 요약 & 상태 카드 -->
          <section class="card nutrition-card animate-up">
            <div class="card-header">
              <h3 class="card-title">Daily Nutrition</h3>
              <button class="icon-btn" @click="goDietAnalysis">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M9 18l6-6-6-6" />
                </svg>
              </button>
            </div>

            <div v-if="hasTodayDiet" class="nutrition-content">
              <!-- 왼쪽: 링 차트 -->
              <div class="chart-section">
                <div class="ring-chart-wrapper">
                  <svg class="ring-svg" width="140" height="140">
                    <circle class="ring-bg" stroke-width="10" r="60" cx="70" cy="70" />
                    <circle 
                      class="ring-progress" 
                      stroke-width="10" 
                      r="60" cx="70" cy="70" 
                      :style="{ strokeDashoffset: calculateStrokeDashoffset(kcalPercent) }"
                    />
                  </svg>
                  <div class="ring-center-text">
                    <span class="percent-val">{{ Math.min(kcalPercent, 999) }}<small>%</small></span>
                    <span class="kcal-val">{{ todaySummary.kcal }} kcal</span>
                  </div>
                </div>
                <div class="status-badge" :class="kcalStatusClass">
                  {{ kcalStatusText }}
                </div>
              </div>

              <!-- 오른쪽: 매크로 바 & 상태 메시지 -->
              <div class="macro-section">
                <!-- 상태 알림 배너 -->
                <div class="status-banner" :class="nutritionStatus.levelClass">
                  <div class="sb-icon">{{ nutritionStatus.levelClass === 'pill-good' ? '🌿' : '💪' }}</div>
                  <div class="sb-text">
                    <div class="sb-title">{{ nutritionStatus.label }}</div>
                    <div class="sb-desc">{{ nutritionStatus.message }}</div>
                  </div>
                </div>

                <div class="macro-bars">
                  <div class="macro-item">
                    <div class="macro-label"><span>탄수화물</span> <span class="val">{{ todaySummary.carbRatio }}%</span></div>
                    <div class="bar-bg"><div class="bar-fill carb" :style="{ width: todaySummary.carbRatio + '%' }"></div></div>
                  </div>
                  <div class="macro-item">
                    <div class="macro-label"><span>단백질</span> <span class="val">{{ todaySummary.proteinRatio }}%</span></div>
                    <div class="bar-bg"><div class="bar-fill protein" :style="{ width: todaySummary.proteinRatio + '%' }"></div></div>
                  </div>
                  <div class="macro-item">
                    <div class="macro-label"><span>지방</span> <span class="val">{{ todaySummary.fatRatio }}%</span></div>
                    <div class="bar-bg"><div class="bar-fill fat" :style="{ width: todaySummary.fatRatio + '%' }"></div></div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 데이터 없음 -->
            <div v-else class="empty-state">
              <div class="empty-icon">🥗</div>
              <p>오늘의 첫 끼니를 기록하고<br/>영양 분석을 시작해보세요.</p>
              <button class="primary-btn" @click="goDiet">기록하기</button>
            </div>
          </section>

          <!-- B. 오늘의 식단 타임라인 -->
          <section class="card meals-card animate-up delay-1">
            <div class="card-header">
              <h3 class="card-title">Timeline</h3>
              <button class="add-btn" @click="goDiet">
                <span>+ 추가</span>
              </button>
            </div>

            <div v-if="hasTodayDiet" class="timeline-list">
              <div class="timeline-item" v-for="(meal, index) in meals" :key="meal.type">
                <div class="time-col">
                  <span class="time-label">{{ meal.label }}</span>
                  <div class="line" v-if="index !== meals.length - 1"></div>
                </div>
                <div class="content-col">
                  <div class="meal-box" :class="{ 'empty': !meal.menu }">
                    <div class="meal-info">
                      <span class="menu-name">{{ meal.menu || '기록되지 않음' }}</span>
                      <span class="kcal-tag" v-if="meal.kcal">{{ meal.kcal }} kcal</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-else class="empty-mini">
              <span>아직 기록된 식단이 없습니다.</span>
            </div>
          </section>

        </div>

        <!-- [Right Column] 인사이트 & 소셜 -->
        <div class="col-right">

          <!-- C. Daily Insight & Condition -->
          <section class="card insight-card animate-up delay-2">
            <div class="card-header border-b">
              <h3 class="card-title">Condition Insight</h3>
            </div>
            
            <div class="insight-content">
              <!-- AI Bubble -->
              <div class="ai-comment-box">
                <div class="ai-avatar">🤖</div>
                <div class="ai-text">"{{ conditionSummary }}"</div>
              </div>

              <!-- Condition Bars -->
              <div class="condition-stats">
                <div class="stat-row">
                  <span class="stat-label">에너지</span>
                  <div class="stat-bar"><div class="stat-fill energy" :style="{ width: conditionScores.energy + '%' }"></div></div>
                </div>
                <div class="stat-row">
                  <span class="stat-label">집중력</span>
                  <div class="stat-bar"><div class="stat-fill focus" :style="{ width: conditionScores.focus + '%' }"></div></div>
                </div>
                <div class="stat-row">
                  <span class="stat-label">붓기</span>
                  <div class="stat-bar"><div class="stat-fill bloat" :style="{ width: conditionScores.bloat + '%' }"></div></div>
                </div>
              </div>

              <div class="divider"></div>

              <!-- Lunch Recommendation -->
              <div class="lunch-reco">
                <div class="reco-header">
                  <span class="reco-title">내일 점심 추천</span>
                  <span class="reco-date">{{ tomorrowKey }} 기준</span>
                </div>
                
                <div class="reco-chips">
                  <button 
                    v-for="opt in lunchOptions" 
                    :key="opt.key"
                    class="chip"
                    :class="{ active: selectedLunchType === opt.key }"
                    @click="selectLunchType(opt.key)"
                  >
                    {{ opt.label }}
                  </button>
                </div>

                <div class="reco-result" v-if="selectedLunch">
                  <div class="res-icon">🍽️</div>
                  <div class="res-info">
                    <div class="res-name">{{ selectedLunch.title }}</div>
                    <div class="res-desc">{{ selectedLunch.desc }}</div>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <!-- D. Challenge -->
          <section class="card challenge-card animate-up delay-3">
            <div class="card-bg-pattern"></div>
            <div class="card-header white">
              <h3 class="card-title text-white">Challenge</h3>
              <button class="text-btn" @click="goChallenge">전체보기</button>
            </div>

            <div v-if="currentChallenge" class="challenge-body">
              <div class="ch-badge">{{ currentChallenge.dDayLabel }}</div>
              <div class="ch-title">{{ currentChallenge.name }}</div>
              <div class="ch-progress">
                <div class="prog-track">
                  <div class="prog-fill" :style="{ width: currentChallenge.progress + '%' }"></div>
                </div>
                <div class="prog-text">
                  <span>달성률</span>
                  <strong>{{ currentChallenge.progress }}%</strong>
                </div>
              </div>
            </div>

            <div v-else class="challenge-empty">
              <div class="ce-text">새로운 도전을 시작해보세요!</div>
              <button class="ce-btn" @click="goChallengeCreate">챌린지 시작하기</button>
            </div>
          </section>

          <!-- E. Community -->
          <section class="card community-card animate-up delay-4">
            <div class="card-header">
              <h3 class="card-title">Community</h3>
              <button class="link-btn" @click="goCommunity">더보기</button>
            </div>

            <div class="post-list" v-if="communityPosts.length">
              <div 
                v-for="post in communityPosts" 
                :key="post.id" 
                class="post-item"
                @click="goCommunityDetail(post.id)"
              >
                <div class="post-avatar">{{ post.nickname.charAt(0) }}</div>
                <div class="post-content">
                  <div class="post-title">{{ post.title }}</div>
                  <div class="post-meta">
                    <span class="author">{{ post.nickname }}</span>
                    <span class="likes">❤️ {{ post.likes }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="empty-mini">
              <span>최근 게시글이 없습니다.</span>
            </div>
          </section>

        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/* [Logic Preserved] 
  기존 로직(API 호출, 상태 관리, 라우터 이동)은 100% 동일하게 유지합니다. 
*/
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import dietApi from '@/api/diet'
import challengeApi from '@/api/challenge'
import communityApi from '@/api/community'
import authApi from '@/api/auth'
import gamificationApi from '@/api/gamification'

const router = useRouter()
const selectedDate = ref(new Date().toISOString().substring(0, 10))

const LEVEL_THRESHOLDS = [0, 150, 400, 800, 1400, 2200, 3200]
const LEVEL_META = {
  1: { name: '시작 접시', emoji: '🌱' },
  2: { name: '균형 한입', emoji: '🌿' },
  3: { name: '데일리 플레이트', emoji: '🍽️' },
  4: { name: '루틴 메이커', emoji: '⚡' },
  5: { name: '영양 밸런서', emoji: '✨' },
  6: { name: '컨디션 코치', emoji: '🧠' },
  7: { name: 'MEDEAT 마스터', emoji: '👑' },
}

const progress = ref(null)
const userIdRef = ref(null)

const loadProgress = async () => {
  try {
    const userRes = await authApi.getUser()
    const user = userRes.data
    const userId = user?.userId

    userIdRef.value = userId ?? null

    if (!userId) {
      progress.value = null
      return
    }

    const res = await gamificationApi.getProgress(userId, selectedDate.value)
    progress.value = res.data
  } catch (e) {
    progress.value = null
    userIdRef.value = null
  }
}

const levelMeta = computed(() => {
  const lv = Number(progress.value?.level || 1)
  return LEVEL_META[lv] || { name: '성장 중', emoji: '🌱' }
})

const levelStartXp = computed(() => {
  const lv = Number(progress.value?.level || 1)
  const idx = Math.max(0, Math.min(lv - 1, LEVEL_THRESHOLDS.length - 1))
  return LEVEL_THRESHOLDS[idx] || 0
})

const levelCap = computed(() => {
  const next = Number(progress.value?.nextLevelXp ?? 0)
  const start = levelStartXp.value
  return Math.max(1, next - start)
})

const levelXpInThisLevel = computed(() => {
  const total = Number(progress.value?.totalXp ?? 0)
  const start = levelStartXp.value
  return Math.max(0, total - start)
})

const xpPercent = computed(() => {
  if (!progress.value) return 0
  const v = (levelXpInThisLevel.value / levelCap.value) * 100
  return Math.max(0, Math.min(100, Math.round(v)))
})

const remainToNext = computed(() => {
  if (!progress.value) return 0
  const next = Number(progress.value.nextLevelXp ?? 0)
  const total = Number(progress.value.totalXp ?? 0)
  return Math.max(0, next - total)
})

const todaySummary = ref({ kcal: 0, goalKcal: 0, carbRatio: 0, proteinRatio: 0, fatRatio: 0 })
const meals = ref([
  { type: 'BREAKFAST', label: '아침', kcal: 0, menu: '' },
  { type: 'LUNCH', label: '점심', kcal: 0, menu: '' },
  { type: 'DINNER', label: '저녁', kcal: 0, menu: '' },
  { type: 'SNACK', label: '간식', kcal: 0, menu: '' }
])

const challenges = ref([])
const communityPosts = ref([])
const selectedLunchType = ref('LIGHT')

const hasTodayDiet = computed(() => todaySummary.value.kcal > 0)

const kcalPercent = computed(() => {
  if (!todaySummary.value.goalKcal) return 0
  const v = (todaySummary.value.kcal / todaySummary.value.goalKcal) * 100
  return Math.max(0, Math.min(120, Math.round(v)))
})

const calculateStrokeDashoffset = (percent) => {
  const circumference = 2 * Math.PI * 60
  const p = Math.min(percent, 100)
  return circumference - (p / 100) * circumference
}

const kcalStatusText = computed(() => {
  if (!todaySummary.value.kcal) return '기록 없음'
  if (kcalPercent.value < 80) return '에너지 부족'
  if (kcalPercent.value <= 110) return '적정 섭취'
  return '에너지 과잉'
})

const kcalStatusClass = computed(() => {
  if (!todaySummary.value.kcal) return 'st-none'
  if (kcalPercent.value < 80) return 'st-low'
  if (kcalPercent.value <= 110) return 'st-good'
  return 'st-high'
})

const clamp = (v, min = 0, max = 100) => Math.max(min, Math.min(max, v))

const seededRand01 = (seedStr) => {
  let h = 2166136261
  for (let i = 0; i < seedStr.length; i++) {
    h ^= seedStr.charCodeAt(i)
    h = Math.imul(h, 16777619)
  }
  return ((h >>> 0) % 10000) / 10000
}

const pickBySeed = (seedStr, arr) => {
  if (!arr || !arr.length) return ''
  const r = seededRand01(seedStr)
  return arr[Math.floor(r * arr.length)]
}

const balanceScore = computed(() => {
  if (!hasTodayDiet.value) return 0
  const c = Number(todaySummary.value.carbRatio || 0)
  const p = Number(todaySummary.value.proteinRatio || 0)
  const f = Number(todaySummary.value.fatRatio || 0)

  const scoreNear = (x, lo, hi) => {
    if (x < lo) return clamp(100 - (lo - x) * 6)
    if (x > hi) return clamp(100 - (x - hi) * 6)
    return 100
  }

  const s = (scoreNear(c, 35, 55) + scoreNear(p, 25, 35) + scoreNear(f, 20, 30)) / 3
  return clamp(Math.round(s))
})

const insightType = computed(() => {
  if (!hasTodayDiet.value) return 'NO_LOG'
  const k = kcalPercent.value
  const c = Number(todaySummary.value.carbRatio || 0)
  const p = Number(todaySummary.value.proteinRatio || 0)
  const bal = balanceScore.value

  if (k < 45) return 'LOW_KCAL'
  if (p < 18) return 'LOW_PROTEIN'
  if (c > 58 && bal < 75) return 'HIGH_CARB'
  if (k > 110) return 'OVER_KCAL'
  return 'GOOD'
})

const conditionScores = computed(() => {
  if (!hasTodayDiet.value) return { energy: 18, focus: 22, bloat: 35 }
  const k = kcalPercent.value
  const bal = balanceScore.value
  const c = Number(todaySummary.value.carbRatio || 0)
  const p = Number(todaySummary.value.proteinRatio || 0)
  const energy = clamp(Math.round((Math.min(k, 120) / 120) * 75 + bal * 0.25))
  const focus = clamp(Math.round(p * 1.9 + bal * 0.30))
  const bloat = clamp(Math.round(c * 1.2 + (100 - bal) * 0.35))
  return { energy, focus, bloat }
})

const INSIGHT_TEMPLATES = {
  NO_LOG: [
    '아직 기록이 없어요. 한 끼만 기록해도 컨디션 예측이 시작돼요!',
    '첫 기록만 남겨도 내일 추천이 더 정확해져요.',
    '가벼운 간식이라도 기록하면 “패턴”이 잡혀요.'
  ],
  LOW_KCAL: [
    '오늘은 섭취가 조금 적어요. 내일은 단백질+탄수 한 끼 보충 추천!',
    '에너지가 떨어질 수 있어요. 간단히 요거트/우유/바나나도 좋아요.',
    '내일은 점심을 “든든하게”로 맞추면 컨디션이 안정될 가능성이 커요.'
  ],
  LOW_PROTEIN: [
    '단백질이 부족해 보여요. 내일은 단백질 1개만 추가해봐요!',
    '집중력이 흔들릴 수 있어요. 다음 끼니는 단백질부터 고르는 걸 추천해요.',
    '포만감이 빨리 꺼질 수 있어요. 계란/두부/닭가슴살 같은 보충 추천!'
  ],
  HIGH_CARB: [
    '탄수 비중이 높아요. 다음 끼니는 단백질을 조금 더 얹어볼까요?',
    '몸이 무거울 수 있어요. 물 + 단백질 중심으로 밸런스 리셋 추천!',
    '밥/면 위주였다면 내일은 단백질+채소를 먼저 채워봐요.'
  ],
  OVER_KCAL: [
    '오늘은 칼로리가 조금 높았어요. 내일은 “가볍게”로 리듬 맞추기 추천!',
    '붓기 점수가 올라갈 수 있어요. 수분 + 가벼운 산책이 도움돼요.',
    '내일은 국물/채소 중심으로 가볍게 시작하면 회복이 빨라요.'
  ],
  GOOD: [
    '오늘 밸런스 좋아요. 이 루틴이면 내일도 컨디션이 안정적일 확률↑',
    '탄·단·지 비율이 깔끔해요. 지금 페이스 유지 추천!',
    '기록이 안정적이라 내일 추천 정확도가 점점 좋아지고 있어요.'
  ],
}

const conditionSummary = computed(() => {
  const uid = userIdRef.value ?? 'guest'
  const seed = `${uid}|${selectedDate.value}|${insightType.value}|${todaySummary.value.kcal}|${todaySummary.value.carbRatio}-${todaySummary.value.proteinRatio}-${todaySummary.value.fatRatio}`
  return pickBySeed(seed, INSIGHT_TEMPLATES[insightType.value]) || '오늘도 기록을 이어가볼까요?'
})

const autoLunchType = computed(() => {
  if (!hasTodayDiet.value) return 'LIGHT'
  if (insightType.value === 'LOW_KCAL') return 'STRONG'
  if (insightType.value === 'OVER_KCAL') return 'LIGHT'
  if (insightType.value === 'HIGH_CARB') return 'SOUP'
  if (insightType.value === 'LOW_PROTEIN') return 'STRONG'
  return 'LIGHT'
})

const toDateOnly = (s) => {
  if (!s) return null
  const d = new Date(String(s).slice(0, 10))
  d.setHours(0, 0, 0, 0)
  return d
}
const dayDiff = (a, b) => Math.floor((a.getTime() - b.getTime()) / 86400000)

const loadSummary = async () => {
  try {
    const [analysisRes, listRes] = await Promise.all([
      dietApi.analyzeIt(selectedDate.value, selectedDate.value),
      dietApi.getList(selectedDate.value),
    ])

    const analysis = analysisRes.data || {}
    const ps = analysis.periodSummary || analysis

    const todayKcal = ps.totalKcal ?? ps.totalCalorie ?? ps.kcal ?? 0
    const goalKcal = ps.goalKcal ?? ps.targetKcal ?? 0

    const list = listRes.data || []
    let tC = 0, tP = 0, tF = 0
    list.forEach(d => { tC += d.totalCarb || 0; tP += d.totalProtein || 0; tF += d.totalFat || 0 })
    const sum = tC + tP + tF

    let cR = 0, pR = 0, fR = 0
    if (sum > 0) {
      cR = Math.round((tC / sum) * 100)
      pR = Math.round((tP / sum) * 100)
      fR = 100 - cR - pR
      if (fR < 0) fR = 0
    }

    todaySummary.value = { kcal: todayKcal, goalKcal, carbRatio: cR, proteinRatio: pR, fatRatio: fR }
  } catch (e) {
    todaySummary.value = { kcal: 0, goalKcal: 2000, carbRatio: 0, proteinRatio: 0, fatRatio: 0 }
  }
}

const buildMealTitle = (d) => {
  if (Array.isArray(d.items) && d.items.length > 0) {
    const names = d.items.map(it => it.foodName || it.food_name || it.name).filter(Boolean)
    if (names.length) return names.join(', ')
  }
  const memo = (d.memo || '').trim()
  if (memo) return memo
  if (d.photoPath) return '사진 기록'
  return '기록 있음'
}

const loadMeals = async () => {
  try {
    const res = await dietApi.getList(selectedDate.value)
    const list = res.data || []

    const map = {
      '아침': { kcal: 0, menu: '' },
      '점심': { kcal: 0, menu: '' },
      '저녁': { kcal: 0, menu: '' },
      '간식': { kcal: 0, menu: '' },
    }

    list.forEach(d => {
      const key = d.mealTime
      if (map[key]) {
        map[key].kcal = d.totalCalorie || 0
        map[key].menu = buildMealTitle(d)
      }
    })

    meals.value = [
      { type: 'BREAKFAST', label: '아침', ...map['아침'] },
      { type: 'LUNCH', label: '점심', ...map['점심'] },
      { type: 'DINNER', label: '저녁', ...map['저녁'] },
      { type: 'SNACK', label: '간식', ...map['간식'] },
    ]
  } catch (e) {}
}

const loadChallenge = async () => {
  try {
    const res = await challengeApi.getList('EAT')
    const data = res.data || {}
    const list = data.ongoing || data.ongoingChallenges || []
    if (!list.length) { challenges.value = []; return }

    const first = list[0]
    const ucId = first.userChallengeId || first.id
    if (!ucId) { challenges.value = []; return }

    const detail = await challengeApi.getDetail(ucId, 'EAT').then(r => r.data).catch(() => null)
    if (!detail) { challenges.value = []; return }

    const ch = detail.challenge || {}
    const uc = detail.userChallenge || {}
    const logs = detail.logs || []

    const total = Number(ch.periodDays || uc.periodDays || 0) || 7
    const success = logs.filter(l => l.status === 'SUCCESS').length
    const progress = total > 0 ? Math.round((success / total) * 100) : 0

    const start = toDateOnly(uc.startDate)
    const today = new Date()
    today.setHours(0, 0, 0, 0)

    let remaining = null
    if (start && total > 0) {
      const passed = dayDiff(today, start)
      remaining = (total - 1) - passed
    }

    let dDayLabel = 'D-0'
    if (remaining == null) dDayLabel = 'D-0'
    else if (remaining <= 0) dDayLabel = 'D-0'
    else dDayLabel = `D-${remaining}`

    challenges.value = [{
      id: ucId,
      name: ch.title || first.title || '진행 중 챌린지',
      progress,
      dDayLabel,
    }]
  } catch (e) {
    challenges.value = []
  }
}

const loadCommunity = async () => {
  try {
    const res = await communityApi.getList('EAT')
    const list = (res.data?.list || res.data || []).slice(0, 3)
    communityPosts.value = list.map(p => ({
      id: p.postId || p.id,
      title: p.title,
      likes: p.likeCount || 0,
      nickname: p.writerId || '익명',
    }))
  } catch (e) {
    communityPosts.value = []
  }
}

const currentChallenge = computed(() => challenges.value[0] || null)

const nutritionStatus = computed(() => {
  const k = kcalPercent.value
  const { carbRatio: c, proteinRatio: p, fatRatio: f } = todaySummary.value

  if (!todaySummary.value.kcal) {
    return { levelClass: 'pill-empty', label: 'No Data', message: '식단을 기록해주세요.', alerts: [] }
  }

  let level = 'pill-good', label = 'Perfect', message = '영양 밸런스가 아주 완벽해요!'
  const alerts = []

  if (k < 70) { level = 'pill-warn'; label = 'Low Energy'; message = '에너지가 더 필요해요 🔋' }
  else if (k > 110) { level = 'pill-warn'; label = 'High Energy'; message = '칼로리가 조금 높아요 🔥' }

  if (p < 15) alerts.push('단백질 부족 🥩')
  if (c > 65) alerts.push('탄수화물 과다 🍚')
  if (f > 35) { alerts.push('지방 과다 🥑'); if (k > 110) level = 'pill-risk' }

  if (level === 'pill-risk') { label = 'Warning'; message = '식단 조절이 필요해 보여요 🏥' }

  return { levelClass: level, label, message, alerts }
})

const nutritionAlerts = computed(() => nutritionStatus.value.alerts)

const lunchOptions = [
  { key: 'LIGHT',  label: '가볍게' },
  { key: 'STRONG', label: '든든하게' },
  { key: 'SOUP',   label: '따뜻하게' },
]

const lunchCandidates = {
  LIGHT: [
    { title: '닭가슴살 샐러드', desc: '450kcal · 부담 없는 한 끼' },
    { title: '두부 샐러드', desc: '380kcal · 단백질 + 가벼운 포만감' },
    { title: '연어 포케', desc: '520kcal · 오메가3 챙기기' },
    { title: '곤약비빔면 + 삶은계란', desc: '430kcal · 가벼운 탄수' },
    { title: '그릭요거트볼', desc: '360kcal · 빠르게 먹기 좋음' },
    { title: '채소비빔밥(밥 반)', desc: '480kcal · 야채 위주로' },
  ],
  STRONG: [
    { title: '현미밥 & 불고기', desc: '600kcal · 에너지 충전' },
    { title: '제육 + 쌈채소', desc: '710kcal · 채소 곁들이기' },
    { title: '닭갈비 덮밥(밥 70%)', desc: '720kcal · 든든하고 맛있게' },
    { title: '참치마요 덮밥(마요 적게)', desc: '680kcal · 간편 든든' },
    { title: '소고기미역국 + 잡곡밥', desc: '650kcal · 단백질+포만감' },
  ],
  SOUP: [
    { title: '맑은 버섯전골', desc: '500kcal · 속 편한 국물' },
    { title: '순두부찌개 + 밥 반', desc: '620kcal · 따뜻하고 든든' },
    { title: '닭곰탕 + 밥 반', desc: '580kcal · 속 편한 국물' },
    { title: '된장찌개 + 생선구이', desc: '640kcal · 한식 밸런스' },
    { title: '쌀국수(면 적게)', desc: '600kcal · 따뜻한 면' },
  ],
}

const tomorrowKey = computed(() => {
  const d = new Date()
  d.setDate(d.getDate() + 1)
  const yyyy = d.getFullYear()
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  return `${yyyy}-${mm}-${dd}`
})

const pickDailyIndex = (len, seedStr) => {
  if (!len) return 0
  let hash = 0
  for (let i = 0; i < seedStr.length; i++) {
    hash = (hash * 31 + seedStr.charCodeAt(i)) >>> 0
  }
  return hash % len
}

const selectedLunch = computed(() => {
  const list = lunchCandidates[selectedLunchType.value] || []
  if (!list.length) return null
  const seed = `${tomorrowKey.value}|${selectedLunchType.value}`
  const idx = pickDailyIndex(list.length, seed)
  return list[idx]
})

const selectLunchType = (k) => { selectedLunchType.value = k }

const goDiet = () => router.push({ name: 'diet-list' })
const goDietAnalysis = () => router.push({ name: 'diet-analysis' })
const goChallenge = () => router.push({ name: 'challenge-list', query: { mode: 'EAT' } })
const goChallengeCreate = () => router.push({ name: 'challenge-new', query: { mode: 'EAT' } })
const goCommunity = () => router.push({ name: 'community-list', query: { mode: 'EAT' } })
const goCommunityDetail = (id) => router.push({ name: 'community-detail', params: { id } })

onMounted(async () => {
  await loadProgress()
  await loadSummary()
  await loadMeals()
  await loadChallenge()
  await loadCommunity()
  selectedLunchType.value = autoLunchType.value
})
</script>

<style scoped>
@import url('https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/static/pretendard.css');

* { box-sizing: border-box; }

.dashboard-container {
  min-height: 100vh;
  background-color: #F8FAFC; /* Slate-50 */
  font-family: 'Pretendard', sans-serif;
  color: #1E293B; /* Slate-800 */
  position: relative;
  overflow-x: hidden;
  padding: 40px 32px; /* 좌우 패딩을 조금 더 확보 (20px -> 32px) */
}

/* --- Background Decoration --- */
.bg-decoration {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%; z-index: 0; pointer-events: none;
}
.orb {
  position: absolute; border-radius: 50%; filter: blur(80px); opacity: 0.6;
}
.orb-1 {
  width: 400px; height: 400px; background: #D1FAE5; /* Emerald-100 */
  top: -100px; right: -100px; animation: float 12s infinite ease-in-out;
}
.orb-2 {
  width: 300px; height: 300px; background: #E0F2FE; /* Sky-100 */
  bottom: -50px; left: -50px; animation: float 15s infinite ease-in-out reverse;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(20px, -20px); }
}

.dashboard-content {
  position: relative; 
  z-index: 1; 
  width: 100%;       /* 가로를 꽉 채우도록 설정 */
  max-width: 1600px; /* 기존 1240px -> 1600px로 변경하여 와이드 화면 대응 */
  margin: 0 auto;
}

/* --- Header Section --- */
.header-section {
  display: flex; justify-content: space-between; align-items: flex-end;
  margin-bottom: 32px; flex-wrap: wrap; gap: 20px;
}
.header-title-group { display: flex; align-items: center; gap: 16px; }
.emoji-icon { font-size: 42px; filter: drop-shadow(0 4px 6px rgba(0,0,0,0.1)); }
.page-title {
  font-size: 32px; font-weight: 800; color: #064E3B; /* Emerald-900 */
  line-height: 1.2; letter-spacing: -0.5px; margin: 0;
}
.page-subtitle { font-size: 15px; color: #64748B; font-weight: 500; margin: 4px 0 0; }

.header-stats-group { display: flex; align-items: center; gap: 16px; flex-wrap: wrap; }
.date-badge {
  background: #fff; padding: 8px 16px; border-radius: 99px;
  font-size: 14px; font-weight: 700; color: #475569;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05); display: flex; align-items: center; gap: 6px;
}

/* XP Card in Header */
.xp-stat-card {
  background: #fff; padding: 10px 20px; border-radius: 16px;
  display: flex; align-items: center; gap: 20px;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.15); border: 1px solid rgba(16, 185, 129, 0.1);
}
.xp-info { display: flex; flex-direction: column; align-items: flex-start; }
.level-badge { font-size: 11px; font-weight: 800; color: #10B981; text-transform: uppercase; letter-spacing: 0.5px; }
.level-name { font-size: 14px; font-weight: 700; color: #1E293B; }

.xp-bar-container { display: flex; flex-direction: column; width: 140px; gap: 4px; }
.xp-bar { width: 100%; height: 6px; background: #E2E8F0; border-radius: 99px; overflow: hidden; }
.xp-fill { height: 100%; background: #10B981; border-radius: 99px; transition: width 0.5s ease; }
.xp-text { font-size: 10px; color: #94A3B8; display: flex; justify-content: space-between; }
.next-xp { color: #CBD5E1; }

.streak-badge {
  background: #FFF7ED; color: #EA580C; font-size: 12px; font-weight: 800;
  padding: 6px 12px; border-radius: 8px;
}

/* --- Layout Grid --- */
.main-grid {
  display: grid; grid-template-columns: 1.5fr 1fr; gap: 24px;
}

/* --- Common Card Styles --- */
.card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  border-radius: 24px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(255,255,255,0.8);
  overflow: hidden; margin-bottom: 24px;
  transition: transform 0.2s, box-shadow 0.2s;
}
.card:hover { transform: translateY(-2px); box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05); }

.card-header {
  padding: 20px 24px; display: flex; justify-content: space-between; align-items: center;
}
.card-header.border-b { border-bottom: 1px solid #F1F5F9; }
.card-title { font-size: 18px; font-weight: 700; color: #0F172A; margin: 0; }

/* Buttons */
.icon-btn {
  width: 32px; height: 32px; border-radius: 50%; border: 1px solid #E2E8F0;
  background: #fff; color: #64748B; cursor: pointer; display: flex; align-items: center; justify-content: center;
  transition: all 0.2s;
}
.icon-btn:hover { background: #F8FAFC; color: #10B981; border-color: #10B981; }

.add-btn {
  padding: 6px 14px; background: #ECFDF5; color: #059669; border: none; border-radius: 8px;
  font-size: 13px; font-weight: 700; cursor: pointer; transition: 0.2s;
}
.add-btn:hover { background: #D1FAE5; }

.text-btn { background: rgba(255,255,255,0.2); color: #fff; border: none; padding: 4px 10px; border-radius: 6px; font-size: 12px; cursor: pointer; }
.link-btn { background: none; border: none; color: #64748B; font-size: 13px; font-weight: 600; cursor: pointer; }
.link-btn:hover { color: #10B981; }

/* --- Nutrition Card (A) --- */
.nutrition-content { padding: 0 24px 28px; display: flex; gap: 32px; align-items: center; }
.chart-section { display: flex; flex-direction: column; align-items: center; gap: 12px; }
.ring-chart-wrapper { position: relative; width: 140px; height: 140px; }
.ring-svg { transform: rotate(-90deg); }
.ring-bg { fill: none; stroke: #F1F5F9; }
.ring-progress { fill: none; stroke: url(#gradientGreen); stroke-linecap: round; transition: stroke-dashoffset 1s ease; stroke: #10B981; }
.ring-center-text {
  position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);
  display: flex; flex-direction: column; align-items: center;
}
.percent-val { font-size: 28px; font-weight: 800; color: #064E3B; line-height: 1; }
.percent-val small { font-size: 14px; font-weight: 600; color: #94A3B8; }
.kcal-val { font-size: 12px; font-weight: 600; color: #64748B; margin-top: 4px; }

.status-badge {
  padding: 4px 12px; border-radius: 99px; font-size: 12px; font-weight: 700;
}
.st-low { background: #FEF3C7; color: #B45309; }
.st-good { background: #D1FAE5; color: #059669; }
.st-high { background: #FEE2E2; color: #B91C1C; }
.st-none { background: #F1F5F9; color: #94A3B8; }

.macro-section { flex: 1; display: flex; flex-direction: column; gap: 16px; }

/* Status Banner within Card */
.status-banner {
  padding: 12px 16px; border-radius: 12px; display: flex; align-items: center; gap: 12px;
}
.status-banner.pill-good { background: #ECFDF5; border: 1px solid #D1FAE5; }
.status-banner.pill-warn { background: #FFFBEB; border: 1px solid #FEF3C7; }
.status-banner.pill-risk { background: #FEF2F2; border: 1px solid #FEE2E2; }
.status-banner.pill-empty { background: #F8FAFC; border: 1px dashed #CBD5E1; }

.sb-icon { font-size: 20px; }
.sb-title { font-size: 13px; font-weight: 700; color: #334155; }
.sb-desc { font-size: 12px; color: #64748B; }

.macro-bars { display: flex; flex-direction: column; gap: 10px; }
.macro-item { display: flex; flex-direction: column; gap: 4px; }
.macro-label { display: flex; justify-content: space-between; font-size: 12px; color: #64748B; font-weight: 600; }
.macro-label .val { color: #334155; font-weight: 700; }
.bar-bg { height: 8px; background: #F1F5F9; border-radius: 4px; overflow: hidden; }
.bar-fill { height: 100%; border-radius: 4px; transition: width 0.8s ease; }
.bar-fill.carb { background: #F59E0B; }
.bar-fill.protein { background: #10B981; }
.bar-fill.fat { background: #EF4444; }

/* --- Meals Timeline (B) --- */
.timeline-list { padding: 0 24px 24px; display: flex; flex-direction: column; gap: 0; }
.timeline-item { display: flex; gap: 16px; min-height: 60px; }
.time-col { display: flex; flex-direction: column; align-items: center; width: 40px; }
.time-label { font-size: 12px; font-weight: 700; color: #94A3B8; margin-bottom: 8px; }
.line { width: 2px; flex: 1; background: #E2E8F0; border-radius: 2px; }

.content-col { flex: 1; padding-bottom: 20px; }
.meal-box {
  background: #fff; border: 1px solid #E2E8F0; padding: 12px 16px; border-radius: 12px;
  transition: 0.2s; display: flex; align-items: center;
}
.meal-box:hover { border-color: #10B981; box-shadow: 0 4px 6px -1px rgba(16, 185, 129, 0.1); }
.meal-box.empty { background: #F8FAFC; border: 1px dashed #CBD5E1; color: #94A3B8; }
.meal-info { display: flex; justify-content: space-between; width: 100%; align-items: center; }
.menu-name { font-size: 14px; font-weight: 600; color: #334155; }
.kcal-tag { font-size: 11px; color: #10B981; background: #ECFDF5; padding: 2px 8px; border-radius: 6px; font-weight: 700; }

/* --- Insight Card (C) --- */
.insight-content { padding: 20px 24px; }
.ai-comment-box {
  background: #F0FDF4; padding: 16px; border-radius: 16px; display: flex; gap: 12px; align-items: flex-start;
  margin-bottom: 20px;
}
.ai-avatar { font-size: 20px; }
.ai-text { font-size: 14px; color: #166534; font-weight: 500; line-height: 1.5; }

.condition-stats { display: flex; flex-direction: column; gap: 10px; margin-bottom: 24px; }
.stat-row { display: flex; align-items: center; gap: 12px; font-size: 13px; color: #64748B; font-weight: 600; }
.stat-bar { flex: 1; height: 6px; background: #F1F5F9; border-radius: 3px; overflow: hidden; }
.stat-fill { height: 100%; border-radius: 3px; }
.stat-fill.energy { background: #FBBF24; }
.stat-fill.focus { background: #3B82F6; }
.stat-fill.bloat { background: #F97316; }

.divider { height: 1px; background: #F1F5F9; margin-bottom: 20px; }

.reco-header { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 12px; }
.reco-title { font-size: 14px; font-weight: 700; color: #334155; }
.reco-date { font-size: 11px; color: #94A3B8; }

.reco-chips { display: flex; gap: 8px; margin-bottom: 12px; }
.chip {
  padding: 6px 12px; border-radius: 99px; font-size: 12px; font-weight: 600; border: 1px solid #E2E8F0;
  background: #fff; color: #64748B; cursor: pointer; transition: 0.2s;
}
.chip.active { background: #10B981; color: #fff; border-color: #10B981; box-shadow: 0 2px 6px rgba(16, 185, 129, 0.3); }

.reco-result {
  background: #F8FAFC; border-radius: 12px; padding: 12px; display: flex; gap: 12px; align-items: center;
}
.res-icon { font-size: 24px; background: #fff; width: 40px; height: 40px; display: flex; align-items: center; justify-content: center; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.03); }
.res-name { font-size: 13px; font-weight: 700; color: #1E293B; }
.res-desc { font-size: 11px; color: #64748B; }

/* --- Challenge Card (D) --- */
.challenge-card { background: linear-gradient(135deg, #059669, #10B981); border: none; position: relative; }
.card-bg-pattern {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%; opacity: 0.1;
  background-image: radial-gradient(#fff 1px, transparent 1px); background-size: 20px 20px; pointer-events: none;
}
.challenge-body { padding: 0 24px 28px; color: #fff; position: relative; z-index: 1; }
.ch-badge {
  display: inline-block; padding: 4px 8px; background: rgba(255,255,255,0.2); border-radius: 6px;
  font-size: 11px; font-weight: 700; margin-bottom: 8px;
}
.ch-title { font-size: 18px; font-weight: 700; margin-bottom: 16px; }
.ch-progress { background: rgba(0,0,0,0.2); padding: 12px; border-radius: 12px; }
.prog-track { height: 8px; background: rgba(255,255,255,0.2); border-radius: 4px; overflow: hidden; margin-bottom: 8px; }
.prog-fill { height: 100%; background: #fff; border-radius: 4px; }
.prog-text { display: flex; justify-content: space-between; font-size: 12px; opacity: 0.9; }

.challenge-empty { text-align: center; padding: 30px 20px; color: #fff; }
.ce-text { margin-bottom: 12px; font-weight: 600; font-size: 14px; }
.ce-btn {
  background: #fff; color: #059669; border: none; padding: 8px 16px; border-radius: 99px;
  font-weight: 700; font-size: 13px; cursor: pointer;
}

/* --- Community Card (E) --- */
.post-list { padding: 0 24px 24px; display: flex; flex-direction: column; gap: 12px; }
.post-item {
  display: flex; gap: 12px; align-items: center; padding: 8px; border-radius: 12px;
  transition: 0.2s; cursor: pointer;
}
.post-item:hover { background: #F8FAFC; }
.post-avatar {
  width: 36px; height: 36px; background: #E0E7FF; color: #4F46E5;
  border-radius: 10px; display: flex; align-items: center; justify-content: center; font-weight: 700;
}
.post-content { flex: 1; }
.post-title { font-size: 14px; font-weight: 600; color: #334155; margin-bottom: 2px; }
.post-meta { font-size: 11px; color: #94A3B8; display: flex; gap: 8px; }

/* Empty States */
.empty-state { text-align: center; padding: 40px 0; color: #94A3B8; }
.empty-icon { font-size: 32px; margin-bottom: 10px; opacity: 0.6; }
.primary-btn {
  margin-top: 12px; background: #10B981; color: #fff; border: none; padding: 8px 20px;
  border-radius: 8px; font-weight: 600; cursor: pointer;
}
.empty-mini { text-align: center; padding: 20px; color: #CBD5E1; font-size: 13px; }

/* Animation Utils */
.animate-up { animation: fadeInUp 0.5s ease-out forwards; opacity: 0; transform: translateY(20px); }
.delay-1 { animation-delay: 0.1s; }
.delay-2 { animation-delay: 0.2s; }
.delay-3 { animation-delay: 0.3s; }
.delay-4 { animation-delay: 0.4s; }

@keyframes fadeInUp {
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 1024px) {
  .main-grid { grid-template-columns: 1fr; }
}
@media (max-width: 640px) {
  .header-section { flex-direction: column; align-items: flex-start; }
  .xp-stat-card { width: 100%; justify-content: space-between; }
  .nutrition-content { flex-direction: column; }
}
</style>