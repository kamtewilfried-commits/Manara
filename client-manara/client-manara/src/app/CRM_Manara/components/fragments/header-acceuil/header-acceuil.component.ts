
import { Component, HostListener, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ThemeService } from '../../../services/theme.service';
import { I18nService } from '../../../services/i18n.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-header-acceuil',
  standalone: false,
  templateUrl: './header-acceuil.component.html',
  styleUrls: ['./header-acceuil.component.css']
})
export class HeaderAcceuilComponent implements OnInit {

  email: string = '';
  role: string = '';
  isLoggedIn = false;

  langOpen = false;
  mobileMenuOpen = false;

  constructor(
    private router: Router,
    private authService: AuthService,
    public theme: ThemeService,
    public i18n: I18nService
  ) {}

  ngOnInit(): void {
    this.refreshLoginState();
    this.validateSession();
  }

  refreshLoginState(): void {
    this.email = localStorage.getItem('email') || '';
    this.role = localStorage.getItem('role') || '';
    this.isLoggedIn = !!localStorage.getItem('userId') || !!this.email || !!this.role;
  }

  /**
   * Si /api/auth/me échoue (réseau) ou qu’il n’y a pas de cookie de session (souvent en dev HTTP
   * avec SameSite=None + Secure), on garde l’état issu du localStorage après login Angular.
   * Sinon on synchronise email/rôle depuis le serveur.
   */
  private validateSession(): void {
    this.authService.me().subscribe({
      next: (response) => {
        if (response?.success && response.user) {
          this.isLoggedIn = true;
          this.email = response.user.email || '';
          this.role = response.user.role || '';
          localStorage.setItem('userId', String(response.user.id));
          localStorage.setItem('email', this.email);
          localStorage.setItem('role', this.role);
          return;
        }
        this.refreshLoginState();
      },
      error: () => this.refreshLoginState()
    });
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => this.clearLocalAuthStateAndRedirect(),
      error: () => this.clearLocalAuthStateAndRedirect()
    });
  }

  private clearLocalAuthStateAndRedirect(): void {
    this.clearLocalAuthState();
    this.router.navigate(['/login']);
  }

  private clearLocalAuthState(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('userId');
    localStorage.removeItem('email');
    localStorage.removeItem('role');
    localStorage.removeItem('pendingUserId');
    this.isLoggedIn = false;
    this.email = '';
    this.role = '';
  }

  toggleMobileMenu(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  toggleLang(): void {
    this.langOpen = !this.langOpen;
  }

  selectLang(l: 'fr' | 'en'): void {
    this.i18n.setLang(l);
    this.langOpen = false;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.lang-wrap')) {
      this.langOpen = false;
    }
    if (!target.closest('.mobile-nav-wrap')) {
      this.mobileMenuOpen = false;
    }
  }

  /*logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.router.navigate(['/login']);
  }*/
}