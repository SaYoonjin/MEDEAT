<script setup>
import { ref, watch } from "vue";
import { useRoute } from "vue-router";
import Header from "@/components/layout/Header.vue";
import AiChatButton from "@/components/common/AiChatButton.vue"; // ✅ 추가

const route = useRoute();

/**
 * 🌟 이제 모드 제어는 Header.vue 내부에서 URL 쿼리를 통해 이루어집니다.
 * MainLayout은 전체적인 구조(Header + View)만 담당하는 깨끗한 래퍼가 됩니다.
 */
</script>

<template>
  <div class="layout-container">
    <!-- 상단 고정 내비게이션 -->
    <Header />

    <!-- 메인 컨텐츠 영역 (Header의 fixed 높이만큼 여백 확보) -->
    <main class="content-body">
      <div class="page-content">
        <router-view />
      </div>
    </main>

    <!-- ✅ 전역 AI 버튼 (모든 페이지 공통) -->
    <AiChatButton />
  </div>
</template>

<style scoped>
.layout-container {
  min-height: 100vh;
  background-color: #f8f9fc; /* 디자인 가이드에 맞춘 부드러운 배경 */
}

.content-body {
  width: 100%;
  /* 헤더가 공중에 떠(fixed) 있으므로 상단 여백을 충분히 확보합니다. */
  padding-top: clamp(100px, 10vw, 120px); 
  display: flex;
  flex-direction: column;
  align-items: center;
  box-sizing: border-box;
}

.page-content {
  width: min(1600px, 100%);
  padding: 0 clamp(16px, 5vw, 60px);
  box-sizing: border-box;
  flex: 1;
}

/* 모바일 대응 */
@media (max-width: 520px) {
  .content-body {
    padding-top: 90px;
  }
}
</style>
