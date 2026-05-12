<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import DashboardBarChart from '../../components/admin/DashboardBarChart.vue'
import { getDashboardSummary, getMonthlyStats } from '../../services/adminDashboard'
import { ApiError } from '../../services/api'
import { authUser, logout } from '../../stores/auth'
import type { AdminDashboardSummary, AdminMonthlyStats } from '../../types/dashboard'

const router = useRouter()

const summary = ref<AdminDashboardSummary | null>(null)
const monthlyStats = ref<AdminMonthlyStats[]>([])
const loading = ref(true)
const errorMessage = ref('')
const monthlyStatsLoading = ref(true)
const monthlyStatsError = ref('')

const compactNumber = new Intl.NumberFormat('vi-VN')

const summaryCards = computed(() => {
  const current = summary.value

  return [
    { label: 'Tổng người dùng', value: current?.totalUsers ?? 0 },
    { label: 'Tổng sách', value: current?.totalBooks ?? 0 },
    { label: 'Tổng chương', value: current?.totalChapters ?? 0 },
    { label: 'Tổng danh mục', value: current?.totalCategories ?? 0 },
    { label: 'Tổng lượt đọc', value: current?.totalViews ?? 0 },
    { label: 'Tổng bình luận', value: current?.totalComments ?? 0 },
    { label: 'Báo cáo chờ xử lý', value: current?.pendingReports ?? 0 },
  ]
})

async function handleAuthFailure() {
  logout()
  await router.replace({
    name: 'admin-login',
    query: {
      redirect: '/admin',
    },
  })
}

async function loadSummary() {
  loading.value = true
  errorMessage.value = ''

  try {
    summary.value = await getDashboardSummary()
  } catch (error) {
    if (error instanceof ApiError && (error.status === 401 || error.status === 403)) {
      await handleAuthFailure()
      return
    }

    errorMessage.value = 'Không thể tải dữ liệu. Vui lòng thử lại.'
  } finally {
    loading.value = false
  }
}

async function loadMonthlyStatsData() {
  monthlyStatsLoading.value = true
  monthlyStatsError.value = ''

  try {
    monthlyStats.value = await getMonthlyStats()
  } catch (error) {
    if (error instanceof ApiError && (error.status === 401 || error.status === 403)) {
      await handleAuthFailure()
      return
    }

    monthlyStats.value = []
    monthlyStatsError.value = 'Không thể tải dữ liệu. Vui lòng thử lại.'
  } finally {
    monthlyStatsLoading.value = false
  }
}

async function loadDashboard() {
  await Promise.all([loadSummary(), loadMonthlyStatsData()])
}

function handleLogout() {
  logout()
  router.replace('/admin/login')
}

onMounted(loadDashboard)
</script>

