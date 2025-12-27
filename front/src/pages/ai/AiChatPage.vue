<template>
  <div class="ai-chat-page">
    <!-- 헤더 -->
    <header class="chat-header">
      <div class="header-content">
        <div class="logo-area">
          <div class="logo-icon">M</div>
          <h2>메딧 AI</h2>
        </div>
        <p class="subtitle">Medical Intelligent Assistant</p>
      </div>
    </header>

    <!-- 채팅 본문 -->
    <div class="chat-body" ref="chatBody">
      <div class="messages-container">
        <div
          v-for="(msg, index) in messages"
          :key="index"
          :class="['message-wrapper', msg.role]"
        >
          <!-- 아이콘 (AI일 경우) -->
          <div v-if="msg.role === 'assistant'" class="profile-icon ai">
            <span>AI</span>
          </div>

          <!-- 메시지 내용 -->
          <div class="message-content">
            <!-- ✅ AI 메시지는 마크다운 렌더링 -->
            <div
              v-if="msg.role === 'assistant'"
              class="markdown-body"
              v-html="renderMarkdown(msg.content)"
            ></div>

            <!-- ✅ 사용자 메시지는 기존 그대로 -->
            <div v-else class="user-text">
              {{ msg.content }}
            </div>
          </div>
        </div>

        <!-- ✅ 로딩 인디케이터 (답변 생성 중) -->
        <div v-if="isLoading" class="message-wrapper assistant">
          <div class="profile-icon ai">
            <span>AI</span>
          </div>
          <div class="message-content loading-content">
            <span class="loading-text">답변을 생성하고 있습니다</span>
            <div class="loading-dots">
              <span>.</span><span>.</span><span>.</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 입력창 -->
    <div class="chat-input-area">
      <div class="input-wrapper">
        <input
          v-model="input"
          @keyup.enter="send"
          placeholder="건강 상담이나 의학 정보를 물어보세요..."
          type="text"
          :disabled="isLoading"
        />
        <button @click="send" :disabled="!input.trim() || isLoading">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
            <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"></path>
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from "vue";
import axios from "axios";
import { marked } from "marked";

const input = ref("");
const isLoading = ref(false); // 로딩 상태 추가
const messages = ref([
  { role: "assistant", content: "안녕하세요! **메딧 AI**입니다 😊\n궁금한 점이 있으신가요?" },
]);

// ✅ 마크다운 변환 함수
const renderMarkdown = (text) => {
  return marked.parse(text || "");
};

// 스크롤 최하단 이동 함수
const scrollToBottom = () => {
  nextTick(() => {
    const chatBody = document.querySelector(".chat-body");
    if (chatBody) {
      chatBody.scrollTop = chatBody.scrollHeight;
    }
  });
};

const send = async () => {
  if (!input.value.trim() || isLoading.value) return;

  const userMessage = input.value;
  messages.value.push({ role: "user", content: userMessage });
  input.value = "";
  
  // 로딩 시작
  isLoading.value = true;
  scrollToBottom();

  try {
    const res = await axios.post("/api/chatbot", {
      message: userMessage,
    });

    messages.value.push({
      role: "assistant",
      content: res.data.answer,
    });
  } catch (e) {
    messages.value.push({
      role: "assistant",
      content: "⚠️ **응답 중 오류**가 발생했어요.",
    });
  } finally {
    // 로딩 종료
    isLoading.value = false;
    scrollToBottom();
  }
};
</script>

<style scoped>
/* 전체 레이아웃 (스크롤 방지 및 꽉 찬 화면) */
.ai-chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh; /* Fallback */
  height: 100dvh; /* ✅ 모바일 브라우저 주소창 대응 */
  overflow: hidden; /* 전체 페이지 스크롤 방지 */
  background-color: #f4f6f9;
  font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, system-ui, Roboto, sans-serif;
}

/* 헤더 디자인 */
.chat-header {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  padding: 16px 20px;
  /* ✅ 안전 영역(Safe Area) 패딩 추가: 상태바에 가려지는 문제 해결 */
  padding-top: calc(16px + env(safe-area-inset-top));
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  z-index: 10;
  flex-shrink: 0; /* 헤더 크기 고정 */
}

.ai-chat-page {
  height: calc(100dvh - 120px);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #f4f6f9;
}

.header-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  max-width: 800px;
  margin: 0 auto;
  width: 100%;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-icon {
  width: 32px;
  height: 32px;
  background: white;
  color: #2563eb;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 18px;
}

.chat-header h2 {
  font-size: 1.25rem;
  font-weight: 700;
  margin: 0;
}

.subtitle {
  font-size: 0.85rem;
  opacity: 0.9;
  margin: 4px 0 0 42px;
  font-weight: 300;
}

