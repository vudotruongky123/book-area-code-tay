<template>
  <footer class="app-footer">
    <div class="app-footer__inner">
      <section class="app-footer__main" aria-label="Thông tin BOOK AREA">
        <div class="app-footer__brand">
          <RouterLink class="app-footer__logo" to="/" aria-label="Về trang chủ BOOK AREA">
            <span class="app-footer__logo-mark" aria-hidden="true">
              <svg viewBox="0 0 24 24" focusable="false">
                <path d="M5 5.8c0-1.1.9-2 2-2h10.2c.9 0 1.6.7 1.6 1.6v14.8H7c-1.1 0-2-.9-2-2V5.8Z" />
                <path d="M7.2 3.8v13.7c0 .7.6 1.2 1.2 1.2h10.4" />
                <path d="M9.4 7.5h6.4M9.4 10.5h5.2" />
              </svg>
            </span>
            <span class="app-footer__wordmark">BOOK AREA</span>
          </RouterLink>

          <p class="app-footer__slogan">
            Một góc thư viện số dành cho những cuốn sách yên tĩnh, những lần đọc chậm, và
            những câu chuyện còn ở lại.
          </p>

          <a class="app-footer__support" href="mailto:ledong04082006@gmail.com">
            <span class="app-footer__support-icon" aria-hidden="true">
              <svg viewBox="0 0 24 24" focusable="false">
                <path d="M4.8 6.8h14.4v10.4H4.8V6.8Z" />
                <path d="m5.4 7.4 6.6 5.2 6.6-5.2" />
              </svg>
            </span>
            <span>Hỗ trợ: ledong04082006@gmail.com</span>
          </a>
        </div>

        <nav class="app-footer__nav" aria-label="Liên kết nhanh">
          <h2>LIÊN KẾT NHANH</h2>
          <RouterLink to="/">Trang chủ</RouterLink>
          <RouterLink to="/library">Thư viện của tôi</RouterLink>
          <a href="/#/latest-books" @click.prevent="scrollToLatestBooks">Sách mới nhất</a>
          <RouterLink to="/login">Đăng nhập hệ thống</RouterLink>
        </nav>

        <div class="app-footer__architecture">
          <h2>KIẾN TRÚC TRIỂN KHAI SAU</h2>
          <p>
            Mô hình giao diện tối ưu hóa khả năng chuyển đổi trực tiếp sang các Component
            hiển thị của Vue 3 (SFC) kết nối tới Spring Boot API.
          </p>

          <ul class="app-footer__tags" aria-label="Công nghệ triển khai">
            <li v-for="tag in architectureTags" :key="tag">
              <span aria-hidden="true">
                <svg viewBox="0 0 12 12" focusable="false">
                  <path d="M2.5 2.5h7v7h-7v-7Z" />
                  <path d="M4 6h4" />
                </svg>
              </span>
              {{ tag }}
            </li>
          </ul>
        </div>
      </section>

      <section class="app-footer__bottom" aria-label="Bản quyền">
        <p>© 2026 BOOK AREA. Dành cho những người đọc cần một khoảng lặng.</p>
        <p>Điều khoản dịch vụ <span aria-hidden="true">•</span> Chính sách bảo mật</p>
      </section>
    </div>
  </footer>
</template>

<script setup>
const architectureTags = ['Vue 3 Vuex', 'Spring Boot', 'SQL Server', 'MinIO Storage']

function scrollToLatestBooks() {
  const latestBooks = document.getElementById('latest-books')

  if (latestBooks) {
    latestBooks.scrollIntoView({
      behavior: 'smooth',
      block: 'start',
    })
    return
  }

  window.location.hash = '#/'
}
</script>

<style scoped>
.app-footer {
  width: 100%;
  margin-top: clamp(3rem, 6vw, 5.5rem);
  background: #f4eadc;
  color: #5f554b;
}

.app-footer__inner {
  width: min(100% - clamp(2rem, 5vw, 4rem), 1200px);
  margin: 0 auto;
}

.app-footer__main {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(11rem, 0.65fr) minmax(0, 1fr);
  gap: clamp(2rem, 5vw, 4.5rem);
  padding: 48px 0;
}

.app-footer__brand,
.app-footer__nav,
.app-footer__architecture {
  min-width: 0;
}

