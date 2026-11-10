import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LoginResponse } from '../../models/login-response.model';

@Component({
  selector: 'app-verification',
  standalone: false,
  templateUrl: './verification.component.html',
  styleUrls: ['./verification.component.css']
})
export class VerificationComponent {
  code: string = '';
  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;

  pendingUserId: number | null = null;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    const storedUserId = localStorage.getItem('pendingUserId');
    this.pendingUserId = storedUserId ? Number(storedUserId) : null;
  }

  onSubmit(): void {
    if (!this.code.trim()) {
      this.errorMessage = 'Veuillez entrer le code OTP.';
      return;
    }

    if (!this.pendingUserId) {
      this.errorMessage = 'Session expirée. Veuillez recommencer la connexion.';
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';
    this.isLoading = true;

    this.authService.verifyOtp({
      userId: this.pendingUserId,
      otp: this.code,
      key: ''
    }).subscribe({
      next: (response: LoginResponse) => {
        this.isLoading = false;

        if (!response.success) {
          this.errorMessage = response.message || 'Code invalide ou expiré.';
          return;
        }

       localStorage.removeItem('pendingUserId');

if (response.user?.id) {
  localStorage.setItem('userId', response.user.id.toString());
}

if (response.user?.role) {
  localStorage.setItem('role', response.user.role);
}

if (response.user?.email) {
  localStorage.setItem('email', response.user.email);
}
if (response.user?.firstName) {
  localStorage.setItem('firstName', response.user.firstName);
}
if (response.user?.lastName) {
  localStorage.setItem('lastName', response.user.lastName);
}

this.redirectByRole(response.user?.role);
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage =
          error?.error?.message || 'Code invalide ou expiré.';
      }
    });
  }

  onResendCode(): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.isLoading = true;

    this.authService.resendOtp().subscribe({
      next: (response: LoginResponse) => {
        this.isLoading = false;
        this.successMessage = response.message || 'Un nouveau code a été envoyé.';
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage =
          error?.error?.message || "Impossible de renvoyer le code.";
      }
    });
  }

  private redirectByRole(role?: string): void {
    if (role === 'ADMIN') {
      this.router.navigate(['/dashboard']);
    } else if (role === 'ANIMATEUR') {
      this.router.navigate(['/animateur/dashboard']);
    } else if (role === 'PARENT') {
      this.router.navigate(['/parent/activites']);
    } else {
      this.router.navigate(['/']);
    }
  }
}