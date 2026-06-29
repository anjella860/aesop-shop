<template>
  <div class="checkout-page">
    <div class="container">
      <div class="page-header">
        <p class="page-subtitle">CHECKOUT</p>
        <h1 class="page-title">주문 및 결제</h1>
        <div class="divider"></div>
      </div>

      <div class="checkout-layout">
        <!-- 왼쪽: 배송 정보 입력 -->
        <div class="checkout-form">
          <section class="form-section">
            <h2 class="form-section__title">배송 정보</h2>
            <div class="form-group">
              <label class="form-label">수령인 이름 *</label>
              <input
                v-model="form.receiverName"
                type="text"
                class="form-input"
                placeholder="이름을 입력하세요"
              />
            </div>
            <div class="form-group">
              <label class="form-label">연락처 *</label>
              <input
                v-model="form.receiverPhone"
                type="tel"
                class="form-input"
                placeholder="010-0000-0000"
              />
            </div>
            <div class="form-group">
              <label class="form-label">배송 주소 *</label>
              <input
                v-model="form.receiverAddress"
                type="text"
                class="form-input"
                placeholder="배송받으실 주소를 입력하세요"
              />
            </div>
          </section>

          <section class="form-section">
            <h2 class="form-section__title">결제 수단</h2>
            <div class="payment-methods">
              <label
                v-for="method in paymentMethods"
                :key="method.value"
                class="payment-method"
                :class="{ active: form.paymentMethod === method.value }"
              >
                <input
                  type="radio"
                  v-model="form.paymentMethod"
                  :value="method.value"
                  hidden
                />
                <span class="method-icon">{{ method.icon }}</span>
                <span class="method-label">{{ method.label }}</span>
              </label>
            </div>
            <div v-if="isBankTransfer" class="bank-transfer-info">
              <p class="bank-transfer-info__title">입금 계좌</p>
              <p>국민은행 123456-00-000000</p>
              <p>예금주: 에이솝 쇼핑몰</p>
              <small>주문 후 관리자 입금 확인 시 주문 상태가 변경됩니다.</small>
            </div>
          </section>
        </div>

        <!-- 오른쪽: 주문 요약 -->
        <div class="order-summary">
          <h2 class="summary-title">주문 상품</h2>
          <div class="summary-items">
            <div v-for="item in cartItems" :key="item.id" class="summary-item">
              <div class="summary-item__img">
                <img
                  :src="item.imageUrl || fallbackImg"
                  :alt="item.name"
                  @error="onImgError"
                />
              </div>
              <div class="summary-item__info">
                <p class="summary-item__name">{{ item.name }}</p>
                <p class="summary-item__qty">수량: {{ item.quantity }}</p>
                <p class="summary-item__price">
                  {{ (item.price * item.quantity).toLocaleString() }}원
                </p>
              </div>
            </div>
          </div>

          <div class="summary-total">
            <div class="total-row">
              <span>상품 금액</span>
              <span>{{ totalPrice.toLocaleString() }}원</span>
            </div>
            <div class="total-row">
              <span>배송비</span>
              <span>{{
                shippingFee === 0 ? "무료" : shippingFee.toLocaleString() + "원"
              }}</span>
            </div>
            <div class="total-row total-row--final">
              <span>최종 결제 금액</span>
              <span>{{ (totalPrice + shippingFee).toLocaleString() }}원</span>
            </div>
          </div>

          <button
            class="btn-pay"
            @click="handlePay"
            :disabled="isPaying || cartItems.length === 0"
          >
            <span v-if="isPaying">처리 중...</span>
            <span v-else
              >{{ (totalPrice + shippingFee).toLocaleString() }}원
              결제하기</span
            >
          </button>

          <p class="pay-notice">토스페이먼츠를 통해 안전하게 결제됩니다</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { cartAPI, orderAPI, paymentAPI } from "../api/index.js";

const route = useRoute();
const router = useRouter();
const cartItems = ref([]);
const directCheckoutItem = ref(null);
const isPaying = ref(false);
const fallbackImg =
  "https://via.placeholder.com/80x80/3D4A2E/FAFAF7?text=AESOP";
const TOSS_CLIENT_KEY =
  import.meta.env.VITE_TOSS_CLIENT_KEY ||
  "test_ck_26DlbXAaV0OZv4EwM2YK3qY50Q9R";

const form = ref({
  receiverName: "",
  receiverPhone: "",
  receiverAddress: "",
  paymentMethod: "카드",
});

