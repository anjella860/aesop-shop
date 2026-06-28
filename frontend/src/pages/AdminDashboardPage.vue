<template>
  <main class="admin-page">
    <section class="admin-hero">
      <div>
        <p class="eyebrow">Admin</p>
        <h1>관리자 대시보드</h1>
        <p class="hero-copy">상품, 주문, 회원, 공지사항을 한 화면에서 관리합니다.</p>
      </div>
      <button class="refresh-btn" type="button" @click="loadDashboard" :disabled="loading">
        {{ loading ? "불러오는 중" : "새로고침" }}
      </button>
    </section>

    <p v-if="errorMessage" class="alert">{{ errorMessage }}</p>

    <section class="summary-grid">
      <article class="summary-card">
        <span>상품</span>
        <strong>{{ products.length }}</strong>
      </article>
      <article class="summary-card">
        <span>주문</span>
        <strong>{{ orders.length }}</strong>
      </article>
      <article class="summary-card">
        <span>회원</span>
        <strong>{{ members.length }}</strong>
      </article>
      <article class="summary-card">
        <span>재고 부족</span>
        <strong>{{ lowStockProducts.length }}</strong>
      </article>
    </section>

    <section class="admin-section product-editor">
      <div class="section-heading">
        <div>
          <p class="eyebrow">Products</p>
          <h2>상품 관리</h2>
        </div>
        <button class="ghost-btn" type="button" @click="resetProductForm">새 상품</button>
      </div>

      <form class="product-form" @submit.prevent="saveProduct">
        <label>
          상품명
          <input v-model.trim="productForm.name" required />
        </label>
        <label>
          카테고리
          <select v-model.number="productForm.categoryId" required>
            <option v-for="category in childCategories" :key="category.id" :value="category.id">
              {{ category.name }}
            </option>
          </select>
        </label>
        <label>
          용량
          <input v-model.trim="productForm.volume" placeholder="100ml" />
        </label>
        <label>
          가격
          <input v-model.number="productForm.price" type="number" min="0" required />
        </label>
        <label>
          재고
          <input v-model.number="productForm.stock" type="number" min="0" required />
        </label>
        <label>
          피부 타입
          <input v-model.trim="productForm.skinType" />
        </label>
        <label class="wide">
          주요 성분
          <input v-model.trim="productForm.ingredients" />
        </label>
        <label class="wide">
          설명
          <textarea v-model.trim="productForm.description" rows="3"></textarea>
        </label>
        <label class="check-row">
          <input v-model="productForm.isBestseller" type="checkbox" />
          베스트셀러
        </label>
        <div class="form-actions">
          <button class="primary-btn" type="submit">{{ productForm.id ? "상품 수정" : "상품 등록" }}</button>
        </div>
      </form>

      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>상품</th>
              <th>카테고리</th>
              <th>가격</th>
              <th>재고</th>
              <th>관리</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="product in products" :key="product.id">
              <td>
                <div class="product-cell">
                  <img :src="product.imageUrl || fallbackImg" :alt="product.name" @error="onImgError" />
                  <span>{{ product.name }}</span>
                </div>
              </td>
              <td>{{ categoryName(product.categoryId) }}</td>
              <td>{{ formatPrice(product.price) }}</td>
              <td :class="{ danger: product.stock <= 10 }">{{ product.stock }}</td>
              <td class="actions">
                <button type="button" @click="editProduct(product)">수정</button>
                <button type="button" @click="removeProduct(product.id)">삭제</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <section class="admin-grid">
      <article class="admin-section">
        <div class="section-heading">
          <div>
            <p class="eyebrow">Orders</p>
            <h2>주문 관리</h2>
          </div>
        </div>
        <div class="table-wrap compact">
          <table>
            <thead>
              <tr>
                <th>주문</th>
                <th>수령인</th>
                <th>금액</th>
                <th>상태</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in orders" :key="order.id">
                <td>#{{ order.id }}</td>
                <td>{{ order.receiverName }}</td>
                <td>{{ formatPrice(order.totalPrice) }}</td>
                <td>
                  <select :value="order.status" @change="changeOrderStatus(order.id, $event.target.value)">
                    <option v-for="status in orderStatuses" :key="status" :value="status">{{ status }}</option>
                  </select>
                </td>
              </tr>
              <tr v-if="orders.length === 0">
                <td colspan="4" class="empty">주문이 없습니다.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </article>

      <article class="admin-section">
        <div class="section-heading">
          <div>
            <p class="eyebrow">Members</p>
            <h2>회원 관리</h2>
          </div>
        </div>
        <div class="member-list">
          <div class="member-row" v-for="member in members" :key="member.id">
            <div>
              <strong>{{ member.name }}</strong>
              <span>{{ member.email }}</span>
            </div>
            <select :value="member.role" @change="changeMemberRole(member.id, $event.target.value)">
              <option value="USER">USER</option>
              <option value="ADMIN">ADMIN</option>
            </select>
            <button type="button" @click="removeMember(member.id)">삭제</button>
          </div>
          <p v-if="members.length === 0" class="empty">회원이 없습니다.</p>
        </div>
      </article>
    </section>

    <section class="admin-section">
      <div class="section-heading">
        <div>
          <p class="eyebrow">Notice</p>
          <h2>공지사항 관리</h2>
        </div>
        <button class="ghost-btn" type="button" @click="resetNoticeForm">새 공지</button>
      </div>
      <form class="notice-form" @submit.prevent="saveNotice">
        <input v-model.trim="noticeForm.title" placeholder="제목" required />
        <input v-model.trim="noticeForm.author" placeholder="작성자" required />
        <textarea v-model.trim="noticeForm.content" placeholder="내용" rows="4" required></textarea>
        <button class="primary-btn" type="submit">{{ noticeForm.id ? "공지 수정" : "공지 등록" }}</button>
      </form>
      <div class="notice-list">
        <div class="notice-row" v-for="notice in notices" :key="notice.id">
          <div>
            <strong>{{ notice.title }}</strong>
            <span>{{ notice.author }} · 조회 {{ notice.hits }}</span>
          </div>
          <div class="actions">
            <button type="button" @click="editNotice(notice)">수정</button>
            <button type="button" @click="removeNotice(notice.id)">삭제</button>
          </div>
        </div>
        <p v-if="notices.length === 0" class="empty">공지사항이 없습니다.</p>
      </div>
    </section>
  </main>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { adminAPI, categoryAPI, noticeAPI, productAPI } from "../api/index.js";

