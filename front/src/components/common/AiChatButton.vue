<template>
  <!-- 
    aria-label을 추가하여 접근성을 높였습니다. 
    마우스 호버 시 'expanded' 클래스 효과가 적용됩니다.
  -->
  <button class="ai-chat-button" @click="goChat" aria-label="Open AI Chat">
    <div class="button-content">
      <!-- AI 느낌의 스파클 아이콘 (SVG) -->
      <svg 
        class="ai-icon" 
        xmlns="http://www.w3.org/2000/svg" 
        viewBox="0 0 24 24" 
        fill="currentColor"
      >
        <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" style="display:none"/> <!-- Checkmark placeholder hidden -->
        <path d="M19,9.3V4h-5.3l-2.7-3L8.3,4H3v5.3L0.3,12l2.7,2.7V20h5.3l2.7,3l2.7-3h5.3v-5.3l2.7-2.7L19,9.3z M17,12h-2v2h-2v-2h-2v-2h2V8h2v2h2V12z"/> <!-- Sparkle/Star shape -->
        <path d="M12,2C6.48,2,2,6.48,2,12s4.48,10,10,10s10-4.48,10-10S17.52,2,12,2z M12,20c-4.41,0-8-3.59-8-8s3.59-8,8-8s8,3.59,8,8 S15.59,20,12,20z M11,7h2v2h-2V7z M11,11h2v6h-2V11z" opacity="0"/> <!-- Helper -->
        <!-- Clean Sparkles Icon -->
        <path d="M19 10.5V8h-2.5v2.5H19zM19 13h-2.5v2.5H19V13zM13 19v-2.5h-2.5V19H13zM13 5.5V3h-2.5v2.5H13zM6.5 19H9v-2.5H6.5V19zM6.5 5.5H9V3H6.5V5.5zM12 15.5c1.93 0 3.5-1.57 3.5-3.5s-1.57-3.5-3.5-3.5s-3.5 1.57-3.5 3.5S10.07 15.5 12 15.5z"/>
      </svg>
      
      <!-- 텍스트: 호버 시 나타남 -->
      <span class="ai-label">MEDEAT AI</span>
    </div>

    <!-- 배경 애니메이션 효과 -->
    <div class="pulse-ring"></div>
  </button>
</template>

<script setup>
import { useRouter } from "vue-router";

const router = useRouter();

const goChat = () => {
  router.push("/ai-chat");
};
</script>

<style scoped>
.ai-chat-button {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 1000;

  /* 초기 모양: 원형 */
  width: 60px;
  height: 60px;
  border-radius: 30px; /* pill shape를 위해 높이의 절반 */
  
  border: none;
  cursor: pointer;
  
  /* ✨ 고급스러운 그라데이션 (Deep Indigo -> Vivid Purple) */
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 50%, #db2777 100%);
  background-size: 200% 200%;
  
  color: white;
  box-shadow: 
    0 4px 15px rgba(124, 58, 237, 0.4),
    inset 0 1px 1px rgba(255, 255, 255, 0.3);

  /* 애니메이션 설정 */
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  animation: bg-pan 3s infinite alternate; /* 배경 색상이 은은하게 흐름 */
  
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden; /* 텍스트 숨김 처리 */
}

/* 내부 콘텐츠 정렬 */
.button-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px; /* 아이콘과 텍스트 사이 간격 */
  white-space: nowrap; /* 텍스트 줄바꿈 방지 */
}

/* 아이콘 스타일 */
.ai-icon {
  width: 28px;
  height: 28px;
  fill: white;
  flex-shrink: 0; /* 아이콘 크기 고정 */
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.2));
}

/* 텍스트 라벨 (기본 상태: 숨김) */
.ai-label {
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 0.5px;
  opacity: 0;
  max-width: 0;
  transform: translateX(10px);
  transition: all 0.3s ease;
}

/* ✨ HOVER 효과: 버튼이 길어지며 텍스트 등장 */
.ai-chat-button:hover {
  width: 160px; /* 너비 확장 */
  box-shadow: 
    0 8px 25px rgba(124, 58, 237, 0.6),
    inset 0 1px 1px rgba(255, 255, 255, 0.4);
  transform: translateY(-2px);
}

.ai-chat-button:hover .ai-label {
  opacity: 1;
  max-width: 100px;
  transform: translateX(0);
  margin-right: 4px;
}

/* 클릭 효과 */
.ai-chat-button:active {
  transform: scale(0.95);
  box-shadow: 0 2px 10px rgba(124, 58, 237, 0.4);
}

/* 🌊 Pulse(파동) 애니메이션 링 */
.pulse-ring {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 30px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  z-index: -1;
  animation: pulse-ring 2s cubic-bezier(0.215, 0.61, 0.355, 1) infinite;
}

/* 배경 그라데이션 흐름 애니메이션 */
@keyframes bg-pan {
  0% { background-position: 0% 50%; }
  100% { background-position: 100% 50%; }
}

/* 파동 애니메이션 키프레임 */
@keyframes pulse-ring {
  0% {
    transform: scale(0.9);
    opacity: 1;
  }
  100% {
    transform: scale(1.5);
    opacity: 0;
  }
}
</style>