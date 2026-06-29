<template>
  <main class="admin-page">
    <aside class="admin-sidebar" aria-label="관리자 메뉴">
      <div class="sidebar-head">
        <RouterLink to="/" class="sidebar-brand">
          <span>AESOP</span>
          <strong>ADMIN</strong>
        </RouterLink>
        <button
          class="admin-menu-toggle"
          type="button"
          :class="{ open: adminMenuOpen }"
          @click="adminMenuOpen = !adminMenuOpen"
          aria-label="관리자 메뉴 열기"
        >
          <span class="admin-menu-toggle__text">관리 메뉴</span>
          <span class="admin-menu-toggle__lines" aria-hidden="true">
            <span></span>
            <span></span>
            <span></span>
          </span>
        </button>
      </div>

      <nav class="sidebar-nav" :class="{ open: adminMenuOpen }">
        <button
          v-for="tab in adminTabs"
          :key="tab.key"
          class="sidebar-nav__item"
          :class="{ active: activeTab === tab.key }"
          type="button"
          @click="selectAdminTab(tab.key)"
        >
          <span>{{ tab.label }}</span>
          <strong v-if="tab.count !== null">{{ tab.count }}</strong>
        </button>
      </nav>
    </aside>

    <section class="admin-workspace">
      <header class="admin-hero">
        <div>
          <p class="eyebrow">Admin</p>
          <h1>{{ activeTabLabel }}</h1>
          <p class="hero-copy">{{ activeTabDescription }}</p>
        </div>
        <button class="refresh-btn" type="button" @click="loadDashboard" :disabled="loading">
          {{ loading ? "불러오는 중" : "새로고침" }}
        </button>
      </header>

      <p v-if="errorMessage" class="alert">{{ errorMessage }}</p>

      <section v-if="activeTab === 'overview'" class="tab-panel">
        <div class="summary-grid">
          <button class="summary-card" type="button" @click="activeTab = 'products'">
            <span>상품</span>
            <strong>{{ products.length }}</strong>
          </button>
          <button class="summary-card" type="button" @click="activeTab = 'orders'">
            <span>주문</span>
            <strong>{{ orders.length }}</strong>
          </button>
          <button class="summary-card" type="button" @click="activeTab = 'members'">
            <span>회원</span>
            <strong>{{ members.length }}</strong>
          </button>
          <button class="summary-card" type="button" @click="activeTab = 'products'">
            <span>재고 부족</span>
            <strong>{{ lowStockProducts.length }}</strong>
          </button>
          <button class="summary-card" type="button" @click="activeTab = 'qna'">
            <span>미답변 QnA</span>
            <strong>{{ unansweredQnas.length }}</strong>
          </button>
        </div>

        <div class="overview-grid">
          <section class="admin-section">
            <div class="section-heading">
              <div>
                <p class="eyebrow">Inventory</p>
                <h2>재고 확인</h2>
              </div>
              <button class="ghost-btn" type="button" @click="activeTab = 'products'">상품 관리</button>
            </div>
            <div class="list-stack">
              <div class="list-row" v-for="product in lowStockProducts.slice(0, 5)" :key="product.id">
                <div>
                  <strong>{{ product.name }}</strong>
                  <span>{{ categoryName(product.categoryId) }}</span>
                </div>
                <em>{{ product.stock }}개</em>
              </div>
              <p v-if="lowStockProducts.length === 0" class="empty">재고 부족 상품이 없습니다.</p>
            </div>
          </section>

          <section class="admin-section">
            <div class="section-heading">
              <div>
                <p class="eyebrow">QnA</p>
                <h2>미답변 문의</h2>
              </div>
              <button class="ghost-btn" type="button" @click="activeTab = 'qna'">답변 관리</button>
            </div>
            <div class="list-stack">
              <div class="list-row" v-for="qna in unansweredQnas.slice(0, 5)" :key="qna.id">
                <div>
                  <strong>{{ qna.title }}</strong>
                  <span>{{ productName(qna.productId) }}</span>
                </div>
                <em>{{ formatDate(qna.createdAt) }}</em>
              </div>
              <p v-if="unansweredQnas.length === 0" class="empty">미답변 문의가 없습니다.</p>
            </div>
          </section>
        </div>
      </section>

      <section v-if="activeTab === 'products'" class="tab-panel admin-section">
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

      <section v-if="activeTab === 'orders'" class="tab-panel admin-section">
        <div class="section-heading">
          <div>
            <p class="eyebrow">Orders</p>
            <h2>주문 관리</h2>
          </div>
        </div>
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>주문</th>
                <th>수령인</th>
                <th>연락처</th>
                <th>금액</th>
                <th>상태</th>
                <th>주문일</th>
                <th>배송</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in orders" :key="order.id">
                <td>#{{ order.id }}</td>
                <td>{{ order.receiverName }}</td>
                <td>{{ order.receiverPhone }}</td>
                <td>{{ formatPrice(order.totalPrice) }}</td>
                <td>
                  <select :value="order.status" @change="changeOrderStatus(order.id, $event.target.value)">
                    <option v-for="status in orderStatuses" :key="status" :value="status">{{ orderStatusLabel(status) }}</option>
                  </select>
                </td>
                <td>{{ formatDate(order.orderedAt) }}</td>
                <td>
                  <div class="delivery-cell">
                    <span>{{ order.deliveryCompany || "미등록" }}</span>
                    <small>{{ order.trackingNumber || "-" }}</small>
                    <button type="button" @click="editDelivery(order)">배송등록</button>
                  </div>
                </td>
              </tr>
              <tr v-if="orders.length === 0">
                <td colspan="7" class="empty">주문이 없습니다.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <section v-if="activeTab === 'members'" class="tab-panel admin-section">
        <div class="section-heading">
          <div>
            <p class="eyebrow">Members</p>
            <h2>회원 관리</h2>
          </div>
        </div>
        <form class="search-row" @submit.prevent="searchMembers">
          <input v-model.trim="memberSearch" placeholder="이메일로 회원 검색" />
          <button class="ghost-btn" type="submit">검색</button>
          <button class="ghost-btn" type="button" @click="resetMemberSearch">전체</button>
        </form>
        <div class="member-list">
          <div class="member-row" v-for="member in members" :key="member.id">
            <div>
              <strong>{{ member.name }}</strong>
              <span>{{ member.email }}</span>
            </div>
            <span>{{ member.phone || "연락처 없음" }}</span>
            <select :value="member.role" @change="changeMemberRole(member.id, $event.target.value)">
              <option value="USER">일반회원</option>
              <option value="ADMIN">관리자</option>
            </select>
            <button type="button" @click="removeMember(member.id)">삭제</button>
          </div>
          <p v-if="members.length === 0" class="empty">회원이 없습니다.</p>
        </div>
      </section>

      <section v-if="activeTab === 'qna'" class="tab-panel admin-section">
        <div class="section-heading">
          <div>
            <p class="eyebrow">QnA</p>
            <h2>QnA 답변 관리</h2>
          </div>
        </div>
        <div class="qna-list">
          <article class="qna-item" v-for="qna in qnas" :key="qna.id">
            <div class="qna-item__head">
              <span class="status-badge" :class="qna.isAnswered ? 'status--delivered' : 'status--pending'">
                {{ qna.isAnswered ? "답변완료" : "답변대기" }}
              </span>
              <strong>{{ qna.title }}</strong>
              <small>{{ formatDate(qna.createdAt) }}</small>
            </div>
            <p class="qna-meta">상품 {{ productName(qna.productId) }} · 회원 #{{ qna.memberId }}</p>
            <p class="qna-content">{{ qna.content }}</p>
            <div v-if="qna.isAnswered" class="qna-answer">
              <span>답변</span>
              <p>{{ qna.answer }}</p>
              <div class="actions">
                <button type="button" @click="editQnaAnswer(qna)">답변 수정</button>
                <button type="button" @click="deleteQnaAnswer(qna.id)">답변 삭제</button>
              </div>
            </div>
            <form v-else class="qna-answer-form" @submit.prevent="submitQnaAnswer(qna.id)">
              <textarea v-model.trim="answerMap[qna.id]" rows="3" placeholder="답변을 입력하세요" required></textarea>
              <button class="primary-btn" type="submit">답변 등록</button>
            </form>
          </article>
          <p v-if="qnas.length === 0" class="empty">등록된 QnA가 없습니다.</p>
        </div>
      </section>

      <section v-if="activeTab === 'reviews'" class="tab-panel admin-section">
        <div class="section-heading">
          <div>
            <p class="eyebrow">Reviews</p>
            <h2>리뷰 관리</h2>
          </div>
        </div>
        <div class="qna-list">
          <article class="qna-item" v-for="review in reviews" :key="review.id">
            <div class="qna-item__head">
              <span class="status-badge status--delivered">리뷰</span>
              <strong>{{ productName(review.productId) }}</strong>
              <small>{{ formatDate(review.createdAt) }}</small>
            </div>
            <p class="qna-meta">회원 #{{ review.memberId }} · 평점 {{ review.rating }}</p>
            <p class="qna-content">{{ review.content }}</p>
            <div class="actions">
              <button type="button" @click="removeReview(review.id)">리뷰 삭제</button>
            </div>
          </article>
          <p v-if="reviews.length === 0" class="empty">등록된 리뷰가 없습니다.</p>
        </div>
      </section>

      <section v-if="activeTab === 'notices'" class="tab-panel admin-section">
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
    </section>
  </main>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { adminAPI, categoryAPI, noticeAPI, productAPI } from "../api/index.js";

