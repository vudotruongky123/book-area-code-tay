<script setup>
import { ref, onMounted, computed  } from 'vue'
import axios from "axios"

const categories = ref([])
const books = ref([])
const searchQuery = ref('')
const selectedCategory = ref(null)

onMounted(() => {
  axios.get("http://localhost:8080/data/category")
    .then(res => {
      categories.value = res.data
    })
})

onMounted(() => {
  axios.get("http://localhost:8080/data/book")
    .then(res => {
      books.value = res.data
    })
})

const filteredBooks = computed(() => {
  if(searchQuery.value != "" && selectedCategory.value){
    // Tìm kiếm theo tên sách
    return books.value.filter(b =>
      b.title.toLowerCase().includes(searchQuery.value.toLowerCase()) &&
      b.categories.some(c => c.id === selectedCategory.value)
    );
  }else if(searchQuery.value != ""){
    // Tìm kiếm theo tên sách
    return books.value.filter(b =>
      b.title.toLowerCase().includes(searchQuery.value.toLowerCase())
    );
  }else if(!selectedCategory.value) {
    // ALL → trả toàn bộ
    return books.value
  }else{
    // lọc theo category id
    return books.value.filter(b =>
      b.categories.some(c => c.id === selectedCategory.value)
    );
  }  
});


</script>

<template>
  <section class="content-section section-products">
    <div class="section-heading">
      <div>
        <p class="eyebrow">Database Library</p>
        <h2>Discover books from our complete collection.</h2>
      </div>
    </div>

    <!-- Search and Filter -->
    <div class="products-controls">
      <div class="search-box">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="Search by title or author..."
          class="search-input"
        />
      </div>

      <div class="filter-buttons">
        <button
          :class="{ active: selectedCategory === null }"
          @click="selectedCategory = null"
          class="filter-btn"
        >
          All
        </button>
        <button
          v-for="c in categories"
          :key="c.id"
          :class="{ active: selectedCategory === c.id }"
          @click="selectedCategory = c.id"
          class="filter-btn"
        >
          {{ c.name }}
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-state">
      <p>Loading books...</p>
    </div>

    <!-- Products Grid -->
    <div v-else class="products-layout">
      <div v-if="filteredBooks.length === 0" class="no-results">
        <p>No books found matching your search.</p>
      </div>

      <div v-else class="product-grid">
        <article
          v-for="b in filteredBooks"
          :key="b.id"
          class="product-card"
        >
          <div class="product-cover">
            <img
              v-if="b.images"
              :src="b.images"
              :alt="b.title"
              class="cover-image"
            />
            <div v-else class="cover-placeholder">
              <span class="category-badge">{{ b.categories[0]?.name || "Chưa xác định" }}</span>
            </div>
          </div>  

          <div class="product-content">
            <p class="product-category">{{ b.categories[0]?.name || "Chưa xác định" }}</p>
            <h3 class="product-title">{{ b.title }}</h3>
            <p class="product-author">by: {{ b.authors[0]?.name || "Chưa xác định" }}</p>
            <p class="product-description">{{ b.description }}</p>

            <div class="product-footer">
              <div class="product-meta">
                <span v-if="b.rating" class="rating">
                  ⭐ {{ b.rating }}
                </span>
                <span class="price">${{ b.price.toLocaleString('vi-VN') }}</span>
              </div>
              <button class="button button-small">Read More</button>
            </div>
          </div>
        </article>
      </div>
    </div>
  </section>
</template>

<style scoped>
/* Section Styling */
.content-section {
  display: flex;
  flex-direction: column;
  gap: 3rem;
  padding: 4rem 2rem;
  max-width: 1400px;
  margin: 0 auto;
  min-height: 200vh;
}

.section-heading {
  display: flex;
  gap: 2rem;
  align-items: flex-start;
  margin-top: 40px;
}

.section-heading > div:last-child h2 {
  font-size: 2rem;
  line-height: 1.2;
  margin: 0.5rem 0 0;
  color: #1a1a1a;
  font-weight: 600;
}

