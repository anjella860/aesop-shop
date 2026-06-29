<template>
  <div class="category-page">
    <div class="category-hero">
      <div class="category-hero__bg" style="background: linear-gradient(160deg, #7a6a4a 0%, #4a3a20 100%)"></div>
      <div class="category-hero__content">
        <p class="hero-en">GIFTS</p>
        <h1 class="hero-title">선물하기</h1>
      </div>
    </div>
    <div class="container">
      <div class="product-grid" v-if="products.length > 0">
        <div class="product-card" v-for="p in products" :key="p.id" @click="go(p.id)">
          <div class="product-card__img">
            <span class="badge-gift">GIFT</span>
            <img :src="p.imageUrl || fb" :alt="p.name" @error="err" />
          </div>
          <div class="product-card__info">
            <h3 class="product-card__name">{{ p.name }}</h3>
            <p class="product-card__volume">{{ p.volume }}</p>
            <p class="product-card__price">{{ p.price?.toLocaleString() }}원</p>
          </div>
        </div>
      </div>
      <div v-else class="empty"><p>등록된 선물 상품이 없습니다.</p></div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { productAPI } from "../api/index.js";
const router = useRouter();
const products = ref([]);
const fb = "https://via.placeholder.com/400x480/3D4A2E/FAFAF7?text=AESOP";
const err = (e) => { e.target.src = fb; };
const go = (id) => router.push(`/product/${id}`);
onMounted(async () => {
  try { const res = await productAPI.getBestsellers(); products.value = res.data; } catch(e) {}
});
</script>
<style scoped>
.category-page { background-color: var(--color-bg); padding-bottom: 80px; }
.category-hero { position: relative; height: 320px; display: flex; align-items: center; justify-content: center; text-align: center; margin-bottom: 60px; overflow: hidden; }
.category-hero__bg { position: absolute; inset: 0; }
.category-hero__content { position: relative; z-index: 1; color: var(--color-bg); }
.hero-en { font-family: var(--font-en); font-size: 11px; letter-spacing: 6px; color: var(--color-gold-light); margin-bottom: 12px; }
.hero-title { font-family: var(--font-kr); font-size: 40px; font-weight: 300; }
.product-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 32px; }
.product-card { cursor: pointer; position: relative; }
.product-card__img { position: relative; aspect-ratio: 3/4; overflow: hidden; background-color: var(--color-border-light); margin-bottom: 16px; }
.badge-gift { position: absolute; top: 12px; left: 12px; background-color: var(--color-olive); color: white; font-family: var(--font-en); font-size: 10px; letter-spacing: 2px; padding: 3px 8px; z-index: 1; }
.product-card__img img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s; }
.product-card:hover .product-card__img img { transform: scale(1.05); }
.product-card__name { font-size: 14px; color: var(--color-text); margin-bottom: 4px; }
.product-card__volume { font-size: 12px; color: var(--color-text-sub); margin-bottom: 6px; }
.product-card__price { font-family: var(--font-en); font-size: 14px; color: var(--color-olive); }
.empty { text-align: center; padding: 80px 0; color: var(--color-text-sub); font-size: 14px; }
@media (max-width: 1024px) { .product-grid { grid-template-columns: repeat(2, 1fr); } }


/* portfolio mobile grid polish */
@media (max-width: 640px) {
  .category-page {
    padding-bottom: 64px;
  }

  .category-hero {
    height: 240px;
    margin-bottom: 36px;
  }

  .hero-title {
    font-size: 34px;
  }

  .hero-subtitle {
    letter-spacing: 5px;
  }

  .product-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 24px 14px;
  }

  .product-card__name {
    font-size: 14px;
    line-height: 1.45;
  }

  .product-card__volume,
  .product-card__price {
    font-size: 12px;
  }
}

@media (max-width: 360px) {
  .product-grid {
    grid-template-columns: 1fr;
  }
}

</style>
