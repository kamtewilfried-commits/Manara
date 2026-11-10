import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-oauth-success',
  template: '<p>Connexion Google en cours...</p>',
  standalone: false
})
export class OAuthSuccessComponent implements OnInit {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.me().subscribe({
      next: (res: any) => {
        const user = res.user || res;

        localStorage.setItem('userId', user.userId || user.id);
        localStorage.setItem('email', user.email);
        localStorage.setItem('role', user.role);

        if (user.role === 'ADMIN') {
          this.router.navigate(['/dashboard']);
        } else if (user.role === 'ANIMATEUR') {
          this.router.navigate(['/animateur/dashboard']);
        } else {
          this.router.navigate(['/parent/activites']);
        }
      },
      error: () => {
        this.router.navigate(['/login']);
      }
    });
  }
}