<template>
  <main class="admin-layout">
    <aside class="admin-sidebar">
      <RouterLink class="admin-sidebar__brand" to="/">
        <span class="admin-sidebar__eyebrow">Book Area</span>
        <strong>Quản trị hệ thống</strong>
      </RouterLink>

      <div class="admin-sidebar__card">
        <p class="admin-sidebar__label">Điều hướng</p>
        <strong>Tổng quan dashboard</strong>
        <span>Theo dõi người dùng, nội dung và nhịp đọc trên toàn hệ thống.</span>
      </div>

      <div class="admin-sidebar__card">
        <p class="admin-sidebar__label">Quyền truy cập</p>
        <strong>{{ authUser?.fullName || 'Quản trị viên' }}</strong>
        <span>{{ authUser?.email }}</span>
      </div>
    </aside>

    <section class="admin-main">
      <header class="admin-topbar">
        <div>
          <p class="admin-topbar__eyebrow">Dashboard</p>
          <h1>Bảng điều khiển quản trị</h1>
          <p class="admin-topbar__subtitle">
            Theo dõi hoạt động đọc sách, người dùng, nội dung và hiệu suất hệ thống
            Book Area.
          </p>
        </div>

        <div class="admin-topbar__actions">
          <button type="button" class="admin-button admin-button--ghost" @click="loadDashboard">
            Làm mới
          </button>
          <details class="profile-menu">
            <summary class="profile-menu__trigger">
              <span class="profile-menu__avatar">
                {{ (authUser?.fullName || authUser?.email || 'A').slice(0, 1).toUpperCase() }}
              </span>
              <span class="profile-menu__meta">
                <strong>{{ authUser?.fullName || 'Quản trị viên' }}</strong>
                <small>{{ authUser?.email }}</small>
              </span>
            </summary>

            <div class="profile-menu__panel">
              <p>Tài khoản quản trị đang hoạt động.</p>
              <button type="button" class="admin-button" @click="handleLogout">Đăng xuất</button>
            </div>
          </details>
        </div>
      </header>

      <section v-if="loading" class="admin-loading" aria-live="polite">
        <div v-for="index in 4" :key="index" class="admin-loading__item"></div>
      </section>

      <section v-else-if="summary" class="admin-content">
        <section class="summary-grid">
          <article v-for="card in summaryCards" :key="card.label" class="summary-card">
            <p>{{ card.label }}</p>
            <strong>{{ compactNumber.format(card.value) }}</strong>
          </article>
        </section>

        <section class="panel panel--wide">
          <div class="panel__header">
            <div>
              <h2>Thống kê theo tháng</h2>
              <p class="panel__subtitle">
                Người dùng mới, sách mới, chương mới và lượt đọc trong từng tháng.
              </p>
            </div>

            <button type="button" class="admin-button admin-button--ghost" @click="loadMonthlyStatsData">
              Làm mới
            </button>
          </div>

          <DashboardBarChart
            :stats="monthlyStats"
            :loading="monthlyStatsLoading"
            :error="monthlyStatsError"
            @retry="loadMonthlyStatsData"
          />
        </section>

        <section class="content-grid">
          <section class="panel">
            <div class="panel__header">
              <div>
                <h2>Sách nổi bật</h2>
                <p class="panel__subtitle">
                  Những đầu sách có lượt đọc và tương tác cao nhất.
                </p>
              </div>
            </div>

            <div class="empty-state">
              <strong>Không có dữ liệu</strong>
              <p>Danh sách sách nổi bật sẽ xuất hiện khi hệ thống có thống kê phù hợp.</p>
            </div>
          </section>

          <section class="panel">
            <div class="panel__header">
              <div>
                <h2>Hoạt động gần đây</h2>
                <p class="panel__subtitle">
                  Các thao tác và tương tác mới nhất trong hệ thống.
                </p>
              </div>
            </div>

            <div class="empty-state">
              <strong>Không có dữ liệu</strong>
              <p>Hoạt động mới sẽ được hiển thị tại đây khi có dữ liệu phát sinh.</p>
            </div>
          </section>
        </section>
      </section>

      <section v-else class="panel panel--error">
        <h2>Không thể tải dữ liệu. Vui lòng thử lại.</h2>
        <button type="button" class="admin-button" @click="loadDashboard">Thử lại</button>
      </section>
    </section>
  </main>
</template>

<style scoped>
.admin-layout {
  --dash-bg: oklch(0.953 0.011 73);
  --dash-surface: oklch(0.985 0.006 74 / 0.96);
  --dash-line: oklch(0.83 0.015 72);
  --dash-line-strong: oklch(0.67 0.042 48);
  --dash-text: oklch(0.25 0.016 44);
  --dash-soft: oklch(0.5 0.012 48);
  --dash-accent: oklch(0.67 0.11 58);
  --dash-accent-deep: oklch(0.53 0.11 44);
  display: grid;
  grid-template-columns: 260px minmax(0, 1fr);
  gap: 1.2rem;
  width: 100%;
  min-height: 100vh;
  padding: 1.2rem;
  background:
    radial-gradient(circle at top right, rgba(255, 231, 179, 0.2), transparent 22rem),
    linear-gradient(180deg, rgba(255, 250, 243, 0.95), rgba(245, 236, 224, 0.9)),
    var(--dash-bg);
}

