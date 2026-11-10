import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Registration } from '../models/registration.model';
import { PlanningEvent } from '../models/planning-event.model';


@Injectable({
  providedIn: 'root'
})
export class RegistrationService {

  private apiUrl = 'http://localhost:8080/api/registrations';

  constructor(private http: HttpClient) { }

  getRegistrationById(id: number): Observable<Registration> {
    return this.http.get<Registration>(`${this.apiUrl}/${id}`);
  }

  getAllRegistrations(): Observable<Registration[]> {
    return this.http.get<Registration[]>(this.apiUrl);
  }

  getRegistrationsByParent(parentId: number): Observable<Registration[]> {
    return this.http.get<Registration[]>(`${this.apiUrl}/parent/${parentId}`);
  }

  getRegistrationsBySession(sessionId: number): Observable<Registration[]> {
    return this.http.get<Registration[]>(`${this.apiUrl}/session/${sessionId}`);
  }

  getRecentRegistrations(limit: number = 5): Observable<Registration[]> {
    const params = new HttpParams().set('limit', limit);
    return this.http.get<Registration[]>(`${this.apiUrl}/recent`, { params });
  }

  countActives(parentId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count/active/parent/${parentId}`);
  }

  countPending(parentId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count/pending/parent/${parentId}`);
  }

  countAll(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count/all`);
  }

  countPendingAll(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count/pending/all`);
  }

  countByAnimateur(animateurId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count/animateur/${animateurId}`, {
      withCredentials: true
    });
  }

  getUpcomingByParent(parentId: number): Observable<Registration[]> {
    return this.http.get<Registration[]>(`${this.apiUrl}/upcoming/parent/${parentId}`);
  }

  inscrire(childId: number, sessionId: number): Observable<Registration> {
    const params = new HttpParams()
      .set('childId', childId)
      .set('sessionId', sessionId);

    return this.http.post<Registration>(`${this.apiUrl}/inscrire`, null, { params });
  }

  confirmer(id: number, adminId: number): Observable<void> {
    const params = new HttpParams().set('adminId', adminId);
    return this.http.patch<void>(`${this.apiUrl}/${id}/confirm`, {}, { params });
  }

  annuler(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/cancel`, {});
  }

  promouvoir(id: number, adminId: number): Observable<void> {
    const params = new HttpParams().set('adminId', adminId);
    return this.http.patch<void>(`${this.apiUrl}/${id}/promote`, {}, { params });
  }

  savePresences(sessionId: number, childPresents: number[]): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/presences/${sessionId}`, childPresents);
  }

  getPlanningEvents(parentId: number, semaine?: string): Observable<PlanningEvent[]> {
    let params = new HttpParams();
    if (semaine) {
      params = params.set('semaine', semaine);
    }

    return this.http.get<PlanningEvent[]>(`${this.apiUrl}/planning-events/${parentId}`, { params });
  }

  getRegistrationsByAnimateur(animateurId: number): Observable<Registration[]> {
  return this.http.get<Registration[]>(
    `${this.apiUrl}/animateur/${animateurId}`,
    { withCredentials: true }
  );
}
confirmerByAnimateur(id: number, animateurId: number): Observable<void> {
  const params = new HttpParams().set('animateurId', animateurId);

  return this.http.patch<void>(
    `${this.apiUrl}/${id}/confirm-animateur`,
    {},
    { params, withCredentials: true }
  );
}

}