.app-footer__logo {
  display: inline-flex;
  align-items: center;
  gap: 0.85rem;
  color: #2f2924;
}

.app-footer__logo:hover {
  color: #2f2924;
}

.app-footer__logo-mark {
  display: grid;
  width: 3rem;
  height: 3rem;
  place-items: center;
  border-radius: 8px;
  background: #a77b50;
  color: #fffaf3;
  flex: 0 0 auto;
}

.app-footer__logo-mark svg,
.app-footer__support-icon svg,
.app-footer__tags svg {
  width: 1.25rem;
  height: 1.25rem;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 1.6;
}

.app-footer__wordmark {
  font-family: 'Prata', Georgia, serif;
  font-size: clamp(1.45rem, 3vw, 2.2rem);
  font-weight: 700;
  line-height: 1;
  letter-spacing: 0.06em;
}

.app-footer__slogan {
  max-width: 36rem;
  margin: 1.35rem 0 0;
  color: #5f554b;
  font-size: 1rem;
  font-style: italic;
  line-height: 1.8;
  text-wrap: pretty;
}

.app-footer__support {
  display: inline-flex;
  align-items: center;
  gap: 0.55rem;
  max-width: 100%;
  margin-top: 1.25rem;
  color: #5f554b;
  font-size: 0.95rem;
  line-height: 1.5;
}

.app-footer__support:hover {
  color: #8f633c;
}

.app-footer__support-icon {
  display: inline-grid;
  place-items: center;
  color: #a77b50;
  flex: 0 0 auto;
}

.app-footer h2 {
  margin: 0 0 1rem;
  color: #2f2924;
  font-family: 'Prata', Georgia, serif;
  font-size: 0.93rem;
  font-weight: 700;
  line-height: 1.35;
  letter-spacing: 0.08em;
}

.app-footer__nav {
  display: grid;
  align-content: start;
  gap: 0.72rem;
}

.app-footer__nav a {
  width: max-content;
  max-width: 100%;
  color: #5f554b;
  font-size: 0.96rem;
  line-height: 1.45;
  transition:
    color 180ms ease-out,
    transform 180ms ease-out;
}

.app-footer__nav a:hover {
  color: #8f633c;
  transform: translateX(3px);
}

.app-footer__architecture p {
  max-width: 32rem;
  margin: 0;
  color: #5f554b;
  font-size: 0.98rem;
  line-height: 1.72;
  text-wrap: pretty;
}

.app-footer__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.55rem;
  margin: 1.15rem 0 0;
  padding: 0;
  list-style: none;
}

.app-footer__tags li {
  display: inline-flex;
  align-items: center;
  gap: 0.42rem;
  min-height: 2rem;
  padding: 0.38rem 0.6rem;
  border: 1px solid rgba(167, 123, 80, 0.34);
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.35);
  color: #5f554b;
  font-size: 0.82rem;
  line-height: 1.25;
}

.app-footer__tags span {
  display: grid;
  place-items: center;
  color: #a77b50;
}

.app-footer__tags svg {
  width: 0.85rem;
  height: 0.85rem;
  stroke-width: 1.7;
}

.app-footer__bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 1.2rem 0 1.35rem;
  border-top: 1px solid rgba(167, 123, 80, 0.28);
  color: #6b6157;
  font-size: 0.9rem;
  line-height: 1.55;
}

.app-footer__bottom p {
  margin: 0;
}

.app-footer__bottom p:last-child {
  text-align: right;
}

@media (max-width: 900px) {
  .app-footer__main {
    grid-template-columns: 1fr;
    gap: 2.2rem;
  }

  .app-footer__slogan,
  .app-footer__architecture p {
    max-width: 100%;
  }
}

@media (max-width: 640px) {
  .app-footer__inner {
    width: min(100% - 2rem, 1200px);
  }

  .app-footer__main {
    padding: 40px 0;
  }

  .app-footer__logo {
    align-items: flex-start;
  }

  .app-footer__logo-mark {
    width: 2.75rem;
    height: 2.75rem;
  }

  .app-footer__support {
    align-items: flex-start;
  }

  .app-footer__support span:last-child {
    overflow-wrap: anywhere;
  }

  .app-footer__bottom {
    align-items: flex-start;
    flex-direction: column;
  }

  .app-footer__bottom p:last-child {
    text-align: left;
  }
}
</style>
