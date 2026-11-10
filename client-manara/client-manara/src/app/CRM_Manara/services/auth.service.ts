import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginRequest } from '../models/login-request.model';
import { LoginResponse } from '../models/login-response.model';
import { OtpLoginRequest } from '../models/otp-login-request';

export interface RegisterPayload {
  firstName: string;
  lastName: string;
  date: string;
  email: string;
  phone: string;
  password: string;
}
export interface RegisterResponse {
  success?: boolean;
  message?: string;
  redirectUrl?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/api/auth';

  // Utilisateur en mémoire après connexion
  private currentUser: LoginResponse | null = null;

  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, request, {
      withCredentials: true
    }).pipe(
      // Sauvegarde l'utilisateur en mémoire après connexion réussie
      tap(user => this.currentUser = user)
    );
  }

  verifyOtp(request: OtpLoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/verify-otp`, request, {
      withCredentials: true
    }).pipe(
      tap(user => this.currentUser = user)
    );
  }

  resendOtp(): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/resend-otp`, {}, {
      withCredentials: true
    });
  }

  me(): Observable<LoginResponse> {
    return this.http.get<LoginResponse>(`${this.apiUrl}/me`, {
      withCredentials: true
    }).pipe(
      tap(user => this.currentUser = user)
    );
  }

  logout(): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/logout`, {}, {
      withCredentials: true
    }).pipe(
      tap(() => this.currentUser = null)
    );
  }

  register(payload: RegisterPayload): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${this.apiUrl}/register`, payload, {
      withCredentials: true
    });
  }

  checkVerificationCode(code: string): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(
      `${this.apiUrl}/verify-email-code`,
      { code },
      { withCredentials: true }
    );
  }

  // ---- Méthodes utilitaires ----

  // Retourne l'ID de l'utilisateur connecté
  getCurrentUserId(): number {
  return this.currentUser?.userId ?? 0;
}

  // Retourne l'utilisateur complet en mémoire
  getCurrentUser(): LoginResponse | null {
    return this.currentUser;
  }

  // Vérifie si un utilisateur est connecté
  isLoggedIn(): boolean {
    return this.currentUser !== null;
  }

  // Retourne le rôle de l'utilisateur connecté
  getRole(): string {
    return this.currentUser?.role ?? '';
  }

  verifyEmailToken(token: string) {
    const params = new HttpParams().set('token', token);
    return this.http.get<{ message: string }>(`${this.apiUrl}/api/auth/verify-email`, {
      params,
      withCredentials: true
    });
  }
loginWithGoogle(): void {
  window.location.href = 'http://localhost:8080/oauth2/authorization/google';
}
}