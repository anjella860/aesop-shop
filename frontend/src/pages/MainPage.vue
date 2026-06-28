<template>
  <div class="main-page">
    <!-- 히어로 배너 -->
    <section class="hero">
      <div class="hero__bg"></div>
      <div class="hero__content">
        <p class="hero__sub">NEW COLLECTION</p>
        <h1 class="hero__title">자연에서 온<br />순수한 아름다움</h1>
        <p class="hero__desc">피부와 감각을 위한 정제된 포뮬러</p>
        <RouterLink to="/new-notable" class="btn-outline hero__btn"
          >컬렉션 보기</RouterLink
        >
      </div>
    </section>

    <!-- 베스트셀러 -->
    <section class="section bestseller">
      <div class="container">
        <div class="section__header">
          <p class="section-subtitle">BESTSELLER</p>
          <h2 class="section-title">베스트셀러</h2>
          <div class="divider"></div>
        </div>
        <div class="product-grid" v-if="bestsellers.length > 0">
          <div
            class="product-card"
            v-for="product in bestsellers"
            :key="product.id"
            @click="goToProduct(product.id)"
          >
            <div class="product-card__img">
              <img
                :src="product.imageUrl"
                :alt="product.name"
                @error="onImgError"
              />
            </div>
            <div class="product-card__info">
              <p class="product-card__category">
                {{ getCategoryName(product.categoryId) }}
              </p>
              <h3 class="product-card__name">{{ product.name }}</h3>
              <p class="product-card__volume">{{ product.volume }}</p>
              <p class="product-card__price">
                {{ product.price.toLocaleString() }}원
              </p>
            </div>
          </div>
        </div>
        <div class="section__more">
          <RouterLink to="/skincare" class="btn-outline">전체 보기</RouterLink>
        </div>
      </div>
    </section>

    <!-- 카테고리 배너 -->
    <section class="section category-banner">
      <div class="container">
        <div class="banner-grid">
          <RouterLink to="/skincare" class="banner-card">
            <div class="banner-card__bg banner-card__bg--skincare"></div>
            <div class="banner-card__content">
              <h3>스킨케어</h3>
              <p>Skincare</p>
            </div>
          </RouterLink>
          <RouterLink to="/fragrance" class="banner-card">
            <div class="banner-card__bg banner-card__bg--fragrance"></div>
            <div class="banner-card__content">
              <h3>향수</h3>
              <p>Fragrance</p>
            </div>
          </RouterLink>
          <RouterLink to="/hand-body" class="banner-card">
            <div class="banner-card__bg banner-card__bg--handbody"></div>
            <div class="banner-card__content">
              <h3>핸드 & 바디</h3>
              <p>Hand & Body</p>
            </div>
          </RouterLink>
        </div>
      </div>
    </section>

    <!-- 신제품 -->
    <section class="section new-arrivals">
      <div class="container">
        <div class="section__header">
          <p class="section-subtitle">NEW ARRIVALS</p>
          <h2 class="section-title">신제품</h2>
          <div class="divider"></div>
        </div>
        <div class="product-grid" v-if="newProducts.length > 0">
          <div
            class="product-card"
            v-for="product in newProducts"
            :key="product.id"
            @click="goToProduct(product.id)"
          >
            <div class="product-card__img">
              <img
                :src="product.imageUrl"
                :alt="product.name"
                @error="onImgError"
              />
            </div>
            <div class="product-card__info">
              <p class="product-card__category">
                {{ getCategoryName(product.categoryId) }}
              </p>
              <h3 class="product-card__name">{{ product.name }}</h3>
              <p class="product-card__volume">{{ product.volume }}</p>
              <p class="product-card__price">
                {{ product.price.toLocaleString() }}원
              </p>
            </div>
          </div>
        </div>
        <div class="section__more">
          <RouterLink to="/new-notable" class="btn-outline"
            >신제품 전체 보기</RouterLink
          >
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { productAPI, categoryAPI } from "../api/index.js";

const router = useRouter();
const bestsellers = ref([]);
const newProducts = ref([]);
const categories = ref([]);

const fetchData = async () => {
  try {
    const [productsRes, categoriesRes] = await Promise.all([
      productAPI.getAll(),
      categoryAPI.getAll(),
    ]);
    categories.value = categoriesRes.data;
    const all = productsRes.data;
    bestsellers.value = all.filter((p) => p.isBestseller).slice(0, 4);
    newProducts.value = all.slice(-4).reverse();
  } catch (e) {
    console.error("데이터 로딩 실패", e);
  }
};

