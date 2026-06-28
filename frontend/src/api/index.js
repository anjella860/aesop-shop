import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true,
});

// 상품 API
export const productAPI = {
  getAll: () => api.get("/api/products"),
  getById: (id) => api.get(`/api/products/${id}`),
  getByCategory: (categoryId) =>
    api.get(`/api/products/category/${categoryId}`),
  getBestsellers: () => api.get("/api/products/bestsellers"),
};

// 카테고리 API
export const categoryAPI = {
  getAll: () => api.get("/api/categories"),
  getParents: () => api.get("/api/categories/parents"),
  getChildren: (parentId) => api.get(`/api/categories/${parentId}/children`),
};

// 회원 API
export const memberAPI = {
  login: (data) => api.post("/api/members/login", data),
  signup: (data) => api.post("/api/members/signup", data),
  getMyInfo: () => api.get("/api/members/me"),
  updateMyInfo: (data) => api.put("/api/members/me", data),
};

// 장바구니 API
export const cartAPI = {
  getCart: () => api.get("/api/cart"),
  addToCart: (data) => api.post("/api/cart", data),
  updateCart: (id, data) => api.put(`/api/cart/${id}`, data),
  removeFromCart: (id) => api.delete(`/api/cart/${id}`),
};

// 주문 API
export const orderAPI = {
  getOrders: () => api.get("/api/orders"),
  getOrderById: (id) => api.get(`/api/orders/${id}`),
  createOrder: (data) => api.post("/api/orders", data),
};

// 결제 API
export const paymentAPI = {
  confirm: (data) => api.post("/api/payments/confirm", data),
  fail: (data) => api.post("/api/payments/fail", data),
  getByOrder: (orderId) => api.get(`/api/payments/order/${orderId}`),
};

// 리뷰 API
export const reviewAPI = {
  getByProduct: (productId) => api.get(`/api/reviews/product/${productId}`),
  create: (data) => api.post("/api/reviews", data),
  delete: (id) => api.delete(`/api/reviews/${id}`),
};

// QnA API
export const qnaAPI = {
  getByProduct: (productId) => api.get(`/api/qna/product/${productId}`),
  create: (data) => api.post("/api/qna", data),
  delete: (id) => api.delete(`/api/qna/${id}`),
};

// 공지사항 API
export const noticeAPI = {
  getAll: () => api.get("/api/notice"),
  getById: (id) => api.get(`/api/notice/${id}`),
};

export default api;
