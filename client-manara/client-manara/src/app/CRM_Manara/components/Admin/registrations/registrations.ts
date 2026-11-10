import { Component, OnInit } from '@angular/core';
import { Registration } from '../../../models/registration.model';
import { RegistrationService } from '../../../services/registration.service';

@Component({
  selector: 'app-registrations',
  standalone: false,
  templateUrl: './registrations.html'
})
export class RegistrationsAdmin implements OnInit {
  succes = '';
  erreur = '';

  registrations: Registration[] = [];
  filteredRegistrations: Registration[] = [];

  searchTerm = '';
  statutFilter = '';
  attenteFilter = '';

  private adminId: number | null = null;

  constructor(private registrationService: RegistrationService) {}

  ngOnInit(): void {
    const storedAdminId = localStorage.getItem('userId');
    this.adminId = storedAdminId ? Number(storedAdminId) : null;
    this.loadRegistrations();
  }

  loadRegistrations(): void {
    this.registrationService.getAllRegistrations().subscribe({
      next: (data) => {
        this.registrations = data || [];
        this.filteredRegistrations = [...this.registrations];
        this.filtrer();
      },
      error: (err) => {
        console.error('Erreur chargement inscriptions', err);
        this.erreur = 'Impossible de charger les inscriptions.';
      }
    });
  }

  filtrer(): void {
    const search = this.searchTerm.toLowerCase();

    this.filteredRegistrations = this.registrations.filter(reg => {
      const statutText = this.getStatusLabel(reg.status).toLowerCase();

      const attenteText = reg.waitlist ? 'oui' : 'non';

      const contenu = `
        ${this.getParentName(reg)}
        ${this.getChildName(reg)}
        ${this.getActivityName(reg)}
        ${statutText}
        ${attenteText}
      `.toLowerCase();

      return (
        contenu.includes(search) &&
        (this.statutFilter === '' || statutText === this.statutFilter.toLowerCase()) &&
        (this.attenteFilter === '' || attenteText === this.attenteFilter.toLowerCase())
      );
    });
  }

  confirmer(id: number): void {
    if (!this.adminId) {
      this.erreur = "Admin introuvable. Reconnectez-vous.";
      return;
    }
    this.registrationService.confirmer(id, this.adminId).subscribe({
      next: () => {
        this.succes = 'Inscription confirmée.';
        this.erreur = '';
        this.loadRegistrations();
      },
      error: (err) => {
        console.error('Erreur confirmation inscription', err);
        this.erreur = 'Erreur lors de la confirmation.';
      }
    });
  }

  promouvoir(id: number): void {
    if (!this.adminId) {
      this.erreur = "Admin introuvable. Reconnectez-vous.";
      return;
    }
    this.registrationService.promouvoir(id, this.adminId).subscribe({
      next: () => {
        this.succes = "Inscription promue (sortie de liste d'attente).";
        this.erreur = '';
        this.loadRegistrations();
      },
      error: (err) => {
        console.error('Erreur promotion inscription', err);
        this.erreur = 'Erreur lors de la promotion.';
      }
    });
  }

  annuler(id: number): void {
    const confirmation = confirm('Annuler cette inscription ?');
    if (confirmation) {
      this.registrationService.annuler(id).subscribe({
        next: () => {
          this.succes = 'Inscription annulée.';
          this.erreur = '';
          this.loadRegistrations();
        },
        error: (err) => {
          console.error('Erreur annulation inscription', err);
          this.erreur = "Erreur lors de l'annulation.";
        }
      });
    }
  }

  getParentName(reg: any): string {
    return (
      reg.parentFullName ||
      `${reg.child?.parent?.firstName || ''} ${reg.child?.parent?.lastName || ''}`.trim() ||
      '—'
    );
  }

  getChildName(reg: any): string {
    return (
      reg.childFullName ||
      `${reg.childFirstName || ''} ${reg.childLastName || ''}`.trim() ||
      `${reg.child?.firstName || ''} ${reg.child?.lastName || ''}`.trim() ||
      '—'
    );
  }

  getActivityName(reg: any): string {
    return reg.activityName || reg.session?.activity?.name || '—';
  }

  getSessionStartDate(reg: any): string {
    return reg.sessionStartDate || reg.session?.startDate || '';
  }

  getConfirmedByName(reg: any): string {
    return (
      reg.confirmedByFullName ||
      `${reg.confirmedBy?.firstName || ''} ${reg.confirmedBy?.lastName || ''}`.trim() ||
      '—'
    );
  }

  getStatusLabel(status?: string): string {
    switch (status) {
      case 'CONFIRMED':
        return 'Confirmée';
      case 'PENDING':
      case 'PENDING_PAYMENT':
        return 'En attente';
      case 'CANCELLED':
        return 'Annulée';
      default:
        return status || 'En attente';
    }
  }

  canConfirm(reg: Registration): boolean {
    return reg.status === 'PENDING' || reg.status === 'PENDING_PAYMENT';
  }

  canCancel(reg: Registration): boolean {
    return reg.status !== 'CANCELLED';
  }
}