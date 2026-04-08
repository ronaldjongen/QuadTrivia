export interface LoginRequest {
  username: string
  password: string
}
export interface LoginResult {
  username: string | null
  isAuthenticated: boolean
}
