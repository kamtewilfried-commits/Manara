import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-verify-email',
  standalone: false,
  template: `
    <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-indigo-600 to-purple-700 p-4">
      <div class="w-full max-w-sm bg-white/20 backdrop-blur-lg rounded-2xl p-8 shadow-lg text-center">

        @if (isLoading) {
          <div class="flex flex-col items-center gap-4">
            <div class="w-12 h-12 rounded-full border-4 border-white/30 border-t-white animate-spin"></div>
            <p class="text-white font-semibold">Vérification en cours...</p>
          </div>
        }

        @if (!isLoading && success) {
          <div class="flex flex-col items-center gap-4">
            <div class="w-16 h-16 rounded-full bg-emerald-500 flex items-center justify-center shadow-lg">
              <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24">
                <polyline points="20 6 9 17 4 12"/>
              </svg>
            </div>
            <h2 class="text-xl font-bold text-white">Email vérifié !</h2>
            <p class="text-white/80 text-sm">{{ message }}</p>
            <button type="button"
                    (click)="goToLogin()"
                    class="mt-2 inline-flex items-center justify-center w-full py-3 rounded-xl bg-white text-indigo-700 font-semibold hover:bg-indigo-50 transition">
              Se connecter
            </button>
          </div>
        }

        @if (!isLoading && !success) {
          <div class="flex flex-col items-center gap-4">
            <div class="w-16 h-16 rounded-full bg-red-500 flex items-center justify-center shadow-lg">
              <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24">
                <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </div>
            <h2 class="text-xl font-bold text-white">Lien invalide</h2>
            <p class="text-white/80 text-sm">{{ message }}</p>
            <button type="button"
                    (click)="goHome()"
                    class="mt-2 inline-flex items-center justify-center w-full py-3 rounded-xl bg-white/20 text-white font-semibold hover:bg-white/30 transition">
              Retour à l'accueil
            </button>
          </div>
        }

      </div>
    </div>
  `
})
export class VerifyEmailComponent implements OnInit {

  message = '';
  isLoading = true;
  success = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (!token) {
      this.isLoading = false;
      this.success = false;
      this.message = 'Lien de vérification manquant ou invalide.';
      return;
    }

    this.authService.verifyEmailToken(token).subscribe({
      next: (res: any) => {
        this.isLoading = false;
        this.success = true;
        this.message = res.message || 'Votre adresse email a été confirmée.';
      },
      error: () => {
        this.isLoading = false;
        this.success = false;
        this.message = 'Ce lien est expiré ou déjà utilisé.';
      }
    });
  }

  goToLogin(): void {
    void this.router.navigate(['/login']);
  }

  goHome(): void {
    void this.router.navigate(['/']);
  }
}