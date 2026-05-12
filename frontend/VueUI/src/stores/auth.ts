import { computed, reactive } from 'vue'

import { ApiError, apiFetch } from '../services/api'
import type { AuthResponse, AuthUser, LoginPayload } from '../types/auth'

const STORAGE_KEY = 'book-area-admin-session'

type AuthState = {
  token: string
  refreshToken: string
  user: AuthUser | null
  hydrated: boolean
}

const state = reactive<AuthState>({
  token: '',
  refreshToken: '',
  user: null,
  hydrated: false,
})

function persistSession() {
  if (!state.token || !state.refreshToken || !state.user) {
    localStorage.removeItem(STORAGE_KEY)
    return
  }

  localStorage.setItem(
    STORAGE_KEY,
    JSON.stringify({
      token: state.token,
      refreshToken: state.refreshToken,
      user: state.user,
    }),
  )
}

function assignSession(session: Partial<AuthState>) {
  state.token = session.token ?? ''
  state.refreshToken = session.refreshToken ?? ''
  state.user = session.user ?? null
  persistSession()
}

export function hydrateSession() {
  if (state.hydrated) {
    return
  }

  state.hydrated = true

  const raw = localStorage.getItem(STORAGE_KEY)
  if (!raw) {
    return
  }

  try {
    const parsed = JSON.parse(raw) as Partial<AuthState>
    state.token = parsed.token ?? ''
    state.refreshToken = parsed.refreshToken ?? ''
    state.user = parsed.user ?? null
  } catch {
    localStorage.removeItem(STORAGE_KEY)
  }
}

export const authUser = computed(() => state.user)
export const isAdminAuthenticated = () =>
  Boolean(state.token && state.user?.roles?.includes('ADMIN'))

export function authHeaders(headers: HeadersInit = {}) {
  hydrateSession()

  const normalized = new Headers(headers)

  if (state.token) {
    normalized.set('Authorization', `Bearer ${state.token}`)
  }

  return normalized
}

export async function loginAsAdmin(payload: LoginPayload) {
  const response = await apiFetch<AuthResponse>('/api/auth/login', {
    method: 'POST',
    body: payload,
  })

  assignSession(response)

  if (!response.user.roles.includes('ADMIN')) {
    logout()
    throw new Error('Tài khoản này không có quyền truy cập khu vực quản trị.')
  }

  return response.user
}

export function logout() {
  state.token = ''
  state.refreshToken = ''
  state.user = null
  localStorage.removeItem(STORAGE_KEY)
}

async function refreshSession() {
  hydrateSession()

  if (!state.refreshToken) {
    logout()
    throw new Error('Phiên đăng nhập đã hết hạn.')
  }

  const refreshed = await apiFetch<AuthResponse>('/api/auth/refresh', {
    method: 'POST',
    body: {
      refreshToken: state.refreshToken,
    },
  })

  assignSession(refreshed)
  return refreshed
}

export async function fetchWithAuth<T>(
  path: string,
  init: Omit<RequestInit, 'body'> & {
    body?: BodyInit | Record<string, unknown> | null
  } = {},
) {
  hydrateSession()

  const execute = () =>
    apiFetch<T>(path, {
      ...init,
      headers: authHeaders(init.headers),
    })

  try {
    return await execute()
  } catch (error) {
    if (error instanceof ApiError && error.status === 401 && state.refreshToken) {
      await refreshSession()
      return execute()
    }

    throw error
  }
}

export async function ensureAdminAccess() {
  hydrateSession()

  if (!state.token) {
    return false
  }

  try {
    const user = await fetchWithAuth<AuthUser>('/api/auth/me')
    assignSession({
      token: state.token,
      refreshToken: state.refreshToken,
      user,
    })
    if (!user.roles.includes('ADMIN')) {
      logout()
      return false
    }

    return true
  } catch {
    logout()
    return false
  }
}