const paymentMethods = [
  { value: "카드", label: "카드 결제", icon: "💳" },
  { value: "무통장입금", label: "무통장입금", icon: "🏦" },
];

const isBankTransfer = computed(
  () => form.value.paymentMethod === "무통장입금",
);

const totalPrice = computed(() =>
  cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0),
);
const shippingFee = computed(() => (totalPrice.value >= 50000 ? 0 : 3000));

const onImgError = (e) => {
  e.target.src = fallbackImg;
};

const getDirectCheckoutItem = () => {
  if (route.query.mode !== "direct") return null;
  try {
    const item = JSON.parse(sessionStorage.getItem("directCheckoutItem") || "null");
    if (!item?.productId || !item?.quantity) return null;
    return item;
  } catch (e) {
    return null;
  }
};

const fetchCart = async () => {
  const directItem = getDirectCheckoutItem();
  if (directItem) {
    directCheckoutItem.value = directItem;
    cartItems.value = [
      {
        id: `direct-${directItem.productId}`,
        productId: directItem.productId,
        quantity: directItem.quantity,
        name: directItem.name,
        price: directItem.price,
        volume: directItem.volume,
        imageUrl: directItem.imageUrl,
        categoryName: directItem.categoryName,
      },
    ];
    return;
  }

  try {
    const res = await cartAPI.getCart();
    cartItems.value = res.data;
  } catch (e) {
    console.error("장바구니 조회 실패", e);
  }
};

const handlePay = async () => {
  const receiverName = form.value.receiverName.trim();
  const receiverPhone = form.value.receiverPhone.trim();
  const receiverAddress = form.value.receiverAddress.trim();

  if (!receiverName || !receiverPhone || !receiverAddress) {
    alert("배송 정보를 모두 입력해주세요.");
    return;
  }

  if (!/^010-?\d{4}-?\d{4}$/.test(receiverPhone)) {
    alert("연락처는 010-0000-0000 형식으로 입력해주세요.");
    return;
  }

  if (receiverAddress.length < 5) {
    alert("배송 주소를 정확히 입력해주세요.");
    return;
  }

  if (cartItems.value.length === 0) {
    alert("장바구니가 비어있습니다.");
    return;
  }

  isPaying.value = true;

  try {
    // 1. 주문 생성
    const orderPayload = {
      receiverName,
      receiverPhone,
      receiverAddress,
      paymentMethod: form.value.paymentMethod,
    };
    if (directCheckoutItem.value) {
      orderPayload.items = [
        {
          productId: directCheckoutItem.value.productId,
          quantity: directCheckoutItem.value.quantity,
        },
      ];
    }

    const orderRes = await orderAPI.createOrder(orderPayload);
    const orderId = `${orderRes.data.id}-${Date.now()}`;
    const finalAmount = totalPrice.value + shippingFee.value;

    if (isBankTransfer.value) {
      alert("주문이 접수되었습니다. 안내된 계좌로 입금해주세요.");
      sessionStorage.removeItem("directCheckoutItem");
      router.push("/mypage");
      return;
    }

    // 2. 토스페이먼츠 카드 결제 요청
    const tossPayments = await loadTossPayments();

    await tossPayments.requestPayment("카드", {
      amount: finalAmount,
      orderId,
      orderName:
        cartItems.value[0].name +
        (cartItems.value.length > 1
          ? ` 외 ${cartItems.value.length - 1}건`
          : ""),
      customerName: receiverName,
      customerMobilePhone: receiverPhone.replace(/[^0-9]/g, ""),
      successUrl: `${window.location.origin}/payment/success`,
      failUrl: `${window.location.origin}/payment/fail`,
    });
  } catch (e) {
    console.error("결제 요청 실패", e);
    if (e.code !== "USER_CANCEL") {
      const serverMessage = e.response?.data?.message || e.response?.data;
      alert(serverMessage || "결제 중 오류가 발생했습니다.");
    }
    isPaying.value = false;
  }
};

// 토스페이먼츠 SDK 동적 로드
const loadTossPayments = () => {
  return new Promise((resolve, reject) => {
    if (window.TossPayments) {
      resolve(window.TossPayments(TOSS_CLIENT_KEY));
      return;
    }
    const script = document.createElement("script");
    script.src = "https://js.tosspayments.com/v1/payment";
    script.onload = () => resolve(window.TossPayments(TOSS_CLIENT_KEY));
    script.onerror = reject;
    document.head.appendChild(script);
  });
};

onMounted(() => {
  fetchCart();
});
</script>

