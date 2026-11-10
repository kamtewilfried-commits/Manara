import { Component, OnInit } from '@angular/core';
import { Notification } from '../../../models/notification.model';
import { Activity } from '../../../models/activity.model';
import { User } from '../../../models/user.model';
import { NotificationService } from '../../../services/notification.service';
import { ActivityService } from '../../../services/activity.service';
import { UserService } from '../../../services/user.service';

interface NotificationHistoryRow {
  recipientLabel: string;
  subject: string;
  type: string;
  sentAt: string;
  emailSent: boolean;
}

@Component({
  selector: 'app-notifications',
   standalone: false,
  templateUrl: './notifications.html',
  styleUrls: ['./notifications.css']
})
export class NotificationsAdmin implements OnInit {

  notifications: Notification[] = [];
  historyRows: NotificationHistoryRow[] = [];
  activities: Activity[] = [];
  parents: User[] = [];

  succes: string = '';
  erreur: string = '';

  notifForm = {
    recipientType: 'ALL',
    type: 'REMINDER',
    activityId: null as number | null,
    parentId: null as number | null,
    subject: '',
    message: ''
  };

  constructor(
    private notificationService: NotificationService,
    private activityService: ActivityService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
    this.loadActivities();
    this.loadParents();
  }

  loadNotifications(): void {
    this.notificationService.getAllNotifications().subscribe({
      next: (data: Notification[]) => {
        this.notifications = data;
        this.rebuildHistoryRows();
      },
      error: (err) => {
        console.error('Erreur lors du chargement des notifications', err);
        this.erreur = 'Impossible de charger l’historique des notifications.';
      }
    });
  }

  loadActivities(): void {
    this.activityService.getAllActivities().subscribe({
      next: (data: Activity[]) => {
        this.activities = data;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des activités', err);
      }
    });
  }

  loadParents(): void {
    this.userService.getAllParents().subscribe({
      next: (data: User[]) => {
        this.parents = data;
        this.rebuildHistoryRows();
      },
      error: (err) => {
        console.error('Erreur lors du chargement des parents', err);
      }
    });
  }

  envoyerNotification(): void {
    this.succes = '';
    this.erreur = '';

    if (!this.notifForm.subject.trim() || !this.notifForm.message.trim()) {
      this.erreur = 'Le sujet et le message sont obligatoires.';
      return;
    }

    if (this.notifForm.recipientType === 'ACTIVITY' && !this.notifForm.activityId) {
      this.erreur = 'Veuillez choisir une activité.';
      return;
    }

    if (this.notifForm.recipientType === 'ONE' && !this.notifForm.parentId) {
      this.erreur = 'Veuillez choisir un parent.';
      return;
    }

    this.notificationService.sendNotification(this.notifForm).subscribe({
      next: () => {
        this.succes = 'Notification envoyée avec succès.';
        this.resetForm();
        this.loadNotifications();
      },
      error: (err) => {
        console.error('Erreur lors de l’envoi de la notification', err);
        this.erreur = 'Erreur lors de l’envoi de la notification.';
      }
    });
  }

  resetForm(): void {
    this.notifForm = {
      recipientType: 'ALL',
      type: 'REMINDER',
      activityId: null,
      parentId: null,
      subject: '',
      message: ''
    };
  }

  private rebuildHistoryRows(): void {
    const rows: NotificationHistoryRow[] = [];
    const allParentsCount = this.parents.length;
    const grouped = new Map<string, Notification[]>();

    for (const notif of this.notifications) {
      const sentAt = notif.sentAt ? new Date(notif.sentAt) : new Date();
      const secondBucket = Math.floor(sentAt.getTime() / 1000);
      const key = `${notif.type}|${notif.subject}|${notif.message}|${secondBucket}`;
      const current = grouped.get(key) || [];
      current.push(notif);
      grouped.set(key, current);
    }

    for (const group of grouped.values()) {
      if (allParentsCount > 1 && group.length === allParentsCount) {
        const first = group[0];
        rows.push({
          recipientLabel: 'Tous les parents',
          subject: first.subject,
          type: first.type,
          sentAt: first.sentAt,
          emailSent: group.every((n) => !!n.emailSent)
        });
      } else {
        for (const notif of group) {
          rows.push({
            recipientLabel:
              notif.userFullName ||
              `${notif.user?.firstName || ''} ${notif.user?.lastName || ''}`.trim() ||
              'Parent',
            subject: notif.subject,
            type: notif.type,
            sentAt: notif.sentAt,
            emailSent: !!notif.emailSent
          });
        }
      }
    }

    rows.sort((a, b) => new Date(b.sentAt).getTime() - new Date(a.sentAt).getTime());
    this.historyRows = rows;
  }
}