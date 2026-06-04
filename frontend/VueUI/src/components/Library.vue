<script setup>
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import UserDropdown from './UserDropdown.vue'
import { getBooks } from '../services/api'
import { authUser } from '../stores/auth'

const router = useRouter()
const searchQuery = ref('')
const activeFilter = ref('All')
const books = ref([])
const isLoading = ref(false)
const errorMessage = ref('')
const hasLoadedBooks = ref(false)

const navItems = [
  { label: 'Home', to: '/' },
  { label: 'Catalog', sectionId: 'catalog' },
]

const isSignedIn = computed(() => Boolean(authUser.value))

const filters = ['All', 'With PDF', 'No PDF', 'In stock']

const bookCount = computed(() => books.value.length)

const displayBooks = computed(() =>
  books.value.map((book) => ({
    ...book,
    initials: getInitials(book.title),
    description: book.description || 'No description yet.',
    publisherName: book.publisherName || 'Unknown publisher',
  })),
)

const filteredBooks = computed(() => {
  const query = searchQuery.value.trim().toLowerCase()

  return displayBooks.value.filter((book) => {
    const matchesFilter =
      activeFilter.value === 'All' ||
      (activeFilter.value === 'With PDF' && Boolean(book.pdfUrl)) ||
      (activeFilter.value === 'No PDF' && !book.pdfUrl) ||
      (activeFilter.value === 'In stock' && Number(book.stock) > 0)

    const matchesQuery =
      !query ||
      [book.title, book.description, book.publisherName]
        .join(' ')
        .toLowerCase()
        .includes(query)

    return matchesFilter && matchesQuery
  })
})

async function loadBooks() {
  isLoading.value = true
  errorMessage.value = ''
  hasLoadedBooks.value = false

  try {
    const payload = await getBooks()
    books.value = Array.isArray(payload) ? payload : []
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : 'Could not load books.'
  } finally {
    isLoading.value = false
    await nextTick()
    hasLoadedBooks.value = true
  }
}

function getInitials(title) {
  if (!title) {
    return 'BA'
  }

  return title
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((word) => word.charAt(0))
    .join('')
    .toUpperCase()
}

function formatPrice(price) {
  if (price == null) {
    return 'Updating'
  }

  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    maximumFractionDigits: 0,
  }).format(Number(price))
}

function stockLabel(stock) {
  if (stock == null) {
    return 'Updating'
  }

  if (Number(stock) <= 0) {
    return 'Out of stock'
  }

  return `${stock} in stock`
}

function openBook(book) {
  if (book.pdfUrl) {
    window.open(book.pdfUrl, '_blank', 'noopener,noreferrer')
  }
}

function scrollToSection(sectionId) {
  document.getElementById(sectionId)?.scrollIntoView({
    behavior: 'smooth',
    block: 'start',
  })
}

function handleNavClick(item) {
  if (item.to) {
    router.push(item.to)
    return
  }

  scrollToSection(item.sectionId)
}

watch([searchQuery, activeFilter], async () => {
  hasLoadedBooks.value = false
  await nextTick()
  hasLoadedBooks.value = true
})

onMounted(async () => {
  await nextTick()
  loadBooks()
})
</script>

