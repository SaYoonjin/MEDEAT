<script setup>
import { defineProps } from 'vue';
import EatDashboard from '@/pages/dashboard/EatDashboard.vue';
import MediDashboard from '@/pages/dashboard/MediDashboard.vue';

// props로 받은 mode 값
const props = defineProps({
  mode: {
    type: String,
    default: 'EAT'
  }
});
</script>

<template>
  <div>
    <!-- 모드에 따른 대시보드 컴포넌트 렌더링 -->
    <EatDashboard v-if="props.mode === 'EAT'" />
    <MediDashboard v-else-if="props.mode === 'MEDI_EAT'" />
    <p v-else>모드 정보가 없습니다.</p>
  </div>
</template>
