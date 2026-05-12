import { createRouter, createWebHashHistory } from 'vue-router'

import { ensureAdminAccess, hydrateSession } from '../stores/auth'
import AdminDashboardView from '../views/admin/AdminDashboardView.vue'
import AdminLoginView from '../views/admin/AdminLoginView.vue'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: {
        title: 'Book Area',
      },
    },
    {
      path: '/admin',
      name: 'admin-dashboard',
      component: AdminDashboardView,
      meta: {
        title: 'Admin Dashboard',
        requiresAdmin: true,
      },
    },
    {
      path: '/admin/login',
      name: 'admin-login',
      component: AdminLoginView,
      meta: {
        title: 'Admin Login',
        guestOnly: true,
      },
    },
  ],
})

router.beforeEach(async (to) => {
  hydrateSession()

  const needsAdminCheck = Boolean(to.meta.requiresAdmin || to.meta.guestOnly)
  const hasAccess = needsAdminCheck ? await ensureAdminAccess() : false

  if (to.meta.guestOnly && hasAccess) {
    return { name: 'admin-dashboard' }
  }

  if (to.meta.requiresAdmin) {
    if (!hasAccess) {
      return {
        name: 'admin-login',
        query: { redirect: to.fullPath },
      }
    }
  }

  return true
})

router.afterEach((to) => {
  document.title = (to.meta.title as string | undefined)
    ? `${to.meta.title} | Book Area`
    : 'Book Area'
})

export default router
