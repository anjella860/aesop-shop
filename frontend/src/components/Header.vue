<template>
  <header class="header" :class="{ 'header--scrolled': isScrolled }">
    <div class="header__inner">
      <RouterLink to="/" class="header__logo">
        <span class="logo-text">AESOP</span>
      </RouterLink>

      <nav class="header__nav">
        <RouterLink to="/new-notable" class="nav-item">신제품 & 추천</RouterLink>
        <RouterLink to="/skincare" class="nav-item">스킨케어</RouterLink>
        <RouterLink to="/hand-body" class="nav-item">핸드 & 바디</RouterLink>
        <RouterLink to="/fragrance" class="nav-item">향수</RouterLink>
        <RouterLink to="/hair" class="nav-item">헤어</RouterLink>
        <RouterLink to="/gifts" class="nav-item">기프트</RouterLink>
      </nav>

      <div class="header__actions">
        <template v-if="isAdmin">
          <RouterLink to="/admin" class="action-icon admin-link" title="관리자">
            관리자
          </RouterLink>
          <button class="action-icon logout-btn" type="button" @click="handleLogout">
            로그아웃
          </button>
        </template>

        <template v-else>
          <RouterLink :to="isLoggedIn ? '/mypage' : '/login'" class="action-icon" :title="isLoggedIn ? '마이페이지' : '로그인'">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="20"
              height="20"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="1.5"
            >
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
              <circle cx="12" cy="7" r="4" />
            </svg>
          </RouterLink>

          <RouterLink to="/cart" class="action-icon cart-icon" title="장바구니">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="20"
              height="20"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="1.5"
            >
              <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z" />
              <line x1="3" y1="6" x2="21" y2="6" />
              <path d="M16 10a4 4 0 0 1-8 0" />
            </svg>
            <span v-if="cartCount > 0" class="cart-badge">{{ cartCount }}</span>
          </RouterLink>
        </template>

        <button
          v-if="!isAdmin"
          class="menu-toggle"
          @click="toggleMenu"
          :class="{ open: menuOpen }"
          type="button"
          aria-label="메뉴 열기"
        >
          <span></span>
          <span></span>
          <span></span>
        </button>
      </div>
    </div>

    <div v-if="!isAdmin" class="mobile-menu" :class="{ open: menuOpen }">
      <RouterLink to="/new-notable" class="mobile-nav-item" @click="closeMenu">신제품 & 추천</RouterLink>
      <RouterLink to="/skincare" class="mobile-nav-item" @click="closeMenu">스킨케어</RouterLink>
      <RouterLink to="/hand-body" class="mobile-nav-item" @click="closeMenu">핸드 & 바디</RouterLink>
      <RouterLink to="/fragrance" class="mobile-nav-item" @click="closeMenu">향수</RouterLink>
      <RouterLink to="/hair" class="mobile-nav-item" @click="closeMenu">헤어</RouterLink>
      <RouterLink to="/gifts" class="mobile-nav-item" @click="closeMenu">기프트</RouterLink>

      <template v-if="isAdmin">
        <RouterLink to="/admin" class="mobile-nav-item" @click="closeMenu">관리자</RouterLink>
        <button class="mobile-nav-item mobile-logout-btn" type="button" @click="handleLogout">로그아웃</button>
      </template>
      <template v-else>
        <RouterLink :to="isLoggedIn ? '/mypage' : '/login'" class="mobile-nav-item" @click="closeMenu">
          {{ isLoggedIn ? "마이페이지" : "로그인" }}
        </RouterLink>
        <RouterLink to="/cart" class="mobile-nav-item" @click="closeMenu">
          장바구니<span v-if="cartCount > 0"> ({{ cartCount }})</span>
        </RouterLink>
      </template>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { cartAPI, memberAPI } from "../api/index.js";

const route = useRoute();
const router = useRouter();
const isScrolled = ref(false);
const menuOpen = ref(false);
const cartCount = ref(0);
const isLoggedIn = ref(false);
const isAdmin = ref(false);

const handleScroll = () => {
  isScrolled.value = window.scrollY > 50;
};

const toggleMenu = () => {
  menuOpen.value = !menuOpen.value;
};

const closeMenu = () => {
  menuOpen.value = false;
};

const loadMember = async () => {
  try {
    const response = await memberAPI.getMyInfo();
    isLoggedIn.value = true;
    isAdmin.value = response.data.role === "ADMIN";
  } catch (error) {
    isLoggedIn.value = false;
    isAdmin.value = false;
  }
};

