import { Component, Input, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { ThemeService } from '../../../services/theme.service';
import { I18nService } from '../../../services/i18n.service';

@Component({
  selector: 'app-header-animateur',
  standalone: false,
  templateUrl: './header-animateur.component.html'
})
export class HeaderAnimateurComponent {

  @Input() connectedUser: any;

  langOpen = false;

  constructor(
    private router: Router,
    public theme: ThemeService,
    public i18n: I18nService
  ) {}

  getInitiales(): string {
    const first = this.connectedUser?.firstName?.charAt(0) || '';
    const last  = this.connectedUser?.lastName?.charAt(0)  || '';
    return (first + last).toUpperCase() || 'AN';
  }

  toggleLang(): void {
    this.langOpen = !this.langOpen;
  }

  selectLang(l: 'fr' | 'en'): void {
    this.i18n.setLang(l);
    this.langOpen = false;
  }

  // Ferme le dropdown si on clique en dehors
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.lang-wrap')) {
      this.langOpen = false;
    }
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.router.navigate(['/login']);
  }
}