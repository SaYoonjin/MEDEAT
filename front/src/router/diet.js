// src/router/diet.js
import DietList from '@/pages/diet/DietList.vue'
import DietForm from '@/pages/diet/DietForm.vue'
import DietDetail from '@/pages/diet/DietDetail.vue'
import DietAnalysis from '@/pages/diet/DietAnalysis.vue'

export default [
  // 목록
  {
    path: '/diet',
    name: 'diet-list',
    component: DietList
  },

  // 신규 작성
  {
    path: '/diet/form',
    name: 'diet-create',
    component: DietForm
  },

  // 수정
  {
    path: '/diet/edit/:id',
    name: 'diet-edit',
    component: DietForm,
    props: true
  },

  // 상세 보기
  {
    path: '/diet/detail/:id',
    name: 'diet-detail',
    component: DietDetail,
    props: true
  },

  // 분석 화면
  {
    path: '/diet/analysis',
    name: 'diet-analysis',
    component: DietAnalysis
  }
]