const loadCartCount = async () => {
  if (!isLoggedIn.value || isAdmin.value) {
    cartCount.value = 0;
    return;
  }

  try {
    const response = await cartAPI.getCart();
    cartCount.value = response.data.reduce(
      (sum, item) => sum + Number(item.quantity || 0),
      0
    );
  } catch (error) {
    cartCount.value = 0;
  }
};

const refreshHeader = async () => {
  await loadMember();
  await loadCartCount();
};

const handleLogout = async () => {
  try {
    await memberAPI.logout();
  } finally {
    isLoggedIn.value = false;
    isAdmin.value = false;
    cartCount.value = 0;
    closeMenu();
    window.dispatchEvent(new Event("cart-updated"));
    router.push("/");
  }
};

onMounted(() => {
  window.addEventListener("scroll", handleScroll);
  window.addEventListener("cart-updated", loadCartCount);
  window.addEventListener("focus", refreshHeader);
  refreshHeader();
});

watch(
  () => route.fullPath,
  () => {
    closeMenu();
    refreshHeader();
  }
);

onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
  window.removeEventListener("cart-updated", loadCartCount);
  window.removeEventListener("focus", refreshHeader);
});
</script>

<style scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background-color: var(--color-bg);
  border-bottom: 1px solid transparent;
  transition: all 0.3s ease;
  height: var(--header-height);
}

.header--scrolled {
  border-bottom-color: var(--color-border);
  box-shadow: 0 2px 20px rgba(61, 74, 46, 0.06);
}

.header__inner {
  max-width: var(--container-width);
  margin: 0 auto;
  padding: 0 40px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header__logo {
  flex-shrink: 0;
}

.logo-text {
  font-family: var(--font-en);
  font-size: 22px;
  font-weight: 400;
  letter-spacing: 6px;
  color: var(--color-olive);
  transition: color 0.3s ease;
}

.logo-text:hover {
  color: var(--color-gold);
}

.header__nav {
  display: flex;
  align-items: center;
  gap: 36px;
}

.nav-item {
  font-family: var(--font-kr);
  font-size: 13px;
  font-weight: 400;
  letter-spacing: 1px;
  color: var(--color-text);
  position: relative;
  padding-bottom: 2px;
  transition: color 0.3s ease;
}

.nav-item::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 1px;
  background-color: var(--color-gold);
  transition: width 0.3s ease;
}

.nav-item:hover,
.nav-item.router-link-active {
  color: var(--color-olive);
}

.nav-item:hover::after,
.nav-item.router-link-active::after {
  width: 100%;
}

.header__actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.action-icon {
  color: var(--color-text);
  display: flex;
  align-items: center;
  transition: color 0.3s ease;
}

.action-icon:hover {
  color: var(--color-olive);
}

.admin-link,
.logout-btn {
  font-size: 14px;
  letter-spacing: 1px;
}

.logout-btn {
  font-family: var(--font-kr);
}

.cart-icon {
  position: relative;
}

.cart-badge {
  position: absolute;
  top: -6px;
  right: -6px;
  background-color: var(--color-gold);
  color: white;
  font-size: 10px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  border-radius: 999px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.menu-toggle {
  display: none;
  flex-direction: column;
  gap: 5px;
  padding: 4px;
}

.menu-toggle span {
  display: block;
  width: 22px;
  height: 1px;
  background-color: var(--color-text);
  transition: all 0.3s ease;
}

.menu-toggle.open span:nth-child(1) {
  transform: translateY(6px) rotate(45deg);
}

.menu-toggle.open span:nth-child(2) {
  opacity: 0;
}

.menu-toggle.open span:nth-child(3) {
  transform: translateY(-6px) rotate(-45deg);
}

.mobile-menu {
  display: none;
  flex-direction: column;
  background-color: var(--color-bg);
  border-top: 1px solid var(--color-border);
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s ease;
}

.mobile-menu.open {
  max-height: 560px;
}

.mobile-nav-item {
  padding: 16px 40px;
  font-size: 14px;
  letter-spacing: 1px;
  color: var(--color-text);
  border-bottom: 1px solid var(--color-border-light);
  transition: color 0.3s ease;
  text-align: left;
}

.mobile-nav-item:hover {
  color: var(--color-olive);
}

.mobile-logout-btn {
  font-family: var(--font-kr);
}

@media (max-width: 1024px) {
  .header__nav {
    display: none;
  }

  .menu-toggle {
    display: flex;
  }

  .mobile-menu {
    display: flex;
  }
}

@media (max-width: 768px) {
  .header__inner {
    padding: 0 20px;
  }
}
</style>
