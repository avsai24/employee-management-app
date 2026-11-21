export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  refreshToken: string;
  username: string;
  role: string;
  expiresInMs: number;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}
