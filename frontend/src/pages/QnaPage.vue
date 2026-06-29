<template>
  <div class="qna-page">
    <div class="container">
      <div class="page-header">
        <p class="page-subtitle">CUSTOMER SERVICE</p>
        <h1 class="page-title">1:1 문의</h1>
        <div class="divider"></div>
      </div>

      <!-- 문의 작성 -->
      <div class="qna-form-box">
        <h2 class="form-title">문의 작성</h2>
        <div class="form-group">
          <label class="form-label">상품 선택</label>
          <select v-model.number="selectedProductId" class="form-input" @change="fetchQna">
            <option v-for="product in products" :key="product.id" :value="product.id">{{ product.name }}</option>
          </select>
        </div>
        <div class="form-group">
          <label class="form-label">제목</label>
          <input v-model="form.title" type="text" class="form-input" placeholder="문의 제목을 입력하세요" />
        </div>
        <div class="form-group">
          <label class="form-label">내용</label>
          <textarea v-model="form.content" class="form-textarea" rows="5" placeholder="문의 내용을 입력하세요"></textarea>
        </div>
        <p v-if="msg" class="submit-msg">{{ msg }}</p>
        <button class="btn-submit" @click="submitQna">문의 등록</button>
      </div>

      <!-- 문의 목록 -->
      <div class="qna-list">
        <h2 class="list-title">문의 내역</h2>
        <div v-if="qnaList.length === 0" class="empty">
          <p>등록된 문의가 없습니다.</p>
        </div>
        <div v-else>
          <div v-for="qna in qnaList" :key="qna.id" class="qna-item">
            <div class="qna-item__header" @click="toggleQna(qna.id)">
              <span class="qna-status" :class="qna.isAnswered ? 'answered' : 'pending'">
                {{ qna.isAnswered ? '답변완료' : '답변대기' }}
              </span>
              <span class="qna-title">{{ qna.title }}</span>
              <span class="qna-date">{{ formatDate(qna.createdAt) }}</span>
              <span class="qna-toggle">{{ openId === qna.id ? '▲' : '▽' }}</span>
            </div>
            <div v-if="openId === qna.id" class="qna-item__body">
              <div v-if="editingId === qna.id" class="qna-edit">
                <input v-model="editForm.title" class="form-input" />
                <textarea v-model="editForm.content" class="form-textarea" rows="4"></textarea>
                <button class="btn-submit" @click="updateQna(qna.id)">수정 저장</button>
              </div>
              <p v-else class="qna-content">{{ qna.content }}</p>
              <div class="qna-actions">
                <button class="btn-small" @click="startEdit(qna)">수정</button>
                <button class="btn-small btn-danger" @click="deleteQna(qna.id)">삭제</button>
              </div>
              <div v-if="qna.answer" class="qna-answer">
                <span class="answer-label">답변</span>
                <p>{{ qna.answer }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { productAPI, qnaAPI } from "../api/index.js";

const products = ref([]);
const selectedProductId = ref(null);
const qnaList = ref([]);
const openId = ref(null);
const msg = ref("");
const form = ref({ title: "", content: "" });
const editingId = ref(null);
const editForm = ref({ title: "", content: "" });

const formatDate = (d) => d ? new Date(d).toLocaleDateString("ko-KR") : "";
const toggleQna = (id) => { openId.value = openId.value === id ? null : id; };

const fetchQna = async () => {
  try {
    if (!selectedProductId.value) return;
    const res = await qnaAPI.getByProduct(selectedProductId.value);
    qnaList.value = res.data;
  } catch (e) { qnaList.value = []; }
};

const submitQna = async () => {
  if (!form.value.title.trim() || !form.value.content.trim()) {
    msg.value = "제목과 내용을 모두 입력해주세요.";
    return;
  }
  try {
    await qnaAPI.create(selectedProductId.value, { ...form.value });
    msg.value = "문의가 등록되었습니다.";
    form.value = { title: "", content: "" };
    await fetchQna();
    setTimeout(() => (msg.value = ""), 2000);
  } catch (e) {
    msg.value = "로그인이 필요합니다.";
  }
};

const startEdit = (qna) => {
  editingId.value = qna.id;
  editForm.value = { title: qna.title, content: qna.content };
};

const updateQna = async (id) => {
  await qnaAPI.update(id, editForm.value);
  editingId.value = null;
  await fetchQna();
};

const deleteQna = async (id) => {
  if (!window.confirm("문의글을 삭제할까요?")) return;
  await qnaAPI.delete(id);
  await fetchQna();
};

onMounted(async () => {
  const res = await productAPI.getAll();
  products.value = res.data;
  selectedProductId.value = products.value[0]?.id || null;
  await fetchQna();
});
</script>

<style scoped>
.qna-page { padding: 60px 0 100px; background-color: var(--color-bg); }

.page-header { text-align: center; margin-bottom: 60px; }
.page-subtitle { font-family: var(--font-en); font-size: 11px; letter-spacing: 5px; color: var(--color-gold); margin-bottom: 12px; }
.page-title { font-family: var(--font-kr); font-size: 28px; font-weight: 300; color: var(--color-olive); margin-bottom: 20px; }
.divider { width: 40px; height: 1px; background-color: var(--color-gold); margin: 0 auto; }

.qna-form-box { background-color: var(--color-border-light); padding: 40px; margin-bottom: 60px; }
.form-title { font-size: 15px; color: var(--color-olive); letter-spacing: 1px; margin-bottom: 28px; font-weight: 400; }
.form-group { margin-bottom: 20px; }
.form-label { display: block; font-size: 12px; letter-spacing: 1px; color: var(--color-text-sub); margin-bottom: 8px; }
.form-input { width: 100%; border: none; border-bottom: 1px solid var(--color-border); background: transparent; padding: 10px 0; font-size: 14px; color: var(--color-text); font-family: var(--font-kr); outline: none; transition: border-color 0.3s; }
.form-input:focus { border-bottom-color: var(--color-olive); }
.form-textarea { width: 100%; border: 1px solid var(--color-border); background: transparent; padding: 12px; font-size: 14px; color: var(--color-text); font-family: var(--font-kr); outline: none; resize: vertical; transition: border-color 0.3s; }
.form-textarea:focus { border-color: var(--color-olive); }
.submit-msg { font-size: 13px; color: var(--color-olive); margin-bottom: 12px; }
.btn-submit { padding: 12px 32px; background-color: var(--color-olive); color: var(--color-bg); border: none; font-family: var(--font-kr); font-size: 13px; letter-spacing: 1px; cursor: pointer; transition: background-color 0.3s; }
.btn-submit:hover { background-color: #2c3820; }

.list-title { font-size: 15px; color: var(--color-olive); letter-spacing: 1px; margin-bottom: 24px; font-weight: 400; }
.empty { text-align: center; padding: 40px 0; color: var(--color-text-sub); font-size: 14px; }

.qna-item { border-bottom: 1px solid var(--color-border); }
.qna-item__header { display: flex; align-items: center; gap: 16px; padding: 20px 0; cursor: pointer; }
.qna-status { font-size: 11px; letter-spacing: 1px; padding: 3px 10px; flex-shrink: 0; }
.qna-status.answered { background-color: #edf2e8; color: var(--color-olive); }
.qna-status.pending { background-color: #f5f0e8; color: #8b7355; }
.qna-title { flex: 1; font-size: 14px; color: var(--color-text); }
.qna-date { font-size: 12px; color: var(--color-text-sub); flex-shrink: 0; }
.qna-toggle { font-size: 12px; color: var(--color-text-sub); flex-shrink: 0; }

.qna-item__body { padding: 20px; background-color: var(--color-border-light); margin-bottom: 0; }
.qna-content { font-size: 14px; color: var(--color-text); line-height: 1.7; margin-bottom: 16px; }
.qna-answer { border-top: 1px solid var(--color-border); padding-top: 16px; }
.answer-label { display: inline-block; font-size: 11px; letter-spacing: 2px; color: var(--color-gold); margin-bottom: 8px; }
.qna-answer p { font-size: 14px; color: var(--color-text); line-height: 1.7; }
.qna-actions { display: flex; gap: 8px; margin-bottom: 12px; }
.btn-small { padding: 6px 12px; border: 1px solid var(--color-border); background: transparent; cursor: pointer; }
.btn-danger { color: #8a2c2c; border-color: #8a2c2c; }
.qna-edit { display: grid; gap: 10px; margin-bottom: 12px; }


/* portfolio qna mobile polish */
@media (max-width: 768px) {
  .qna-page {
    padding: 40px 0 72px;
  }

  .page-header {
    margin-bottom: 40px;
  }

  .qna-form-box {
    padding: 24px 20px;
    margin-bottom: 44px;
  }

  .qna-item__header {
    align-items: flex-start;
    flex-wrap: wrap;
    gap: 10px;
  }

  .qna-title {
    flex-basis: 100%;
    order: 3;
  }

  .qna-date {
    margin-left: auto;
  }

  .qna-item__body {
    padding: 18px;
  }

  .qna-actions {
    flex-wrap: wrap;
  }
}

</style>