const fallbackImg = "https://via.placeholder.com/120x150/3D4A2E/FAFAF7?text=AESOP";
const loading = ref(false);
const errorMessage = ref("");
const products = ref([]);
const categories = ref([]);
const members = ref([]);
const orders = ref([]);
const notices = ref([]);
const orderStatuses = ["PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"];

const productForm = reactive({
  id: null,
  categoryId: null,
  name: "",
  description: "",
  volume: "",
  skinType: "",
  ingredients: "",
  price: 0,
  stock: 0,
  isBestseller: false,
});

const noticeForm = reactive({
  id: null,
  title: "",
  content: "",
  author: "관리자",
});

const childCategories = computed(() => categories.value.filter((category) => category.parentId));
const lowStockProducts = computed(() => products.value.filter((product) => product.stock <= 10));

const formatPrice = (value) => value ? `${Number(value).toLocaleString()}원` : "0원";
const categoryName = (id) => categories.value.find((category) => category.id === id)?.name || `#${id}`;

const onImgError = (event) => {
  event.target.src = fallbackImg;
};

const showError = (message, error) => {
  console.error(message, error);
  errorMessage.value = message;
};

const productPayload = () => ({
  categoryId: productForm.categoryId,
  name: productForm.name,
  description: productForm.description,
  volume: productForm.volume,
  skinType: productForm.skinType,
  ingredients: productForm.ingredients,
  price: Number(productForm.price),
  stock: Number(productForm.stock),
  isBestseller: productForm.isBestseller,
});

const toProductFormData = () => {
  const formData = new FormData();
  formData.append("product", new Blob([JSON.stringify(productPayload())], { type: "application/json" }));
  return formData;
};

const resetProductForm = () => {
  Object.assign(productForm, {
    id: null,
    categoryId: childCategories.value[0]?.id || null,
    name: "",
    description: "",
    volume: "",
    skinType: "",
    ingredients: "",
    price: 0,
    stock: 0,
    isBestseller: false,
  });
};

const resetNoticeForm = () => {
  Object.assign(noticeForm, {
    id: null,
    title: "",
    content: "",
    author: "관리자",
  });
};

