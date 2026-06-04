<script setup>
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { login } from '../../stores/auth'

const route = useRoute()
const router = useRouter()

const form = reactive({
  email: '',
  password: '',
})

const errorMessage = ref('')
const isSubmitting = ref(false)

const successMessage = computed(() =>
  route.query.registered === '1' ? 'Đăng ký thành công, vui lòng đăng nhập.' : '',
)

const redirectTarget = computed(() => {
  const redirect = route.query.redirect
  return typeof redirect === 'string' && redirect.length > 0 ? redirect : ''
})

function resolveTarget(user) {
  const redirect = redirectTarget.value
  const isAdmin = user?.roles?.includes('ADMIN') ?? false

  if (redirect && redirect.startsWith('/') && !redirect.startsWith('/admin')) {
    return redirect
  }

  return isAdmin ? '/admin' : '/library'
}

async function handleSubmit() {
  errorMessage.value = ''
  isSubmitting.value = true

  try {
    const session = await login({
      email: form.email.trim(),
      password: form.password,
    })

    await router.replace(resolveTarget(session.user))
  } catch (error) {
    errorMessage.value =
      error instanceof Error ? error.message : 'Đăng nhập thất bại. Vui lòng thử lại.'
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <main class="auth-page">
    <section class="auth-hero">
      <RouterLink class="auth-back" to="/">Quay về trang đọc sách</RouterLink>
      <p class="auth-eyebrow">BOOK AREA</p>
      <h1>Đăng nhập Book Area</h1>
      <p class="auth-lede">
        Tiếp tục đọc, xem library, hoặc vào dashboard nếu tài khoản của bạn có quyền quản trị.
      </p>
    </section>

    <section class="auth-panel">
      <form class="auth-form" @submit.prevent="handleSubmit">
        <p v-if="successMessage" class="auth-success" role="status">{{ successMessage }}</p>

        <label>
          <span>Email</span>
          <input v-model="form.email" type="email" placeholder="you@example.com" required />
        </label>

        <label>
          <span>Mật khẩu</span>
          <input v-model="form.password" type="password" placeholder="Nhập mật khẩu" required />
        </label>

        <p v-if="errorMessage" class="auth-error">{{ errorMessage }}</p>

        <button class="auth-submit" type="submit" :disabled="isSubmitting" :aria-busy="isSubmitting">
          <span v-if="isSubmitting" class="auth-submit__spinner" aria-hidden="true"></span>
          <span class="auth-submit__label">{{ isSubmitting ? 'Đang đăng nhập...' : 'Đăng nhập' }}</span>
        </button>
      </form>

      <p class="auth-note">
        Chưa có tài khoản?
        <RouterLink to="/register">Đăng ký</RouterLink>
      </p>
    </section>
  </main>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(320px, 28rem);
  gap: 1rem;
  padding: 1rem;
  background:
    linear-gradient(180deg, rgba(255, 250, 243, 0.95), rgba(245, 236, 224, 0.92));
}

.auth-hero,
.auth-panel {
  border: 1px solid color-mix(in oklab, var(--line-soft) 82%, white);
  border-radius: 8px;
  background: color-mix(in oklab, var(--surface) 94%, transparent);
  box-shadow: 0 22px 60px color-mix(in oklab, var(--accent-deep) 10%, transparent);
}

.auth-hero {
  display: grid;
  align-content: start;
  gap: 1rem;
  padding: clamp(1.5rem, 4vw, 3rem);
  background:
    linear-gradient(135deg, oklch(0.97 0.012 78), oklch(0.93 0.018 74)),
    color-mix(in oklab, var(--surface) 92%, transparent);
}

.auth-back {
  width: max-content;
  color: var(--text-soft);
  font-size: 0.9rem;
  transition: color 180ms ease-out;
}

.auth-back:hover {
  color: var(--text-strong);
}

.auth-eyebrow {
  font-size: 0.78rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--text-soft);
}

.auth-hero h1 {
  color: var(--text-strong);
  font-family: 'Raleway', 'Segoe UI', sans-serif;
  font-size: clamp(2.4rem, 5vw, 4.3rem);
  font-weight: 300;
  line-height: 0.96;
  max-width: 12ch;
}

