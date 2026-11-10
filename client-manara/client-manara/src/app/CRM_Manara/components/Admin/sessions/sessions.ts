import { Component, OnInit } from '@angular/core';
import { Session } from '../../../models/session.model';
import { SessionService } from '../../../services/session.service';
import { ActivityService } from '../../../services/activity.service';

interface CalendarDay {
  day: number;
  date: string;
  currentMonth: boolean;
  sessions: Session[];
}

@Component({
  selector: 'app-sessions',
  standalone: false,
  templateUrl: './sessions.html',
  styleUrls: ['./sessions.css']
})
export class SessionsAdmin implements OnInit {

  
  sessions: Session[] = [];
  filteredSessions: Session[] = [];

  activities: string[] = [];

  activiteFilter: string = '';
  statutFilter: string = '';
  succes: string = '';
activitiesList: any[] = [];
  showModal = false;
isEditMode = false;

sessionForm: any = {
  id: undefined,
  activityId: null,
  startDate: '',
  endDate: '',
  location: '',
  capacity: 0,
  status: 'PLANNED'
};

  // CALENDRIER
  calendarDays: CalendarDay[] = [];
  currentDate: Date = new Date();
  weekDays: string[] = ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim'];

  monthNames: string[] = [
    'Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin',
    'Juillet', 'Août', 'Septembre', 'Octobre', 'Novembre', 'Décembre'
  ];

  constructor(private sessionService: SessionService,private activityService: ActivityService) {}

  ngOnInit(): void {
    this.loadSessions();
     this.loadActivities(); 
  }
loadActivities(): void {
  this.activityService.getAllActivities().subscribe({
    next: (data) => {
      this.activitiesList = data;
      console.log('ACTIVITIES:', data); 
    },
    error: (err) => {
      console.error('Erreur activités', err);
    }
  });
}
  loadSessions(): void {
    this.sessionService.getAllSessions().subscribe({
      next: (data) => {
        this.sessions = data;
        this.filteredSessions = data;
        this.activities = this.extractActivities(data);

        this.renderCalendar();
      },
      error: (err) => {
        console.error('Erreur lors du chargement des sessions', err);
      }
    });
  }

  extractActivities(sessions: Session[]): string[] {
    const activityNames = new Set<string>();

    sessions.forEach((session) => {
      if (session.activityName) {
        activityNames.add(session.activityName);
      }
    });

    return Array.from(activityNames);
  }
getPrixSession(s: any): number {
  if (!s.startDate || !s.endDate || !s.activityId) {
    return 0;
  }

  const activity = this.activitiesList.find(a => a.id === s.activityId);

  if (!activity || !activity.prixHoraire) {
    return 0;
  }

  const start = new Date(s.startDate);
  const end = new Date(s.endDate);

  const diffMs = end.getTime() - start.getTime();
  const heures = Math.ceil(diffMs / (1000 * 60 * 60));

  return heures * activity.prixHoraire;
}
  filtrer(): void {
    this.filteredSessions = this.sessions.filter((s) => {
      const matchActivite =
        !this.activiteFilter || s.activityName === this.activiteFilter;

      const matchStatut =
        !this.statutFilter || this.getStatutLabel(s.status) === this.statutFilter;

      return matchActivite && matchStatut;
    });

    this.renderCalendar();
  }

  getStatutLabel(status: string | undefined): string {
    switch (status) {
      case 'PLANNED':
        return 'Planifiée';
      case 'IN_PROGRESS':
        return 'En cours';
      case 'COMPLETED':
        return 'Terminée';
      case 'CANCELLED':
        return 'Annulée';
      default:
        return '';
    }
  }

  annuler(id: number | undefined): void {
    if (!id) return;

    this.sessionService.cancelSession(id).subscribe({
      next: () => {
        this.succes = 'Session annulée avec succès.';
        this.loadSessions();
      },
      error: (err) => {
        console.error("Erreur lors de l'annulation", err);
      }
    });
  }

  // =========================
  // CALENDRIER
  // =========================

  get calendarTitle(): string {
    return `${this.monthNames[this.currentDate.getMonth()]} ${this.currentDate.getFullYear()}`;
  }

  previousMonth(): void {
    this.currentDate = new Date(
      this.currentDate.getFullYear(),
      this.currentDate.getMonth() - 1,
      1
    );

    this.renderCalendar();
  }

  nextMonth(): void {
    this.currentDate = new Date(
      this.currentDate.getFullYear(),
      this.currentDate.getMonth() + 1,
      1
    );

    this.renderCalendar();
  }

