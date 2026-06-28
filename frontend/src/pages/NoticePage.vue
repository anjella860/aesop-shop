<template>
  <div class="notice-page">
    <div class="container">
      <div class="page-header">
        <p class="page-subtitle">CUSTOMER SERVICE</p>
        <h1 class="page-title">공지사항</h1>
        <div class="divider"></div>
      </div>

      <div v-if="notices.length === 0" class="empty">
        <p>등록된 공지사항이 없습니다.</p>
      </div>

      <div v-else class="notice-list">
        <div
          v-for="notice in notices"
          :key="notice.id"
          class="notice-item"
        >
          <div class="notice-item__header" @click="toggle(notice.id)">
            <span class="notice-num">{{ notice.id }}</span>
            <span class="notice-title">{{ notice.title }}</span>
            <span class="notice-date">{{ formatDate(notice.createdAt) }}</span>
            <span class="notice-toggle">{{ openId === notice.id ? '▲' : '▽' }}</span>
          </div>
          <div v-if="openId === notice.id" class="notice-item__body">
            <p>{{ notice.content }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { noticeAPI } from "../api/index.js";

const notices = ref([]);
const openId = ref(null);

const formatDate = (d) => d ? new Date(d).toLocaleDateString("ko-KR") : "";
const toggle = (id) => { openId.value = openId.value === id ? null : id; };

onMounted(async () => {
  try {
    const res = await noticeAPI.getAll();
    notices.value = res.data;
  } catch (e) { notices.value = []; }
});
</script>

<style scoped>
.notice-page { padding: 60px 0 100px; background-color: var(--color-bg); }

.page-header { text-align: center; margin-bottom: 60px; }
.page-subtitle { font-family: var(--font-en); font-size: 11px; letter-spacing: 5px; color: var(--color-gold); margin-bottom: 12px; }
.page-title { font-family: var(--font-kr); font-size: 28px; font-weight: 300; color: var(--color-olive); margin-bottom: 20px; }
.divider { width: 40px; height: 1px; background-color: var(--color-gold); margin: 0 auto; }

.empty { text-align: center; padding: 60px 0; color: var(--color-text-sub); font-size: 14px; }

.notice-list { border-top: 1px solid var(--color-border); }

.notice-item { border-bottom: 1px solid var(--color-border); }

.notice-item__header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px 0;
  cursor: pointer;
  transition: color 0.2s;
}

.notice-item__header:hover .notice-title { color: var(--color-olive); }

.notice-num { font-family: var(--font-en); font-size: 13px; color: var(--color-text-sub); min-width: 32px; flex-shrink: 0; }
.notice-title { flex: 1; font-size: 14px; color: var(--color-text); line-height: 1.4; }
.notice-date { font-size: 12px; color: var(--color-text-sub); flex-shrink: 0; }
.notice-toggle { font-size: 12px; color: var(--color-text-sub); flex-shrink: 0; }

.notice-item__body {
  padding: 20px 24px 28px;
  background-color: var(--color-border-light);
  font-size: 14px;
  color: var(--color-text-sub);
  line-height: 1.8;
}
</style>
