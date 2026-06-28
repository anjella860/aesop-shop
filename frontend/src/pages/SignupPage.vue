<template>
  <div class="auth-page">
    <div class="auth-box">
      <div class="auth-header">
        <p class="auth-subtitle">MEMBER</p>
        <h1 class="auth-title">회원가입</h1>
        <div class="divider"></div>
      </div>

      <form class="auth-form" @submit.prevent="handleSignup">
        <div class="form-group">
          <label class="form-label">이름 *</label>
          <input
            v-model="form.name"
            type="text"
            class="form-input"
            placeholder="이름을 입력하세요"
            required
          />
        </div>
        <div class="form-group">
          <label class="form-label">이메일 *</label>
          <input
            v-model="form.email"
            type="email"
            class="form-input"
            placeholder="이메일을 입력하세요"
            required
          />
        </div>
        <div class="form-group">
          <label class="form-label">비밀번호 *</label>
          <input
            v-model="form.password"
            type="password"
            class="form-input"
            placeholder="8자 이상 입력하세요"
            minlength="8"
            required
          />
        </div>
        <div class="form-group">
          <label class="form-label">비밀번호 확인 *</label>
          <input
            v-model="form.passwordConfirm"
            type="password"
            class="form-input"
            placeholder="비밀번호를 다시 입력하세요"
            required
          />
          <p v-if="passwordMismatch" class="field-error">비밀번호가 일치하지 않습니다.</p>
        </div>
        <div class="form-group">
          <label class="form-label">전화번호</label>
          <input
            v-model="form.phone"
            type="tel"
            class="form-input"
            placeholder="010-0000-0000"
          />
        </div>

        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
        <p v-if="successMsg" class="success-msg">{{ successMsg }}</p>

        <button type="submit" class="btn-submit" :disabled="isLoading || passwordMismatch">
          {{ isLoading ? '처리 중...' : '회원가입' }}
        </button>
      </form>

      <div class="auth-footer">
        <p>이미 회원이신가요?</p>
        <RouterLink to="/login" class="auth-link">로그인</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { memberAPI } from "../api/index.js";

const router = useRouter();
const isLoading = ref(false);
const errorMsg = ref("");
const successMsg = ref("");

const form = ref({
  name: "",
  email: "",
  password: "",
  passwordConfirm: "",
  phone: "",
});

const passwordMismatch = computed(
  () =>
    form.value.passwordConfirm.length > 0 &&
    form.value.password !== form.value.passwordConfirm
);

const handleSignup = async () => {
  if (passwordMismatch.value) return;
  isLoading.value = true;
  errorMsg.value = "";
  successMsg.value = "";
  try {
    await memberAPI.signup({
      name: form.value.name,
      email: form.value.email,
      password: form.value.password,
      phone: form.value.phone,
    });
    successMsg.value = "회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.";
    setTimeout(() => router.push("/login"), 1500);
  } catch (e) {
    errorMsg.value =
      e.response?.data?.message || "회원가입 중 오류가 발생했습니다.";
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--color-bg);
  padding: 40px 20px;
}

.auth-box {
  width: 100%;
  max-width: 440px;
}

.auth-header {
  text-align: center;
  margin-bottom: 48px;
}

.auth-subtitle {
  font-family: var(--font-en);
  font-size: 11px;
  letter-spacing: 5px;
  color: var(--color-gold);
  margin-bottom: 12px;
}

.auth-title {
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

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-bottom: 32px;
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

.field-error {
  font-size: 12px;
  color: #c0392b;
}

.error-msg {
  font-size: 13px;
  color: #c0392b;
  text-align: center;
}

.success-msg {
  font-size: 13px;
  color: var(--color-olive);
  text-align: center;
}

.btn-submit {
  width: 100%;
  padding: 14px;
  background-color: var(--color-olive);
  color: var(--color-bg);
  border: none;
  font-family: var(--font-kr);
  font-size: 14px;
  letter-spacing: 2px;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-top: 8px;
}

.btn-submit:hover:not(:disabled) {
  background-color: #2c3820;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.auth-footer {
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  font-size: 13px;
  color: var(--color-text-sub);
}

.auth-link {
  color: var(--color-olive);
  text-decoration: underline;
  font-size: 13px;
}
</style>