<style scoped>
.checkout-page {
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

/* 레이아웃 */
.checkout-layout {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 60px;
  align-items: start;
}

/* 폼 */
.form-section {
  margin-bottom: 48px;
}

.form-section__title {
  font-family: var(--font-kr);
  font-size: 16px;
  font-weight: 400;
  color: var(--color-olive);
  letter-spacing: 1px;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--color-border);
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 12px;
  letter-spacing: 1px;
  color: var(--color-text-sub);
  margin-bottom: 8px;
}

.form-input {
  width: 100%;
  border: none;
  border-bottom: 1px solid var(--color-border);
  background: transparent;
  padding: 10px 0;
  font-size: 14px;
  color: var(--color-text);
  font-family: var(--font-kr);
  transition: border-color 0.3s;
  outline: none;
}

.form-input:focus {
  border-bottom-color: var(--color-olive);
}

/* 결제 수단 */
.payment-methods {
  display: flex;
  gap: 12px;
}

.payment-method {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 12px;
  border: 1px solid var(--color-border);
  cursor: pointer;
  transition: all 0.3s;
}

.payment-method.active {
  border-color: var(--color-olive);
  background-color: rgba(61, 74, 46, 0.04);
}

.method-icon {
  font-size: 22px;
}

.method-label {
  font-size: 12px;
  letter-spacing: 0.5px;
  color: var(--color-text);
}

.bank-transfer-info {
  margin-top: 16px;
  padding: 16px;
  border: 1px solid var(--color-border);
  background-color: rgba(61, 74, 46, 0.04);
  font-size: 13px;
  line-height: 1.7;
  color: var(--color-text);
}

.bank-transfer-info__title {
  font-weight: 600;
  color: var(--color-olive);
  margin-bottom: 4px;
}

.bank-transfer-info small {
  display: block;
  margin-top: 6px;
  color: var(--color-text-sub);
}

/* 주문 요약 */
.order-summary {
  background-color: var(--color-border-light);
  padding: 32px;
  position: sticky;
  top: 100px;
}

.summary-title {
  font-size: 14px;
  letter-spacing: 2px;
  color: var(--color-olive);
  margin-bottom: 24px;
  font-weight: 400;
}

.summary-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 28px;
  max-height: 280px;
  overflow-y: auto;
}

.summary-item {
  display: flex;
  gap: 12px;
}

.summary-item__img {
  width: 64px;
  height: 64px;
  flex-shrink: 0;
  overflow: hidden;
  background-color: var(--color-border);
}

.summary-item__img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.summary-item__name {
  font-size: 13px;
  color: var(--color-text);
  margin-bottom: 4px;
  line-height: 1.4;
}

.summary-item__qty {
  font-size: 11px;
  color: var(--color-text-sub);
  margin-bottom: 4px;
}

.summary-item__price {
  font-size: 13px;
  color: var(--color-olive);
  font-family: var(--font-en);
}

/* 합계 */
.summary-total {
  border-top: 1px solid var(--color-border);
  padding-top: 20px;
  margin-bottom: 24px;
}

.total-row {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: var(--color-text-sub);
  margin-bottom: 10px;
}

.total-row--final {
  font-size: 15px;
  color: var(--color-olive);
  font-weight: 500;
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid var(--color-border);
}

/* 결제 버튼 */
.btn-pay {
  width: 100%;
  padding: 16px;
  background-color: var(--color-olive);
  color: var(--color-bg);
  border: none;
  font-family: var(--font-kr);
  font-size: 14px;
  letter-spacing: 1px;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-bottom: 12px;
}

.btn-pay:hover:not(:disabled) {
  background-color: var(--color-olive-dark, #2c3820);
}

.btn-pay:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pay-notice {
  text-align: center;
  font-size: 11px;
  color: var(--color-text-sub);
  letter-spacing: 0.5px;
}

/* 반응형 */
@media (max-width: 1024px) {
  .checkout-layout {
    grid-template-columns: 1fr;
  }
  .order-summary {
    position: static;
  }
}


/* portfolio checkout mobile polish */
@media (max-width: 768px) {
  .checkout-page {
    padding: 40px 0 72px;
  }

  .checkout-layout {
    gap: 32px;
  }

  .payment-methods {
    grid-template-columns: 1fr;
  }

  .payment-card {
    min-height: 84px;
  }

  .order-summary {
    padding: 28px 22px;
  }

  .summary-item {
    align-items: flex-start;
  }
}

</style>