.admin-sidebar,
.admin-topbar,
.panel,
.summary-card {
  border: 1px solid color-mix(in oklab, var(--dash-line) 84%, white);
  background: var(--dash-surface);
  box-shadow: 0 24px 60px rgba(77, 50, 29, 0.08);
}

.admin-sidebar {
  display: grid;
  align-content: start;
  gap: 1rem;
  min-width: 0;
  padding: 1.2rem;
  border-radius: 1.5rem;
}

.admin-sidebar__brand,
.admin-sidebar__card {
  display: grid;
  gap: 0.35rem;
}

.admin-sidebar__brand {
  padding-bottom: 1rem;
  border-bottom: 1px solid rgba(117, 83, 57, 0.12);
}

.admin-sidebar__eyebrow,
.admin-topbar__eyebrow,
.admin-sidebar__label {
  font-size: 0.76rem;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: var(--dash-soft);
}

.admin-sidebar__brand strong,
.admin-topbar h1,
.panel h2,
.summary-card strong {
  color: var(--dash-text);
}

.admin-sidebar__card {
  padding: 1rem;
  border: 1px solid rgba(117, 83, 57, 0.1);
  border-radius: 1.1rem;
  background: rgba(255, 255, 255, 0.6);
}

.admin-sidebar__card strong {
  color: var(--dash-text);
}

.admin-sidebar__card span {
  color: var(--dash-soft);
}

.admin-main {
  display: grid;
  align-content: start;
  gap: 1rem;
  min-width: 0;
  width: 100%;
}

.admin-topbar {
  display: flex;
  justify-content: space-between;
  align-items: start;
  gap: 1.2rem;
  min-width: 0;
  padding: 1.4rem 1.5rem;
  border-radius: 1.5rem;
}

.admin-topbar > div:first-child {
  min-width: 0;
  flex: 1 1 auto;
}

.admin-topbar h1 {
  max-width: none;
  font-size: clamp(2rem, 3vw, 2.75rem);
  line-height: 1.05;
  white-space: normal;
  text-wrap: balance;
}

.admin-topbar__subtitle,
.panel__subtitle,
.empty-state p {
  margin-top: 0.45rem;
  color: var(--dash-soft);
}

.admin-topbar__actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex: 0 0 auto;
}

.admin-button {
  min-height: 2.9rem;
  padding: 0.7rem 1rem;
  border-radius: 999px;
  background: linear-gradient(135deg, var(--dash-accent), var(--dash-accent-deep));
  color: white;
  font-weight: 600;
  transition:
    transform 180ms ease-out,
    box-shadow 180ms ease-out,
    filter 180ms ease-out;
}

.admin-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 16px 28px rgba(140, 88, 39, 0.2);
  filter: saturate(1.03);
}

.admin-button--ghost {
  background: rgba(255, 255, 255, 0.82);
  color: var(--dash-text);
  border: 1px solid rgba(117, 83, 57, 0.12);
  box-shadow: none;
}

.profile-menu {
  position: relative;
  min-width: 0;
}

.profile-menu[open] .profile-menu__trigger {
  border-color: color-mix(in oklab, var(--dash-line-strong) 72%, white);
  box-shadow: 0 18px 34px rgba(77, 50, 29, 0.1);
}

.profile-menu__trigger {
  display: inline-flex;
  align-items: center;
  gap: 0.8rem;
  min-width: 15rem;
  max-width: 20rem;
  padding: 0.55rem 0.75rem;
  border: 1px solid rgba(117, 83, 57, 0.12);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.82);
  cursor: pointer;
  list-style: none;
}

.profile-menu__trigger::-webkit-details-marker {
  display: none;
}

.profile-menu__avatar {
  display: grid;
  place-items: center;
  width: 2.4rem;
  height: 2.4rem;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--dash-accent), var(--dash-accent-deep));
  color: white;
  font-weight: 700;
  flex: 0 0 auto;
}