const fallbackImg = "https://via.placeholder.com/120x150/3D4A2E/FAFAF7?text=AESOP";
const activeTab = ref("overview");
const adminMenuOpen = ref(false);
const loading = ref(false);
const errorMessage = ref("");
const products = ref([]);
const categories = ref([]);
const members = ref([]);
const orders = ref([]);
const notices = ref([]);
const qnas = ref([]);
const reviews = ref([]);
const answerMap = reactive({});
const memberSearch = ref("");
const orderStatuses = ["PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"];
const orderStatusLabels = {
  PENDING: "결제 대기",
  CONFIRMED: "주문 확인",
  SHIPPED: "배송 중",
  DELIVERED: "배송 완료",
  CANCELLED: "취소됨",
};
const orderStatusLabel = (status) => orderStatusLabels[status] || status;

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
const unansweredQnas = computed(() => qnas.value.filter((qna) => !qna.isAnswered));

const adminTabs = computed(() => [
  { key: "overview", label: "대시보드", description: "운영 현황을 빠르게 확인합니다.", count: null },
  { key: "products", label: "상품 관리", description: "상품 정보와 재고를 등록하고 수정합니다.", count: products.value.length },
  { key: "orders", label: "주문 관리", description: "주문 상태와 수령 정보를 관리합니다.", count: orders.value.length },
  { key: "members", label: "회원 관리", description: "회원 권한과 계정을 관리합니다.", count: members.value.length },
  { key: "qna", label: "QnA 답변", description: "고객 문의를 확인하고 답변합니다.", count: unansweredQnas.value.length },
  { key: "reviews", label: "리뷰 관리", description: "구매 리뷰를 확인하고 부적절한 리뷰를 삭제합니다.", count: reviews.value.length },
  { key: "notices", label: "공지사항", description: "쇼핑몰 공지사항을 작성하고 수정합니다.", count: notices.value.length },
]);

