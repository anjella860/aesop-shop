<template>
  <div class="auth-page">
    <div class="auth-box">
      <div class="auth-header">
        <p class="auth-subtitle">MEMBER</p>
        <h1 class="auth-title">로그인</h1>
        <div class="divider"></div>
      </div>

      <form class="auth-form" @submit.prevent="handleLogin">
        <div class="form-group">
          <label class="form-label">이메일</label>
          <input
            v-model="form.email"
            type="email"
            class="form-input"
            placeholder="이메일을 입력하세요"
            required
          />
        </div>
        <div class="form-group">
          <label class="form-label">비밀번호</label>
          <input
            v-model="form.password"
            type="password"
            class="form-input"
            placeholder="비밀번호를 입력하세요"
            required
          />
        </div>

        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>

        <button type="submit" class="btn-submit" :disabled="isLoading">
          {{ isLoading ? '로그인 중...' : '로그인' }}
        </button>
      </form>

      <div class="auth-footer">
        <p>아직 회원이 아니신가요?</p>
        <RouterLink to="/signup" class="auth-link">회원가입</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { memberAPI } from "../api/index.js";

const router = useRouter();
const isLoading = ref(false);
const errorMsg = ref("");

const form = ref({
  email: "",
  password: "",
});

const handleLogin = async () => {
  isLoading.value = true;
  errorMsg.value = "";
  try {
    await memberAPI.login(form.value);
    router.push("/");
  } catch (e) {
    errorMsg.value = "이메일 또는 비밀번호가 올바르지 않습니다.";
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

.error-msg {
  font-size: 13px;
  color: #c0392b;
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
