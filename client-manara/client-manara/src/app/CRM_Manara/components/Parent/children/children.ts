import { Component, OnInit } from '@angular/core';
import { ChildService } from '../../../services/child.service';
import { UserService } from '../../../services/user.service';
import { NotificationService } from '../../../services/notification.service';

@Component({
  selector: 'app-children',
  standalone: false,
  templateUrl: './children.html'
})
export class Children implements OnInit {

  connectedUser: any = null;
  children: any[] = [];

  unreadNotifications = 0;

  succes = '';
  erreur = '';

  showAddModal = false;
  isEditMode = false;
  editingChildId: number | null = null;

  newChild: any = {
    firstName: '',
    lastName: '',
    birthDate: '',
    allergies: '',
    medicalNotes: ''
  };

  constructor(
    private childService: ChildService,
    private userService: UserService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadConnectedParent();
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

  loadConnectedParent(): void {
    const email = localStorage.getItem('email');

    if (!email) {
      this.erreur = 'Parent non connecté.';
      return;
    }

    this.userService.getUserByEmail(email).subscribe({
      next: (user) => {
        this.connectedUser = user;
        this.loadChildren();
      },
      error: (err) => {
        console.log('ERREUR USER EMAIL =', err);
        this.erreur = 'Impossible de récupérer le parent connecté.';
      }
    });
  }

  loadChildren(): void {
    const parentId = this.connectedUser?.id;

    if (!parentId) {
      this.erreur = 'Parent introuvable.';
      return;
    }

    this.childService.getChildrenByParentId(parentId).subscribe({
      next: (data) => {
        this.children = data;
      },
      error: (err) => {
        console.log('ERREUR API ENFANTS =', err);
        this.erreur = 'Impossible de charger les enfants.';
      }
    });
  }

  openAddModal(): void {
    this.succes = '';
    this.erreur = '';
    this.isEditMode = false;
    this.editingChildId = null;
    this.resetNewChild();
    this.showAddModal = true;
  }

  openEditModal(child: any): void {
    this.succes = '';
    this.erreur = '';
    this.isEditMode = true;
    this.editingChildId = child.id;
    this.newChild = {
      firstName: child.firstName || '',
      lastName: child.lastName || '',
      birthDate: child.birthDate ? String(child.birthDate).split('T')[0] : '',
      allergies: child.allergies || '',
      medicalNotes: child.medicalNotes || ''
    };
    this.showAddModal = true;
  }

  closeAddModal(): void {
    this.showAddModal = false;
    this.isEditMode = false;
    this.editingChildId = null;
    this.resetNewChild();
  }

  saveChild(): void {
    if (this.isEditMode) {
      this.updateChild();
      return;
    }

    this.addChild();
  }

  addChild(): void {
    if (!this.connectedUser?.id) {
      this.erreur = 'Parent introuvable.';
      return;
    }

    const childToCreate: any = {
      firstName: this.newChild.firstName,
      lastName: this.newChild.lastName,
      birthDate: this.newChild.birthDate,
      allergies: this.newChild.allergies,
      medicalNotes: this.newChild.medicalNotes,
      parent: {
        id: this.connectedUser.id
      },
      registrations: []
    };

    this.childService.createChild(childToCreate).subscribe({
      next: (createdChild) => {
        this.children.push(createdChild);
        this.succes = 'Enfant ajouté avec succès.';
        this.erreur = '';
        this.closeAddModal();
      },
      error: (err) => {
        console.log('ERREUR AJOUT ENFANT =', err);
        this.erreur = 'Impossible d’ajouter l’enfant.';
      }
    });
  }

  updateChild(): void {
    if (!this.connectedUser?.id || !this.editingChildId) {
      this.erreur = 'Enfant introuvable.';
      return;
    }

    const childToUpdate: any = {
      firstName: this.newChild.firstName,
      lastName: this.newChild.lastName,
      birthDate: this.newChild.birthDate,
      allergies: this.newChild.allergies,
      medicalNotes: this.newChild.medicalNotes,
      parent: { id: this.connectedUser.id },
      registrations: []
    };

    this.childService.updateChild(this.editingChildId, childToUpdate).subscribe({
      next: () => {
        this.succes = 'Enfant modifié avec succès.';
        this.erreur = '';
        this.closeAddModal();
        this.loadChildren();
      },
      error: (err) => {
        console.log('ERREUR MODIFICATION ENFANT =', err);
        this.erreur = 'Impossible de modifier l’enfant.';
      }
    });
  }

  deleteChild(child: any): void {
    if (!child?.id) {
      this.erreur = 'Enfant introuvable.';
      return;
    }

    const confirmed = window.confirm(
      `Voulez-vous vraiment supprimer ${child.firstName || 'cet enfant'} ?`
    );
    if (!confirmed) return;

    this.childService.deleteChild(child.id).subscribe({
      next: () => {
        this.succes = 'Enfant supprimé avec succès.';
        this.erreur = '';
        this.children = this.children.filter((c) => c.id !== child.id);
      },
      error: (err) => {
        console.log('ERREUR SUPPRESSION ENFANT =', err);
        this.erreur = 'Impossible de supprimer l’enfant.';
      }
    });
  }

  resetNewChild(): void {
    this.newChild = {
      firstName: '',
      lastName: '',
      birthDate: '',
      allergies: '',
      medicalNotes: ''
    };
  }

  getInitiales(child: any): string {
    const first = child.firstName?.charAt(0) || '';
    const last = child.lastName?.charAt(0) || '';
    return (first + last).toUpperCase();
  }
}