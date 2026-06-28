<template>
  <div class="mypage">
    <div class="container">
      <div class="page-header">
        <p class="page-subtitle">MY ACCOUNT</p>
        <h1 class="page-title">마이페이지</h1>
        <div class="divider"></div>
      </div>

      <!-- 회원 정보 -->
      <div class="member-info" v-if="member">
        <div class="member-greeting">
          <p class="greeting-text">안녕하세요, <strong>{{ member.name }}</strong>님</p>
          <p class="member-email">{{ member.email }}</p>
        </div>
        <button class="btn-logout" @click="handleLogout">로그아웃</button>
      </div>

      <!-- 탭 -->
      <div class="tab-bar">
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'orders' }"
          @click="activeTab = 'orders'"
        >주문 내역</button>
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'profile' }"
          @click="activeTab = 'profile'"
        >정보 수정</button>
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'withdraw' }"
          @click="activeTab = 'withdraw'"
        >회원 탈퇴</button>
      </div>

      <!-- 주문 내역 탭 -->
      <div v-if="activeTab === 'orders'">
        <div v-if="orders.length === 0" class="empty-orders">
          <p>주문 내역이 없습니다.</p>
          <RouterLink to="/" class="btn-outline">쇼핑하러 가기</RouterLink>
        </div>
        <div v-else class="order-list">
          <div v-for="order in orders" :key="order.id" class="order-card">
            <div class="order-card__header">
              <div>
                <span class="order-id">주문번호 #{{ order.id }}</span>
                <span class="order-date">{{ formatDate(order.orderedAt) }}</span>
              </div>
              <span class="order-status" :class="statusClass(order.status)">
                {{ statusLabel(order.status) }}
              </span>
            </div>
            <div class="order-card__body">
              <div class="order-info-row">
                <span>배송지</span>
                <span>{{ order.receiverName }} · {{ order.receiverAddress }}</span>
              </div>
              <div class="order-info-row">
                <span>결제수단</span>
                <span>{{ order.paymentMethod }}</span>
              </div>
              <div v-if="order.deliveryCompany || order.trackingNumber" class="order-info-row">
                <span>배송정보</span>
                <span>{{ order.deliveryCompany || '배송사 준비중' }} {{ order.trackingNumber || '' }}</span>
              </div>
              <div class="order-info-row order-total">
                <span>결제금액</span>
                <span>{{ order.totalPrice?.toLocaleString() }}원</span>
              </div>
              <button
                v-if="canCancel(order.status)"
                class="btn-cancel-order"
                @click="cancelOrder(order.id)"
              >주문 취소</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 정보 수정 탭 -->
      <div v-if="activeTab === 'profile'" class="profile-form">
        <div class="form-group">
          <label class="form-label">이름</label>
          <input v-model="editForm.name" type="text" class="form-input" />
        </div>
        <div class="form-group">
          <label class="form-label">전화번호</label>
          <input v-model="editForm.phone" type="tel" class="form-input" />
        </div>
        <p v-if="updateMsg" class="update-msg">{{ updateMsg }}</p>
        <button class="btn-update" @click="handleUpdate">정보 저장</button>
      </div>

      <div v-if="activeTab === 'withdraw'" class="profile-form">
        <p class="withdraw-copy">회원 탈퇴 시 계정 정보가 삭제되며 되돌릴 수 없습니다.</p>
        <div class="form-group">
          <label class="form-label">비밀번호 확인</label>
          <input v-model="deletePassword" type="password" class="form-input" />
        </div>
        <p v-if="deleteMsg" class="update-msg">{{ deleteMsg }}</p>
        <button class="btn-delete-account" @click="handleDeleteAccount">회원 탈퇴</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { memberAPI, orderAPI } from "../api/index.js";

const router = useRouter();
const member = ref(null);
const orders = ref([]);
const activeTab = ref("orders");
const updateMsg = ref("");
const deleteMsg = ref("");
const deletePassword = ref("");
const editForm = ref({ name: "", phone: "" });

const formatDate = (dateStr) => {
  if (!dateStr) return "";
  return new Date(dateStr).toLocaleDateString("ko-KR");
};

const statusLabel = (status) => {
  const map = {
    PENDING: "결제 대기",
    CONFIRMED: "주문 확인",
    SHIPPED: "배송 중",
    DELIVERED: "배송 완료",
    CANCELLED: "취소됨",
  };
  return map[status] || status;
};

const canCancel = (status) => ["PENDING", "CONFIRMED"].includes(status);

const statusClass = (status) => ({
  "status--pending": status === "PENDING",
  "status--confirmed": status === "CONFIRMED",
  "status--shipped": status === "SHIPPED",
  "status--delivered": status === "DELIVERED",
  "status--cancelled": status === "CANCELLED",
});

const handleLogout = async () => {
  try {
    await fetch("http://localhost:8080/logout", {
      method: "POST",
      credentials: "include",
    });
  } finally {
    router.push("/login");
  }
};

const handleDeleteAccount = async () => {
  if (!deletePassword.value) {
    deleteMsg.value = "비밀번호를 입력해주세요.";
    return;
  }
  if (!window.confirm("정말 탈퇴하시겠습니까?")) return;
  try {
    await memberAPI.deleteMyInfo(deletePassword.value);
    router.push("/signup");
  } catch (e) {
    deleteMsg.value = "회원 탈퇴에 실패했습니다.";
  }
};

