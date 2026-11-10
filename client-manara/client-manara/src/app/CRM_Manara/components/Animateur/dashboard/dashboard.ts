import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { AuthService } from '../../../services/auth.service';
import { ActivityService } from '../../../services/activity.service';
import { SessionService } from '../../../services/session.service';
import { RegistrationService } from '../../../services/registration.service';

@Component({
  selector: 'app-dashboard-animateur',
  standalone: false,
  templateUrl: './dashboard.html'
})
export class Dashboard implements OnInit {
  connectedUser: any = {
    firstName: '',
    lastName: ''
  };

  totalActivites = 0;
  sessionsCetteSemaine = 0;
  totalEnfants = 0;

  sessionDuJour: any = null;
  prochainesSessions: any[] = [];
  erreur = '';

  constructor(
    private authService: AuthService,
    private activityService: ActivityService,
    private sessionService: SessionService,
    private registrationService: RegistrationService
  ) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.authService.me().subscribe({
      next: (response) => {
        const animateurId = response.user?.id || response.userId;
        this.connectedUser = response.user ?? { firstName: '', lastName: '' };

        if (!animateurId) {
          this.erreur = 'Animateur non connecté.';
          return;
        }

        forkJoin({
          totalActivites: this.activityService.countByAnimateur(animateurId),
          sessionsCetteSemaine: this.sessionService.countThisWeek(animateurId),
          totalEnfants: this.registrationService.countByAnimateur(animateurId),
          sessionDuJour: this.sessionService.getCurrentToday(animateurId),
          prochainesSessions: this.sessionService.getUpcomingByAnimateur(animateurId, 5)
        }).subscribe({
          next: (data) => {
            this.totalActivites = data.totalActivites ?? 0;
            this.sessionsCetteSemaine = data.sessionsCetteSemaine ?? 0;
            this.totalEnfants = data.totalEnfants ?? 0;
            this.sessionDuJour = data.sessionDuJour ?? null;
            this.prochainesSessions = data.prochainesSessions ?? [];
          },
          error: () => {
            this.erreur = 'Impossible de charger le tableau de bord animateur.';
          }
        });
      },
      error: () => {
        this.erreur = 'Utilisateur non connecté.';
      }
    });
  }
}