const activeTabInfo = computed(() => adminTabs.value.find((tab) => tab.key === activeTab.value) || adminTabs.value[0]);
const activeTabLabel = computed(() => activeTabInfo.value.label);
const activeTabDescription = computed(() => activeTabInfo.value.description);

const selectAdminTab = (key) => {
  activeTab.value = key;
  adminMenuOpen.value = false;
};

const formatPrice = (value) => value ? `${Number(value).toLocaleString()}원` : "0원";
const categoryName = (id) => categories.value.find((category) => category.id === id)?.name || `#${id}`;
const productName = (id) => products.value.find((product) => product.id === id)?.name || `#${id}`;
const formatDate = (value) => value ? new Date(value).toLocaleDateString("ko-KR") : "";

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
    const [productRes, categoryRes, memberRes, orderRes, noticeRes, qnaRes, reviewRes] = await Promise.all([
      productAPI.getAll(),
      categoryAPI.getAll(),
      adminAPI.getMembers(),
      adminAPI.getOrders(),
      noticeAPI.getAll(),
      adminAPI.getQna(),
      adminAPI.getReviews(),
    ]);
    products.value = productRes.data;
    categories.value = categoryRes.data;
    members.value = memberRes.data;
    orders.value = orderRes.data;
    notices.value = noticeRes.data;
    qnas.value = qnaRes.data;
    reviews.value = reviewRes.data;
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

