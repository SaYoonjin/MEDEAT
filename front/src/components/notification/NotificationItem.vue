<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";

const props = defineProps({
  notification: Object,
});

const emit = defineEmits(["read"]);

const router = useRouter();

const handleClick = async () => {
  // 1️⃣ 읽음 처리 요청
  emit("read", props.notification.notificationId);

  console.log("알림 클릭:", props.notification);

  // 2️⃣ 이동
  const { targetType, targetId, type } = props.notification;

  if (targetType === "POST") {
    router.push(`/community/${targetId}`);
  } else if (targetType === "CHALLENGE") {
    router.push(`/challenge/${targetId}`);
  } else if (type === "LEVEL_UP") {
    router.push("/mypage");
  } else if (type === "MEDICATION_SETTING_CHANGED") {
    router.push("/disease");
  } else if (targetType === "DISEASE") {
  router.push("/disease/DiseaseList");
  }

};


const timeText = computed(() => {
  const diff = (Date.now() - new Date(props.notification.createdAt)) / 1000;
  if (diff < 60) return "방금 전";
  if (diff < 3600) return `${Math.floor(diff/60)}분 전`;
  if (diff < 86400) return `${Math.floor(diff/3600)}시간 전`;
  return `${Math.floor(diff/86400)}일 전`;
});
</script>

<template>
  <div
    class="item"
    :class="{ unread: !notification.read }"
    @click="handleClick"
  >
    <div class="message">{{ notification.message }}</div>
    <div class="time">{{ timeText }}</div>
  </div>
</template>

<style scoped>
.item {
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
}
.item:hover {
  background: #f8fafc;
}
.unread {
  background: #eff6ff;
}
.message {
  font-size: 14px;
}
.time {
  font-size: 12px;
  color: #999;
}
</style>
