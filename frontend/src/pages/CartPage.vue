<template>
  <div class="cart-page">
    <div class="container">
      <div class="page-header">
        <p class="page-subtitle">SHOPPING BAG</p>
        <h1 class="page-title">장바구니</h1>
        <div class="divider"></div>
      </div>

      <!-- 장바구니 비어있을 때 -->
      <div v-if="cartItems.length === 0" class="empty-cart">
        <p class="empty-icon">🛍</p>
        <p class="empty-text">장바구니가 비어있습니다.</p>
        <RouterLink to="/" class="btn-outline">쇼핑 계속하기</RouterLink>
      </div>

      <!-- 장바구니 아이템 -->
      <div v-else class="cart-layout">
        <div class="cart-items">
          <div
            v-for="item in cartItems"
            :key="item.id"
            class="cart-item"
          >
            <div class="cart-item__img">
              <img
                :src="item.imageUrl || fallbackImg"
                :alt="item.name"
                @error="onImgError"
              />
            </div>
            <div class="cart-item__info">
              <p class="cart-item__category">{{ item.categoryName || '' }}</p>
              <p class="cart-item__name">{{ item.name }}</p>
              <p class="cart-item__volume">{{ item.volume }}</p>
              <p class="cart-item__price">{{ item.price?.toLocaleString() }}원</p>
            </div>
            <div class="cart-item__qty">
              <button class="qty-btn" @click="updateQty(item, item.quantity - 1)">−</button>
              <span>{{ item.quantity }}</span>
              <button class="qty-btn" @click="updateQty(item, item.quantity + 1)">+</button>
            </div>
            <div class="cart-item__subtotal">
              {{ (item.price * item.quantity).toLocaleString() }}원
            </div>
            <button class="cart-item__remove" @click="removeItem(item.id)">✕</button>
          </div>
        </div>

        <!-- 주문 요약 -->
        <div class="cart-summary">
          <h2 class="summary-title">주문 요약</h2>
          <div class="summary-rows">
            <div class="summary-row">
              <span>상품 금액</span>
              <span>{{ totalPrice.toLocaleString() }}원</span>
            </div>
            <div class="summary-row">
              <span>배송비</span>
              <span>{{ shippingFee === 0 ? '무료' : shippingFee.toLocaleString() + '원' }}</span>
            </div>
            <p v-if="shippingFee > 0" class="free-shipping-notice">
              {{ (50000 - totalPrice).toLocaleString() }}원 더 구매 시 무료배송
            </p>
          </div>
          <div class="summary-total">
            <span>합계</span>
            <span>{{ (totalPrice + shippingFee).toLocaleString() }}원</span>
          </div>
          <RouterLink to="/checkout" class="btn-checkout">
            주문하기
          </RouterLink>
          <RouterLink to="/" class="btn-continue">쇼핑 계속하기</RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { cartAPI } from "../api/index.js";

const cartItems = ref([]);
const fallbackImg =
  "https://via.placeholder.com/100x120/3D4A2E/FAFAF7?text=AESOP";

const totalPrice = computed(() =>
  cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
);
const shippingFee = computed(() => (totalPrice.value >= 50000 ? 0 : 3000));

const onImgError = (e) => {
  e.target.src = fallbackImg;
};

const fetchCart = async () => {
  try {
    const res = await cartAPI.getCart();
    cartItems.value = res.data;
  } catch (e) {
    cartItems.value = [];
  }
};

const updateQty = async (item, newQty) => {
  if (newQty < 1) return;
  try {
    await cartAPI.updateCart(item.id, { quantity: newQty });
    item.quantity = newQty;
  } catch (e) {
    console.error("수량 변경 실패", e);
  }
};

const removeItem = async (id) => {
  try {
    await cartAPI.removeFromCart(id);
    cartItems.value = cartItems.value.filter((item) => item.id !== id);
  } catch (e) {
    console.error("삭제 실패", e);
  }
};

onMounted(() => {
  fetchCart();
});
</script>

<style scoped>
.cart-page {
  padding: 60px 0 100px;
  min-height: 100vh;
  background-color: var(--color-bg);
}

.page-header {
  text-align: center;
  margin-bottom: 60px;
}

.page-subtitle {
  font-family: var(--font-en);
  font-size: 11px;
  letter-spacing: 5px;
  color: var(--color-gold);
  margin-bottom: 12px;
}

