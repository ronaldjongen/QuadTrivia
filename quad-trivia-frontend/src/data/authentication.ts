export interface LoginRequest {
  username: string
  password: string
}
export interface LoginResult {
  username: string
  isAuthenticated: boolean
}

