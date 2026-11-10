import { Component, OnInit } from '@angular/core';
import { ActivityService } from '../../../services/activity.service';
import { Activity } from '../../../models/activity.model';
import { NotificationService } from '../../../services/notification.service';
import { AuthService } from '../../../services/auth.service';

interface SessionParent {
  activity: Activity;
  capacity: number;
}

@Component({
  selector: 'app-activities-parent',
  standalone: false,
  templateUrl: './activities.html'
})
export class Activities implements OnInit {

  connectedUser = {
    firstName: '',
    lastName: ''
  };

  activityList: Activity[] = [];
  filteredActivityList: Activity[] = [];

  unreadNotifications = 0;
  searchTerm = '';
  selectedCategorie = '';

  erreur = '';

  constructor(
    private activityService: ActivityService,
    private notificationService: NotificationService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadConnectedUser();
    this.loadActivities();
    this.loadUnreadNotifications();
  }

  loadConnectedUser(): void {
    this.authService.me().subscribe({
      next: (response) => {
        if (!response?.success || !response.user) return;
        this.connectedUser = {
          firstName: response.user.firstName || '',
          lastName: response.user.lastName || ''
        };
      },
      error: () => {
        // Keep empty fallback if session lookup fails.
      }
    });
  }

  loadUnreadNotifications(): void {
    const userId = Number(localStorage.getItem('userId'));
    if (!userId) return;

    this.notificationService.countUnread(userId).subscribe({
      next: (count) => this.unreadNotifications = count,
      error: () => this.unreadNotifications = 0
    });
  }

  loadActivities(): void {
    this.activityService.getActiveActivities().subscribe({
      next: (activities) => {
        this.activityList = activities;
        this.filtrer();
      },
      error: () => {
        this.erreur = 'Impossible de charger les activités.';
      }
    });
  }

  filtrer(): void {
    const val = this.searchTerm.toLowerCase().trim();
    const categorie = this.selectedCategorie.toLowerCase().trim();

    this.filteredActivityList = this.activityList.filter(activity => {
      const name = activity.name?.toLowerCase() || '';
      const cat = activity.categorie?.toLowerCase() || '';

      return (
        (!val || name.includes(val) || cat.includes(val)) &&
        (!categorie || cat === categorie)
      );
    });
  }

  filtrerCategorie(categorie: string): void {
    this.selectedCategorie = categorie;
    this.filtrer();
  }
}