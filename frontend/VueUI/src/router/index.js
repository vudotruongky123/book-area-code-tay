import { createRouter, createWebHashHistory } from 'vue-router'

import { authUser, ensureAdminAccess, hydrateSession } from '../stores/auth'
import AdminDashboardView from '../views/admin/AdminDashboardView.vue'
import Homepage from '../components/Homepage.vue'
import LibraryView from '../components/Library.vue'
import LoginView from '../views/auth/LoginView.vue'
import RegisterView from '../views/auth/RegisterView.vue'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Homepage,
      meta: {
        title: 'Book Area',
      },
    },
    {
      path: '/home',
      redirect: { name: 'home' },
    },
    {
      path: '/library',
      name: 'library',
      component: LibraryView,
      meta: {
        title: 'Thư viện',
      },
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: {
        title: 'Đăng nhập',
        guestOnly: true,
      },
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
      meta: {
        title: 'Đăng ký',
        guestOnly: true,
      },
    },
    {
      path: '/admin/login',
      redirect: { name: 'login' },
    },
    {
      path: '/admin',
      name: 'admin-dashboard',
      component: AdminDashboardView,
      meta: {
        title: 'Bảng điều khiển quản trị',
        requiresAdmin: true,
      },
    },
    {
      path: '/dashboard',
      redirect: { name: 'admin-dashboard' },
    },
  ],
})

router.beforeEach(async (to) => {
  hydrateSession()

  const currentUser = authUser.value

  if (to.meta.guestOnly && currentUser) {
    const hasAdminAccess = await ensureAdminAccess()
    return hasAdminAccess ? { name: 'admin-dashboard' } : { name: 'library' }
  }

  if (to.meta.requiresAdmin) {
    if (!currentUser) {
      return {
        name: 'login',
        query: { redirect: to.fullPath },
      }
    }

    const hasAccess = await ensureAdminAccess()
    if (!hasAccess) {
      return authUser.value ? { name: 'library' } : { name: 'login', query: { redirect: to.fullPath } }
    }
  }

  return true
})

router.afterEach((to) => {
  const title = to.meta.title
  document.title = title ? `${title} | Book Area` : 'Book Area'
})

export default router