.profile-menu__meta {
  display: grid;
  min-width: 0;
}

.profile-menu__meta strong,
.profile-menu__meta small {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.profile-menu__meta small {
  color: var(--dash-soft);
}

.profile-menu__panel {
  position: absolute;
  top: calc(100% + 0.65rem);
  right: 0;
  z-index: 4;
  display: grid;
  gap: 0.9rem;
  min-width: 16rem;
  padding: 1rem;
  border: 1px solid rgba(117, 83, 57, 0.12);
  border-radius: 1rem;
  background: rgba(255, 252, 248, 0.98);
  box-shadow: 0 24px 44px rgba(77, 50, 29, 0.14);
}

.profile-menu__panel p {
  color: var(--dash-soft);
}

.admin-loading,
.summary-grid,
.content-grid {
  display: grid;
  gap: 1rem;
}

.admin-loading,
.summary-grid {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.admin-loading__item {
  min-height: 9rem;
  border-radius: 1.35rem;
  background:
    linear-gradient(90deg, rgba(255, 255, 255, 0.54), rgba(255, 255, 255, 0.92), rgba(255, 255, 255, 0.54)),
    rgba(255, 244, 229, 0.8);
  background-size: 220% 100%;
  animation: dashboard-skeleton 1.2s linear infinite;
}

.admin-content {
  display: grid;
  gap: 1rem;
  min-width: 0;
}

.summary-grid {
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
}

.summary-card {
  display: grid;
  gap: 0.55rem;
  min-width: 0;
  padding: 1.15rem;
  border-radius: 1.35rem;
}

.summary-card p {
  color: var(--dash-soft);
  font-size: 0.9rem;
}

.summary-card strong {
  font-size: 1.9rem;
  line-height: 1;
}

.panel {
  display: grid;
  gap: 1rem;
  min-width: 0;
  padding: 1.25rem;
  border-radius: 1.6rem;
}

.panel--wide {
  min-width: 0;
}

.panel__header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  gap: 1rem;
  min-width: 0;
}

.panel__header > div {
  min-width: 0;
}

.content-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.empty-state {
  display: grid;
  gap: 0.45rem;
  min-height: 12rem;
  place-content: center;
  padding: 1.25rem;
  border: 1px dashed rgba(117, 83, 57, 0.18);
  border-radius: 1.2rem;
  background: rgba(255, 252, 248, 0.86);
  text-align: center;
}

.empty-state strong {
  color: var(--dash-text);
}

.panel--error {
  justify-items: start;
}

@keyframes dashboard-skeleton {
  from {
    background-position: 0 0;
  }

  to {
    background-position: 220% 0;
  }
}

@media (max-width: 1180px) {
  .admin-layout {
    grid-template-columns: 1fr;
  }

  .admin-sidebar {
    grid-template-columns: repeat(3, minmax(0, 1fr));
    align-items: start;
  }

  .admin-sidebar__brand {
    padding-bottom: 0;
    border-bottom: 0;
  }
}

@media (max-width: 980px) {
  .admin-loading,
  .content-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .admin-topbar {
    flex-direction: column;
    align-items: stretch;
  }

  .admin-topbar__actions {
    justify-content: space-between;
  }
}

@media (max-width: 760px) {
  .admin-layout {
    padding: 1rem;
  }

  .admin-sidebar,
  .admin-topbar,
  .panel,
  .summary-card {
    border-radius: 1.25rem;
  }

  .admin-sidebar,
  .admin-loading,
  .content-grid,
  .admin-topbar__actions {
    grid-template-columns: 1fr;
    display: grid;
  }

  .panel__header {
    flex-direction: column;
  }

  .profile-menu,
  .profile-menu__trigger {
    width: 100%;
    max-width: none;
  }

  .profile-menu__panel {
    left: 0;
    right: auto;
    min-width: 100%;
  }
}
</style>