<template>
  <div id="library-top" class="library-page">
    <header class="topbar">
      <div class="topbar-side topbar-side-left">
        <RouterLink class="brand" to="/" aria-label="BOOK AREA home">
          <span class="brand-mark" aria-hidden="true">
            <svg viewBox="0 0 96 96" role="presentation">
              <g fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round">
                <path d="M24 73V36c0-13.25 10.75-24 24-24s24 10.75 24 24v37" stroke-width="1.8" />
                <path d="M24 73c9.4-2.4 16.45-2.73 24 3.75" stroke-width="1.8" />
                <path d="M72 73c-9.4-2.4-16.45-2.73-24 3.75" stroke-width="1.8" />
                <path d="M18 84l6-11h48l6 11c-10.4-2.1-19.15-2.3-30 4-10.85-6.3-19.6-6.1-30-4Z" stroke-width="1.8" />
                <path d="M48 76v11" stroke-width="1.6" />
                <path d="M27 79c9.2-1.65 14.6-1.05 21 3" stroke-width="1.5" />
                <path d="M69 79c-9.2-1.65-14.6-1.05-21 3" stroke-width="1.5" />
                <path d="M36 44v22" stroke-width="1.5" />
                <path d="M40 47v18" stroke-width="1.5" />
                <path d="M44 50v15" stroke-width="1.5" />
                <path d="M60 44v22" stroke-width="1.5" />
                <path d="M56 47v18" stroke-width="1.5" />
                <path d="M52 50v15" stroke-width="1.5" />
                <path d="M34 40l10 8" stroke-width="1.3" />
                <path d="M62 40l-10 8" stroke-width="1.3" />
              </g>
              <circle cx="48" cy="42" r="7" fill="currentColor" opacity="0.92" />
              <path d="M48 49.5l7 10.5H41z" fill="currentColor" opacity="0.22" />
              <g fill="currentColor">
                <path d="M48 25l1.6 2.8 2.8 1.6-2.8 1.6-1.6 2.8-1.6-2.8-2.8-1.6 2.8-1.6z" />
                <path d="M59 31l1 1.7 1.7 1-1.7 1-1 1.7-1-1.7-1.7-1 1.7-1z" />
                <path d="M37 33l1 1.7 1.7 1-1.7 1-1 1.7-1-1.7-1.7-1 1.7-1z" />
              </g>
            </svg>
          </span>
          <span class="brand-wordmark">BOOK AREA</span>
        </RouterLink>
      </div>

      <nav class="topbar-nav" aria-label="Primary">
        <button
          v-for="item in navItems"
          :key="item.label"
          type="button"
          @click="handleNavClick(item)"
        >
          {{ item.label }}
        </button>
      </nav>

      <div class="topbar-side topbar-side-right">
        <template v-if="!isSignedIn">
          <RouterLink class="topbar-utility" to="/login">Login</RouterLink>
        </template>

        <template v-else>
          <UserDropdown />
        </template>
      </div>
    </header>

    <main class="library-main">
      <section class="library-hero" aria-labelledby="library-title">
        <div class="library-hero-copy">
          <p class="library-kicker">Library</p>
          <h1 id="library-title">Khám phá kho sách trực tuyến</h1>
          <p>
            tìm kiếm nhanh và đọc ngay những cuốn sách bạn yêu thích.
          </p>

          <form class="library-search" role="search" @submit.prevent>
            <label for="library-search-input">Search the library</label>
            <div class="search-field">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path
                  d="m20 20-4.6-4.6m2.6-5.4a8 8 0 1 1-16 0 8 8 0 0 1 16 0Z"
                  fill="none"
                  stroke="currentColor"
                  stroke-linecap="round"
                  stroke-width="1.7"
                />
              </svg>
              <input
                id="library-search-input"
                v-model="searchQuery"
                type="search"
                placeholder="Search title, description, publisher"
              />
            </div>
          </form>
        </div>
      </section>

      <section id="catalog" class="catalog-section" aria-labelledby="catalog-title">
        <div class="section-intro catalog-intro">
          <div>
            <p class="library-kicker">Catalog</p>
            <h2 id="catalog-title">Sách còn ở trong thư viện</h2>
            <p class="catalog-count">{{ bookCount }} books</p>
          </div>

          <div class="mood-filter" aria-label="Filter books">
            <button
              v-for="filter in filters"
              :key="filter"
              type="button"
              :class="{ active: activeFilter === filter }"
              @click="activeFilter = filter"
            >
              {{ filter }}
            </button>
          </div>
        </div>

        <div class="book-list" :class="{ 'book-list--ready': hasLoadedBooks }" aria-live="polite">
          <div v-if="isLoading" class="empty-state">Loading books from the library...</div>

          <div v-else-if="errorMessage" class="empty-state empty-state-action">
            <span>Could not load the library: {{ errorMessage }}</span>
            <button type="button" @click="loadBooks">Try again</button>
          </div>

          <div v-else-if="books.length === 0" class="empty-state">
            The library is empty. Add books in SQL Server to show them here.
          </div>

          <div v-else-if="filteredBooks.length === 0" class="empty-state">
            No book matches that search yet. Try another title, description, or publisher.
          </div>

          <template v-else>
            <article
              v-for="(book, index) in filteredBooks"
              :key="book.id ?? book.title"
              class="library-book"
              :class="{ 'library-book--animated': hasLoadedBooks && index < 6 }"
              :style="{ '--book-index': Math.min(index, 5) }"
            >
              <div class="library-cover">
                <img v-if="book.coverUrl" :src="book.coverUrl" :alt="`Cover of ${book.title}`" loading="lazy" />
                <strong v-else class="cover-placeholder">{{ book.initials }}</strong>
              </div>

              <div class="library-book-copy">
                <div>
                  <p class="book-author">{{ book.publisherName }}</p>
                  <h3>{{ book.title }}</h3>
                  <p class="book-meta">{{ formatPrice(book.price) }} / {{ stockLabel(book.stock) }}</p>
                </div>
                <p>{{ book.description }}</p>
              </div>

              <div class="library-book-action">
                <button
                  type="button"
                  class="read-button"
                  :disabled="!book.pdfUrl"
                  @click="openBook(book)"
                >
                  {{ book.pdfUrl ? 'Đọc sách' : 'Chưa có PDF' }}
                </button>
              </div>
            </article>
          </template>
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped>
.library-page {
  position: relative;
  min-height: 100vh;
  padding: 6.8rem clamp(1rem, 3vw, 2rem) 5rem;
  color: var(--text);
}

