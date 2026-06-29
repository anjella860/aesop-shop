<template>
  <div class="detail-page" v-if="product">
    <div class="container">
      <div class="detail-layout">
        <!-- 상품 이미지 -->
        <div class="detail-img">
          <img
            :src="product.imageUrl || fallbackImg"
            :alt="product.name"
            @error="onImgError"
          />
        </div>

        <!-- 상품 정보 -->
        <div class="detail-info">
          <p class="detail-category">{{ categoryName }}</p>
          <h1 class="detail-name">{{ product.name }}</h1>
          <p class="detail-volume">{{ product.volume }}</p>
          <p class="detail-price">{{ product.price?.toLocaleString() }}원</p>
          <div class="detail-divider"></div>
          <p class="detail-desc">{{ product.description }}</p>

          <!-- 수량 선택 -->
          <div class="quantity-box">
            <button class="qty-btn" @click="decreaseQty">−</button>
            <span class="qty-value">{{ quantity }}</span>
            <button class="qty-btn" @click="increaseQty">+</button>
          </div>

          <!-- 버튼 -->
          <div class="detail-actions">
            <button class="btn-cart" @click="addToCart">장바구니 담기</button>
            <button class="btn-buy" @click="buyNow">바로 구매</button>
          </div>

          <p v-if="cartMsg" class="cart-msg">{{ cartMsg }}</p>

          <!-- 재고 -->
          <p class="stock-info">재고: {{ product.stock }}개</p>
        </div>
      </div>

      <!-- 리뷰 섹션 -->
      <div class="review-section">
        <div class="section-header">
          <p class="section-subtitle">REVIEW</p>
          <h2 class="section-title">고객 리뷰</h2>
          <p class="section-copy">리뷰는 누구나 볼 수 있으며 작성은 로그인 후 가능합니다.</p>
          <div class="divider"></div>
        </div>

        <div v-if="reviews.length > 0" class="review-list">
          <div v-for="review in reviews" :key="review.id" class="review-item">
            <div class="review-header">
              <span class="review-author">{{ review.memberName || '익명' }}</span>
              <span class="review-date">{{ formatDate(review.createdAt) }}</span>
            </div>
            <p class="review-content">{{ review.content }}</p>
          </div>
        </div>
        <p v-else class="empty-msg">아직 리뷰가 없습니다.</p>

        <!-- 리뷰 작성 -->
        <div class="review-form">
          <textarea
            v-model="newReview"
            class="review-input"
            placeholder="이 제품을 사용해 보셨나요? 솔직한 리뷰를 남겨주세요."
            rows="4"
          ></textarea>
          <div class="review-form__footer">
            <span>작성과 삭제는 로그인한 회원만 가능합니다.</span>
            <button class="btn-review" @click="submitReview">리뷰 등록</button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div v-else class="loading-page">
    <p>상품 정보를 불러오는 중...</p>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { productAPI, categoryAPI, cartAPI, reviewAPI } from "../api/index.js";

const route = useRoute();
const router = useRouter();

const product = ref(null);
const categories = ref([]);
const reviews = ref([]);
const quantity = ref(1);
const newReview = ref("");
const cartMsg = ref("");
const fallbackImg =
  "https://via.placeholder.com/600x700/3D4A2E/FAFAF7?text=AESOP";

const categoryName = computed(() => {
  const cat = categories.value.find((c) => c.id === product.value?.categoryId);
  return cat ? cat.name : "";
});

const onImgError = (e) => {
  e.target.src = fallbackImg;
};

const increaseQty = () => {
  if (quantity.value < (product.value?.stock || 99)) quantity.value++;
};
const decreaseQty = () => {
  if (quantity.value > 1) quantity.value--;
};

const addToCart = async () => {
  try {
    await cartAPI.addToCart({
      productId: product.value.id,
      quantity: quantity.value,
    });
    window.dispatchEvent(new Event("cart-updated"));
    cartMsg.value = "장바구니에 담겼습니다.";
    setTimeout(() => (cartMsg.value = ""), 2000);
    return true;
  } catch (e) {
    cartMsg.value = "로그인이 필요합니다.";
    setTimeout(() => router.push("/login"), 1500);
    return false;
  }
};

const buyNow = async () => {
  const added = await addToCart();
  if (added) {
    router.push("/checkout");
  }
};

const submitReview = async () => {
  if (!newReview.value.trim()) return;
  try {
    await reviewAPI.create(product.value.id, {
      rating: 5,
      content: newReview.value,
    });
    newReview.value = "";
    await fetchReviews();
  } catch (e) {
    alert("리뷰 작성은 로그인이 필요합니다.");
    router.push({ path: "/login", query: { redirect: route.fullPath } });
  }
};

const formatDate = (dateStr) => {
  if (!dateStr) return "";
  return new Date(dateStr).toLocaleDateString("ko-KR");
};

const fetchReviews = async () => {
  try {
    const res = await reviewAPI.getByProduct(route.params.id);
    reviews.value = res.data;
  } catch (e) {
    reviews.value = [];
  }
};

