import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Activity } from '../models/activity.model';

export interface ActivityRequest {
  name: string;
  description?: string;
  categorie?: string;
  prixHoraire: number;
  active: boolean;
  animateur?: {
    id: number;
  } | null;
}

@Injectable({
  providedIn: 'root'
})
export class ActivityService {

  private readonly apiUrl = 'http://localhost:8080/api/activities';

  constructor(private http: HttpClient) {}

  getAllActivities(): Observable<Activity[]> {
    return this.http.get<Activity[]>(this.apiUrl);
  }

  getAfficherActivites(): Observable<Activity[]> {
    return this.http.get<Activity[]>(`${this.apiUrl}/all`);
  }

  getActiveActivities(): Observable<Activity[]> {
    return this.http.get<Activity[]>(`${this.apiUrl}/active`);
  }

  getActivityById(id: number): Observable<Activity> {
    return this.http.get<Activity>(`${this.apiUrl}/${id}`);
  }

  getActivitiesByAnimateur(animateurId: number): Observable<Activity[]> {
    return this.http.get<Activity[]>(`${this.apiUrl}/animateur/${animateurId}`);
  }

  countActiveActivities(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count/active`);
  }

  countByAnimateur(animateurId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count/animateur/${animateurId}`, {
      withCredentials: true
    });
  }

  createActivity(activity: ActivityRequest): Observable<Activity> {
    return this.http.post<Activity>(this.apiUrl, activity);
  }

  updateActivity(id: number, activity: ActivityRequest): Observable<Activity> {
    return this.http.put<Activity>(`${this.apiUrl}/${id}`, activity);
  }

  toggleActivity(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/toggle-active`, {});
  }

  getWeekLabel(semaine?: string): Observable<string> {
    let params = new HttpParams();

    if (semaine) {
      params = params.set('semaine', semaine);
    }

    return this.http.get(`${this.apiUrl}/planning/week-label`, {
      params,
      responseType: 'text'
    });
  }

  getWeekDays(semaine?: string): Observable<string[]> {
    let params = new HttpParams();

    if (semaine) {
      params = params.set('semaine', semaine);
    }

    return this.http.get<string[]>(`${this.apiUrl}/planning/week-days`, { params });
  }

  getTimeSlots(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/planning/time-slots`);
  }

  getPreviousWeek(semaine?: string): Observable<string> {
    let params = new HttpParams();

    if (semaine) {
      params = params.set('semaine', semaine);
    }

    return this.http.get(`${this.apiUrl}/planning/previous-week`, {
      params,
      responseType: 'text'
    });
  }

  getNextWeek(semaine?: string): Observable<string> {
    let params = new HttpParams();

    if (semaine) {
      params = params.set('semaine', semaine);
    }

    return this.http.get(`${this.apiUrl}/planning/next-week`, {
      params,
      responseType: 'text'
    });
  }
  getActiveActivitiesWithSessions(): Observable<Activity[]> {
  return this.http.get<Activity[]>(`${this.apiUrl}/active-with-sessions`);
}
deleteActivity(id: number): Observable<void> {
  return this.http.delete<void>(`${this.apiUrl}/${id}`, {
    withCredentials: true
  });
}
uploadActivityPhoto(id: number, file: File): Observable<Activity> {
  const formData = new FormData();
  formData.append('file', file);

  return this.http.post<Activity>(
    `${this.apiUrl}/${id}/photo`,
    formData,
    { withCredentials: true }
  );
}
}