/* 채팅 본문 (내부 스크롤) */
.chat-body {
  flex: 1; /* 남은 영역 모두 차지 */
  padding: 20px;
  overflow-y: auto; /* 내부 스크롤 허용 */
  display: flex;
  flex-direction: column;
  scroll-behavior: smooth;
}

.messages-container {
  max-width: 800px;
  width: 100%;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding-bottom: 20px;
}

/* 메시지 래퍼 */
.message-wrapper {
  display: flex;
  align-items: flex-start;
  max-width: 85%;
  animation: fadeIn 0.3s ease-out;
}

.message-wrapper.user {
  margin-left: auto;
  flex-direction: row-reverse;
}

.message-wrapper.assistant {
  margin-right: auto;
}

/* 프로필 아이콘 */
.profile-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: bold;
  flex-shrink: 0;
}

.profile-icon.ai {
  background-color: #eff6ff;
  color: #3b82f6;
  border: 1px solid #dbeafe;
  margin-right: 12px;
}

/* 말풍선 공통 */
.message-content {
  padding: 12px 18px;
  border-radius: 16px;
  font-size: 0.95rem;
  line-height: 1.6;
  position: relative;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  word-break: break-word;
}

/* User 말풍선 */
.message-wrapper.user .message-content {
  background: #3b82f6;
  color: white;
  border-bottom-right-radius: 4px;
}

/* Assistant 말풍선 */
.message-wrapper.assistant .message-content {
  background: white;
  color: #333;
  border: 1px solid #e5e7eb;
  border-top-left-radius: 4px;
}

/* 로딩 애니메이션 스타일 */
.loading-content {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #64748b;
  font-size: 0.9rem;
}

.loading-dots {
  display: inline-flex;
}

.loading-dots span {
  animation: bounce 1.4s infinite ease-in-out both;
  font-weight: bold;
  font-size: 1.2rem;
  line-height: 0.8;
  margin-left: 2px;
}

.loading-dots span:nth-child(1) { animation-delay: -0.32s; }
.loading-dots span:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

/* 입력창 영역 (화면 하단 고정) */
.chat-input-area {
  background: white;
  padding: 16px 20px;
  /* ✅ 하단 여백 추가: 30px + 안전 영역으로 넉넉하게 확보 */
  padding-bottom: calc(30px + env(safe-area-inset-bottom));
  border-top: 1px solid #e5e7eb;
  flex-shrink: 0; /* 크기 줄어들지 않음 */
}

.input-wrapper {
  max-width: 800px;
  margin: 0 auto;
  position: relative;
  display: flex;
  align-items: center;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 24px;
  padding: 6px 6px 6px 20px;
  transition: border-color 0.2s;
}

.input-wrapper:focus-within {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.input-wrapper input {
  flex: 1;
  border: none;
  background: transparent;
  padding: 8px 0;
  font-size: 1rem;
  outline: none;
  color: #333;
}

.input-wrapper input:disabled {
  background: transparent;
  color: #94a3b8;
}

.input-wrapper button {
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  margin-left: 10px;
  transition: all 0.2s;
}

.input-wrapper button:disabled {
  background: #cbd5e1;
  cursor: not-allowed;
  transform: none;
}

.input-wrapper button:hover:not(:disabled) {
  background: #2563eb;
  transform: scale(1.05);
}

/* 애니메이션 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* ✅ 마크다운 스타일링 */
.markdown-body ::v-deep(h1),
.markdown-body ::v-deep(h2),
.markdown-body ::v-deep(h3) {
  margin-top: 12px;
  margin-bottom: 8px;
  font-weight: 700;
  color: #1e293b;
}

.markdown-body ::v-deep(p) {
  margin-bottom: 8px;
}

.markdown-body ::v-deep(strong) {
  color: #2563eb;
  font-weight: 700;
}

.markdown-body ::v-deep(ul), 
.markdown-body ::v-deep(ol) {
  padding-left: 20px;
  margin-bottom: 12px;
}

.markdown-body ::v-deep(li) {
  margin-bottom: 4px;
}

.markdown-body ::v-deep(code) {
  background: #f1f5f9;
  color: #ef4444;
  padding: 2px 4px;
  border-radius: 4px;
  font-size: 0.9em;
  font-family: monospace;
}

.markdown-body ::v-deep(pre) {
  background: #1e293b;
  color: #f8fafc;
  padding: 12px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 10px 0;
}

.markdown-body ::v-deep(pre code) {
  background: transparent;
  color: inherit;
  padding: 0;
}

.markdown-body ::v-deep(a) {
  color: #2563eb;
  text-decoration: underline;
}

/* 스크롤바 커스텀 */
.chat-body::-webkit-scrollbar {
  width: 6px;
}
.chat-body::-webkit-scrollbar-track {
  background: transparent;
}
.chat-body::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}
.chat-body::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>