  today(): void {
    this.currentDate = new Date();
    this.renderCalendar();
  }

  renderCalendar(): void {
    this.calendarDays = [];

    const year = this.currentDate.getFullYear();
    const month = this.currentDate.getMonth();

    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);

    let startWeekDay = firstDay.getDay();
    startWeekDay = startWeekDay === 0 ? 6 : startWeekDay - 1;

    const prevMonthLastDate = new Date(year, month, 0).getDate();

    for (let i = startWeekDay; i > 0; i--) {
      this.calendarDays.push({
        day: prevMonthLastDate - i + 1,
        date: '',
        currentMonth: false,
        sessions: []
      });
    }

    for (let day = 1; day <= lastDay.getDate(); day++) {
      const dateObj = new Date(year, month, day);
      const dateString = this.formatDateToYMD(dateObj);

      this.calendarDays.push({
        day,
        date: dateString,
        currentMonth: true,
        sessions: this.getSessionsByDate(dateString)
      });
    }

    const remaining =
      this.calendarDays.length % 7 === 0
        ? 0
        : 7 - (this.calendarDays.length % 7);

    for (let i = 1; i <= remaining; i++) {
      this.calendarDays.push({
        day: i,
        date: '',
        currentMonth: false,
        sessions: []
      });
    }
  }

  getSessionsByDate(date: string): Session[] {
    return this.filteredSessions.filter((s) => {
      if (!s.startDate) return false;

      const sessionDate = this.formatDateToYMD(new Date(s.startDate));
      return sessionDate === date;
    });
  }

  isToday(date: string): boolean {
    return date === this.formatDateToYMD(new Date());
  }

  canAddSessionDate(date: string): boolean {
    if (!date) return false;

    const selected = new Date(`${date}T00:00:00`);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    return selected >= today;
  }

  formatDateToYMD(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }
  formatHour(date: string | Date | undefined): string {
  if (!date) return '';

  const d = new Date(date);

  const hours = String(d.getHours()).padStart(2, '0');
  const minutes = String(d.getMinutes()).padStart(2, '0');

  return `${hours}:${minutes}`;
}
openAddModal(date?: string): void {
  if (date && !this.canAddSessionDate(date)) {
    return;
  }

  this.isEditMode = false;

 this.sessionForm = {
  id: undefined,
  activityId: null,
  startDate: date ? `${date}T09:00` : '',
  endDate: date ? `${date}T10:00` : '',
  location: '',
  capacity: 10,
  status: 'PLANNED'
};

  this.showModal = true;
}

openEditModal(session: Session): void {
  this.isEditMode = true;

this.sessionForm = {
  id: session.id,
  activityId: session.activityId,
  startDate: this.formatDateTimeForInput(session.startDate),
  endDate: this.formatDateTimeForInput(session.endDate),
  location: session.location,
  capacity: session.capacity,
  status: session.status || 'PLANNED'
};
  this.showModal = true;
}

deleteSession(id: number | undefined): void {
  if (!id) return;

  const confirmed = window.confirm('Voulez-vous vraiment supprimer cette session ?');
  if (!confirmed) return;

  this.sessionService.deleteSession(id).subscribe({
    next: () => {
      this.succes = 'Session supprimée avec succès.';
      this.loadSessions();
    },
    error: (err) => {
      console.error('Erreur suppression session', err);
    }
  });
}

closeModal(): void {
  this.showModal = false;
}

saveSession(): void {
  console.log('Payload envoyé:', this.sessionForm);
  if (this.isEditMode) {
    this.sessionService.updateSession(this.sessionForm.id, this.sessionForm).subscribe({
      next: () => {
        this.succes = 'Session modifiée avec succès.';
        this.closeModal();
        this.loadSessions();
      },
      error: (err) => console.error('Erreur modification session', err)
    });
  } else {
    if (!this.canAddSessionDate((this.sessionForm.startDate || '').split('T')[0])) {
      this.succes = 'Impossible d’ajouter une session dans le passé.';
      return;
    }

    this.sessionService.createSession(this.sessionForm).subscribe({
      next: () => {
        this.succes = 'Session ajoutée avec succès.';
        this.closeModal();
        this.loadSessions();
      },
      error: (err) => console.error('Erreur ajout session', err)
    });
  }
}

formatDateTimeForInput(date: any): string {
  if (!date) return '';

  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hours = String(d.getHours()).padStart(2, '0');
  const minutes = String(d.getMinutes()).padStart(2, '0');

  return `${year}-${month}-${day}T${hours}:${minutes}`;
}
}