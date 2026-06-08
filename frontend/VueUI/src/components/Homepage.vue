<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { gsap } from 'gsap'
import AppFooter from './layout/AppFooter.vue'
import UserDropdown from './UserDropdown.vue'
import { getBooks } from '../services/api'
import { authUser } from '../stores/auth'

const router = useRouter()
const pageRoot = ref(null)
const books = ref([])
const isLoading = ref(false)
const errorMessage = ref('')
let motionContext
let homeMotionStarted = false

const navItems = [
  { label: 'Trang chủ', sectionId: 'home' },
  { label: 'Thư viện', to: '/library' },
  { label: 'Sách mới', sectionId: 'latest-books' },
]

const isSignedIn = computed(() => Boolean(authUser.value))

const latestBooks = computed(() =>
  [...books.value]
    .sort((left, right) => getBookTimestamp(right) - getBookTimestamp(left))
    .slice(0, 4)
    .map((book) => ({
      ...book,
      initials: getInitials(book.title),
      publisherName: book.publisherName || 'Book Area',
      summary: summarizeDescription(book.description),
    })),
)

async function loadBooks() {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const payload = await getBooks()
    books.value = Array.isArray(payload) ? payload : []
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : 'Không thể tải danh sách sách.'
  } finally {
    isLoading.value = false
  }
}

