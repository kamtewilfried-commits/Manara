import { Component, OnInit } from '@angular/core';
import { Registration } from '../../../models/registration.model';
import { RegistrationService } from '../../../services/registration.service';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';

@Component({
  selector: 'app-registrations',
  standalone: false,
  templateUrl: './registrations.html',
  styleUrls: ['./registrations.css']
})
export class Registrations implements OnInit {

  connectedUser: {
    firstName?: string;
    lastName?: string;
    email?: string;
    role?: string;
  } | null = null;

  registrations: Registration[] = [];

  parentId!: number;
  activeCount = 0;
  pendingCount = 0;
  loading = false;
  succes = '';
  erreur = '';
  unreadNotifications = 0;

  constructor(
    private registrationService: RegistrationService,
    private authService: AuthService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadConnectedUserAndRegistrations();
    this.loadUnreadNotifications();
  }

  loadUnreadNotifications(): void {
    const userId = Number(localStorage.getItem('userId'));
    if (!userId) return;

    this.notificationService.countUnread(userId).subscribe({
      next: (count) => this.unreadNotifications = count,
      error: () => this.unreadNotifications = 0
    });
  }

  loadConnectedUserAndRegistrations(): void {
    this.authService.me().subscribe({
      next: (response) => {
        this.connectedUser = response.user ?? null;

        const parentId = response.user?.id;

        if (!parentId) {
          this.erreur = 'Parent introuvable.';
          return;
        }

        this.parentId = parentId;
        this.loadRegistrations();
        this.loadCounts();
      },
      error: (err) => {
        console.error('Erreur me():', err);
        this.erreur = 'Utilisateur non connecté.';
      }
    });
  }

  loadRegistrations(): void {
    this.registrationService.getRegistrationsByParent(this.parentId).subscribe({
      next: (data) => {
        this.registrations = data.filter(reg => reg.status === 'CONFIRMED' || reg.status === 'PENDING_PAYMENT');
      },
      error: (err) => {
        console.error('Erreur lors du chargement des inscriptions', err);
        this.erreur = 'Impossible de charger les inscriptions.';
      }
    });
  }

  loadCounts(): void {
    this.registrationService.countActives(this.parentId).subscribe({
      next: (count) => this.activeCount = count,
      error: (err) => console.error('Erreur countActives:', err)
    });

    this.registrationService.countPending(this.parentId).subscribe({
      next: (count) => this.pendingCount = count,
      error: (err) => console.error('Erreur countPending:', err)
    });
  }

  cancelRegistration(id: number | undefined): void {
    if (!id) return;

    if (!confirm('Confirmer l\'annulation de cette inscription ?')) return;

    this.loading = true;
    this.succes = '';
    this.erreur = '';

    this.registrationService.annuler(id).subscribe({
      next: () => {
        this.succes = 'Inscription annulée avec succès.';
        this.loading = false;
        this.loadRegistrations();
        this.loadCounts();

        setTimeout(() => this.succes = '', 4000);
      },
      error: (err) => {
        console.error("Erreur lors de l'annulation", err);
        this.erreur = "Impossible d'annuler cette inscription.";
        this.loading = false;
      }
    });
  }
}