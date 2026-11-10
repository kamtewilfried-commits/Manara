import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { SessionService } from '../../../services/session.service';
import { ActivityService } from '../../../services/activity.service';
import { Session } from '../../../models/session.model';
import { Activity } from '../../../models/activity.model';

@Component({
  selector: 'app-sessions-animateur',
  standalone: false,
  templateUrl: './sessions.html'
})
export class SessionsAnimateur implements OnInit {

  connectedUser: any = null;

  sessions: Session[] = [];
  filteredSessions: Session[] = [];
  activites: Activity[] = [];

  activiteFilter: number | '' = '';
  statutFilter = '';

  errorMessage = '';
  successMessage = '';

  showCreateModal = false;

  newSession = {
    activityId: null as number | null,
    startDate: '',
    endDate: '',
    location: '',
    capacity: 1
  };

  constructor(
    private authService: AuthService,
    private sessionService: SessionService,
    private activityService: ActivityService
  ) {}

  ngOnInit(): void {
      console.log('✅ SessionsAnimateur chargé');
    this.loadConnectedUserAndSessions();
  }

  loadConnectedUserAndSessions(): void {
     console.log('✅ Appel me()');
    this.authService.me().subscribe({
      next: (response) => {
         console.log('✅ USER:', response);

  this.connectedUser = response.user ?? null;

  const animateurId = response.user?.id;

        if (animateurId) {
          this.loadSessions(animateurId);
          this.loadActivities(animateurId);
        }
      },
      error: (err) => {
           console.error('❌ Erreur me():', err);
        console.error(err);
        this.errorMessage = 'Utilisateur non connecté.';
      }
    });
  }
loadSessions(animateurId: number): void {
  this.sessionService.getSessionsByAnimateur(animateurId).subscribe({
    next: (sessions) => {
      console.log('SESSIONS ANGULAR:', sessions);
      this.sessions = sessions;
      this.filteredSessions = [...sessions];
    },
    error: (err) => console.error('ERREUR SESSIONS:', err)
  });
}

loadActivities(animateurId: number): void {
  this.activityService.getActivitiesByAnimateur(animateurId).subscribe({
    next: (activities) => {
      console.log('ACTIVITÉS ANGULAR:', activities);
      this.activites = activities;
    },
    error: (err) => console.error('ERREUR ACTIVITÉS:', err)
  });
}
  filtrer(): void {
    const statut = this.statutFilter.toLowerCase().trim();

    this.filteredSessions = this.sessions.filter(session => {
      const activiteOk =
        !this.activiteFilter ||
        session.activityId === Number(this.activiteFilter);

      const statutTexte = this.getStatusLabel(session.status).toLowerCase();

      const statutOk =
        !statut ||
        statutTexte === statut;

      return activiteOk && statutOk;
    });
  }

  openCreateModal(): void {
    this.showCreateModal = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  closeCreateModal(): void {
    this.showCreateModal = false;

    this.newSession = {
      activityId: null,
      startDate: '',
      endDate: '',
      location: '',
      capacity: 1
    };
  }

  createSession(): void {
    if (!this.connectedUser?.id) {
      this.errorMessage = 'Animateur introuvable.';
      return;
    }

    if (!this.newSession.activityId) {
      this.errorMessage = 'Veuillez choisir une activité.';
      return;
    }

    const payload: any = {
      startDate: this.newSession.startDate,
      endDate: this.newSession.endDate,
      location: this.newSession.location,
      capacity: Number(this.newSession.capacity),
      status: 'PLANNED',
      activityId: this.newSession.activityId
     
    };

    this.sessionService.createSessionForAnimateur(
     this.connectedUser?.id,
      payload
    ).subscribe({
      next: () => {
        this.successMessage = 'Session créée avec succès.';
        this.errorMessage = '';
        this.closeCreateModal();

        if (this.connectedUser?.id) {
          this.loadSessions(this.connectedUser.id);
          this.loadActivities(this.connectedUser.id);
        }
      },
      error: (err) => {
        console.error(err);
        this.successMessage = '';
        this.errorMessage = 'Erreur lors de la création de la session.';
      }
    });
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'PLANNED':
        return 'Planifiée';
      case 'IN_PROGRESS':
        return 'En cours';
      case 'COMPLETED':
        return 'Terminée';
      case 'CANCELLED':
        return 'Annulée';
      default:
        return 'Inconnu';
    }
  }
}