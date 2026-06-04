<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { authIsAdmin, authUser, logout } from '../stores/auth'

const router = useRouter()

const displayName = computed(() => authUser.value?.fullName || authUser.value?.email || 'Tài khoản')
const displayEmail = computed(() => authUser.value?.email || '')
const avatarLabel = computed(() => {
  const source = authUser.value?.fullName || authUser.value?.email || 'BA'

  return source
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((part) => part.charAt(0))
    .join('')
    .toUpperCase()
})

function navigateTo(path) {
  router.push(path)
}

function handleLogout() {
  logout()
  router.push('/login')
}
</script>

<template>
  <div class="dropdown user-dropdown">
    <button
      class="btn user-dropdown__trigger dropdown-toggle"
      type="button"
      data-bs-toggle="dropdown"
      data-bs-auto-close="true"
      aria-expanded="false"
    >
      <span class="user-dropdown__avatar" aria-hidden="true">{{ avatarLabel }}</span>

      <span class="user-dropdown__meta">
        <span class="user-dropdown__name">{{ displayName }}</span>
        <span v-if="displayEmail" class="user-dropdown__email">{{ displayEmail }}</span>
      </span>
    </button>

    <ul class="dropdown-menu dropdown-menu-end user-dropdown__menu">
      <li v-if="authIsAdmin">
        <button type="button" class="dropdown-item user-dropdown__item" @click="navigateTo('/admin')">
          Dashboard/Admin
        </button>
      </li>
      <li>
        <button type="button" class="dropdown-item user-dropdown__item" @click="navigateTo('/library')">
          Thư viện
        </button>
      </li>
      <li>
        <button type="button" class="dropdown-item user-dropdown__item" @click="navigateTo('/library')">
          Thông tin cá nhân
        </button>
      </li>
      <li><hr class="dropdown-divider user-dropdown__divider"></li>
      <li>
        <button type="button" class="dropdown-item user-dropdown__item user-dropdown__item--danger" @click="handleLogout">
          Đăng xuất
        </button>
      </li>
    </ul>
  </div>
</template>