.library-main {
  display: grid;
}

.library-hero,
.catalog-section {
  width: min(100%, var(--content-width));
  margin: 0 auto;
}

.library-hero {
  display: grid;
  gap: 1.2rem;
  padding: clamp(2.5rem, 6vw, 5rem) 0 0;
  animation: library-rise 360ms cubic-bezier(0.22, 1, 0.36, 1) both;
}

.library-hero-copy {
  display: grid;
  justify-items: start;
  gap: 1.1rem;
  max-width: 42rem;
  will-change: transform, opacity;
}

.library-kicker,
.book-author,
.catalog-count {
  font-size: 0.78rem;
  letter-spacing: 0.22em;
  text-transform: uppercase;
  color: var(--text-soft);
}

.library-hero h1,
.section-intro h2 {
  color: var(--text-strong);
  font-family: 'Prata', Georgia, serif;
  font-weight: 400;
}

.library-hero h1 {
  max-width: min(100%, 12ch);
  font-size: clamp(2.9rem, 6vw, 6rem);
  line-height: 0.96;
  letter-spacing: 0.01em;
}

.library-hero-copy > p:not(.library-kicker) {
  max-width: 38rem;
  font-size: clamp(1.02rem, 1.35vw, 1.22rem);
  color: var(--text);
}

.library-search {
  display: grid;
  gap: 0.65rem;
  width: min(100%, 36rem);
  margin-top: 0.75rem;
}

.library-search label {
  color: var(--text-soft);
  font-size: 0.86rem;
}

.search-field {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 0.8rem;
  align-items: center;
  min-height: 3.55rem;
  padding: 0 1rem;
  border: 1px solid color-mix(in oklab, var(--line-soft) 84%, white);
  border-radius: 8px;
  background: color-mix(in oklab, var(--surface) 86%, transparent);
  box-shadow: 0 18px 50px color-mix(in oklab, var(--accent-deep) 10%, transparent);
  transition:
    border-color 180ms ease-out,
    background-color 180ms ease-out,
    box-shadow 180ms ease-out,
    transform 180ms cubic-bezier(0.22, 1, 0.36, 1);
  will-change: transform;
}

.search-field:focus-within {
  transform: translateY(-1px);
  border-color: color-mix(in oklab, var(--accent-deep) 46%, white);
  background: color-mix(in oklab, var(--surface) 94%, white);
  box-shadow: 0 6px 12px color-mix(in oklab, var(--accent-deep) 10%, transparent);
}

