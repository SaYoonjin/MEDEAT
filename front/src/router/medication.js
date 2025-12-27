import MedicationForm from '@/pages/medication/MedicationForm.vue';

// ✨ 메디 스캔 관련
import MediScan from '@/pages/medication/MediScan.vue';
import MediPillDetail from '@/pages/medication/MediPillDetail.vue';
import MediScanNotFound from '@/pages/medication/MediScanNotFound.vue';

export default [
  {
    path: '/medication/new',
    name: 'medication-new',
    component: MedicationForm,
  },
  {
    // 기존 복약 등록 수정용 라우트 (id 사용)
    path: '/medication/:id',
    name: 'medication-edit',
    component: MedicationForm,
    props: true,
  },

  // ----------------------------------------------------
  // 🎯 메디 스캔 기능
  // ----------------------------------------------------

  {
    path: '/medication/scan',
    name: 'medi-scan',
    component: MediScan,
  },
  {
    path: '/medication/scan/not-found',
    name: 'medi-scan-not-found',
    component: MediScanNotFound,
  },
  {
    // ✅ 스캔/검색/최근기록에서 진입하는 상세 페이지
    //    itemSeq 등은 query 로 넘김 (예: /medication/detail?itemSeq=123)
    path: '/medication/detail',
    name: 'medi-pill-detail',
    component: MediPillDetail,
  },

  {
    path: '/medication/reschedule',
    name: 'medication-reschedule',
    component: () => import('@/pages/medication/reschedule.vue'),
  },
];
