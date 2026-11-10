import { Component, OnInit } from '@angular/core';
import { RegistrationService } from '../../../services/registration.service';
import { AuthService } from '../../../services/auth.service';
import { Registration } from '../../../models/registration.model';

@Component({
  selector: 'app-registrations-animateur',
  standalone: false,
  templateUrl: './registration.html'
})
export class RegistrationAnimateur implements OnInit {

  registrations: Registration[] = [];
  filteredRegistrations: Registration[] = [];

  connectedUser: any = null;

  errorMessage = '';
  successMessage = '';

  statusFilter = '';

  constructor(
    private registrationService: RegistrationService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadUserAndRegistrations();
  }

  loadUserAndRegistrations(): void {
    this.authService.me().subscribe({
      next: (res) => {
        this.connectedUser = res.user ?? null;

        const animateurId = res.user?.id || res.userId;

        if (!animateurId) {
          this.errorMessage = 'Animateur introuvable.';
          return;
        }

        this.loadRegistrations(animateurId);
      },
      error: (err) => {
        console.error('ERREUR UTILISATEUR:', err);
        this.errorMessage = 'Erreur utilisateur.';
      }
    });
  }

  loadRegistrations(animateurId: number): void {
    this.registrationService.getRegistrationsByAnimateur(animateurId).subscribe({
      next: (data) => {
        console.log('REGISTRATIONS:', data);
        this.registrations = data;
        this.filteredRegistrations = [...data];
      },
      error: (err) => {
        console.error('ERREUR CHARGEMENT:', err);
        this.errorMessage = 'Erreur chargement inscriptions.';
      }
    });
  }

  filtrer(): void {
    const statut = this.statusFilter.trim();

    this.filteredRegistrations = this.registrations.filter(r =>
      !statut || r.status === statut
    );
  }

  accepter(id: number): void {
    const animateurId = this.connectedUser?.id;

    if (!id) {
      this.errorMessage = 'Inscription introuvable.';
      return;
    }

    if (!animateurId) {
      this.errorMessage = 'Animateur introuvable.';
      return;
    }

    this.registrationService.confirmerByAnimateur(id, animateurId).subscribe({
      next: () => {
        this.successMessage = 'Inscription acceptée.';
        this.errorMessage = '';
        this.loadRegistrations(animateurId);
      },
      error: (err) => {
        console.error('ERREUR CONFIRMATION:', err);
        this.successMessage = '';
        this.errorMessage = 'Erreur lors de la confirmation.';
      }
    });
  }

  refuser(id: number): void {
    const animateurId = this.connectedUser?.id;

    if (!id) {
      this.errorMessage = 'Inscription introuvable.';
      return;
    }

    if (!animateurId) {
      this.errorMessage = 'Animateur introuvable.';
      return;
    }

    this.registrationService.annuler(id).subscribe({
      next: () => {
        this.successMessage = 'Inscription refusée.';
        this.errorMessage = '';
        this.loadRegistrations(animateurId);
      },
      error: (err) => {
        console.error('ERREUR REFUS:', err);
        this.successMessage = '';
        this.errorMessage = 'Erreur lors du refus.';
      }
    });
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'CONFIRMED':
        return 'Confirmée';
      case 'PENDING':
        return 'En attente';
      case 'PENDING_PAYMENT':
        return 'Paiement en attente';
      case 'CANCELLED':
        return 'Annulée';
      default:
        return status;
    }
  }
}