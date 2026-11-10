import { Component, OnInit } from '@angular/core';
import { User } from '../../../models/user.model';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-users',
  standalone: false,
  templateUrl: './users.html',
  styleUrls: ['./users.css']
})
export class Users implements OnInit {

  users: User[] = [];
  filteredUsers: User[] = [];

  searchTerm: string = '';
  roleFilter: string = '';
  statutFilter: string = '';

  succes: string = '';
  erreur: string = '';

  nbParents: number = 0;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUsers();
    this.loadParentCount();
  }

  loadUsers(): void {
  this.userService.afficherUtilisateurs().subscribe({
    next: (data) => {
       console.log('Utilisateurs reçus :', data);
      console.log('Nombre :', data.length);
      console.log('IDs :', data.map(u => u.id));
      this.users = data.map(user => ({
        ...user,
        createdAt: Array.isArray(user.createdAt)
          ? new Date(
              user.createdAt[0],
              user.createdAt[1] - 1,
              user.createdAt[2],
              user.createdAt[3] || 0,
              user.createdAt[4] || 0,
              user.createdAt[5] || 0
            )
          : user.createdAt
      }));
      this.filteredUsers = this.users;
      this.filtrer();
    },
    error: (err) => {
      console.error('Erreur lors du chargement des utilisateurs', err);
      this.erreur = 'Erreur lors du chargement des utilisateurs.';
    }
  });
}

  loadParentCount(): void {
    this.userService.countByRole('PARENT').subscribe({
      next: (count) => {
        this.nbParents = count;
      },
      error: (err) => {
        console.error('Erreur lors du comptage', err);
      }
    });
  }

  filtrer(): void {
    this.filteredUsers = this.users.filter((user) => {
      const fullName = `${user.firstName ?? ''} ${user.lastName ?? ''}`.toLowerCase();
      const email = (user.email ?? '').toLowerCase();
      const term = this.searchTerm.toLowerCase();

      const matchSearch =
        !this.searchTerm ||
        fullName.includes(term) ||
        email.includes(term);

      const matchRole =
        !this.roleFilter || user.role === this.roleFilter;

      const matchStatut =
        !this.statutFilter ||
        (this.statutFilter === 'Actif' && user.active === true) ||
        (this.statutFilter === 'Inactif' && user.active === false);

      return matchSearch && matchRole && matchStatut;
    });
  }

  toggleUser(user: User): void {
    if (!user.id) return;

    this.succes = '';
    this.erreur = '';

    this.userService.toggleActive(user.id).subscribe({
      next: () => {
        this.succes = user.active
          ? 'Utilisateur désactivé avec succès.'
          : 'Utilisateur réactivé avec succès.';
        this.loadUsers();
      },
      error: (err) => {
        console.error("Erreur lors de l'activation/désactivation", err);
        this.erreur = "Erreur lors de l'activation/désactivation de l'utilisateur.";
      }
    });
  }
  modalOpen = false;
isEditMode = false;
selectedUserId?: number;

formUser: User = {
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  password:'',
  role: 'ANIMATEUR',
  active: true
} as User;

openAddModal(): void {
  this.isEditMode = false;
  this.selectedUserId = undefined;

  this.formUser = {
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    password: '',
    role: 'ANIMATEUR',
    active: true
  } as User;

  this.modalOpen = true;
}

openEditModal(user: User): void {
  this.isEditMode = true;
  this.selectedUserId = user.id;

  this.formUser = {
    ...user
  };

  this.modalOpen = true;
}

closeModal(): void {
  this.modalOpen = false;
}

saveUser(): void {
  this.succes = '';
  this.erreur = '';

  if (this.isEditMode && this.selectedUserId) {
    this.userService.updateProfile(this.selectedUserId, this.formUser).subscribe({
      next: () => {
        this.succes = 'Utilisateur modifié avec succès.';
        this.closeModal();
        this.loadUsers();
      },
      error: (err) => {
        console.error(err);
        this.erreur = 'Erreur lors de la modification.';
      }
    });
  } else {
    this.userService.createSimpleUser(this.formUser).subscribe({
      next: () => {
        this.succes = 'Utilisateur ajouté avec succès.';
        this.closeModal();
        this.loadUsers();
      },
      error: (err) => {
        console.error(err);
        this.erreur = "Erreur lors de l'ajout.";
      }
    });
  }
}
deleteModalOpen = false;
userToDelete?: User;

openDeleteModal(user: User) {
  this.userToDelete = user;
  this.deleteModalOpen = true;
}

confirmDelete() {
  if (!this.userToDelete?.id) return;

  this.userService.deleteUser(this.userToDelete.id).subscribe({
    next: () => {
      this.succes = 'Utilisateur supprimé.';
      this.deleteModalOpen = false;
      this.loadUsers();
    },
    error: () => {
      this.erreur = 'Erreur suppression.';
    }
  });
}
}