const searchMembers = async () => {
  try {
    if (!memberSearch.value) {
      await loadDashboard();
      return;
    }
    const response = await adminAPI.searchMembers(memberSearch.value);
    members.value = response.data;
  } catch (error) {
    showError("회원 검색에 실패했습니다.", error);
  }
};

const resetMemberSearch = async () => {
  memberSearch.value = "";
  await loadDashboard();
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

const editDelivery = async (order) => {
  const deliveryCompany = window.prompt("택배사를 입력하세요.", order.deliveryCompany || "");
  if (deliveryCompany === null) return;
  const trackingNumber = window.prompt("송장번호를 입력하세요.", order.trackingNumber || "");
  if (trackingNumber === null) return;
  try {
    await adminAPI.updateDelivery(order.id, { deliveryCompany, trackingNumber });
    await loadDashboard();
  } catch (error) {
    showError("배송 정보 저장에 실패했습니다.", error);
  }
};

const submitQnaAnswer = async (id) => {
  const answer = answerMap[id];
  if (!answer) return;
  try {
    await adminAPI.answerQna(id, answer);
    answerMap[id] = "";
    await loadDashboard();
  } catch (error) {
    showError("QnA 답변 등록에 실패했습니다.", error);
  }
};

const editQnaAnswer = async (qna) => {
  const answer = window.prompt("답변을 수정하세요.", qna.answer || "");
  if (answer === null || !answer.trim()) return;
  try {
    await adminAPI.answerQna(qna.id, answer);
    await loadDashboard();
  } catch (error) {
    showError("QnA 답변 수정에 실패했습니다.", error);
  }
};

const deleteQnaAnswer = async (id) => {
  if (!window.confirm("답변을 삭제할까요?")) return;
  try {
    await adminAPI.deleteQnaAnswer(id);
    await loadDashboard();
  } catch (error) {
    showError("QnA 답변 삭제에 실패했습니다.", error);
  }
};

const removeReview = async (id) => {
  if (!window.confirm("리뷰를 삭제할까요?")) return;
  try {
    await adminAPI.deleteReview(id);
    await loadDashboard();
  } catch (error) {
    showError("리뷰 삭제에 실패했습니다.", error);
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
  activeTab.value = "notices";
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
  background: #f6f5ef;
  color: var(--color-text);
  display: grid;
  grid-template-columns: 240px minmax(0, 1fr);
  min-height: calc(100vh - var(--header-height));
}

.admin-sidebar {
  background: var(--color-olive);
  color: var(--color-bg);
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - var(--header-height));
  padding: 28px 0;
  position: sticky;
  top: var(--header-height);
}

.sidebar-brand {
  border-bottom: 1px solid rgba(250, 250, 247, 0.14);
  color: var(--color-bg);
  display: grid;
  gap: 8px;
  padding: 0 26px 28px;
}

.sidebar-brand span {
  font-family: var(--font-en);
  font-size: 22px;
  letter-spacing: 6px;
}

.sidebar-brand strong {
  color: var(--color-gold-light);
  font-family: var(--font-en);
  font-size: 11px;
  font-weight: 400;
  letter-spacing: 3px;
}

.sidebar-nav {
  display: grid;
  gap: 4px;
  padding: 18px 12px;
}

.sidebar-nav__item {
  align-items: center;
  background: transparent;
  border: 0;
  color: rgba(250, 250, 247, 0.72);
  display: flex;
  justify-content: space-between;
  min-height: 44px;
  padding: 0 14px;
  text-align: left;
}

.sidebar-nav__item:hover,
.sidebar-nav__item.active {
  background: rgba(250, 250, 247, 0.12);
  color: var(--color-bg);
}

.sidebar-nav__item.active {
  border-left: 2px solid var(--color-gold-light);
}

.sidebar-nav__item strong {
  color: var(--color-gold-light);
  font-family: var(--font-en);
  font-size: 12px;
  font-weight: 400;
}

.admin-workspace {
  min-width: 0;
  padding: 32px;
}

.admin-hero,
.admin-section,
.summary-card {
  background: #fffef9;
  border: 1px solid var(--color-border);
}

.admin-hero {
  align-items: center;
  display: flex;
  gap: 24px;
  justify-content: space-between;
  margin-bottom: 24px;
  padding: 28px;
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
  font-size: 30px;
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

.tab-panel {
  animation: fadeIn 0.16s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(4px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.summary-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  margin-bottom: 24px;
}

.summary-card {
  cursor: pointer;
  display: block;
  min-height: 112px;
  padding: 22px;
  text-align: left;
}

.summary-card:hover {
  border-color: var(--color-olive);
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

.overview-grid {
  display: grid;
  gap: 24px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.admin-section {
  padding: 28px;
}

.section-heading {
  align-items: center;
  display: flex;
  gap: 20px;
  justify-content: space-between;
  margin-bottom: 22px;
}

.search-row {
  display: grid;
  gap: 10px;
  grid-template-columns: minmax(0, 1fr) auto auto;
  margin-bottom: 18px;
}

.delivery-cell {
  display: grid;
  gap: 4px;
}

.delivery-cell small {
  color: var(--color-text-sub);
}

.delivery-cell button {
  justify-self: start;
  min-height: 30px;
}

.product-form,
.notice-form {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(3, minmax(0, 1fr));
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
  background: var(--color-bg);
  border: 1px solid var(--color-border);
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

.list-stack,
.member-list,
.notice-list,
.qna-list {
  display: grid;
  gap: 10px;
}

.list-row,
.member-row,
.notice-row,
.qna-item {
  border-bottom: 1px solid var(--color-border-light);
  padding: 14px 0;
}

.list-row,
.member-row,
.notice-row {
  align-items: center;
  display: grid;
  gap: 12px;
}

.list-row {
  grid-template-columns: minmax(0, 1fr) auto;
}

.member-row {
  grid-template-columns: minmax(0, 1fr) 160px 120px auto;
}

.notice-row {
  grid-template-columns: minmax(0, 1fr) auto;
}

.list-row strong,
.member-row strong,
.notice-row strong {
  display: block;
  font-size: 14px;
  font-weight: 400;
  margin-bottom: 4px;
}

.list-row span,
.member-row span,
.notice-row span {
  color: var(--color-text-sub);
  display: block;
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.list-row em {
  color: var(--color-olive);
  font-style: normal;
}

.qna-item__head {
  align-items: center;
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
}

.qna-item__head strong {
  font-size: 15px;
  font-weight: 400;
}

.qna-item__head small,
.qna-meta {
  color: var(--color-text-sub);
  font-size: 12px;
}

.qna-content {
  font-size: 14px;
  line-height: 1.7;
  margin: 12px 0;
}

.qna-answer {
  background: var(--color-bg);
  border: 1px solid var(--color-border-light);
  padding: 14px;
}

.qna-answer span {
  color: var(--color-gold);
  display: block;
  font-size: 11px;
  letter-spacing: 1px;
  margin-bottom: 6px;
}

.qna-answer p {
  font-size: 13px;
  line-height: 1.7;
}

.qna-answer-form {
  display: grid;
  gap: 10px;
}

.qna-answer-form button {
  justify-self: start;
}

.status-badge {
  display: inline-flex;
  font-size: 11px;
  padding: 4px 10px;
}

.status--pending {
  background: #f5f0e8;
  color: #8b7355;
}

.status--delivered {
  background: #e8f5ec;
  color: #2d7a4f;
}

.alert {
  background: #fff4ef;
  border: 1px solid #d99a84;
  color: #8f3f2d;
  margin-bottom: 24px;
  padding: 14px 16px;
}

@media (max-width: 1180px) {
  .summary-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .overview-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .admin-page {
    grid-template-columns: 1fr;
  }

  .admin-sidebar {
    min-height: auto;
    padding: 18px 0 12px;
    position: static;
  }

  .sidebar-brand {
    padding: 0 20px 18px;
  }

  .sidebar-nav {
    display: flex;
    overflow-x: auto;
    padding: 12px 14px 4px;
  }

  .sidebar-nav__item {
    flex: 0 0 auto;
    min-width: 132px;
  }

  .sidebar-nav__item.active {
    border-left: 0;
    border-bottom: 2px solid var(--color-gold-light);
  }

  .admin-workspace {
    padding: 24px;
  }

  .product-form,
  .notice-form {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .admin-workspace {
    padding: 16px;
  }

  .admin-hero,
  .section-heading {
    align-items: flex-start;
    flex-direction: column;
  }

  .summary-grid,
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
  .notice-row,
  .qna-item__head {
    align-items: flex-start;
    grid-template-columns: 1fr;
  }

  .qna-item__head {
    flex-direction: column;
  }

  h1 {
    font-size: 26px;
  }
}


/* portfolio admin responsive polish */
@media (max-width: 900px) {
  .admin-page {
    min-height: 100vh;
  }

  .admin-sidebar {
    top: var(--header-height);
    z-index: 2;
  }

  .admin-workspace {
    min-width: 0;
  }

  .admin-hero {
    gap: 18px;
  }

  .refresh-btn,
  .ghost-btn,
  .primary-btn {
    min-height: 42px;
  }

  .table-wrap {
    margin: 0 -24px;
    padding: 0 24px 10px;
    -webkit-overflow-scrolling: touch;
  }
}

@media (max-width: 640px) {
  .admin-sidebar {
    padding-top: 14px;
  }

  .sidebar-brand {
    align-items: flex-start;
    flex-direction: column;
    gap: 2px;
  }

  .sidebar-nav__item {
    min-width: 118px;
    padding: 12px 14px;
  }

  .admin-hero h1 {
    font-size: 26px;
    line-height: 1.3;
  }

  .hero-copy {
    font-size: 13px;
  }

  .summary-card {
    min-height: 94px;
  }

  .table-wrap {
    margin: 0 -20px;
    padding: 0 20px 10px;
  }

  table {
    min-width: 640px;
  }

  .product-form label,
  .notice-form input,
  .notice-form textarea,
  .search-row input,
  .qna-answer-form textarea {
    min-width: 0;
  }

  .search-row {
    grid-template-columns: 1fr;
  }

  .search-row .ghost-btn,
  .form-actions .primary-btn,
  .notice-form .primary-btn {
    width: 100%;
  }

  .member-row,
  .notice-row {
    gap: 14px;
  }

  .member-row select,
  .member-row button,
  .actions button,
  .delivery-cell button {
    width: 100%;
  }
}



/* portfolio admin mobile clipping fix */
@media (max-width: 900px) {
  .admin-page {
    width: 100%;
    max-width: 100%;
    overflow-x: hidden;
  }

  .admin-sidebar {
    width: 100%;
    max-width: 100vw;
    overflow: hidden;
  }

  .sidebar-brand {
    width: 100%;
  }

  .sidebar-nav {
    box-sizing: border-box;
    display: flex;
    gap: 8px;
    max-width: 100%;
    overflow-x: auto;
    overflow-y: hidden;
    padding: 12px 16px 8px;
    scroll-snap-type: x proximity;
    scrollbar-width: thin;
    -webkit-overflow-scrolling: touch;
  }

  .sidebar-nav__item {
    flex: 0 0 auto;
    min-width: 112px;
    scroll-snap-align: start;
  }

  .admin-workspace {
    width: 100%;
    max-width: 100vw;
    overflow: hidden;
  }
}

@media (max-width: 420px) {
  .sidebar-nav {
    padding-left: 12px;
    padding-right: 12px;
  }

  .sidebar-nav__item {
    min-width: 102px;
    padding: 0 12px;
  }

  .sidebar-nav__item strong {
    margin-left: 10px;
  }
}


/* portfolio admin mobile hamburger menu */
.sidebar-head {
  display: block;
}

.admin-menu-toggle {
  display: none;
}

@media (max-width: 900px) {
  .admin-sidebar {
    padding: 0;
  }

  .sidebar-head {
    align-items: center;
    border-bottom: 1px solid rgba(250, 250, 247, 0.14);
    display: flex;
    justify-content: space-between;
    padding: 18px 20px;
  }

  .sidebar-brand {
    border-bottom: 0;
    padding: 0;
  }

  .admin-menu-toggle {
    align-items: center;
    background: transparent;
    border: 1px solid rgba(250, 250, 247, 0.28);
    display: inline-flex;
    flex-direction: column;
    gap: 5px;
    height: 42px;
    justify-content: center;
    min-height: 42px;
    padding: 0;
    width: 46px;
  }

  .admin-menu-toggle span {
    background: var(--color-bg);
    display: block;
    height: 1px;
    transition: transform 0.2s ease, opacity 0.2s ease;
    width: 20px;
  }

  .admin-menu-toggle.open span:nth-child(1) {
    transform: translateY(6px) rotate(45deg);
  }

  .admin-menu-toggle.open span:nth-child(2) {
    opacity: 0;
  }

  .admin-menu-toggle.open span:nth-child(3) {
    transform: translateY(-6px) rotate(-45deg);
  }

  .sidebar-nav {
    box-sizing: border-box;
    display: grid;
    gap: 8px;
    max-height: 0;
    max-width: 100%;
    overflow: hidden;
    padding: 0 16px;
    scroll-snap-type: none;
    transition: max-height 0.24s ease, padding 0.24s ease;
  }

  .sidebar-nav.open {
    max-height: 560px;
    padding: 14px 16px 16px;
  }

  .sidebar-nav__item {
    flex: initial;
    min-width: 0;
    scroll-snap-align: none;
    width: 100%;
  }
}


/* keep dashboard tab visible on mobile while other admin tabs stay in hamburger */
@media (max-width: 900px) {
  .sidebar-nav {
    max-height: 72px;
    padding: 12px 16px 14px;
  }

  .sidebar-nav:not(.open) .sidebar-nav__item:not(:first-child) {
    display: none;
  }

  .sidebar-nav.open .sidebar-nav__item {
    display: flex;
  }
}


/* portfolio admin visible mobile menu trigger */
@media (max-width: 900px) {
  .admin-menu-toggle {
    align-items: center;
    border-color: rgba(250, 250, 247, 0.38);
    color: var(--color-bg);
    flex-direction: row;
    gap: 10px;
    height: 44px;
    padding: 0 12px;
    width: auto;
  }

  .admin-menu-toggle:hover {
    background: rgba(250, 250, 247, 0.12);
    color: var(--color-bg);
  }

  .admin-menu-toggle__text {
    background: transparent;
    display: inline;
    font-size: 13px;
    height: auto;
    letter-spacing: 0;
    width: auto;
  }

  .admin-menu-toggle__lines {
    background: transparent;
    display: inline-flex;
    flex-direction: column;
    gap: 5px;
    height: auto;
    width: 20px;
  }

  .admin-menu-toggle__lines span {
    background: var(--color-bg);
    display: block;
    height: 1px;
    width: 20px;
  }

  .admin-menu-toggle.open .admin-menu-toggle__lines span:nth-child(1) {
    transform: translateY(6px) rotate(45deg);
  }

  .admin-menu-toggle.open .admin-menu-toggle__lines span:nth-child(2) {
    opacity: 0;
  }

  .admin-menu-toggle.open .admin-menu-toggle__lines span:nth-child(3) {
    transform: translateY(-6px) rotate(-45deg);
  }
}

@media (max-width: 420px) {
  .sidebar-head {
    padding-left: 18px;
    padding-right: 18px;
  }

  .admin-menu-toggle__text {
    font-size: 12px;
  }
}


/* fix admin menu button text being treated as hamburger lines */
@media (max-width: 900px) {
  .admin-menu-toggle .admin-menu-toggle__text,
  .admin-menu-toggle.open .admin-menu-toggle__text {
    background: transparent !important;
    display: inline-block !important;
    height: auto !important;
    line-height: 1 !important;
    opacity: 1 !important;
    transform: none !important;
    transition: none !important;
    white-space: nowrap !important;
    width: auto !important;
    writing-mode: horizontal-tb !important;
  }

  .admin-menu-toggle .admin-menu-toggle__lines,
  .admin-menu-toggle.open .admin-menu-toggle__lines {
    background: transparent !important;
    display: inline-flex !important;
    flex-direction: column !important;
    gap: 5px !important;
    height: auto !important;
    opacity: 1 !important;
    transform: none !important;
    width: 20px !important;
  }

  .admin-menu-toggle .admin-menu-toggle__lines span {
    background: var(--color-bg) !important;
    display: block !important;
    height: 1px !important;
    opacity: 1;
    width: 20px !important;
  }

  .admin-menu-toggle.open .admin-menu-toggle__lines span:nth-child(1) {
    transform: translateY(6px) rotate(45deg) !important;
  }

  .admin-menu-toggle.open .admin-menu-toggle__lines span:nth-child(2) {
    opacity: 0 !important;
  }

  .admin-menu-toggle.open .admin-menu-toggle__lines span:nth-child(3) {
    transform: translateY(-6px) rotate(-45deg) !important;
  }
}

</style>
