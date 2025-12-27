<script setup>
import { ref, onMounted } from "vue";
import { fetchUnreadCount } from "@/api/notification";

const emit = defineEmits(["toggle"]);

const unreadCount = ref(0);

const loadCount = async () => {
  const res = await fetchUnreadCount();
  unreadCount.value = res.data;
};

defineExpose({ loadCount });

onMounted(loadCount);
</script>

<template>
  <div class="bell" @click="emit('toggle')">
    🔔
    <span v-if="unreadCount > 0" class="badge">
      {{ unreadCount }}
    </span>
  </div>
</template>

<style scoped>
.bell {
  position: relative;
  font-size: 20px;
  cursor: pointer;
}
.badge {
  position: absolute;
  top: -6px;
  right: -6px;
  background: #ef4444;
  color: white;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 999px;
}
</style>
