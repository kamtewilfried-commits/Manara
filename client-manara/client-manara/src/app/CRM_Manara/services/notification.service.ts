import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Notification } from '../models/notification.model';
import { NotificationForm } from '../models/notification-form';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private apiUrl = 'http://localhost:8080/api/notifications';

  constructor(private http: HttpClient) {}

  getNotificationsByUser(userId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/user/${userId}`);
  }

  getAllNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/admin`);
  }

  getRecentNotifications(userId: number, limit: number = 5): Observable<Notification[]> {
    const params = new HttpParams().set('limit', limit.toString());
    return this.http.get<Notification[]>(`${this.apiUrl}/user/${userId}/recent`, { params });
  }

  countUnread(userId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/user/${userId}/unread-count`);
  }

  markRead(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/read`, {});
  }

  markAllRead(userId: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/user/${userId}/read-all`, {});
  }

  sendNotification(form: NotificationForm): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/admin/send`, form);
  }
}