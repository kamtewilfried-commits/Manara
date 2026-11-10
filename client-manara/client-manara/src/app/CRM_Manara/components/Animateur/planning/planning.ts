import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { SessionService } from '../../../services/session.service';
import { SessionPlanningEvent } from '../../../models/session-planning-event.model';

@Component({
  selector: 'app-planning-animateur',
  standalone: false,
  templateUrl: './planning.html'
})
export class PlanningAnimateur implements OnInit {
  connectedUser: any = null;
  events: SessionPlanningEvent[] = [];
  erreur = '';
  loading = false;

  constructor(
    private authService: AuthService,
    private sessionService: SessionService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.erreur = '';

    this.authService.me().subscribe({
      next: (response) => {
        const animateurId = response.user?.id || response.userId;
        this.connectedUser = response.user ?? {
          firstName: '',
          lastName: ''
        };

        if (!animateurId) {
          this.loading = false;
          this.erreur = 'Animateur non connecté.';
          return;
        }

        this.sessionService.getPlanningEvents(animateurId).subscribe({
          next: (data) => {
            this.events = data ?? [];
            this.loading = false;
          },
          error: () => {
            this.loading = false;
            this.erreur = 'Impossible de charger le planning.';
          }
        });
      },
      error: () => {
        this.loading = false;
        this.erreur = 'Utilisateur non connecté.';
      }
    });
  }

  get orderedDays(): string[] {
    const set = new Set<string>();
    for (const e of this.events) {
      if (e.day) set.add(e.day);
    }
    return [...set].sort((a, b) => a.localeCompare(b, 'fr'));
  }

  eventsForDay(day: string): SessionPlanningEvent[] {
    return this.events
      .filter((e) => e.day === day)
      .sort((a, b) => (a.slot || '').localeCompare(b.slot || '', 'fr'));
  }
}
