import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LoginResponse } from '../../models/login-response.model';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.html'
})
export class LoginComponent {

  email: string = '';
  password: string = '';
  otp: string = '';

  requiresOtp: boolean = false;
  pendingUserId: number | null = null;

  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;



  constructor(
    private authService: AuthService,
    private router: Router,
  
  ) {}

  onLogin(): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.isLoading = true;

    this.authService.login({
      email: this.email,
      password: this.password
    }).subscribe({
      next: (response: LoginResponse) => {
        this.isLoading = false;

        if (!response.success) {
          this.errorMessage = response.message;
          return;
        }

 if (response.requiresOtp) {
  const pendingId = response.user?.id;

  if (!pendingId) {
    this.errorMessage = 'Utilisateur en attente introuvable.';
    return;
  }

  localStorage.setItem('pendingUserId', pendingId.toString());
  this.router.navigate(['/verification']);
  return;
}

// COMPTE SANS OTP
this.handleRedirect(
  response.user?.role,
  response.user?.id,
  response.user?.email,
  response.user?.firstName,
  response.user?.lastName
);

    
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'Erreur serveur.';
      }
    });
  }

  onVerifyOtp(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.pendingUserId) {
      this.errorMessage = 'Utilisateur en attente introuvable.';
      return;
    }

    this.isLoading = true;

    this.authService.verifyOtp({
      userId: this.pendingUserId,
      otp: this.otp,
      key: ''
    }).subscribe({
      next: (response: LoginResponse) => {
        this.isLoading = false;

        if (!response.success) {
          this.errorMessage = response.message;
          return;
        }

       this.handleRedirect(
        response.user?.role,
        response.user?.id,
        response.user?.email,
        response.user?.firstName,
        response.user?.lastName
      );
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'Erreur lors de la vérification OTP.';
      }
    });
  }

  onResendOtp(): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.isLoading = true;

    this.authService.resendOtp().subscribe({
      next: (response: LoginResponse) => {
        this.isLoading = false;
        this.successMessage = response.message;
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = "Erreur lors de l'envoi du nouveau code.";
      }
    });
  }
private handleRedirect(
  role?: string,
  userId?: number,
  email?: string,
  firstName?: string,
  lastName?: string
): void {
  localStorage.removeItem('pendingUserId');

  if (!role || !userId) {
    this.errorMessage = 'Réponse de connexion invalide.';
    return;
  }

  localStorage.setItem('userId', userId.toString());
  localStorage.setItem('role', role);
  localStorage.setItem('email', email || this.email);
  localStorage.setItem('firstName', firstName || '');
  localStorage.setItem('lastName', lastName || '');

  this.router.navigate(['/']);
}

loginWithGoogle(): void {
  window.location.href = 'http://localhost:8080/oauth2/authorization/google';
}
}