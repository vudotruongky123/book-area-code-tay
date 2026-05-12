<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { loginAsAdmin } from '../../stores/auth'

const route = useRoute()
const router = useRouter()

const form = reactive({
  email: '',
  password: '',
})

const errorMessage = ref('')
const isSubmitting = ref(false)

const redirectTarget = computed(() => {
  const redirect = route.query.redirect
  return typeof redirect === 'string' && redirect.length > 0 ? redirect : '/admin'
})

async function handleSubmit() {
  errorMessage.value = ''
  isSubmitting.value = true

  try {
    await loginAsAdmin({
      email: form.email.trim(),
      password: form.password,
    })

    await router.replace(redirectTarget.value)
  } catch (error) {
    errorMessage.value =
      error instanceof Error ? error.message : 'Đăng nhập thất bại. Vui lòng thử lại.'
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <main class="admin-auth">
    <section class="admin-auth__intro">
      <RouterLink class="admin-auth__back" to="/">Quay về trang đọc sách</RouterLink>
      <p class="admin-auth__eyebrow">Admin control room</p>
      <h1>Đăng nhập để quản trị thư viện số của Book Area.</h1>
      <p class="admin-auth__lede">
        Theo dõi tăng trưởng nội dung, người dùng và doanh thu trong một không gian gọn, sáng
        rõ, ưu tiên tác vụ.
      </p>

      <div class="admin-auth__signals">
        <article>
          <span>01</span>
          <h2>JWT session</h2>
          <p>Frontend sẽ tự giữ phiên, refresh token và khóa route admin khi role không hợp lệ.</p>
        </article>
        <article>
          <span>02</span>
          <h2>Dashboard metrics</h2>
          <p>Tổng quan người dùng, catalog, tương tác và doanh thu được lấy trực tiếp từ API.</p>
        </article>
        <article>
          <span>03</span>
          <h2>Fast handoff</h2>
          <p>Vào là thấy ngay các con số chính, khu vực vận hành và mức độ đầy dữ liệu hiện có.</p>
        </article>
      </div>
    </section>

    <section class="admin-auth__panel">
      <div class="admin-auth__panel-head">
        <p>Role required</p>
        <strong>ADMIN</strong>
      </div>

      <form class="admin-auth__form" @submit.prevent="handleSubmit">
        <label>
          <span>Email</span>
          <input v-model="form.email" type="email" placeholder="admin@bookarea.vn" required />
        </label>

        <label>
          <span>Mật khẩu</span>
          <input v-model="form.password" type="password" placeholder="Nhập mật khẩu" required />
        </label>

        <p v-if="errorMessage" class="admin-auth__error">{{ errorMessage }}</p>

        <button class="admin-auth__submit" type="submit" :disabled="isSubmitting">
          <span>{{ isSubmitting ? 'Đang xác thực...' : 'Vào dashboard' }}</span>
        </button>
      </form>

      <div class="admin-auth__note">
        <p>Chỉ tài khoản có role `ADMIN` mới có thể vượt qua route guard và truy cập dashboard.</p>
      </div>
    </section>
  </main>
</template>

<style scoped>
.admin-auth {
  --auth-bg: oklch(0.95 0.013 74);
  --auth-surface: oklch(0.985 0.006 74 / 0.9);
  --auth-surface-deep: oklch(0.28 0.022 42);
  --auth-border: oklch(0.82 0.018 70);
  --auth-border-strong: oklch(0.63 0.045 48);
  --auth-text: oklch(0.27 0.016 46);
  --auth-soft: oklch(0.48 0.014 48);
  --auth-accent: oklch(0.66 0.11 58);
  --auth-accent-deep: oklch(0.53 0.11 45);
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(320px, 30rem);
  gap: 1.4rem;
  padding: 1.4rem;
}

.admin-auth__intro,
.admin-auth__panel {
  position: relative;
  overflow: hidden;
  border: 1px solid color-mix(in oklab, var(--auth-border) 86%, white);
  border-radius: 2rem;
  background: var(--auth-surface);
  box-shadow: 0 30px 90px rgba(76, 47, 28, 0.08);
}

.admin-auth__intro {
  padding: clamp(1.6rem, 4vw, 3rem);
  background:
    radial-gradient(circle at top left, rgba(255, 237, 196, 0.86), rgba(255, 237, 196, 0) 22rem),
    linear-gradient(180deg, rgba(255, 251, 244, 0.92), rgba(247, 238, 223, 0.72)),
    var(--auth-bg);
}

.admin-auth__intro::after {
  content: '';
  position: absolute;
  right: -4rem;
  bottom: -4rem;
  width: 15rem;
  height: 15rem;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(164, 103, 43, 0.18), rgba(164, 103, 43, 0));
  filter: blur(10px);
}

.admin-auth__back {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  margin-bottom: 2rem;
  color: var(--auth-soft);
  transition:
    color 180ms ease-out,
    transform 180ms ease-out;
}

.admin-auth__back:hover {
  color: var(--auth-text);
  transform: translateX(-2px);
}

.admin-auth__eyebrow,
.admin-auth__panel-head p,
.admin-auth__signals span {
  font-size: 0.78rem;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: var(--auth-soft);
}

.admin-auth h1 {
  max-width: 11ch;
  font-size: clamp(2.7rem, 5vw, 4.5rem);
  line-height: 0.95;
  color: var(--auth-text);
}

.admin-auth__lede {
  max-width: 35rem;
  margin-top: 1.4rem;
  font-size: 1.05rem;
  color: var(--auth-soft);
}

.admin-auth__signals {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 1rem;
  margin-top: clamp(2rem, 5vw, 4rem);
}

.admin-auth__signals article {
  padding-top: 1rem;
  border-top: 1px solid rgba(110, 75, 47, 0.16);
}

.admin-auth__signals h2,
.admin-auth__panel-head strong {
  margin-top: 0.5rem;
  font-size: 1.15rem;
  color: var(--auth-text);
}

.admin-auth__signals p {
  margin-top: 0.55rem;
  color: var(--auth-soft);
}

.admin-auth__panel {
  display: grid;
  align-content: start;
  gap: 1.5rem;
  padding: 1.4rem;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.88), rgba(246, 239, 230, 0.76)),
    var(--auth-surface);
}

