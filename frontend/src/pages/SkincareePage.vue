<template>
  <div class="category-page">
    <div class="category-hero">
      <div class="category-hero__bg category-hero__bg--skincare"></div>
      <div class="category-hero__content">
        <p class="hero-en">SKINCARE</p>
        <h1 class="hero-title">스킨케어</h1>
      </div>
    </div>

    <div class="container">
      <div class="product-grid" v-if="products.length > 0">
        <div
          class="product-card"
          v-for="product in products"
          :key="product.id"
          @click="goToProduct(product.id)"
        >
          <div class="product-card__img">
            <img
              :src="product.imageUrl || fallbackImg"
              :alt="product.name"
              @error="onImgError"
            />
          </div>
          <div class="product-card__info">
            <h3 class="product-card__name">{{ product.name }}</h3>
            <p class="product-card__volume">{{ product.volume }}</p>
            <p class="product-card__price">{{ product.price?.toLocaleString() }}원</p>
          </div>
        </div>
      </div>
      <div v-else class="empty">
        <p>등록된 상품이 없습니다.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { loadProductsForCategory } from "../utils/categoryProducts.js";

const router = useRouter();
const products = ref([]);
const fallbackImg =
  "https://via.placeholder.com/400x480/3D4A2E/FAFAF7?text=AESOP";

const onImgError = (e) => { e.target.src = fallbackImg; };
const goToProduct = (id) => router.push(`/product/${id}`);

onMounted(async () => {
  try {
    products.value = await loadProductsForCategory(1);
  } catch (e) {
    console.error(e);
  }
});
</script>

<style scoped>
.category-page { background-color: var(--color-bg); padding-bottom: 80px; }

.category-hero {
  position: relative;
  height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  margin-bottom: 60px;
  overflow: hidden;
}

.category-hero__bg {
  position: absolute;
  inset: 0;
}

.category-hero__bg--skincare {
  background: linear-gradient(160deg, #4a5e35 0%, #2c3820 100%);
}

.category-hero__content {
  position: relative;
  z-index: 1;
  color: var(--color-bg);
}

.hero-en {
  font-family: var(--font-en);
  font-size: 11px;
  letter-spacing: 6px;
  color: var(--color-gold-light);
  margin-bottom: 12px;
}

.hero-title {
  font-family: var(--font-kr);
  font-size: 40px;
  font-weight: 300;
  color: var(--color-bg);
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
}

.product-card { cursor: pointer; }

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

.product-card:hover .product-card__img img { transform: scale(1.05); }

.product-card__name {
  font-size: 14px;
  font-weight: 400;
  color: var(--color-text);
  margin-bottom: 4px;
}

.product-card__volume {
  font-size: 12px;
  color: var(--color-text-sub);
  margin-bottom: 6px;
}

.product-card__price {
  font-family: var(--font-en);
  font-size: 14px;
  color: var(--color-olive);
}

.empty {
  text-align: center;
  padding: 80px 0;
  color: var(--color-text-sub);
  font-size: 14px;
}

@media (max-width: 1024px) { .product-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 640px) { .product-grid { grid-template-columns: 1fr 1fr; } }
</style>


