import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { UserService } from '../../../services/user.service';
import { ActivityService } from '../../../services/activity.service';

@Component({
  selector: 'app-profile-animateur',
  standalone: false,
  templateUrl: './profil.html'
})
export class ProfileAnimateur implements OnInit {
  succes = '';
  erreur = '';
  connectedUserId: number | null = null;

  connectedUser: any = {
    firstName: '',
    lastName: '',
    email: ''
  };

  userForm = {
    firstName: '',
    lastName: '',
    email: ''
  };

  passwordForm = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  };

  activitesAssignees: any[] = [];

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private activityService: ActivityService
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.authService.me().subscribe({
      next: (response) => {
        const user = response.user;
        this.connectedUserId = user?.id ?? response.userId ?? null;
        this.connectedUser = user ?? { firstName: '', lastName: '', email: '' };

        this.userForm = {
          firstName: this.connectedUser.firstName || '',
          lastName: this.connectedUser.lastName || '',
          email: this.connectedUser.email || ''
        };

        if (this.connectedUserId) {
          this.activityService.getActivitiesByAnimateur(this.connectedUserId).subscribe({
            next: (activities) => this.activitesAssignees = activities ?? [],
            error: () => this.activitesAssignees = []
          });
        }
      },
      error: () => {
        this.erreur = 'Impossible de charger le profil.';
      }
    });
  }

  getInitiales(): string {
    const first = this.connectedUser.firstName?.charAt(0) || '';
    const last = this.connectedUser.lastName?.charAt(0) || '';
    return (first + last).toUpperCase();
  }

  enregistrerProfil(): void {
    this.succes = '';
    this.erreur = '';

    if (!this.userForm.firstName || !this.userForm.lastName || !this.userForm.email) {
      this.erreur = 'Veuillez remplir tous les champs du profil.';
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
      email: this.userForm.email
    }).subscribe({
      next: (updated) => {
        this.connectedUser = updated;
        this.userForm = {
          firstName: updated.firstName || '',
          lastName: updated.lastName || '',
          email: updated.email || ''
        };
        this.succes = 'Profil mis à jour avec succès.';
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
      this.erreur = 'La confirmation du mot de passe ne correspond pas.';
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
        this.passwordForm = {
          currentPassword: '',
          newPassword: '',
          confirmPassword: ''
        };
        this.succes = 'Mot de passe modifié avec succès.';
      },
      error: () => {
        this.erreur = 'Erreur lors du changement de mot de passe.';
      }
    });
  }
}