.admin-auth__panel::before {
  content: '';
  position: absolute;
  inset: 0 auto auto 0;
  width: 100%;
  height: 0.24rem;
  background: linear-gradient(90deg, var(--auth-accent), var(--auth-accent-deep));
}

.admin-auth__panel-head {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 1rem;
}

.admin-auth__form {
  display: grid;
  gap: 1rem;
}

.admin-auth__form label {
  display: grid;
  gap: 0.45rem;
}

.admin-auth__form span {
  font-size: 0.92rem;
  color: var(--auth-text);
}

.admin-auth__form input {
  min-height: 3.5rem;
  padding: 0.95rem 1rem;
  border: 1px solid rgba(117, 83, 57, 0.18);
  border-radius: 1rem;
  background: rgba(255, 255, 255, 0.76);
  color: var(--auth-text);
  transition:
    border-color 180ms ease-out,
    box-shadow 180ms ease-out,
    transform 180ms ease-out;
}

.admin-auth__form input:hover,
.admin-auth__form input:focus {
  border-color: color-mix(in oklab, var(--auth-border-strong) 72%, white);
  box-shadow: 0 18px 32px rgba(110, 75, 47, 0.08);
  transform: translateY(-1px);
}

.admin-auth__error {
  padding: 0.9rem 1rem;
  border-radius: 1rem;
  background: rgba(155, 53, 41, 0.08);
  color: rgb(140, 47, 35);
}

.admin-auth__submit {
  min-height: 3.5rem;
  border-radius: 1rem;
  background: linear-gradient(135deg, var(--auth-accent), var(--auth-accent-deep));
  color: white;
  font-weight: 600;
  transition:
    transform 180ms ease-out,
    box-shadow 180ms ease-out,
    filter 180ms ease-out;
}

.admin-auth__submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 22px 36px rgba(143, 88, 35, 0.24);
  filter: saturate(1.03);
}

.admin-auth__submit:disabled {
  cursor: wait;
  opacity: 0.72;
}

.admin-auth__note {
  padding-top: 1rem;
  border-top: 1px solid rgba(117, 83, 57, 0.14);
  color: var(--auth-soft);
}

@media (max-width: 980px) {
  .admin-auth {
    grid-template-columns: 1fr;
  }

  .admin-auth__signals {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .admin-auth {
    padding: 1rem;
  }

  .admin-auth__intro,
  .admin-auth__panel {
    border-radius: 1.5rem;
  }

  .admin-auth h1 {
    max-width: none;
  }
}
</style>
