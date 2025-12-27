// src/router/dashboard.js
import DashboardRouter from '@/pages/dashboard/DashboardRouter.vue';

export default [
  {
    path: '/dashboard',
    name: 'dashboard',
    component: DashboardRouter,
    props: route => ({
      mode: route.query.mode || 'EAT', // 기본값 EAT
    }),
  },

  // MEDI-EAT 모드 진입용 편의 URL
  {
    path: '/dashboard-medeat',
    redirect: '/dashboard?mode=MEDI_EAT',
  },
];
