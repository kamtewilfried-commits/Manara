import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { SessionService } from '../../../services/session.service';
import { RegistrationService } from '../../../services/registration.service';
import { Session } from '../../../models/session.model';
import { Registration } from '../../../models/registration.model';

@Component({
  selector: 'app-presences',
  standalone: false,
  templateUrl: './presences.html'
})
export class Presences implements OnInit {
  succes = '';
  erreur = '';
  loading = false;

  connectedUser: any = null;

  session: Session | null = null;

  registrations: Registration[] = [];

  constructor(
    private authService: AuthService,
    private sessionService: SessionService,
    private registrationService: RegistrationService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.erreur = '';
    this.succes = '';

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

        this.sessionService.getCurrentToday(animateurId).subscribe({
          next: (session) => {
            this.session = session;

            if (!session?.id) {
              this.loading = false;
              this.registrations = [];
              return;
            }

            this.registrationService.getRegistrationsBySession(session.id).subscribe({
              next: (registrations) => {
                this.registrations = registrations;
                this.loading = false;
              },
              error: () => {
                this.loading = false;
                this.erreur = 'Impossible de charger les inscriptions de la session.';
              }
            });
          },
          error: () => {
            this.loading = false;
            this.erreur = 'Impossible de charger la session du jour.';
          }
        });
      },
      error: () => {
        this.loading = false;
        this.erreur = 'Utilisateur non connecté.';
      }
    });
  }

  getNombrePresents(): number {
    return this.registrations.filter(reg => reg.present).length;
  }

  enregistrerPresences(): void {
    const presences = this.registrations
      .filter(reg => reg.present)
      .map(reg => reg.childId)
      .filter((id): id is number => typeof id === 'number');

    if (!this.session?.id) {
      this.erreur = 'Aucune session sélectionnée.';
      return;
    }

    this.registrationService.savePresences(this.session.id, presences).subscribe({
      next: () => {
        this.succes = 'Présences enregistrées avec succès.';
        this.erreur = '';
      },
      error: () => {
        this.erreur = "Impossible d'enregistrer les présences.";
      }
    });
  }
}