function getBookTimestamp(book) {
  const timestamp = Date.parse(book?.createdAt ?? '')
  return Number.isNaN(timestamp) ? 0 : timestamp
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

function summarizeDescription(description) {
  if (!description) {
    return 'Những cuốn sách mới thêm sẽ xuất hiện tại đây.'
  }

  const words = description.trim().split(/\s+/)

  if (words.length <= 18) {
    return description.trim()
  }

  return `${words.slice(0, 18).join(' ')}...`
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

function goToLibrary() {
  router.push('/library')
}

function initHomeMotion() {
  if (
    homeMotionStarted ||
    !pageRoot.value ||
    window.matchMedia('(prefers-reduced-motion: reduce)').matches
  ) {
    return
  }

  homeMotionStarted = true

  motionContext = gsap.context(() => {
    const timeline = gsap.timeline({
      defaults: {
        duration: 0.48,
        ease: 'power2.out',
      },
    })

    timeline
      .from('.topbar', { y: -12, duration: 0.34 })
      .from('.hero-media', { scale: 1.018, duration: 0.55 }, '-=0.26')
      .from('.hero-rail', { x: -12, duration: 0.36 }, '-=0.34')
      .from('.eyebrow, .hero-headline span, .hero-text, .hero-actions .button', {
        y: 18,
        duration: 0.42,
        stagger: 0.045,
      }, '-=0.28')
  }, pageRoot.value)
}

onMounted(async () => {
  await nextTick()
  initHomeMotion()
  loadBooks()
})

onUnmounted(() => {
  motionContext?.revert()
})
</script>

<template>
  <div ref="pageRoot" class="page-shell">
    <header class="topbar">
      <div class="topbar-side topbar-side-left">
        <button class="brand" type="button" aria-label="Về trang chủ Book Area" @click="scrollToSection('home')">
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
        </button>
      </div>

      <nav class="topbar-nav" aria-label="Điều hướng chính">
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
          <RouterLink class="topbar-utility" to="/login">Đăng nhập</RouterLink>
        </template>

        <template v-else>
          <UserDropdown />
        </template>
      </div>
    </header>

    <main class="homepage">
      <section id="home" class="hero">
        <div class="hero-media" aria-hidden="true"></div>
        <div class="hero-rail" aria-hidden="true">
          <span class="hero-rail-line"></span>
          <p>kệ sách yên, câu chuyện còn mãi</p>
          <span class="hero-rail-mark"></span>
        </div>

        <div class="hero-inner">
          <div class="hero-copy">
            <p class="eyebrow">BOOK AREA</p>
            <h1 class="hero-headline">
              <span>ĐỌC</span>
              <span>NHỮNG</span>
              <span>TRANG</span>
              <span>CÒN MÃI</span>
            </h1>
            <p class="hero-text">
              Một thư viện số dịu lại vào cuối ngày, nơi bạn tìm sách, mở tiếp chương đang đọc và nghe những câu chuyện được chọn lọc có gu.
            </p>

            <div class="hero-actions">
              <button class="button button-primary" type="button" @click="goToLibrary">
                Bắt đầu đọc
              </button>
              <button class="button button-secondary" type="button" @click="goToLibrary">
                Khám phá thư viện
              </button>
            </div>
          </div>
        </div>
      </section>

      <section id="latest-books" class="content-section latest-section">
        <div class="section-heading">
          <div class="section-mark">
            <span>Mới</span>
          </div>
          <div>
            <p class="eyebrow">Sách vừa cập nhật</p>
            <h2>Những cuốn mới trên kệ tối nay.</h2>
          </div>
        </div>

        <div v-if="isLoading" class="section-empty">Đang tải những cuốn sách mới nhất...</div>

        <div v-else-if="errorMessage" class="section-empty section-empty-action">
          <span>Không thể tải sách mới nhất: {{ errorMessage }}</span>
          <button type="button" @click="loadBooks">Tải lại</button>
        </div>

        <div v-else-if="latestBooks.length === 0" class="section-empty">
          Chưa có sách nào được thêm vào thư viện.
        </div>

        <div v-else class="latest-grid">
          <article v-for="book in latestBooks" :key="book.id ?? book.title" class="latest-book">
            <div class="latest-cover">
              <img v-if="book.coverUrl" :src="book.coverUrl" :alt="`Bìa sách ${book.title}`" loading="lazy" />
              <strong v-else>{{ book.initials }}</strong>
            </div>

            <div class="latest-copy">
              <p class="latest-meta">{{ book.publisherName }}</p>
              <h3>{{ book.title }}</h3>
              <p>{{ book.summary }}</p>
            </div>

            <div class="latest-action">
              <button
                class="button button-secondary latest-button"
                type="button"
                :disabled="!book.pdfUrl"
                @click="openBook(book)"
              >
                {{ book.pdfUrl ? 'Đọc sách' : 'Chưa có PDF' }}
              </button>
            </div>
          </article>
        </div>
      </section>
    </main>

    <AppFooter />
  </div>
</template>

<style scoped>
.page-shell {
  padding-inline: 0;
  background:
    linear-gradient(180deg, rgba(255, 250, 241, 0.42), transparent 34rem),
    radial-gradient(ellipse at 86% 18%, color-mix(in oklab, var(--accent) 18%, transparent), transparent 30rem);
}

.hero {
  position: relative;
  min-height: min(100svh, 980px);
  border-bottom: 1px solid color-mix(in oklab, var(--line-soft) 58%, white);
  background: oklch(0.96 0.017 76);
}

.hero-inner {
  width: min(100%, 1440px);
  margin: 0;
  grid-template-columns: minmax(0, 39rem) 1fr;
  padding: clamp(7.5rem, 12vw, 10rem) clamp(1.2rem, 4vw, 4.6rem) clamp(3.5rem, 6vw, 5.5rem);
  padding-left: clamp(5.6rem, 8vw, 8rem);
}

.hero-media {
  background-position: 73% center;
  filter: sepia(0.1) saturate(0.84) contrast(0.92) brightness(0.98);
  transform: scale(1.015);
}

.topbar,
.hero-media,
.hero-rail,
.hero-headline span,
.hero-text,
.hero-actions .button {
  will-change: transform;
}

.hero::before {
  background:
    linear-gradient(
      90deg,
      oklch(0.966 0.018 76 / 0.98) 0%,
      oklch(0.956 0.02 74 / 0.94) 24%,
      oklch(0.94 0.024 72 / 0.74) 44%,
      oklch(0.9 0.026 68 / 0.28) 68%,
      transparent 100%
    );
  backdrop-filter: blur(5px) saturate(0.94);
}

.hero::after {
  background:
    radial-gradient(ellipse at 18% 36%, rgba(255, 246, 226, 0.52), transparent 24rem),
    linear-gradient(180deg, rgba(54, 35, 22, 0.06), transparent 24%),
    linear-gradient(0deg, rgba(46, 29, 18, 0.14), transparent 30%);
  opacity: 0.64;
}

.hero-rail {
  display: grid;
  position: absolute;
  left: clamp(1.1rem, 2.5vw, 2.9rem);
  top: 50%;
  z-index: 3;
  transform: translateY(-50%);
}

.hero-rail p {
  color: color-mix(in oklab, var(--text-strong) 72%, transparent);
}

.hero-rail-mark {
  transform: rotate(45deg);
}

.hero-copy {
  max-width: 39rem;
}

.eyebrow {
  margin-bottom: clamp(0.95rem, 1.4vw, 1.4rem);
  color: color-mix(in oklab, var(--accent-deep) 88%, var(--text-strong));
}

.hero-copy h1 {
  max-width: min(100%, 10.6ch);
  font-size: clamp(3.6rem, 6.2vw, 6rem);
  line-height: 0.94;
  letter-spacing: 0.035em;
}

.hero-text {
  max-width: 31rem;
  margin-top: clamp(18px, 2vw, 28px);
  color: color-mix(in oklab, var(--text-strong) 74%, var(--text));
  font-size: clamp(1rem, 1.2vw, 1.12rem);
  line-height: 1.8;
}

.hero-actions {
  margin-top: clamp(22px, 2.5vw, 34px);
}

.hero-actions .button {
  border-radius: 8px;
}

.hero-actions .button-primary {
  background: color-mix(in oklab, var(--accent) 88%, var(--accent-deep));
}

.hero-actions .button-secondary {
  border-color: rgba(73, 45, 25, 0.16);
  background: rgba(255, 249, 238, 0.7);
}

@media (max-width: 768px) {
  .hero-inner {
    grid-template-columns: 1fr;
    padding-left: 24px;
    padding-right: 24px;
  }

  .hero-rail {
    display: none;
  }
}

.latest-section {
  width: min(100%, var(--content-width));
  margin: 0 auto;
  display: grid;
  gap: clamp(1.4rem, 3vw, 2.4rem);
  padding-inline: clamp(1rem, 2.5vw, 2rem);
}

.latest-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: clamp(0.85rem, 1.5vw, 1.2rem);
}

.latest-book {
  display: grid;
  grid-template-rows: auto 1fr auto;
  gap: 1rem;
  align-items: stretch;
  padding: 0.85rem;
  border: 1px solid color-mix(in oklab, var(--line-soft) 84%, white);
  border-radius: 8px;
  background:
    linear-gradient(180deg, color-mix(in oklab, var(--surface) 94%, white), color-mix(in oklab, var(--surface-soft) 62%, white));
  box-shadow: 0 4px 8px color-mix(in oklab, var(--accent-deep) 8%, transparent);
  transition:
    transform 220ms cubic-bezier(0.22, 1, 0.36, 1),
    border-color 220ms ease-out,
    box-shadow 220ms ease-out;
}

.latest-book:hover {
  transform: translateY(-3px);
  border-color: color-mix(in oklab, var(--line-strong) 72%, white);
  box-shadow: 0 6px 8px color-mix(in oklab, var(--accent-deep) 12%, transparent);
}

.latest-cover {
  position: relative;
  min-height: clamp(12rem, 22vw, 18rem);
  overflow: hidden;
  border-radius: 6px;
  background: linear-gradient(160deg, oklch(0.92 0.02 88), oklch(0.66 0.05 62) 50%, oklch(0.3 0.03 45));
  box-shadow: inset 0 0 0 1px rgba(255, 245, 225, 0.22);
}

.latest-cover::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    linear-gradient(90deg, rgba(34, 22, 15, 0.18), transparent 18%),
    radial-gradient(ellipse at 72% 12%, rgba(255, 248, 229, 0.18), transparent 12rem);
}