onMounted(async () => {
  try {
    const [productRes, categoriesRes] = await Promise.all([
      productAPI.getById(route.params.id),
      categoryAPI.getAll(),
    ]);
    product.value = productRes.data;
    categories.value = categoriesRes.data;
    await fetchReviews();
  } catch (e) {
    console.error("상품 조회 실패", e);
  }
});
</script>

<style scoped>
.detail-page {
  padding: 60px 0 100px;
  background-color: var(--color-bg);
}

.loading-page {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-sub);
}

.detail-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 80px;
  margin-bottom: 80px;
}

/* 이미지 */
.detail-img {
  aspect-ratio: 3/4;
  overflow: hidden;
  background-color: var(--color-border-light);
}

.detail-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 정보 */
.detail-category {
  font-size: 11px;
  letter-spacing: 3px;
  color: var(--color-gold);
  text-transform: uppercase;
  margin-bottom: 12px;
}

.detail-name {
  font-family: var(--font-kr);
  font-size: 26px;
  font-weight: 300;
  color: var(--color-olive);
  margin-bottom: 8px;
  line-height: 1.4;
}

.detail-volume {
  font-size: 13px;
  color: var(--color-text-sub);
  margin-bottom: 16px;
}

.detail-price {
  font-family: var(--font-en);
  font-size: 22px;
  color: var(--color-olive);
  letter-spacing: 1px;
  margin-bottom: 24px;
}

.detail-divider {
  height: 1px;
  background-color: var(--color-border);
  margin-bottom: 24px;
}

.detail-desc {
  font-size: 14px;
  line-height: 1.9;
  color: var(--color-text-sub);
  margin-bottom: 32px;
}

/* 수량 */
.quantity-box {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 28px;
}

.qty-btn {
  width: 36px;
  height: 36px;
  border: 1px solid var(--color-border);
  background: transparent;
  font-size: 18px;
  color: var(--color-text);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.qty-btn:hover {
  border-color: var(--color-olive);
  color: var(--color-olive);
}

.qty-value {
  font-family: var(--font-en);
  font-size: 18px;
  color: var(--color-text);
  min-width: 24px;
  text-align: center;
}

/* 버튼 */
.detail-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.btn-cart,
.btn-buy {
  flex: 1;
  padding: 14px;
  font-family: var(--font-kr);
  font-size: 13px;
  letter-spacing: 1px;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.btn-cart {
  background: transparent;
  border: 1px solid var(--color-olive);
  color: var(--color-olive);
}

.btn-cart:hover {
  background-color: var(--color-olive);
  color: var(--color-bg);
}

.btn-buy {
  background-color: var(--color-olive);
  color: var(--color-bg);
}

.btn-buy:hover {
  background-color: #2c3820;
}

.cart-msg {
  font-size: 13px;
  color: var(--color-olive);
  margin-bottom: 12px;
}

.stock-info {
  font-size: 12px;
  color: var(--color-text-sub);
  letter-spacing: 0.5px;
}

/* 리뷰 */
.review-section {
  border-top: 1px solid var(--color-border);
  padding-top: 60px;
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.section-subtitle {
  font-family: var(--font-en);
  font-size: 11px;
  letter-spacing: 5px;
  color: var(--color-gold);
  margin-bottom: 10px;
}

.section-title {
  font-family: var(--font-kr);
  font-size: 22px;
  font-weight: 300;
  color: var(--color-olive);
  margin-bottom: 10px;
}

.section-copy {
  color: var(--color-text-sub);
  font-size: 13px;
  margin-bottom: 16px;
}

.divider {
  width: 40px;
  height: 1px;
  background-color: var(--color-gold);
  margin: 0 auto;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-bottom: 40px;
}

.review-item {
  padding: 24px;
  background-color: var(--color-border-light);
}

.review-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.review-author {
  font-size: 13px;
  color: var(--color-olive);
  font-weight: 500;
}

.review-date {
  font-size: 12px;
  color: var(--color-text-sub);
}

.review-content {
  font-size: 14px;
  color: var(--color-text);
  line-height: 1.7;
}

.empty-msg {
  text-align: center;
  font-size: 14px;
  color: var(--color-text-sub);
  padding: 40px 0;
}

.review-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-width: 640px;
  margin: 0 auto;
}

.review-input {
  width: 100%;
  border: 1px solid var(--color-border);
  background: transparent;
  padding: 16px;
  font-size: 14px;
  color: var(--color-text);
  font-family: var(--font-kr);
  outline: none;
  resize: vertical;
  transition: border-color 0.3s;
}

.review-input:focus {
  border-color: var(--color-olive);
}

.review-form__footer {
  align-items: center;
  display: flex;
  gap: 16px;
  justify-content: space-between;
}

.review-form__footer span {
  color: var(--color-text-sub);
  font-size: 12px;
}

.btn-review {
  align-self: flex-end;
  padding: 10px 28px;
  background-color: var(--color-olive);
  color: var(--color-bg);
  border: none;
  font-family: var(--font-kr);
  font-size: 13px;
  letter-spacing: 1px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-review:hover {
  background-color: #2c3820;
}

/* 반응형 */
@media (max-width: 1024px) {
  .detail-layout {
    grid-template-columns: 1fr;
    gap: 40px;
  }
}
</style>