.search-field svg {
  width: 1.15rem;
  color: var(--accent-deep);
}

.search-field input {
  width: 100%;
  min-width: 0;
  background: transparent;
  color: var(--text-strong);
  outline: 0;
}

.search-field input::placeholder {
  color: color-mix(in oklab, var(--text-soft) 72%, white);
}

.catalog-section {
  display: grid;
  gap: clamp(1.4rem, 3vw, 2.2rem);
  padding: clamp(3rem, 7vw, 5rem) 0 0;
  animation: library-rise 360ms cubic-bezier(0.22, 1, 0.36, 1) 80ms both;
}

.section-intro {
  display: grid;
  gap: 0.7rem;
  max-width: 42rem;
}

.section-intro h2 {
  font-size: clamp(2rem, 4vw, 4rem);
  line-height: 1.03;
}

.catalog-intro {
  max-width: none;
  grid-template-columns: minmax(0, 0.7fr) minmax(320px, 1fr);
  align-items: end;
}

.mood-filter {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 0.55rem;
}

.mood-filter button {
  position: relative;
  min-height: 2.5rem;
  padding: 0.58rem 0.88rem;
  border: 1px solid color-mix(in oklab, var(--line-soft) 82%, white);
  border-radius: 999px;
  background: color-mix(in oklab, var(--surface) 72%, transparent);
  color: var(--text);
  font-size: 0.86rem;
  transition:
    color 180ms ease-out,
    border-color 180ms ease-out,
    background-color 180ms ease-out,
    transform 180ms ease-out;
}

.mood-filter button:hover,
.mood-filter button.active {
  transform: translateY(-1px);
  border-color: color-mix(in oklab, var(--accent-deep) 42%, white);
  background: color-mix(in oklab, var(--accent-glow) 54%, white);
  color: var(--text-strong);
}

.mood-filter button:active {
  transform: translateY(0) scale(0.98);
}

.book-list {
  display: grid;
  gap: 0.85rem;
}

.library-book {
  display: grid;
  grid-template-columns: 8.5rem minmax(0, 1fr) minmax(7rem, auto);
  gap: clamp(1rem, 2vw, 1.6rem);
  align-items: center;
  min-height: 12rem;
  padding: 0.8rem clamp(0.8rem, 1.8vw, 1.2rem);
  border: 1px solid color-mix(in oklab, var(--line-soft) 78%, white);
  border-radius: 8px;
  background: color-mix(in oklab, var(--surface) 88%, transparent);
  box-shadow: 0 12px 36px color-mix(in oklab, var(--accent-deep) 7%, transparent);
  transition:
    transform 220ms cubic-bezier(0.22, 1, 0.36, 1),
    border-color 220ms ease-out,
    box-shadow 220ms ease-out;
}

.library-book--animated {
  animation: library-book-in 260ms cubic-bezier(0.22, 1, 0.36, 1) both;
  animation-delay: calc(var(--book-index, 0) * 40ms);
  will-change: transform, opacity;
}

.library-book:hover {
  transform: translateY(-3px);
  border-color: color-mix(in oklab, var(--line-strong) 72%, white);
  box-shadow: 0 22px 56px color-mix(in oklab, var(--accent-deep) 12%, transparent);
}

.library-cover {
  position: relative;
  min-height: 10.3rem;
  overflow: hidden;
  border-radius: 6px;
  background: linear-gradient(160deg, oklch(0.88 0.07 83), oklch(0.58 0.12 52) 48%, oklch(0.25 0.04 42));
  box-shadow: inset 0 0 0 1px rgba(255, 245, 225, 0.22);
}

.library-cover::before {
  content: '';
  position: absolute;
  inset: 0;
  z-index: 1;
  background:
    linear-gradient(90deg, rgba(34, 22, 15, 0.18), transparent 18%),
    repeating-linear-gradient(0deg, rgba(255, 248, 229, 0.08) 0 1px, transparent 1px 13px);
}