const getCategoryName = (categoryId) => {
  const cat = categories.value.find((c) => c.id === categoryId);
  return cat ? cat.name : "";
};

const goToProduct = (id) => {
  router.push(`/product/${id}`);
};

const onImgError = (e) => {
  e.target.src = "https://via.placeholder.com/400x400/3D4A2E/FAFAF7?text=AESOP";
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped>
/* 히어로 */
.hero {
  position: relative;
  height: 100vh;
  min-height: 600px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  overflow: hidden;
}

.hero__bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #2c3820 0%, #3d4a2e 40%, #5c6e42 100%);
}

.hero__content {
  position: relative;
  z-index: 1;
  color: var(--color-bg);
}

.hero__sub {
  font-family: var(--font-en);
  font-size: 12px;
  letter-spacing: 6px;
  color: var(--color-gold-light);
  margin-bottom: 24px;
}

.hero__title {
  font-family: var(--font-en);
  font-size: 72px;
  font-weight: 300;
  line-height: 1.15;
  letter-spacing: 2px;
  color: var(--color-bg);
  margin-bottom: 20px;
}

.hero__desc {
  font-size: 16px;
  color: rgba(250, 250, 247, 0.7);
  letter-spacing: 2px;
  margin-bottom: 40px;
}

.hero__btn {
  border-color: var(--color-bg);
  color: var(--color-bg);
}

.hero__btn:hover {
  background-color: var(--color-bg);
  color: var(--color-olive);
}

/* 섹션 공통 */
.section {
  padding: 80px 0;
}

.section__header {
  margin-bottom: 48px;
}

.section__more {
  text-align: center;
  margin-top: 48px;
}

/* 상품 그리드 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
}

.product-card {
  cursor: pointer;
}

.product-card__img {
  aspect-ratio: 3/4;
  overflow: hidden;
  background-color: var(--color-border-light);
  margin-bottom: 16px;
}

.product-card__img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.product-card:hover .product-card__img img {
  transform: scale(1.05);
}

.product-card__category {
  font-size: 11px;
  letter-spacing: 2px;
  color: var(--color-gold);
  text-transform: uppercase;
  margin-bottom: 6px;
}

.product-card__name {
  font-family: var(--font-kr);
  font-size: 15px;
  font-weight: 400;
  color: var(--color-text);
  margin-bottom: 4px;
  line-height: 1.4;
}

.product-card__volume {
  font-size: 12px;
  color: var(--color-text-sub);
  margin-bottom: 8px;
}

.product-card__price {
  font-family: var(--font-en);
  font-size: 15px;
  color: var(--color-olive);
  letter-spacing: 1px;
}

/* 카테고리 배너 */
.banner-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.banner-card {
  position: relative;
  aspect-ratio: 3/4;
  overflow: hidden;
  display: block;
}

.banner-card__bg {
  position: absolute;
  inset: 0;
  transition: transform 0.5s ease;
}

.banner-card__bg--skincare {
  background: linear-gradient(160deg, #4a5e35 0%, #2c3820 100%);
}

.banner-card__bg--fragrance {
  background: linear-gradient(160deg, #8b7355 0%, #5c4a2e 100%);
}

.banner-card__bg--handbody {
  background: linear-gradient(160deg, #3d5a4a 0%, #2a3d35 100%);
}

.banner-card:hover .banner-card__bg {
  transform: scale(1.05);
}

.banner-card__content {
  position: relative;
  z-index: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-end;
  padding: 32px;
  text-align: center;
  color: var(--color-bg);
}

.banner-card__content h3 {
  font-family: var(--font-kr);
  font-size: 20px;
  font-weight: 400;
  margin-bottom: 4px;
}

.banner-card__content p {
  font-family: var(--font-en);
  font-size: 12px;
  letter-spacing: 3px;
  color: var(--color-gold-light);
}

/* 반응형 */
@media (max-width: 1024px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .hero__title {
    font-size: 48px;
  }
}

@media (max-width: 768px) {
  .banner-grid {
    grid-template-columns: 1fr;
  }

  .hero__title {
    font-size: 36px;
  }
}
</style>
