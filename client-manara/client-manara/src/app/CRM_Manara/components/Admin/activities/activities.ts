import { Component, OnInit } from '@angular/core';
import { Activity } from '../../../models/activity.model';
import { ActivityService } from '../../../services/activity.service';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-activities',
  standalone: false,
  templateUrl: './activities.html',
  styleUrls: ['./activities.css']
})
export class ActivitiesAdmin implements OnInit {

  activities: Activity[] = [];
  succes: string = '';

  selectedPhoto: File | null = null;

  showModal: boolean = false;
  isEditMode: boolean = false;

   // succes = '';
  erreur = '';
  activityToDelete: any = null;
deleteModalOpen = false;
animateurs: any[] = [];
  formActivity: any = {
    id: null,
    name: '',
    description: '',
    categorie: '',
    prixHoraire: 0,
    animateurId: null,
    active: true
  };

  constructor(private activityService: ActivityService, private userService: UserService) {}

  ngOnInit(): void {
    this.loadActivities();
     this.loadAnimateurs();
  }
 loadAnimateurs(): void {
  this.userService.getAllAnimateurs().subscribe({
    next: (data) => {
      console.log('ANIMATEURS =', data);
      this.animateurs = data;
    },
    error: (err) => {
      console.error(err);
    }
  });
}
  loadActivities(): void {
    this.activityService.getAllActivities().subscribe({
      next: (data: Activity[]) => {
        this.activities = data;
      },
      error: (err) => {
        console.error('Erreur chargement activités', err);
      }
    });
  }

  openCreateModal(): void {
    this.isEditMode = false;
      this.selectedPhoto = null;
    this.formActivity = {
      id: null,
      name: '',
      description: '',
      categorie: '',
      prixHoraire: 0,
      animateurId: null,
      active: true
    };
    this.showModal = true;
  }

  openEditModal(activity: Activity): void {
    this.isEditMode = true;
      this.selectedPhoto = null;
    this.formActivity = {
      id: activity.id ?? null,
      name: activity.name ?? '',
      description: activity.description ?? '',
      categorie: activity.categorie ?? '',
      prixHoraire: activity.prixHoraire??0,
      animateurId: activity.animateurId ?? null,
      active: activity.active ?? false
    };
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

saveActivity(): void {
  if (!this.formActivity.name || this.formActivity.name.trim() === '') {
    alert('Veuillez entrer un nom.');
    return;
  }

  if (!this.formActivity.prixHoraire || Number(this.formActivity.prixHoraire) <= 0) {
    alert('Veuillez entrer un prix horaire supérieur à 0.');
    return;
  }

  const payload = {
    name: this.formActivity.name,
    description: this.formActivity.description,
    categorie: this.formActivity.categorie,
    prixHoraire: Number(this.formActivity.prixHoraire),
    active: this.formActivity.active,
    animateur: this.formActivity.animateurId
      ? { id: Number(this.formActivity.animateurId) }
      : null
  };

  const afterSave = (activity: Activity) => {
    if (this.selectedPhoto && activity.id) {
      this.activityService.uploadActivityPhoto(activity.id, this.selectedPhoto).subscribe({
        next: () => {
          this.succes = this.isEditMode
            ? 'Activité modifiée avec photo.'
            : 'Activité créée avec photo.';

          this.selectedPhoto = null;
          this.closeModal();
          this.loadActivities();
          this.clearSuccessLater();
        },
        error: (err) => {
          console.error('Erreur upload photo', err);
          this.erreur = 'Activité enregistrée, mais erreur lors de l’upload de la photo.';
          this.closeModal();
          this.loadActivities();
        }
      });
    } else {
      this.succes = this.isEditMode
        ? 'Activité modifiée avec succès.'
        : 'Activité créée avec succès.';

      this.closeModal();
      this.loadActivities();
      this.clearSuccessLater();
    }
  };

  if (this.isEditMode && this.formActivity.id) {
    this.activityService.updateActivity(this.formActivity.id, payload).subscribe({
      next: (activity: Activity) => afterSave(activity),
      error: (err) => {
        console.error('Erreur modification activité', err);
        this.erreur = 'Erreur lors de la modification.';
      }
    });
  } else {
    this.activityService.createActivity(payload).subscribe({
      next: (activity: Activity) => afterSave(activity),
      error: (err) => {
        console.error('Erreur création activité', err);
        this.erreur = 'Erreur lors de la création.';
      }
    });
  }
}
openDeleteModal(activity: any): void {
  this.activityToDelete = activity;
  this.deleteModalOpen = true;
}

closeDeleteModal(): void {
  this.activityToDelete = null;
  this.deleteModalOpen = false;
}

confirmDelete(): void {
  if (!this.activityToDelete?.id) return;

  this.activityService.deleteActivity(this.activityToDelete.id).subscribe({
    next: () => {
      this.succes = 'Activité supprimée avec succès.';
      this.closeDeleteModal();
      this.loadActivities(); // ou le nom de ta méthode qui recharge les activités
    },
    error: () => {
      this.erreur = 'Impossible de supprimer cette activité.';
    }
  });
}
  toggle(activity: Activity): void {
    if (!activity.id) return;

    this.activityService.toggleActivity(activity.id).subscribe({
      next: () => {
        activity.active = !activity.active;
        this.succes = activity.active
          ? 'Activité réactivée avec succès.'
          : 'Activité désactivée avec succès.';
        this.clearSuccessLater();
      },
      error: (err) => {
        console.error('Erreur toggle activité', err);
      }
    });
  }


  private clearSuccessLater(): void {
    setTimeout(() => {
      this.succes = '';
    }, 3000);
  }

  onPhotoSelected(event: Event): void {
  const input = event.target as HTMLInputElement;

  if (input.files && input.files.length > 0) {
    this.selectedPhoto = input.files[0];
  }
}
}