<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

const bookdetail = ref({
  images: [{id: '', imageFileName: ''}],
  title: '',
  price: '',
  authors: [{id:'', name:''}],
  categories: [{id:'', name:''}],
  description: '',
  publisher: [{id:'', name:'', description: '', createdAt: ''}]
})

onMounted(() => {
  axios.get("http://localhost:8080/data/bookdetails/1")
    .then(res => {
      bookdetail.value = res.data
    })
    .catch(err => {
      console.error("Lỗi:", err)
    })
})

</script>

<template>
  <div class="book-detail-page">
    <!-- Book Detail -->
    <div>
      <!-- Breadcrumb -->
      <div class="breadcrumb">
        <RouterLink to="/">Trang chủ</RouterLink>
        <span>Tên sách</span>
        <span></span>
      </div>

      <!-- Main Content -->
      <div class="detail-layout">
        <!-- Left: Book Cover -->
        <div class="cover-section">
          <div class="cover-wrapper">
            <img
              v-if="bookdetail.images"
              :src="bookdetail.images[0]?.imageFileName"
              :alt="bookdetail.title"
              class="cover-image"
            />
            <div>
              <span>Category</span>
            </div>
          </div>
        </div>

        <!-- Right: Book Info -->
        <div class="info-section">
          <!-- Title -->
          <h1 class="book-title">{{ bookdetail.title }}</h1>

          <!-- Category -->
          <p class="book-category">
            {{ bookdetail.categories[0]?.name }}
          </p>

          <!-- Price -->
          <div class="price-section">
            <span class="price">${{ bookdetail.price }}</span>
          </div>

          <!-- Divider -->
          <div class="divider"></div>

          <!-- Author -->
          <div class="info-group">
            <label>Tác giả:</label>
            <p>{{ bookdetail.authors[0]?.name }}</p>
          </div>

          <!-- Publisher -->
          <div class="info-group">
            <label>Nhà xuất bản:</label>
            <p>{{ bookdetail.publisher[0]?.name }}</p>
          </div>

          <!-- Description -->
          <div class="info-group description-group">
            <label>Mô tả:</label>
            <p class="description-text">{{ bookdetail.description }}</p>
          </div>

          <!-- Additional Info -->
          <!-- <div class="info-grid" v-if="bookdetail.pageNumber != null || bookdetail.createdAt != null">
            <div class="info-item" v-if="book.rating">
              <span class="label">Đánh giá:</span>
              <span class="value">⭐ {{ book.rating }}/5</span>
            </div>
            <div class="info-item" v-if="bookdetail.pageNumber != null">
              <span class="label">Số trang:</span>
              <span class="value">{{ bookdetail.pageNumber }}</span>
            </div>
            <div class="info-item" v-if="bookdetail.publicationDate != null">
              <span class="label">Ngày xuất bản:</span>
              <span class="value">{{ new Date(bookdetail.createdAt).toLocaleDateString('vi-VN') }}</span>
            </div>
          </div> -->

          <!-- Divider -->
          <div class="divider"></div>

          <!-- Add to Cart Button -->
          <button class="button button-add-cart" @click="">
            <span class="cart-icon">🛒</span>
            Thêm vào giỏ hàng
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.book-detail-page {
  min-height: 100vh;
  padding: 2rem clamp(1rem, 2.5%, 2.5rem);
  margin-top: 4.5rem;
}

/* Breadcrumb */
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 3rem;
  font-size: 0.95rem;
  color: var(--text-soft);
}

.breadcrumb a {
  color: var(--accent-deep);
  text-decoration: none;
  transition: color 200ms ease;
}

.breadcrumb a:hover {
  color: var(--accent);
  text-decoration: underline;
}

/* Layout */
.detail-layout {
  display: grid;
  grid-template-columns: 3fr 4fr;
  gap: 3rem;
  max-width: 1400px;
  margin: 0 auto;
}

/* Left Section: Cover */
.cover-section {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 500px;
}

.cover-wrapper {
  width: 100%;
  aspect-ratio: 3 / 4;
  border-radius: 16px;
  overflow: hidden;
  background: linear-gradient(135deg, #f5e8d8 0%, #d9b88d 100%);
  box-shadow: 0 20px 60px rgba(139, 96, 57, 0.2);
  border: 1px solid rgba(139, 96, 57, 0.1);
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(245, 221, 154, 0.95), rgba(208, 156, 84, 0.9));
  font-size: 1.5rem;
  color: #5a3321;
  font-weight: 600;
  text-align: center;
  padding: 2rem;
}

