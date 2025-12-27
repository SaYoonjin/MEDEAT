<script setup>
import { RouterView, useRoute } from "vue-router";
import { computed } from "vue";

// ⭐ MainLayout 불러오기
import MainLayout from "@/layouts/MainLayout.vue";

// 현재 라우트 정보
const route = useRoute();

const noLayoutPages = ["challenge-chat", "login", "signup", "signup-complete", "home", "intro", "find-id", "reset-password"];
// ⭐ 현재 페이지가 레이아웃을 사용할지 판단
const useLayout = computed(() => {
  return !noLayoutPages.includes(route.name);
});
</script>

<template>
  <div id="app-wrapper">
    <!-- ⭐ 레이아웃 적용이 필요한 페이지 -->
    <MainLayout v-if="useLayout" />

    <!-- ⭐ 로그인/회원가입 등은 레이아웃 없이 단독 렌더링 -->
    <RouterView v-else />
  </div>
</template>

<style>
html,
body,
#app,
#app-wrapper {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
}

body {
  background: #f5f7fa;
}

#app,
#app-wrapper {
  display: block;
}
</style>