const cancelOrder = async (id) => {
  if (!window.confirm("주문을 취소하시겠습니까?")) return;
  await orderAPI.cancelOrder(id);
  const ordersRes = await orderAPI.getOrders();
  orders.value = ordersRes.data;
};

const handleUpdate = async () => {
  try {
    await memberAPI.updateMyInfo(editForm.value);
    updateMsg.value = "정보가 저장되었습니다.";
    setTimeout(() => (updateMsg.value = ""), 2000);
  } catch (e) {
    updateMsg.value = "저장에 실패했습니다.";
  }
};

onMounted(async () => {
  try {
    const [memberRes, ordersRes] = await Promise.all([
      memberAPI.getMyInfo(),
      orderAPI.getOrders(),
    ]);
    member.value = memberRes.data;
    orders.value = ordersRes.data;
    editForm.value = {
      name: memberRes.data.name || "",
      phone: memberRes.data.phone || "",
    };
  } catch (e) {
    router.push("/login");
  }
});
</script>

<style scoped>
.mypage {
  padding: 60px 0 100px;
  min-height: 100vh;
  background-color: var(--color-bg);
}

.page-header {
  text-align: center;
  margin-bottom: 48px;
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

/* 회원 정보 */
.member-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  background-color: var(--color-border-light);
  margin-bottom: 40px;
}

.greeting-text {
  font-size: 15px;
  color: var(--color-text);
  margin-bottom: 4px;
}

.member-email {
  font-size: 12px;
  color: var(--color-text-sub);
}

.btn-logout {
  padding: 8px 20px;
  border: 1px solid var(--color-border);
  background: transparent;
  font-family: var(--font-kr);
  font-size: 12px;
  color: var(--color-text-sub);
  cursor: pointer;
  transition: all 0.3s;
  letter-spacing: 0.5px;
}

.btn-logout:hover {
  border-color: var(--color-olive);
  color: var(--color-olive);
}

/* 탭 */
.tab-bar {
  display: flex;
  border-bottom: 1px solid var(--color-border);
  margin-bottom: 40px;
}

.tab-btn {
  padding: 14px 28px;
  background: none;
  border: none;
  font-family: var(--font-kr);
  font-size: 14px;
  color: var(--color-text-sub);
  cursor: pointer;
  letter-spacing: 0.5px;
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
  transition: all 0.3s;
}

.tab-btn.active {
  color: var(--color-olive);
  border-bottom-color: var(--color-olive);
}

/* 주문 목록 */
.empty-orders {
  text-align: center;
  padding: 60px 0;
  color: var(--color-text-sub);
  font-size: 14px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  border: 1px solid var(--color-border);
}

.order-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background-color: var(--color-border-light);
  border-bottom: 1px solid var(--color-border);
}

.order-id {
  font-size: 13px;
  color: var(--color-olive);
  font-family: var(--font-en);
  margin-right: 12px;
}

.order-date {
  font-size: 12px;
  color: var(--color-text-sub);
}

.order-status {
  font-size: 12px;
  letter-spacing: 0.5px;
  padding: 4px 12px;
}

.status--pending { background-color: #f5f0e8; color: #8b7355; }
.status--confirmed { background-color: #edf2e8; color: #3d4a2e; }
.status--shipped { background-color: #e8f0f5; color: #2c5f8a; }
.status--delivered { background-color: #e8f5ec; color: #2d7a4f; }
.status--cancelled { background-color: #f5e8e8; color: #8a2c2c; }

.order-card__body {
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.order-info-row {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: var(--color-text-sub);
}

.order-info-row span:first-child {
  flex-shrink: 0;
  margin-right: 20px;
}

.order-total {
  font-size: 14px;
  color: var(--color-olive);
  font-weight: 500;
  padding-top: 10px;
  border-top: 1px solid var(--color-border);
}

/* 정보 수정 */
.profile-form {
  max-width: 480px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 12px;
  letter-spacing: 1px;
  color: var(--color-text-sub);
}

.form-input {
  border: none;
  border-bottom: 1px solid var(--color-border);
  background: transparent;
  padding: 10px 0;
  font-size: 14px;
  color: var(--color-text);
  font-family: var(--font-kr);
  outline: none;
  transition: border-color 0.3s;
}

.form-input:focus {
  border-bottom-color: var(--color-olive);
}

.update-msg {
  font-size: 13px;
  color: var(--color-olive);
}

.btn-update {
  align-self: flex-start;
  padding: 12px 32px;
  background-color: var(--color-olive);
  color: var(--color-bg);
  border: none;
  font-family: var(--font-kr);
  font-size: 13px;
  letter-spacing: 1px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-update:hover {
  background-color: #2c3820;
}

.withdraw-copy {
  color: var(--color-text-sub);
  font-size: 14px;
  line-height: 1.7;
}

.btn-delete-account {
  align-self: flex-start;
  padding: 12px 32px;
  background-color: #8a2c2c;
  color: white;
  border: none;
  font-family: var(--font-kr);
  font-size: 13px;
  letter-spacing: 1px;
  cursor: pointer;
}
</style>