.latest-cover img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.latest-cover strong {
  position: absolute;
  inset: 0;
  display: grid;
  place-items: center;
  color: rgba(255, 246, 225, 0.9);
  font-family: 'Prata', Georgia, serif;
  font-size: clamp(1.8rem, 3.8vw, 3rem);
  font-weight: 400;
}

.latest-copy {
  display: grid;
  align-content: start;
  gap: 0.62rem;
  padding-inline: 0.1rem;
}

.latest-meta {
  font-size: 0.8rem;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: var(--text-soft);
}

.latest-copy h3 {
  color: var(--text-strong);
  font-size: clamp(1.18rem, 1.5vw, 1.45rem);
  line-height: 1.08;
}

.latest-copy p:last-child {
  max-width: 58ch;
  color: var(--text-soft);
  font-size: 0.93rem;
  line-height: 1.62;
}

.latest-action {
  display: flex;
  align-items: flex-end;
  padding-top: 0.3rem;
}

.latest-button {
  width: 100%;
  min-width: 0;
  min-height: 2.9rem;
  border-radius: 8px;
}

.latest-button:disabled {
  opacity: 0.58;
}

.section-empty {
  padding: 1.25rem;
  border: 1px solid color-mix(in oklab, var(--line-soft) 82%, white);
  border-radius: 8px;
  background: color-mix(in oklab, var(--surface) 82%, transparent);
  color: var(--text-soft);
}

.section-empty-action {
  display: flex;
  flex-wrap: wrap;
  gap: 0.85rem;
  align-items: center;
  justify-content: space-between;
}

.section-empty-action button {
  min-height: 2.4rem;
  padding: 0 0.9rem;
  border: 1px solid color-mix(in oklab, var(--accent-deep) 34%, white);
  border-radius: 8px;
  background: color-mix(in oklab, var(--accent-glow) 54%, white);
  color: var(--text-strong);
  font-weight: 700;
}

@media (max-width: 980px) {
  .latest-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .latest-book {
    align-items: stretch;
  }

  .latest-action {
    display: block;
  }
}

@media (max-width: 720px) {
  .hero {
    min-height: 88svh;
  }

  .hero::before {
    background:
      linear-gradient(
        90deg,
        oklch(0.966 0.018 76 / 0.98) 0%,
        oklch(0.956 0.02 74 / 0.9) 48%,
        oklch(0.94 0.024 72 / 0.62) 76%,
        oklch(0.9 0.026 68 / 0.3) 100%
      );
  }

  .hero-copy h1 {
    max-width: min(100%, 9.4ch);
    font-size: clamp(2.25rem, 10.8vw, 3.45rem);
    line-height: 0.98;
    letter-spacing: 0.025em;
  }

  .hero-text {
    max-width: min(100%, 18rem);
    color: var(--text-strong);
    line-height: 1.7;
  }

  .latest-grid {
    grid-template-columns: 1fr;
  }

  .latest-book {
    padding: 0.8rem;
  }

  .latest-cover {
    min-height: 15rem;
  }

  .latest-button {
    width: 100%;
  }
}
</style>
