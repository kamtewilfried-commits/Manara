import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ThemeService {

  private dark = false;

  constructor() {
    const saved = localStorage.getItem('manara-theme');
    if (saved === 'dark') this.apply(true);
  }

  toggle(): void {
    this.apply(!this.dark);
  }

  private apply(dark: boolean): void {
    this.dark = dark;
    // ← Ajoute/retire la classe 'dark' sur <html>
    // Pas besoin de tailwind.config.js avec cette approche
    if (dark) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
    localStorage.setItem('manara-theme', dark ? 'dark' : 'light');
  }

  isDark(): boolean {
    return this.dark;
  }
}