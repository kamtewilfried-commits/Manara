import { Component, Input, HostListener, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ThemeService } from '../../../services/theme.service';
import { I18nService } from '../../../services/i18n.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-header-parent',
  standalone: false,
  templateUrl: './header-parents.component.html'
})
export class HeaderParentsComponent implements OnInit {

  @Input() connectedUser: {
    firstName?: string;
    lastName?: string;
    email?: string;
    role?: string;
  } | null = null;

  email = '';
  role = '';
  isLoggedIn = false;

  langOpen = false;
  unreadCount = 0;

  constructor(
    private router: Router,
    private authService: AuthService,
    public theme: ThemeService,
    public i18n: I18nService
  ) {}

  ngOnInit(): void {
    this.email = localStorage.getItem('email') || '';
    this.role = localStorage.getItem('role') || '';
    this.isLoggedIn = !!localStorage.getItem('userId');

    // Always refresh with backend session data to avoid stale header info.
    if (this.isLoggedIn) {
      this.authService.me().subscribe({
        next: (response) => {
          if (!response?.success || !response.user) return;
          this.connectedUser = response.user;
          this.email = response.user.email || this.email;
          this.role = response.user.role || this.role;
          localStorage.setItem('email', this.email);
          localStorage.setItem('role', this.role);
        },
        error: () => {
          // Keep localStorage/input fallback if the session endpoint fails.
        }
      });
    }
  }

  get displayName(): string {
    const first = this.connectedUser?.firstName?.trim() || '';
    const last = this.connectedUser?.lastName?.trim() || '';
    return [first, last].filter(Boolean).join(' ').trim() || this.email || this.i18n.t('common.user');
  }

  getInitiales(): string {
    const first = this.connectedUser?.firstName?.charAt(0) || this.email?.charAt(0) || '';
    const last = this.connectedUser?.lastName?.charAt(0) || '';
    return (first + last).toUpperCase() || 'PA';
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
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => this.clearSessionAndRedirect(),
      error: () => this.clearSessionAndRedirect()
    });
  }

  private clearSessionAndRedirect(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('userId');
    localStorage.removeItem('email');
    localStorage.removeItem('role');
    localStorage.removeItem('firstName');
    localStorage.removeItem('lastName');
    localStorage.removeItem('pendingUserId');
    this.isLoggedIn = false;
    this.router.navigate(['/login']);
  }
}