.page-title {
  font-family: var(--font-kr);
  font-size: 28px;
  font-weight: 300;
  color: var(--color-olive);
  margin-bottom: 20px;
}

.divider {
  width: 40px;
  height: 1px;
  background-color: var(--color-gold);
  margin: 0 auto;
}

/* 빈 장바구니 */
.empty-cart {
  text-align: center;
  padding: 80px 0;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.empty-text {
  font-size: 15px;
  color: var(--color-text-sub);
  margin-bottom: 32px;
  letter-spacing: 0.5px;
}

/* 레이아웃 */
.cart-layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 60px;
  align-items: start;
}

/* 아이템 */
.cart-items {
  display: flex;
  flex-direction: column;
}

.cart-item {
  display: grid;
  grid-template-columns: 100px 1fr auto auto auto;
  gap: 20px;
  align-items: center;
  padding: 24px 0;
  border-bottom: 1px solid var(--color-border);
}

.cart-item__img {
  width: 100px;
  height: 120px;
  overflow: hidden;
  background-color: var(--color-border-light);
}

.cart-item__img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cart-item__category {
  font-size: 10px;
  letter-spacing: 2px;
  color: var(--color-gold);
  text-transform: uppercase;
  margin-bottom: 4px;
}

.cart-item__name {
  font-size: 14px;
  color: var(--color-text);
  margin-bottom: 4px;
  line-height: 1.4;
}

.cart-item__volume {
  font-size: 12px;
  color: var(--color-text-sub);
  margin-bottom: 8px;
}

.cart-item__price {
  font-family: var(--font-en);
  font-size: 13px;
  color: var(--color-olive);
}

.cart-item__qty {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: var(--color-text);
}

.qty-btn {
  width: 28px;
  height: 28px;
  border: 1px solid var(--color-border);
  background: transparent;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text);
  transition: all 0.2s;
}

.qty-btn:hover {
  border-color: var(--color-olive);
  color: var(--color-olive);
}

.cart-item__subtotal {
  font-family: var(--font-en);
  font-size: 14px;
  color: var(--color-olive);
  min-width: 80px;
  text-align: right;
}

.cart-item__remove {
  background: none;
  border: none;
  font-size: 14px;
  color: var(--color-text-sub);
  cursor: pointer;
  padding: 4px;
  transition: color 0.2s;
}

.cart-item__remove:hover {
  color: var(--color-text);
}

/* 요약 */
.cart-summary {
  background-color: var(--color-border-light);
  padding: 32px;
  position: sticky;
  top: 100px;
}

.summary-title {
  font-size: 14px;
  letter-spacing: 2px;
  color: var(--color-olive);
  font-weight: 400;
  margin-bottom: 24px;
}

.summary-rows {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: var(--color-text-sub);
}

.free-shipping-notice {
  font-size: 11px;
  color: var(--color-gold);
  letter-spacing: 0.3px;
  text-align: right;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  font-size: 15px;
  color: var(--color-olive);
  font-weight: 500;
  padding: 16px 0;
  border-top: 1px solid var(--color-border);
  border-bottom: 1px solid var(--color-border);
  margin-bottom: 20px;
}

.btn-checkout {
  display: block;
  width: 100%;
  padding: 14px;
  background-color: var(--color-olive);
  color: var(--color-bg);
  text-align: center;
  font-family: var(--font-kr);
  font-size: 14px;
  letter-spacing: 1px;
  transition: background-color 0.3s;
  margin-bottom: 12px;
  text-decoration: none;
}

.btn-checkout:hover {
  background-color: #2c3820;
}

.btn-continue {
  display: block;
  text-align: center;
  font-size: 12px;
  color: var(--color-text-sub);
  letter-spacing: 0.5px;
  text-decoration: underline;
  transition: color 0.3s;
}

.btn-continue:hover {
  color: var(--color-olive);
}

/* 반응형 */
@media (max-width: 1024px) {
  .cart-layout {
    grid-template-columns: 1fr;
  }
  .cart-summary {
    position: static;
  }
}

@media (max-width: 768px) {
  .cart-item {
    grid-template-columns: 80px 1fr;
    grid-template-rows: auto auto auto;
  }
  .cart-item__qty,
  .cart-item__subtotal,
  .cart-item__remove {
    grid-column: 2;
  }
}
</style>
