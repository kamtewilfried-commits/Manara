import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';
import { UserService } from '../../../services/user.service';
import { User } from '../../../models/user.model';

@Component({
  selector: 'app-profile-parent',
  standalone: false,
  templateUrl: './profile.html'
})
export class Profile implements OnInit {
  succes = '';
  erreur = '';
  connectedUserId: number | null = null;

  connectedUser: User = {
    id: 0,
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    role: 'PARENT',
    password: '',
    active: true,
    createdAt: new Date()
  };

  unreadNotifications = 0;

  userForm = {
    firstName: '',
    lastName: '',
    email: '',
    phone: ''
  };

  passwordForm = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  };

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    const localUserId = Number(localStorage.getItem('userId'));
    if (localUserId) {
      this.connectedUserId = localUserId;
      this.userService.getUserById(localUserId).subscribe({
        next: (user) => {
          this.connectedUser = { ...this.connectedUser, ...user };
          this.userForm = {
            firstName: this.connectedUser.firstName || '',
            lastName: this.connectedUser.lastName || '',
            email: this.connectedUser.email || '',
            phone: this.connectedUser.phone || ''
          };
          localStorage.setItem('email', this.connectedUser.email || '');
          localStorage.setItem('firstName', this.connectedUser.firstName || '');
          localStorage.setItem('lastName', this.connectedUser.lastName || '');
          this.loadUnreadNotifications();
        },
        error: () => this.loadProfileFromSession()
      });
      return;
    }

    this.loadProfileFromSession();
  }

  private loadProfileFromSession(): void {
    this.authService.me().subscribe({
      next: (response) => {
        if (!response?.success || !response.user) {
          this.erreur = 'Impossible de charger le profil.';
          return;
        }

        this.connectedUserId = response.user.id ?? null;
        this.connectedUser = {
          ...this.connectedUser,
          ...response.user
        };

        this.userForm = {
          firstName: this.connectedUser.firstName || '',
          lastName: this.connectedUser.lastName || '',
          email: this.connectedUser.email || '',
          phone: this.connectedUser.phone || ''
        };

        localStorage.setItem('userId', String(this.connectedUserId || ''));
        localStorage.setItem('email', this.connectedUser.email || '');
        localStorage.setItem('firstName', this.connectedUser.firstName || '');
        localStorage.setItem('lastName', this.connectedUser.lastName || '');

        this.loadUnreadNotifications();
      },
      error: () => {
        this.erreur = 'Impossible de charger le profil.';
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

  getInitiales(): string {
    return (
      (this.connectedUser.firstName?.charAt(0) || '') +
      (this.connectedUser.lastName?.charAt(0) || '')
    ).toUpperCase();
  }

  enregistrerProfil(): void {
    this.succes = '';
    this.erreur = '';

    if (!this.userForm.firstName || !this.userForm.lastName || !this.userForm.email) {
      this.erreur = 'Veuillez remplir tous les champs.';
      return;
    }

    if (!this.connectedUserId) {
      this.erreur = 'Utilisateur introuvable.';
      return;
    }

    this.userService.updateProfile(this.connectedUserId, {
      ...this.connectedUser,
      firstName: this.userForm.firstName,
      lastName: this.userForm.lastName,
      email: this.userForm.email,
      phone: this.userForm.phone
    }).subscribe({
      next: (updated) => {
        this.connectedUser = { ...this.connectedUser, ...updated };
        this.userForm = {
          firstName: this.connectedUser.firstName || '',
          lastName: this.connectedUser.lastName || '',
          email: this.connectedUser.email || '',
          phone: this.connectedUser.phone || ''
        };
        localStorage.setItem('email', this.connectedUser.email || '');
        localStorage.setItem('firstName', this.connectedUser.firstName || '');
        localStorage.setItem('lastName', this.connectedUser.lastName || '');
        this.succes = 'Profil mis à jour.';
      },
      error: () => {
        this.erreur = 'Erreur lors de la mise à jour du profil.';
      }
    });
  }

  changerMotDePasse(): void {
    this.succes = '';
    this.erreur = '';

    if (
      !this.passwordForm.currentPassword ||
      !this.passwordForm.newPassword ||
      !this.passwordForm.confirmPassword
    ) {
      this.erreur = 'Veuillez remplir tous les champs du mot de passe.';
      return;
    }

    if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
      this.erreur = 'Les mots de passe ne correspondent pas.';
      return;
    }

    if (!this.connectedUserId) {
      this.erreur = 'Utilisateur introuvable.';
      return;
    }

    this.userService.changePassword(this.connectedUserId, {
      currentPassword: this.passwordForm.currentPassword,
      newPassword: this.passwordForm.newPassword,
      confirmPassword: this.passwordForm.confirmPassword
    }).subscribe({
      next: () => {
        this.succes = 'Mot de passe modifié.';
        this.passwordForm = {
          currentPassword: '',
          newPassword: '',
          confirmPassword: ''
        };
      },
      error: () => {
        this.erreur = 'Erreur lors du changement de mot de passe.';
      }
    });
  }
}