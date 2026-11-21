import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse } from './auth.models';

const TOKEN_KEY = 'auth_token';
const USER_KEY = 'auth_user';
const REMEMBER_KEY = 'remember_username';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = 'http://localhost:8080/api/auth';

  private readonly tokenSignal = signal<string | null>(this.readToken());
  private readonly usernameSignal = signal<string | null>(this.readUsername());

  readonly isAuthenticated = computed(() => !!this.tokenSignal());
  readonly username = computed(() => this.usernameSignal());

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router
  ) {}

  login(payload: LoginRequest, rememberMe: boolean): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, payload).pipe(
      tap((res) => {
        localStorage.setItem(TOKEN_KEY, res.token);
        localStorage.setItem(USER_KEY, JSON.stringify({ username: res.username, role: res.role }));
        this.tokenSignal.set(res.token);
        this.usernameSignal.set(res.username);

        if (rememberMe) {
          localStorage.setItem(REMEMBER_KEY, payload.username);
        } else {
          localStorage.removeItem(REMEMBER_KEY);
        }
      })
    );
  }

  logout(): void {
    const token = this.getToken();
    if (token) {
      this.http.post(`${this.apiUrl}/logout`, {}).subscribe({ error: () => undefined });
    }
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.tokenSignal.set(null);
    this.usernameSignal.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return this.tokenSignal();
  }

  getRememberedUsername(): string {
    return localStorage.getItem(REMEMBER_KEY) ?? '';
  }

  private readToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  private readUsername(): string | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw).username ?? null;
    } catch {
      return null;
    }
  }
}
