import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Session } from '../models/session.model';
import { SessionPlanningEvent } from '../models/session-planning-event.model';

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  private readonly apiUrl = 'http://localhost:8080/api/sessions';

  constructor(private http: HttpClient) {}

  getAllSessions(): Observable<Session[]> {
    return this.http.get<Session[]>(this.apiUrl);
  }

  getSessionById(id: number): Observable<Session> {
    return this.http.get<Session>(`${this.apiUrl}/${id}`);
  }

  getAllWithActivity(): Observable<Session[]> {
    return this.http.get<Session[]>(`${this.apiUrl}/with-activity`);
  }

  getOptionalSessionById(id: number): Observable<Session | null> {
    return this.http.get<Session | null>(`${this.apiUrl}/optional/${id}`);
  }

  getSessionsByAnimateur(animateurId: number): Observable<Session[]> {
    return this.http.get<Session[]>(`${this.apiUrl}/animateur/${animateurId}`, { withCredentials: true });
    
  }

  getCurrentToday(animateurId: number): Observable<Session | null> {
    return this.http.get<Session | null>(`${this.apiUrl}/animateur/${animateurId}/current`, {
      withCredentials: true
    });
  }

  countThisWeek(animateurId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/animateur/${animateurId}/count-this-week`, {
      withCredentials: true
    });
  }

  getUpcomingByAnimateur(animateurId: number, limit: number = 5): Observable<Session[]> {
    const params = new HttpParams().set('limit', limit.toString());
    return this.http.get<Session[]>(`${this.apiUrl}/animateur/${animateurId}/upcoming`, {
      params,
      withCredentials: true
    });
  }

  getSessionsByActivity(activityId: number): Observable<Session[]> {
    return this.http.get<Session[]>(`${this.apiUrl}/activity/${activityId}`);
  }

  getSessionsBetween(from: string, to: string): Observable<Session[]> {
    const params = new HttpParams()
      .set('from', from)
      .set('to', to);

    return this.http.get<Session[]>(`${this.apiUrl}/between`, { params });
  }

  getUpcomingSessions(): Observable<Session[]> {
    return this.http.get<Session[]>(`${this.apiUrl}/upcoming`);
  }

 /* createSession(session: Session): Observable<Session> {
    return this.http.post<Session>(this.apiUrl, session);
  }

  updateSession(id: number, session: Session): Observable<Session> {
    return this.http.put<Session>(`${this.apiUrl}/${id}`, session);
  }*/

  updateSession(id: number, session: Session): Observable<Session> {
  return this.http.put<Session>(
    `${this.apiUrl}/${id}`,
    session,
    { withCredentials: true }
  );
}

createSession(session: Session): Observable<Session> {
  return this.http.post<Session>(
    this.apiUrl,
    session,
    { withCredentials: true }
  );
}
createSessionForAnimateur(animateurId: number, session: any): Observable<Session> {
  return this.http.post<Session>(
    `${this.apiUrl}/animateur/${animateurId}`,
    session,
    { withCredentials: true }
  );
}
  cancelSession(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/cancel`, {});
  }

  deleteSession(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getPlanningEvents(animateurId: number, semaine?: string): Observable<SessionPlanningEvent[]> {
    let params = new HttpParams();

    if (semaine) {
      params = params.set('semaine', semaine);
    }

    return this.http.get<SessionPlanningEvent[]>(
      `${this.apiUrl}/planning-events/${animateurId}`,
      { params, withCredentials: true }
    );
  }


}