const loadDashboard = async () => {
  loading.value = true;
  errorMessage.value = "";
  try {
    const [productRes, categoryRes, memberRes, orderRes, noticeRes] = await Promise.all([
      productAPI.getAll(),
      categoryAPI.getAll(),
      adminAPI.getMembers(),
      adminAPI.getOrders(),
      noticeAPI.getAll(),
    ]);
    products.value = productRes.data;
    categories.value = categoryRes.data;
    members.value = memberRes.data;
    orders.value = orderRes.data;
    notices.value = noticeRes.data;
    if (!productForm.categoryId) resetProductForm();
  } catch (error) {
    showError("관리자 권한이 필요하거나 서버 응답을 불러오지 못했습니다.", error);
  } finally {
    loading.value = false;
  }
};

const saveProduct = async () => {
  try {
    if (productForm.id) {
      await adminAPI.updateProduct(productForm.id, toProductFormData());
    } else {
      await adminAPI.addProduct(toProductFormData());
    }
    await loadDashboard();
    resetProductForm();
  } catch (error) {
    showError("상품 저장에 실패했습니다.", error);
  }
};

const editProduct = (product) => {
  Object.assign(productForm, {
    id: product.id,
    categoryId: product.categoryId,
    name: product.name,
    description: product.description || "",
    volume: product.volume || "",
    skinType: product.skinType || "",
    ingredients: product.ingredients || "",
    price: product.price || 0,
    stock: product.stock || 0,
    isBestseller: Boolean(product.isBestseller),
  });
};

const removeProduct = async (id) => {
  if (!window.confirm("상품을 삭제할까요?")) return;
  try {
    await adminAPI.deleteProduct(id);
    await loadDashboard();
  } catch (error) {
    showError("상품 삭제에 실패했습니다.", error);
  }
};

const changeOrderStatus = async (id, status) => {
  try {
    await adminAPI.changeOrderStatus(id, status);
    await loadDashboard();
  } catch (error) {
    showError("주문 상태 변경에 실패했습니다.", error);
  }
};

const changeMemberRole = async (id, role) => {
  try {
    await adminAPI.changeMemberRole(id, role);
    await loadDashboard();
  } catch (error) {
    showError("회원 등급 변경에 실패했습니다.", error);
  }
};

const removeMember = async (id) => {
  if (!window.confirm("회원을 삭제할까요?")) return;
  try {
    await adminAPI.deleteMember(id);
    await loadDashboard();
  } catch (error) {
    showError("회원 삭제에 실패했습니다.", error);
  }
};

const saveNotice = async () => {
  try {
    const payload = {
      title: noticeForm.title,
      content: noticeForm.content,
      author: noticeForm.author,
    };
    if (noticeForm.id) {
      await adminAPI.updateNotice(noticeForm.id, payload);
    } else {
      await adminAPI.addNotice(payload);
    }
    await loadDashboard();
    resetNoticeForm();
  } catch (error) {
    showError("공지사항 저장에 실패했습니다.", error);
  }
};

const editNotice = (notice) => {
  Object.assign(noticeForm, {
    id: notice.id,
    title: notice.title,
    content: notice.content,
    author: notice.author || "관리자",
  });
};

const removeNotice = async (id) => {
  if (!window.confirm("공지사항을 삭제할까요?")) return;
  try {
    await adminAPI.deleteNotice(id);
    await loadDashboard();
  } catch (error) {
    showError("공지사항 삭제에 실패했습니다.", error);
  }
};

onMounted(loadDashboard);
</script>

<style scoped>
.admin-page {
  background: var(--color-bg);
  color: var(--color-text);
  min-height: 100vh;
  padding: calc(var(--header-height) + 40px) 40px 80px;
}

.admin-hero,
.admin-section,
.summary-card {
  border: 1px solid var(--color-border);
  background: #fffef9;
}

.admin-hero {
  max-width: 1280px;
  margin: 0 auto 24px;
  padding: 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}

.eyebrow {
  color: var(--color-olive);
  font-family: var(--font-en);
  font-size: 12px;
  margin-bottom: 8px;
  text-transform: uppercase;
}

h1,
h2 {
  font-weight: 400;
  letter-spacing: 0;
}

h1 {
  font-size: 32px;
  margin-bottom: 10px;
}

h2 {
  font-size: 22px;
}

.hero-copy,
.empty {
  color: var(--color-text-sub);
  font-size: 14px;
}

