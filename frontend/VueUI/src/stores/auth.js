import { computed, reactive } from 'vue'


import {
  ApiError,
  authHeaders,
  clearStoredSession,
  getCurrentUser,
  getStoredSession,
  apiFetch,
  login as apiLogin,
  logout as clearAuthSession,
  register as apiRegister,
  setStoredSession,
} from '../services/api'

const state = reactive({
  accessToken: '',
  refreshToken: '',
  user: null,
  hydrated: false,
})

function applySession(session) {
  state.accessToken = session?.accessToken ?? session?.token ?? ''
  state.refreshToken = session?.refreshToken ?? ''
  state.user = session?.user ?? null

  if (state.accessToken && state.user) {
    setStoredSession({
      accessToken: state.accessToken,
      refreshToken: state.refreshToken,
      tokenType: session?.tokenType ?? 'Bearer',
      user: state.user,
    })
  } else {
    clearStoredSession()
  }
}

export function hydrateSession() {
  if (state.hydrated) {
    return
  }

  state.hydrated = true

  const stored = getStoredSession()
  if (!stored) {
    return
  }

  applySession(stored)
}

export const authUser = computed(() => state.user)
export const authToken = computed(() => state.accessToken)
export const authRefreshToken = computed(() => state.refreshToken)
export const authIsAuthenticated = computed(() => Boolean(state.accessToken && state.user))
export const authIsAdmin = computed(() => Boolean(state.user?.roles?.includes('ADMIN')))

export function getStoredUser() {
  hydrateSession()
  return state.user
}

export function getToken() {
  hydrateSession()
  return state.accessToken
}

export async function login(payload) {
  const session = await apiLogin(payload)
  applySession(session)
  return session
}

export async function register(payload) {
  return apiRegister(payload)
}

export function logout() {
  state.accessToken = ''
  state.refreshToken = ''
  state.user = null
  clearAuthSession()
}

async function refreshSession() {
  hydrateSession()

  if (!state.refreshToken) {
    logout()
    throw new Error('Phiên đăng nhập đã hết hạn.')
  }

  const payload = await apiFetch('/api/auth/refresh', {
    method: 'POST',
    headers: authHeaders(),
    body: {
      refreshToken: state.refreshToken,
    },
  })

  applySession(payload)
  return payload
}

export async function fetchWithAuth(path, init = {}) {
  hydrateSession()

  const execute = () =>
    apiFetch(path, {
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

  if (!state.accessToken) {
    return false
  }

  try {
    const user = await getCurrentUser()
    applySession({
      accessToken: state.accessToken,
      refreshToken: state.refreshToken,
      tokenType: 'Bearer',
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

hydrateSession()