.eyebrow {
  font-size: 0.875rem;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: #8b6039;
  font-weight: 600;
  margin: 0;
}

/* Controls */
.products-controls {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.search-box {
  flex: 1;
}

.search-input {
  width: 100%;
  padding: 0.875rem 1rem;
  border: 1px solid #d4b896;
  border-radius: 6px;
  font-size: 1rem;
  background: #faf7f2;
  color: #1a1a1a;
  transition: all 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: #8b6039;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(139, 96, 57, 0.1);
}

.filter-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.filter-btn {
  padding: 0.625rem 1.25rem;
  border: 1px solid #d4b896;
  border-radius: 24px;
  background: transparent;
  color: #8b6039;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-btn:hover {
  border-color: #8b6039;
  background: #faf7f2;
}

.filter-btn.active {
  background: #8b6039;
  color: #fff;
  border-color: #8b6039;
}

/* Grid Layout */
.products-layout {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 2rem;
}

/* Product Card */
.product-card {
  display: flex;
  flex-direction: column;
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
  border: 1px solid #e8dcc8;
  transition: all 0.3s ease;
  height: 100%;
}

.product-card:hover {
  border-color: #8b6039;
  box-shadow: 0 8px 24px rgba(139, 96, 57, 0.15);
  transform: translateY(-4px);
}

.product-cover {
  position: relative;
  width: 100%;
  aspect-ratio: 3 / 4;
  overflow: hidden;
  background: linear-gradient(135deg, #f5e8d8 0%, #d9b88d 100%);
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(245, 221, 154, 0.95), rgba(208, 156, 84, 0.9));
  font-size: 1rem;
  color: #5a3321;
  font-weight: 600;
}

.category-badge {
  background: rgba(255, 255, 255, 0.9);
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.05em;
  color: #8b6039;
}

/* Product Content */
.product-content {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  padding: 1.5rem;
  flex: 1;
}

.product-category {
  font-size: 0.75rem;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: #8b6039;
  font-weight: 600;
  margin: 0;
}

.product-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0.25rem 0;
  line-height: 1.3;
}

.product-author {
  font-size: 0.875rem;
  color: #8b6039;
  margin: 0;
  font-weight: 500;
}

.product-description {
  font-size: 0.875rem;
  color: #666;
  line-height: 1.5;
  margin: 0.5rem 0;
  flex: 1;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #e8dcc8;
}

.product-meta {
  display: flex;
  gap: 1rem;
  align-items: center;
  font-size: 0.875rem;
}

.rating {
  color: #f5a623;
  font-weight: 600;
}

.price {
  font-size: 1.125rem;
  font-weight: 700;
  color: #8b6039;
}

/* Button */
.button {
  padding: 0.625rem 1.25rem;
  border: none;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.button-small {
  background: #8b6039;
  color: #fff;
  padding: 0.5rem 1rem;
  font-size: 0.75rem;
}

.button-small:hover {
  background: #6b4728;
  transform: scale(1.05);
}

/* States */
.loading-state,
.no-results {
  text-align: center;
  padding: 3rem 2rem;
  color: #666;
  font-size: 1.125rem;
}

/* Responsive */
@media (max-width: 768px) {
  .content-section {
    padding: 2rem 1rem;
    gap: 2rem;
  }

  .section-heading {
    flex-direction: column;
    gap: 1rem;
  }

  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 1.5rem;
  }

  .section-heading > div:last-child h2 {
    font-size: 1.5rem;
  }

  .filter-buttons {
    gap: 0.5rem;
  }

  .filter-btn {
    padding: 0.5rem 1rem;
    font-size: 0.8rem;
  }
}

@media (max-width: 480px) {
  .product-grid {
    grid-template-columns: 1fr;
  }

  .search-input {
    padding: 0.75rem;
    font-size: 1rem;
  }
}
</style>