.library-cover img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  position: absolute;
  inset: 0;
  z-index: 2;
  display: grid;
  place-items: center;
  color: rgba(255, 246, 225, 0.92);
  font-family: 'Prata', Georgia, serif;
  font-size: clamp(1.9rem, 4vw, 3.2rem);
  font-weight: 400;
}

.library-book-copy {
  display: grid;
  gap: 0.8rem;
}

.library-book-copy h3 {
  color: var(--text-strong);
  font-size: clamp(1.3rem, 2vw, 1.95rem);
  line-height: 1.08;
}

.book-meta {
  margin-top: 0.2rem;
  color: var(--text-soft);
}

.library-book-copy > p {
  max-width: 58ch;
}

.library-book-action {
  display: grid;
  justify-items: end;
  gap: 0.7rem;
}

.library-book-action .read-button {
  display: grid;
  min-width: 7.8rem;
  min-height: 2.8rem;
  padding: 0 1rem;
  place-items: center;
  border: 1px solid color-mix(in oklab, var(--line-soft) 82%, white);
  border-radius: 8px;
  background: color-mix(in oklab, var(--surface) 86%, white);
  color: var(--text-strong);
  font-weight: 700;
  transition:
    transform 180ms ease-out,
    border-color 180ms ease-out,
    background-color 180ms ease-out;
}

.library-book-action .read-button:hover:not(:disabled) {
  transform: translateY(-1px);
  border-color: color-mix(in oklab, var(--accent-deep) 44%, white);
  background: color-mix(in oklab, var(--accent-glow) 62%, white);
}

.library-book-action .read-button:active:not(:disabled) {
  transform: translateY(0) scale(0.98);
}

.library-book-action .read-button:disabled {
  cursor: not-allowed;
  opacity: 0.58;
}

.empty-state {
  padding: 1.4rem;
  border: 1px solid color-mix(in oklab, var(--line-soft) 82%, white);
  border-radius: 8px;
  color: var(--text-soft);
  background: color-mix(in oklab, var(--surface) 82%, transparent);
}

.empty-state-action {
  display: flex;
  flex-wrap: wrap;
  gap: 0.85rem;
  align-items: center;
  justify-content: space-between;
}

.empty-state-action button {
  min-height: 2.4rem;
  padding: 0 0.9rem;
  border: 1px solid color-mix(in oklab, var(--accent-deep) 34%, white);
  border-radius: 8px;
  background: color-mix(in oklab, var(--accent-glow) 54%, white);
  color: var(--text-strong);
  font-weight: 700;
}

@media (max-width: 1020px) {
  .catalog-intro {
    grid-template-columns: 1fr;
  }

  .mood-filter {
    justify-content: flex-start;
  }

  .library-book {
    grid-template-columns: 7rem minmax(0, 1fr);
    align-items: start;
  }

  .library-book-action {
    grid-column: 1 / -1;
    justify-items: start;
  }
}

@media (max-width: 760px) {
  .library-page {
    padding: 10rem 0.9rem 5rem;
  }

  .library-hero h1 {
    max-width: 100%;
    font-size: clamp(2.5rem, 14vw, 4.6rem);
    letter-spacing: 0.005em;
  }

  .library-book {
    grid-template-columns: 5.8rem minmax(0, 1fr);
    gap: 0.85rem;
  }

  .library-cover {
    min-height: 8.4rem;
  }

  .library-book-action .read-button {
    width: 100%;
  }
}

@keyframes library-rise {
  from {
    opacity: 0;
    transform: translateY(14px);
  }
}

@keyframes library-book-in {
  from {
    opacity: 0;
    transform: translateY(14px);
  }
}

@media (prefers-reduced-motion: reduce) {
  .library-hero,
  .catalog-section,
  .library-book--animated {
    animation-duration: 0.01ms !important;
    animation-delay: 0ms !important;
    animation-iteration-count: 1 !important;
  }

  .search-field,
  .mood-filter button,
  .library-book,
  .library-book-action .read-button {
    transition-duration: 0.01ms !important;
  }
}
</style>
