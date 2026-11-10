import { Component, OnInit } from '@angular/core';
import { RegistrationService } from '../../../services/registration.service';
import { UserService } from '../../../services/user.service';
import { PlanningEvent } from '../../../models/planning-event.model';
import { NotificationService } from '../../../services/notification.service';

@Component({
  selector: 'app-planning-parent',
  standalone: false,
  templateUrl: './planning.html'
})
export class PlanningParent implements OnInit {

  connectedUser: any = null;
  unreadNotifications = 0;

  planningEvents: PlanningEvent[] = [];

  weekDays: string[] = [];
  timeSlots: string[] = [
    '08:00', '09:00', '10:00', '11:00',
    '12:00', '13:00', '14:00', '15:00',
    '16:00', '17:00', '18:00'
  ];

  currentWeekStart!: Date;
  weekLabel = '';
  erreur = '';

  constructor(
    private registrationService: RegistrationService,
    private userService: UserService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    const parentId = Number(localStorage.getItem('userId'));
    const email = localStorage.getItem('email');

    if (!parentId) {
      this.erreur = 'Parent non connecté.';
      return;
    }

    if (email) {
      this.userService.getUserByEmail(email).subscribe({
        next: user => this.connectedUser = user
      });
    }

    this.currentWeekStart = this.getStartOfWeek(new Date());
    this.generateWeekDays();
    this.loadPlanning(parentId);
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
isEventInCurrentWeek(startDate: string): boolean {
  const eventDate = new Date(startDate);

  const start = new Date(this.currentWeekStart);
  start.setHours(0, 0, 0, 0);

  const end = new Date(this.currentWeekStart);
  end.setDate(end.getDate() + 7);
  end.setHours(0, 0, 0, 0);

  return eventDate >= start && eventDate < end;
}
loadPlanning(parentId: number): void {
  this.erreur = '';

  this.registrationService.getRegistrationsByParent(parentId).subscribe({
    next: (registrations: any[]) => {
      console.log('registrations reçues =', registrations);

      this.planningEvents = registrations
        .filter(reg => reg.sessionStartDate)
        .map(reg => ({
          activityName: reg.activityName || `Session #${reg.sessionId}`,
          childName: reg.childFullName || `${reg.childFirstName || ''} ${reg.childLastName || ''}`.trim(),
          location: reg.sessionLocation || '',
          status: reg.status || 'PENDING_PAYMENT',
          startDate: reg.sessionStartDate,
          endDate: reg.sessionEndDate
        }))
        .filter(event => this.isEventInCurrentWeek(event.startDate));

      console.log('planningEvents filtrés =', this.planningEvents);
    },
    error: () => {
      this.erreur = 'Impossible de charger le planning.';
    }
  });
}

  getEventsForCell(day: string, slot: string): PlanningEvent[] {
    return this.planningEvents.filter((event: any) => {
      const eventDate = new Date(event.startDate);

      const eventDay = this.formatDay(eventDate);
      const eventSlot = this.formatHourSlot(eventDate);

      return eventDay === day && eventSlot === slot;
    });
  }

  semainePrecedente(): void {
    this.currentWeekStart = new Date(this.currentWeekStart);
    this.currentWeekStart.setDate(this.currentWeekStart.getDate() - 7);

    this.generateWeekDays();
    this.reloadPlanning();
  }

  semaineSuivante(): void {
    this.currentWeekStart = new Date(this.currentWeekStart);
    this.currentWeekStart.setDate(this.currentWeekStart.getDate() + 7);

    this.generateWeekDays();
    this.reloadPlanning();
  }

  allerAujourdhui(): void {
    this.currentWeekStart = this.getStartOfWeek(new Date());

    this.generateWeekDays();
    this.reloadPlanning();
  }

  reloadPlanning(): void {
    const parentId = Number(localStorage.getItem('userId'));

    if (parentId) {
      this.loadPlanning(parentId);
    }
  }

  getStartOfWeek(date: Date): Date {
    const d = new Date(date);
    const day = d.getDay();
    const diff = d.getDate() - day + (day === 0 ? -6 : 1);

    d.setDate(diff);
    d.setHours(0, 0, 0, 0);

    return d;
  }

  generateWeekDays(): void {
    const days: string[] = [];

    for (let i = 0; i < 7; i++) {
      const date = new Date(this.currentWeekStart);
      date.setDate(this.currentWeekStart.getDate() + i);
      days.push(this.formatDay(date));
    }

    this.weekDays = days;

    const start = new Date(this.currentWeekStart);
    const end = new Date(this.currentWeekStart);
    end.setDate(start.getDate() + 6);

    this.weekLabel =
      `${start.toLocaleDateString('fr-CA')} au ${end.toLocaleDateString('fr-CA')}`;
  }

  formatDay(date: Date): string {
    return date.toLocaleDateString('fr-CA', {
      weekday: 'short',
      day: '2-digit',
      month: '2-digit'
    });
  }

  formatHourSlot(date: Date): string {
    const hour = date.getHours().toString().padStart(2, '0');
    return `${hour}:00`;
  }

  formatDateForApi(date: Date): string {
    return date.toISOString().split('T')[0];
  }
}