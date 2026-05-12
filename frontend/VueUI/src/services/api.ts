export class ApiError extends Error {
  status: number
  payload: unknown

  constructor(message: string, status: number, payload: unknown) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.payload = payload
  }
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

function buildUrl(path: string) {
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path
  }

  return `${API_BASE_URL}${path.startsWith('/') ? path : `/${path}`}`
}

function normalizeBody(body: BodyInit | Record<string, unknown> | null | undefined) {
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

export async function apiFetch<T>(
  path: string,
  init: Omit<RequestInit, 'body'> & {
    body?: BodyInit | Record<string, unknown> | null
  } = {},
) {
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

  return payload as T
}
