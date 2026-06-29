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
  logout: () => api.post("/api/members/logout"),
  signup: (data) => api.post("/api/members/signup", data),
  getMyInfo: () => api.get("/api/members/me"),
  updateMyInfo: (data) => api.put("/api/members/me", data),
  deleteMyInfo: (password) => api.delete("/mypage", { params: { password } }),
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
  cancelOrder: (id) => api.post(`/api/orders/${id}/cancel`),
};

// 결제 API
export const paymentAPI = {
  confirm: (data) => api.post("/api/payments/confirm", data),
  fail: (data) => api.post("/api/payments/fail", data),
  getByOrder: (orderId) => api.get(`/api/payments/order/${orderId}`),
};

// 리뷰 API
export const reviewAPI = {
  getByProduct: (productId) => api.get(`/api/reviews/${productId}`),
  create: (productId, data) => api.post(`/api/reviews/${productId}`, data),
  delete: (id) => api.delete(`/api/reviews/${id}`),
};

// QnA API
export const qnaAPI = {
  getByProduct: (productId) => api.get(`/api/qna/${productId}`),
  create: (productId, data) => api.post(`/api/qna/${productId}`, data),
  update: (id, data) => api.put(`/api/qna/${id}`, data),
  delete: (id) => api.delete(`/api/qna/${id}`),
};

// 공지사항 API
export const noticeAPI = {
  getAll: () => api.get("/api/notice"),
  getById: (id) => api.get(`/api/notice/${id}`),
};

// 관리자 API
export const adminAPI = {
  getMembers: () => api.get("/admin/members"),
  searchMembers: (email) =>
    api.get("/admin/members/search", { params: { email } }),
  changeMemberRole: (id, role) =>
    api.put(`/admin/members/${id}/role`, null, { params: { role } }),
  deleteMember: (id) => api.delete(`/admin/members/${id}`),
  addProduct: (data) =>
    api.post("/admin/products", data, {
      headers: { "Content-Type": "multipart/form-data" },
    }),
  updateProduct: (id, data) =>
    api.put(`/admin/products/${id}`, data, {
      headers: { "Content-Type": "multipart/form-data" },
    }),
  deleteProduct: (id) => api.delete(`/admin/products/${id}`),
  getOrders: () => api.get("/admin/orders"),
  changeOrderStatus: (id, status) =>
    api.put(`/admin/orders/${id}/status`, null, { params: { status } }),
  updateDelivery: (id, data) =>
    api.put(`/admin/orders/${id}/delivery`, null, { params: data }),
  getQna: () => api.get("/admin/qna"),
  answerQna: (id, answer) =>
    api.put(`/admin/qna/${id}/answer`, null, { params: { answer } }),
  deleteQnaAnswer: (id) => api.delete(`/admin/qna/${id}/answer`),
  getReviews: () => api.get("/admin/reviews"),
  deleteReview: (id) => api.delete(`/admin/reviews/${id}`),
  addNotice: (data) => api.post("/admin/notice", data),
  updateNotice: (id, data) => api.put(`/admin/notice/${id}`, data),
  deleteNotice: (id) => api.delete(`/admin/notice/${id}`),
};

export default api;
