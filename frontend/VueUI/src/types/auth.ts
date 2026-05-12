export type AuthUser = {
  id: number
  email: string
  fullName: string
  phone: string | null
  roles: string[]
  addressIds: number[]
}

export type AuthResponse = {
  token: string
  refreshToken: string
  user: AuthUser
}

export type LoginPayload = {
  email: string
  password: string
}
