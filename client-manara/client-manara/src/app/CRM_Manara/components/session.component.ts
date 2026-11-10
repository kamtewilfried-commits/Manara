import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CartService } from '../services/cart.service';
import { SessionService } from '../services/session.service';
import { ChildService } from '../services/child.service';
import { UserService } from '../services/user.service';
import { ActivityService } from '../services/activity.service';
import { NotificationService } from '../services/notification.service';

@Component({
  selector: 'app-sessions',
  standalone: false,
  templateUrl: './session.component.html'
})
export class SessionComponent implements OnInit {

  connectedUser: any = null;
  unreadNotifications = 0;
  sessions: any[] = [];
  children: any[] = [];
  activity: any = null;

  activityId!: number;
  erreur = '';

  constructor(
    private route: ActivatedRoute,
    private cartService: CartService,
    private sessionService: SessionService,
    private childService: ChildService,
    private userService: UserService,
    private activityService: ActivityService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    const parentId = Number(localStorage.getItem('userId'));
    const email = localStorage.getItem('email');

    this.activityId = Number(this.route.snapshot.paramMap.get('activityId'));

    if (!parentId) {
      this.erreur = 'Parent non connecté.';
      return;
    }

    if (!this.activityId) {
      this.erreur = 'Activité introuvable.';
      return;
    }

    if (email) {
      this.userService.getUserByEmail(email).subscribe({
        next: (user) => this.connectedUser = user
      });
    }

    this.loadActivity();
    this.loadSessionsByActivity();
    this.loadChildren(parentId);
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

  loadActivity(): void {
    this.activityService.getActivityById(this.activityId).subscribe({
      next: (activity) => {
        this.activity = activity;
      },
      error: () => {
        this.erreur = 'Impossible de charger l’activité.';
      }
    });
  }

  loadSessionsByActivity(): void {
    this.sessionService.getSessionsByActivity(this.activityId).subscribe({
      next: (data) => {
        this.sessions = data.map(session => ({
          ...session,
          selectedChildId: null
        }));
      },
      error: () => {
        this.erreur = 'Impossible de charger les sessions de cette activité.';
      }
    });
  }

  loadChildren(parentId: number): void {
    this.childService.getChildrenByParentId(parentId).subscribe({
      next: (data) => this.children = data,
      error: () => this.erreur = 'Impossible de charger les enfants.'
    });
  }

  getPrixSession(session: any): number {
    if (session.prixSession && Number(session.prixSession) > 0) {
      return Number(session.prixSession);
    }

    const start = new Date(session.startDate);
    const end = new Date(session.endDate);

    const heures = Math.ceil(
      (end.getTime() - start.getTime()) / (1000 * 60 * 60)
    );

    const prixHoraire = Number(
      session.activity?.prixHoraire ||
      this.activity?.prixHoraire ||
      0
    );

    return heures * prixHoraire;
  }

  addSessionToCart(session: any): void {
    const child = this.children.find(c => c.id === Number(session.selectedChildId));

    if (!child) {
      alert('Veuillez choisir un enfant.');
      return;
    }

    const prix = this.getPrixSession(session);

    if (prix <= 0) {
      alert('Prix invalide pour cette session.');
      return;
    }

    const added = this.cartService.addToCart({
      sessionId: session.id,
      childId: child.id,
      activityName: session.activity?.name || this.activity?.name || 'Activité',
      childName: `${child.firstName} ${child.lastName}`,
      startDate: session.startDate,
      endDate: session.endDate,
      location: session.location,
      prix
    });

    if (!added) {
      alert('Cette session est déjà dans le panier pour cet enfant.');
      return;
    }

    alert('Session ajoutée au panier.');
  }
}