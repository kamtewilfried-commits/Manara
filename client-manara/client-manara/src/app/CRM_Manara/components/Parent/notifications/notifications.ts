import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../../../services/notification.service';
import { Notification } from '../../../models/notification.model';

type NotificationType = 'CONFIRMATION' | 'REMINDER' | 'CANCELLATION' | 'RECEIPT' | string;

@Component({
  selector: 'app-notifications-parent',
  standalone: false,
  templateUrl: './notifications.html'
})
export class Notifications implements OnInit {

  connectedUser = { firstName: '', lastName: '' };
  userId: number | null = null;

  notifications: Notification[] = [];
  filteredNotifications: Notification[] = [];

  unreadNotifications = 0;
  currentTypeFilter = '';
  isLoading = true;
  errorMessage = '';

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    const storedUserId = localStorage.getItem('userId');
    const firstName = localStorage.getItem('firstName') || '';
    const lastName  = localStorage.getItem('lastName')  || '';
    this.connectedUser = { firstName, lastName };

    if (!storedUserId) {
      this.isLoading = false;
      this.errorMessage = 'Utilisateur non connecté.';
      return;
    }

    this.userId = Number(storedUserId);
    this.loadNotifications();
  }

  loadNotifications(): void {
    if (!this.userId) return;
    this.isLoading = true;

    this.notificationService.getNotificationsByUser(this.userId).subscribe({
      next: (data) => {
        this.notifications = data;
        this.updateUnreadCount();
        this.applyFilter();
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Impossible de charger les notifications.';
        this.isLoading = false;
      }
    });
  }

  filtrerType(type: string): void {
    this.currentTypeFilter = type;
    this.applyFilter();
  }

  marquerLu(notif: Notification): void {
    this.notificationService.markRead(notif.id).subscribe({
      next: () => {
        notif.isRead = true;
        this.updateUnreadCount();
        this.applyFilter();
      }
    });
  }

  marquerToutLu(): void {
    if (!this.userId) return;

    this.notificationService.markAllRead(this.userId).subscribe({
      next: () => {
        this.notifications = this.notifications.map(n => ({ ...n, isRead: true }));
        this.updateUnreadCount();
        this.applyFilter();
      }
    });
  }

  imprimerRecu(notif: Notification): void {
    const contenu = `
      <!DOCTYPE html>
      <html lang="fr">
      <head>
        <meta charset="UTF-8" />
        <title>Reçu – CRM Manara</title>
        <style>
          body { font-family: Arial, sans-serif; padding: 40px; color: #0f172a; }
          h1 { font-size: 22px; margin-bottom: 4px; }
          .sub { color: #64748b; font-size: 13px; margin-bottom: 24px; }
          .row { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #e2e8f0; }
          .label { color: #64748b; }
          .total { font-size: 18px; font-weight: bold; margin-top: 20px; text-align: right; }
          .footer { margin-top: 32px; font-size: 12px; color: #94a3b8; text-align: center; }
        </style>
      </head>
      <body>
        <h1>CRM Manara — Reçu de paiement</h1>
        <p class="sub">Émis le ${new Date(notif.sentAt).toLocaleDateString('fr-CA', { year: 'numeric', month: 'long', day: 'numeric' })}</p>
        <div class="row"><span class="label">Objet</span><span>${notif.subject}</span></div>
        <div class="row"><span class="label">Message</span><span>${notif.message}</span></div>
        <div class="row"><span class="label">Statut</span><span>Payé ✓</span></div>
        <p class="footer">Centre communautaire Manara · Merci pour votre confiance.</p>
      </body>
      </html>`;

    const fenetre = window.open('', '_blank');
    if (fenetre) {
      fenetre.document.write(contenu);
      fenetre.document.close();
      fenetre.focus();
      fenetre.print();
    }
  }

  updateUnreadCount(): void {
    this.unreadNotifications = this.notifications.filter(n => !n.isRead).length;
  }

  applyFilter(): void {
    this.filteredNotifications = this.currentTypeFilter
      ? this.notifications.filter(n => n.type === this.currentTypeFilter)
      : [...this.notifications];
  }

  getTypeLabel(type: NotificationType): string {
    const labels: Record<string, string> = {
      CONFIRMATION: 'Confirmation',
      REMINDER: 'Rappel',
      CANCELLATION: 'Annulation',
      RECEIPT: 'Reçu'
    };
    return labels[type] ?? 'Notification';
  }

  getTypeClass(type: NotificationType): string {
    const classes: Record<string, string> = {
      CONFIRMATION: 'bg-emerald-50 text-emerald-700',
      REMINDER:     'bg-blue-50 text-blue-700',
      CANCELLATION: 'bg-red-50 text-red-700',
      RECEIPT:      'bg-purple-50 text-purple-700'
    };
    return classes[type] ?? 'bg-slate-50 text-slate-700';
  }
}
