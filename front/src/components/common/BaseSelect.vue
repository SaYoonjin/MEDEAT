<template>
  <div class="cselect" :class="{ open: open, disabled: disabled }" ref="root">
    <!-- label (옵션) -->
    <label v-if="label" class="cselect-label">
      <span>{{ label }}</span>
      <span v-if="required" class="required">*</span>
    </label>

    <!-- trigger -->
    <button
      type="button"
      class="cselect-trigger"
      :disabled="disabled"
      @click="toggle"
    >
      <span
        v-if="displayText"
        class="cselect-value"
      >
        {{ displayText }}
      </span>
      <span
        v-else
        class="cselect-placeholder"
      >
        {{ placeholder }}
      </span>

      <span class="cselect-chevron">⌄</span>
    </button>

    <!-- panel -->
    <div v-if="open" class="cselect-panel">
      <!-- 검색(옵션) -->
      <div v-if="searchable" class="cselect-search">
        <input
          v-model="q"
          type="text"
          :placeholder="searchPlaceholder"
          @keydown.stop
        />
      </div>

      <div class="cselect-options">
        <div
          v-for="opt in filteredOptions"
          :key="String(opt.value)"
          class="cselect-option"
          :class="{ selected: isSelected(opt.value) }"
          @click="pick(opt.value)"
        >
          <span>{{ opt.label }}</span>
        </div>

        <div v-if="filteredOptions.length === 0" class="cselect-empty">
          옵션이 없어요.
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from "vue";

const props = defineProps({
  modelValue: { type: [String, Number, Boolean, null], default: "" },
  options: { type: Array, default: () => [] }, // [{label, value}]
  placeholder: { type: String, default: "선택해 주세요" },

  label: { type: String, default: "" },
  required: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },

  searchable: { type: Boolean, default: false },
  searchPlaceholder: { type: String, default: "검색..." },
});

const emit = defineEmits(["update:modelValue", "change"]);

const open = ref(false);
const q = ref("");
const root = ref(null);

const displayText = computed(() => {
  const found = props.options.find((o) => String(o.value) === String(props.modelValue));
  return found?.label || "";
});

const filteredOptions = computed(() => {
  if (!props.searchable) return props.options;
  const keyword = q.value.trim().toLowerCase();
  if (!keyword) return props.options;
  return props.options.filter((o) => String(o.label).toLowerCase().includes(keyword));
});

const isSelected = (v) => String(v) === String(props.modelValue);

const toggle = () => {
  if (props.disabled) return;
  open.value = !open.value;
  if (!open.value) q.value = "";
};

const close = () => {
  open.value = false;
  q.value = "";
};

const pick = (v) => {
  emit("update:modelValue", v);
  emit("change", v);
  close();
};

const onDocClick = (e) => {
  if (!open.value) return;
  const el = root.value;
  if (!el) return;
  if (!el.contains(e.target)) close();
};

const onEsc = (e) => {
  if (e.key === "Escape") close();
};

onMounted(() => {
  document.addEventListener("click", onDocClick);
  document.addEventListener("keydown", onEsc);
});

onBeforeUnmount(() => {
  document.removeEventListener("click", onDocClick);
  document.removeEventListener("keydown", onEsc);
});
</script>

<style scoped>
/* 라벨 */
.cselect-label {
  font-size: 12px;
  font-weight: 600;
  color: #374151;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 6px;
}
.required {
  color: #e11d48;
  font-weight: 700;
}

/* 래퍼 */
.cselect {
  position: relative;
  width: 100%;
}
.cselect.disabled {
  opacity: 0.7;
}

/* 트리거 */
.cselect-trigger {
  width: 100%;
  padding: 10px 40px 10px 12px;
  border-radius: 14px;
  border: 1px solid #e5e7eb;
  font-size: 13px;
  outline: none;
  background-color: #fafafa;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  cursor: pointer;
  transition: border-color 0.15s ease, box-shadow 0.15s ease, background-color 0.15s ease;
}
.cselect-trigger:hover {
  background: #ffffff;
}
.cselect.open .cselect-trigger {
  border-color: #4b7bff;
  background-color: #ffffff;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.18);
}
.cselect-trigger:disabled {
  cursor: not-allowed;
  background: #f3f4f6;
}

/* 값/플레이스홀더 */
.cselect-value {
  color: #111827;
  font-weight: 500;
}
.cselect-placeholder {
  color: #9ca3af;
  font-weight: 500;
}

.cselect-chevron {
  position: absolute;
  right: 12px;
  top: 60%;
  transform: translateY(-50%);
  width: 24px;
  height: 24px;
  border-radius: 999px;

  display: flex;
  align-items: center;
  justify-content: center;

  color: #6b7280;
  pointer-events: none;
  transition: transform 0.15s ease;
}

/* 열렸을 때 회전 */
.cselect.open .cselect-chevron {
  transform: translateY(-50%) rotate(180deg);
}

/* 패널 */
.cselect-panel {
  position: absolute;
  left: 0;
  right: 0;
  margin-top: 8px;
  background: #ffffff;
  border: 1px solid #eef0f5;
  border-radius: 16px;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.1);
  overflow: hidden;
  z-index: 50;
}

/* 검색 */
.cselect-search {
  padding: 10px;
  border-bottom: 1px solid #f0f2f7;
  background: #fbfcff;
}
.cselect-search input {
  width: 100%;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  padding: 9px 10px;
  font-size: 13px;
  outline: none;
  background: #ffffff;
}
.cselect-search input:focus {
  border-color: #4b7bff;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.12);
}

/* 옵션 리스트 */
.cselect-options {
  max-height: 240px;
  overflow-y: auto;
  padding: 6px;
}
.cselect-options::-webkit-scrollbar {
  width: 8px;
}
.cselect-options::-webkit-scrollbar-thumb {
  border-radius: 999px;
  background: #d0d7e2;
}

/* 옵션 */
.cselect-option {
  width: 100%;
  border-radius: 12px;
  padding: 10px 10px;
  font-size: 13px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  color: #111827;
  transition: background 0.12s ease;
}
.cselect-option:hover {
  background: #f6f7ff;
}
.cselect-option.selected {
  background: #eef1ff;
  color: #2f3a66;
  font-weight: 700;
}
.cselect-check {
  width: 22px;
  height: 22px;
  border-radius: 999px;
  background: #4e6cff;
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.cselect-empty {
  padding: 14px 12px;
  font-size: 12px;
  color: #6b7280;
}
</style>