.summary-grid {
  max-width: 1280px;
  margin: 0 auto 24px;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.summary-card {
  padding: 22px;
}

.summary-card span {
  color: var(--color-text-sub);
  display: block;
  font-size: 13px;
  margin-bottom: 10px;
}

.summary-card strong {
  color: var(--color-olive);
  font-family: var(--font-en);
  font-size: 30px;
  font-weight: 400;
}

.admin-section {
  max-width: 1280px;
  margin: 0 auto 24px;
  padding: 28px;
}

.admin-grid {
  max-width: 1280px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1.25fr 0.75fr;
  gap: 24px;
}

.admin-grid .admin-section {
  margin: 0 0 24px;
}

.section-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 22px;
}

.product-form,
.notice-form {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 28px;
}

label {
  color: var(--color-text-sub);
  display: flex;
  flex-direction: column;
  font-size: 12px;
  gap: 8px;
}

input,
select,
textarea {
  border: 1px solid var(--color-border);
  background: var(--color-bg);
  color: var(--color-text);
  font: inherit;
  min-height: 42px;
  padding: 10px 12px;
  width: 100%;
}

textarea {
  resize: vertical;
}

.wide,
.notice-form textarea {
  grid-column: span 2;
}

.check-row {
  align-items: center;
  color: var(--color-text);
  flex-direction: row;
  gap: 10px;
}

.check-row input {
  min-height: auto;
  width: auto;
}

.form-actions {
  align-items: end;
  display: flex;
}

button {
  border: 1px solid var(--color-olive);
  color: var(--color-olive);
  cursor: pointer;
  min-height: 38px;
  padding: 0 14px;
  transition: all 0.2s ease;
}

button:hover:not(:disabled) {
  background: var(--color-olive);
  color: var(--color-bg);
}

button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.primary-btn,
.refresh-btn {
  background: var(--color-olive);
  color: var(--color-bg);
}

.ghost-btn {
  background: transparent;
}

.table-wrap {
  overflow-x: auto;
}

table {
  border-collapse: collapse;
  min-width: 720px;
  width: 100%;
}

.compact table {
  min-width: 520px;
}

th,
td {
  border-bottom: 1px solid var(--color-border-light);
  font-size: 13px;
  padding: 14px 10px;
  text-align: left;
  vertical-align: middle;
}

th {
  color: var(--color-text-sub);
  font-weight: 400;
}

.product-cell {
  align-items: center;
  display: flex;
  gap: 12px;
}

.product-cell img {
  aspect-ratio: 1;
  background: var(--color-border-light);
  height: 48px;
  object-fit: cover;
  width: 48px;
}

.danger {
  color: #a44d3f;
}

.actions {
  display: flex;
  gap: 8px;
}

.member-list,
.notice-list {
  display: grid;
  gap: 10px;
}

.member-row,
.notice-row {
  align-items: center;
  border-bottom: 1px solid var(--color-border-light);
  display: grid;
  gap: 12px;
  padding: 12px 0;
}

.member-row {
  grid-template-columns: minmax(0, 1fr) 120px auto;
}

.notice-row {
  grid-template-columns: minmax(0, 1fr) auto;
}

.member-row strong,
.notice-row strong {
  display: block;
  font-size: 14px;
  font-weight: 400;
  margin-bottom: 4px;
}

.member-row span,
.notice-row span {
  color: var(--color-text-sub);
  display: block;
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.alert {
  background: #fff4ef;
  border: 1px solid #d99a84;
  color: #8f3f2d;
  margin: 0 auto 24px;
  max-width: 1280px;
  padding: 14px 16px;
}

@media (max-width: 1024px) {
  .admin-page {
    padding-left: 24px;
    padding-right: 24px;
  }

  .summary-grid,
  .admin-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .product-form,
  .notice-form {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .admin-page {
    padding: calc(var(--header-height) + 24px) 16px 56px;
  }

  .admin-hero,
  .section-heading {
    align-items: flex-start;
    flex-direction: column;
  }

  .summary-grid,
  .admin-grid,
  .product-form,
  .notice-form {
    grid-template-columns: 1fr;
  }

  .admin-section,
  .admin-hero {
    padding: 20px;
  }

  .wide,
  .notice-form textarea {
    grid-column: auto;
  }

  .member-row,
  .notice-row {
    grid-template-columns: 1fr;
  }

  h1 {
    font-size: 26px;
  }
}
</style>
