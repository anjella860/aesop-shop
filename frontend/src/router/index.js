import { createRouter, createWebHistory } from "vue-router";

const routes = [
  // 메인
  { path: "/", component: () => import("../pages/MainPage.vue") },

  // 상품
  { path: "/skincare", component: () => import("../pages/SkincareePage.vue") },
  { path: "/hand-body", component: () => import("../pages/HandBodyPage.vue") },
  { path: "/fragrance", component: () => import("../pages/FragrancePage.vue") },
  { path: "/hair", component: () => import("../pages/HairPage.vue") },
  { path: "/gifts", component: () => import("../pages/GiftsPage.vue") },
  {
    path: "/new-notable",
    component: () => import("../pages/NewNotablePage.vue"),
  },
  {
    path: "/product/:id",
    component: () => import("../pages/ProductDetailPage.vue"),
  },

  // 회원
  { path: "/login", component: () => import("../pages/LoginPage.vue") },
  { path: "/signup", component: () => import("../pages/SignupPage.vue") },
  { path: "/mypage", component: () => import("../pages/MyPage.vue") },

  // 장바구니 / 주문
  { path: "/cart", component: () => import("../pages/CartPage.vue") },
  { path: "/checkout", component: () => import("../pages/CheckoutPage.vue") },

  // 결제 결과 (토스페이먼츠 콜백)
  {
    path: "/payment/success",
    component: () => import("../pages/PaymentResultPage.vue"),
  },
  {
    path: "/payment/fail",
    component: () => import("../pages/PaymentResultPage.vue"),
  },
  // 고객센터
  { path: "/qna", component: () => import("../pages/QnaPage.vue") },
  { path: "/notice", component: () => import("../pages/NoticePage.vue") },

  // 관리자
  { path: "/admin", component: () => import("../pages/AdminDashboardPage.vue") },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 };
  },
});

export default router;