/* Right Section: Info */
.info-section {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  padding-top: 1rem;
}

/* Title */
.book-title {
  font-family: 'Prata', Georgia, serif;
  font-size: clamp(2rem, 4vw, 2.8rem);
  font-weight: 400;
  line-height: 1.2;
  color: var(--text-strong);
  margin: 0;
}

/* Category */
.book-category {
  font-size: 0.875rem;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: var(--accent-deep);
  font-weight: 600;
  margin: 0;
}

/* Price */
.price-section {
  display: flex;
  align-items: baseline;
  gap: 1rem;
}

.price {
  font-size: 2rem;
  font-weight: 700;
  color: #e74c3c;
  font-family: 'Raleway', sans-serif;
}

/* Divider */
.divider {
  height: 1px;
  background: linear-gradient(
    90deg,
    rgba(139, 96, 57, 0.2),
    rgba(139, 96, 57, 0.1),
    rgba(139, 96, 57, 0.2)
  );
  margin: 0.5rem 0;
}

/* Info Group */
.info-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.info-group label {
  font-size: 0.875rem;
  font-weight: 600;
  letter-spacing: 0.05em;
  color: var(--text-soft);
  text-transform: uppercase;
}

.info-group p {
  font-size: 1rem;
  color: var(--text);
  line-height: 1.6;
  margin: 0;
}

.description-group {
  gap: 0.75rem;
  margin-top: 1rem;
}

.description-text {
  font-size: 1rem !important;
  color: var(--text) !important;
  line-height: 1.8 !important;
  text-align: justify;
}

/* Info Grid */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  padding: 1rem 0;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.info-item .label {
  font-size: 0.8rem;
  font-weight: 600;
  letter-spacing: 0.05em;
  color: var(--text-soft);
  text-transform: uppercase;
}

.info-item .value {
  font-size: 1rem;
  color: var(--text);
  font-weight: 500;
}

/* Buttons */
.button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 1rem 2rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 300ms ease;
}

.button-add-cart {
  background: #27ae60;
  color: #fff;
  min-height: 3.5rem;
  font-size: 1.1rem;
  margin-top: 1rem;
  width: 100%;
  box-shadow: 0 8px 24px rgba(39, 174, 96, 0.25);
}

.button-add-cart:hover {
  background: #229954;
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(39, 174, 96, 0.35);
}

.button-add-cart:active {
  transform: translateY(0);
}

.cart-icon {
  font-size: 1.3rem;
}

.button-primary {
  background: #d18a3b;
  color: #fbf3e7;
  box-shadow: 0 8px 24px rgba(209, 138, 59, 0.25);
}

.button-primary:hover {
  background: #b8711a;
  transform: translateY(-2px);
}

/* States */
.loading-container,
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  gap: 2rem;
  text-align: center;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid rgba(139, 96, 57, 0.2);
  border-top-color: var(--accent-deep);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.error-message {
  font-size: 1.1rem;
  color: #e74c3c;
  margin: 0;
}

/* Responsive */
@media (max-width: 1024px) {
  .detail-layout {
    grid-template-columns: 2fr 3fr;
    gap: 2rem;
  }

  .book-title {
    font-size: clamp(1.8rem, 3vw, 2.5rem);
  }

  .info-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .book-detail-page {
    padding: 1rem 1rem;
  }

  .detail-layout {
    grid-template-columns: 1fr;
    gap: 2rem;
  }

  .cover-section {
    min-height: 400px;
  }

  .cover-wrapper {
    aspect-ratio: 3 / 4;
  }

  .book-title {
    font-size: clamp(1.5rem, 2.5vw, 2.2rem);
  }

  .price {
    font-size: 1.8rem;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .button-add-cart {
    width: 100%;
  }

  .breadcrumb {
    margin-bottom: 2rem;
  }
}

@media (max-width: 480px) {
  .detail-layout {
    gap: 1.5rem;
  }

  .cover-section {
    min-height: 300px;
  }

  .book-title {
    font-size: 1.5rem;
  }

  .price {
    font-size: 1.5rem;
  }

  .button {
    padding: 0.875rem 1.5rem;
  }

  .info-group label {
    font-size: 0.8rem;
  }

  .info-group p {
    font-size: 0.95rem;
  }
}
</style>