.auth-lede {
  max-width: 36rem;
  color: var(--text);
  font-size: 1.02rem;
  line-height: 1.7;
}

.auth-panel {
  display: grid;
  align-content: center;
  gap: 1rem;
  padding: clamp(1.4rem, 3vw, 2rem);
}

.auth-form {
  display: grid;
  gap: 1rem;
}

.auth-form label {
  display: grid;
  gap: 0.45rem;
}

.auth-form span {
  color: var(--text-strong);
  font-size: 0.94rem;
}

.auth-form input {
  min-height: 3.4rem;
  padding: 0.9rem 1rem;
  border: 1px solid color-mix(in oklab, var(--line-soft) 84%, white);
  border-radius: 8px;
  background: color-mix(in oklab, var(--surface) 88%, white);
  color: var(--text-strong);
  transition:
    border-color 180ms ease-out,
    box-shadow 180ms ease-out,
    transform 180ms ease-out,
    background-color 180ms ease-out;
}

.auth-form input:focus {
  border-color: color-mix(in oklab, var(--accent-deep) 54%, white);
  background: color-mix(in oklab, var(--surface) 96%, white);
  box-shadow: 0 0 0 4px color-mix(in oklab, var(--accent-glow) 54%, transparent);
  outline: 0;
  transform: translateY(-1px);
}

.auth-success,
.auth-error {
  padding: 0.9rem 1rem;
  border-radius: 8px;
}

.auth-success {
  background: color-mix(in oklab, var(--accent-glow) 58%, white);
  color: var(--text-strong);
  animation: auth-success-arrive 260ms cubic-bezier(0.22, 1, 0.36, 1) both;
}

.auth-error {
  background: rgba(155, 53, 41, 0.08);
  color: rgb(140, 47, 35);
  animation: auth-error-pulse 260ms cubic-bezier(0.22, 1, 0.36, 1) both;
}

.auth-submit {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.65rem;
  min-height: 3.4rem;
  margin-top: 0.25rem;
  border-radius: 8px;
  background: linear-gradient(135deg, var(--accent), var(--accent-deep));
  color: white;
  font-weight: 700;
  transition:
    transform 160ms cubic-bezier(0.22, 1, 0.36, 1),
    box-shadow 160ms ease-out,
    filter 160ms ease-out;
}

.auth-submit:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 8px 16px color-mix(in oklab, var(--accent-deep) 18%, transparent);
}

.auth-submit:active:not(:disabled) {
  transform: translateY(0) scale(0.98);
}

.auth-submit:disabled {
  opacity: 0.72;
  cursor: wait;
}

.auth-submit__spinner {
  width: 1rem;
  height: 1rem;
  border: 2px solid rgba(255, 255, 255, 0.36);
  border-top-color: rgba(255, 255, 255, 0.96);
  border-radius: 999px;
  animation: auth-spin 760ms linear infinite;
}

.auth-note {
  color: var(--text-soft);
}

.auth-note a {
  color: var(--text-strong);
  font-weight: 700;
}

@media (max-width: 860px) {
  .auth-page {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 520px) {
  .auth-page {
    padding: 0.8rem;
  }
}

@keyframes auth-success-arrive {
  from {
    transform: translateY(8px) scale(0.98);
    filter: blur(3px);
  }
}

@keyframes auth-error-pulse {
  0%,
  100% {
    transform: translateX(0);
  }

  30% {
    transform: translateX(-6px);
  }

  60% {
    transform: translateX(5px);
  }
}

@keyframes auth-spin {
  to {
    transform: rotate(1turn);
  }
}

@media (prefers-reduced-motion: reduce) {
  .auth-success,
  .auth-error,
  .auth-submit__spinner {
    animation-duration: 0.01ms !important;
    animation-delay: 0ms !important;
    animation-iteration-count: 1 !important;
  }

  .auth-form input,
  .auth-submit {
    transition-duration: 0.01ms !important;
  }
}
</style>
