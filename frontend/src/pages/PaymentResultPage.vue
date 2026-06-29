<template>
  <div class="result-page">
    <div class="result-box">
      <!-- 성공 -->
      <template v-if="isSuccess">
        <div class="result-icon success">✓</div>
        <h1 class="result-title">결제가 완료되었습니다</h1>
        <p class="result-desc">주문이 정상적으로 접수되었습니다.</p>
        <div class="result-info" v-if="paymentInfo">
          <div class="info-row">
            <span>주문번호</span>
            <span>{{ paymentInfo.orderId }}</span>
          </div>
          <div class="info-row">
            <span>결제금액</span>
            <span>{{ Number(paymentInfo.amount).toLocaleString() }}원</span>
          </div>
          <div class="info-row">
            <span>결제수단</span>
            <span>{{ paymentInfo.method }}</span>
          </div>
        </div>
      </template>

      <!-- 실패 -->
      <template v-else>
        <div class="result-icon fail">✕</div>
        <h1 class="result-title">결제에 실패했습니다</h1>
        <p class="result-desc">{{ errorMessage || "결제 처리 중 오류가 발생했습니다." }}</p>
      </template>

      <div class="result-actions">
        <RouterLink to="/" class="btn-outline">홈으로</RouterLink>
        <RouterLink v-if="isSuccess" to="/mypage" class="btn-primary">주문 내역 보기</RouterLink>
        <RouterLink v-else to="/checkout" class="btn-primary">다시 시도</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { paymentAPI } from "../api/index.js";

const route = useRoute();
const isSuccess = ref(false);
const paymentInfo = ref(null);
const errorMessage = ref("");

onMounted(async () => {
  const { paymentKey, orderId, amount, code, message } = route.query;

  if (paymentKey && orderId && amount) {
    // 성공 콜백 → 서버에 승인 요청
    try {
      const res = await paymentAPI.confirm({
        paymentKey,
        orderId: String(orderId),
        amount: Number(amount),
      });
      if (res.data.success) {
        isSuccess.value = true;
        paymentInfo.value = res.data;
      } else {
        errorMessage.value = res.data.message;
      }
    } catch (e) {
      errorMessage.value = "결제 승인 중 오류가 발생했습니다.";
    }
  } else if (code) {
    // 실패 콜백
    errorMessage.value = message || "결제가 취소되었습니다.";
    if (orderId) {
      await paymentAPI.fail({
        orderId: String(orderId),
        errorCode: code,
        errorMessage: message,
      });
    }
  }
});
</script>

<style scoped>
.result-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--color-bg);
  padding: 40px 20px;
}

.result-box {
  text-align: center;
  max-width: 480px;
  width: 100%;
}

.result-icon {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin: 0 auto 32px;
}

.result-icon.success {
  background-color: var(--color-olive);
  color: var(--color-bg);
}

.result-icon.fail {
  background-color: #c0392b;
  color: white;
}

.result-title {
  font-family: var(--font-kr);
  font-size: 24px;
  font-weight: 300;
  color: var(--color-olive);
  margin-bottom: 12px;
}

.result-desc {
  font-size: 14px;
  color: var(--color-text-sub);
  margin-bottom: 32px;
  letter-spacing: 0.5px;
}

.result-info {
  background-color: var(--color-border-light);
  padding: 24px;
  margin-bottom: 36px;
  text-align: left;
}

.info-row {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: var(--color-text);
  padding: 8px 0;
  border-bottom: 1px solid var(--color-border);
}

.info-row:last-child {
  border-bottom: none;
}

.result-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.btn-primary {
  padding: 12px 28px;
  background-color: var(--color-olive);
  color: var(--color-bg);
  font-family: var(--font-kr);
  font-size: 13px;
  letter-spacing: 1px;
  transition: background-color 0.3s;
  text-decoration: none;
  display: inline-block;
}

.btn-primary:hover {
  background-color: var(--color-olive-dark, #2c3820);
}
</style>
