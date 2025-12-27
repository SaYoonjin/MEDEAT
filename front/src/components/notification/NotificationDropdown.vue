<script setup>
import { ref, onMounted } from "vue";
import { fetchNotifications, markAsReadBatch } from "@/api/notification";
import NotificationItem from "./NotificationItem.vue";
import { useRouter } from "vue-router";

const router = useRouter();
const notifications = ref([]);

const emit = defineEmits(["refresh", "close"]);

const load = async () => {
  const res = await fetchNotifications();
  notifications.value = res.data;
};

onMounted(async () => {
  await load();

  const ids = notifications.value
    .filter(n => !n.isRead)
    .map(n => n.notificationId);

  if (ids.length > 0) {
    try {
      await markAsReadBatch(ids);
      console.log("✅ 읽음 처리 성공");
      // 부모(Header)에게 알림 리스트와 읽지 않은 개수를 다시 가져오라고 신호를 보냄
      emit("refresh"); 
    } catch (error) {
      console.error("❌ 읽음 처리 실패:", error);
    }
  }
});
</script>

<template>
  <div class="dropdown">
    <NotificationItem
      v-for="n in notifications"
      :key="n.notificationId"
      :notification="n"
    />

    <div
      class="more"
      @click.stop="
        emit('close');
        router.push('/notifications');
      "
    >
      + 이전 알림 더보기
    </div>
  </div>
</template>


<style scoped>
.dropdown {
  position: absolute;
  top: 48px;
  right: 0;
  width: 320px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 12px 30px rgba(0,0,0,0.15);
  padding: 8px;
  z-index: 2000;
}

.empty {
  text-align: center;
  padding: 20px;
  color: #999;
}

.more {
  padding: 10px;
  text-align: center;
  font-size: 13px;
  color: #2563eb;
  cursor: pointer;
}

.more:hover {
  text-decoration: underline;
}
</style>
