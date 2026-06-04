export class ApiError extends Error {
  constructor(message, status, payload) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.payload = payload
  }
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'
const AUTH_STORAGE_KEY = 'book-area-auth-session'

function canUseStorage() {
  return typeof window !== 'undefined' && typeof window.localStorage !== 'undefined'
}

function buildUrl(path) {
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path
  }

  return `${API_BASE_URL}${path.startsWith('/') ? path : `/${path}`}`
}

function normalizeBody(body) {
  if (
    body == null ||
    typeof body === 'string' ||
    body instanceof FormData ||
    body instanceof URLSearchParams ||
    body instanceof Blob
  ) {
    return body
  }

  return JSON.stringify(body)
}

function readStoredSession() {
  if (!canUseStorage()) {
    return null
  }

  const raw = window.localStorage.getItem(AUTH_STORAGE_KEY)
  if (!raw) {
    return null
  }

  try {
    const parsed = JSON.parse(raw)
    const accessToken = parsed.accessToken ?? parsed.token ?? ''

    if (!accessToken || !parsed.user) {
      return null
    }

    return {
      accessToken,
      token: accessToken,
      refreshToken: parsed.refreshToken ?? '',
      tokenType: parsed.tokenType ?? 'Bearer',
      user: parsed.user,
    }
  } catch {
    return null
  }
}

export function setStoredSession(session) {
  if (!canUseStorage()) {
    return session
  }

  if (!session?.accessToken || !session?.user) {
    window.localStorage.removeItem(AUTH_STORAGE_KEY)
    return null
  }

  const normalized = {
    accessToken: session.accessToken,
    token: session.accessToken,
    refreshToken: session.refreshToken ?? '',
    tokenType: session.tokenType ?? 'Bearer',
    user: session.user,
  }

  window.localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(normalized))
  return normalized
}

export function getStoredSession() {
  return readStoredSession()
}

export function getStoredUser() {
  return readStoredSession()?.user ?? null
}

export function getToken() {
  return readStoredSession()?.accessToken ?? ''
}

export function clearStoredSession() {
  if (canUseStorage()) {
    window.localStorage.removeItem(AUTH_STORAGE_KEY)
  }
}

export function authHeaders(headers = {}, token = getToken()) {
  const normalized = new Headers(headers)

  if (token) {
    normalized.set('Authorization', `Bearer ${token}`)
  }

  return normalized
}

export async function apiFetch(path, init = {}) {
  const headers = new Headers(init.headers)
  const body = normalizeBody(init.body)

  if (body != null && !(body instanceof FormData) && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  const response = await fetch(buildUrl(path), {
    ...init,
    headers,
    body,
  })

  const contentType = response.headers.get('content-type') ?? ''
  const payload = contentType.includes('application/json')
    ? await response.json()
    : await response.text()

  if (!response.ok) {
    const message =
      typeof payload === 'object' && payload !== null && 'message' in payload
        ? String(payload.message)
        : response.statusText || 'Request failed'

    throw new ApiError(message, response.status, payload)
  }

  return payload
}

function normalizeAuthResponse(payload) {
  const accessToken = payload?.accessToken ?? payload?.token ?? ''

  return {
    accessToken,
    token: accessToken,
    refreshToken: payload?.refreshToken ?? '',
    tokenType: payload?.tokenType ?? 'Bearer',
    user: payload?.user ?? null,
  }
}

export async function login(credentials) {
  const payload = await apiFetch('/api/auth/login', {
    method: 'POST',
    body: credentials,
  })

  const session = normalizeAuthResponse(payload)
  setStoredSession(session)
  return session
}

export async function register(payload) {
  return apiFetch('/api/auth/register', {
    method: 'POST',
    body: payload,
  })
}

export async function getCurrentUser() {
  const token = getToken()

  if (!token) {
    throw new ApiError('Unauthorized', 401, null)
  }

  return apiFetch('/api/auth/me', {
    headers: authHeaders(),
  })
}

export function logout() {
  clearStoredSession()
}

export function getBooks() {
  return apiFetch('/api/books')
}
