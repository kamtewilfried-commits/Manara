import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ActivityRegistrationStats {
  activityName: string;
  registrationsCount: number;
}

export interface ActivityRevenue {
  activityName: string;
  revenue: number;
}

export interface MonthlyRevenue {
  year: number;
  month: number;
  revenue: number;
}

export interface StatusStats {
  status: string;
  count: number;
}

export interface SessionTimeStats {
  year: number;
  month: number;
  count: number;
}

export interface AnimatorSessionStats {
  animatorName: string;
  sessionsCount: number;
}

export interface ActivityCategoryStats {
  categorie: string;
  count: number;
}

export interface ActivityCategoryPrice {
  categorie: string;
  averagePrice: number;
}

@Injectable({
  providedIn: 'root'
})
export class AdminStatsService {

  private apiUrl = 'http://localhost:8080/api/stats';

  constructor(private http: HttpClient) {}

  getRegistrationsByActivity(): Observable<ActivityRegistrationStats[]> {
    return this.http.get<ActivityRegistrationStats[]>(`${this.apiUrl}/registrations/by-activity`);
  }

  getRevenueByActivity(): Observable<ActivityRevenue[]> {
    return this.http.get<ActivityRevenue[]>(`${this.apiUrl}/revenue/by-activity`);
  }

  getRevenueByMonth(): Observable<MonthlyRevenue[]> {
    return this.http.get<MonthlyRevenue[]>(`${this.apiUrl}/revenue/by-month`);
  }

  getRegistrationsByStatus(): Observable<StatusStats[]> {
    return this.http.get<StatusStats[]>(`${this.apiUrl}/registrations/by-status`);
  }

  getSessionsByMonth(): Observable<SessionTimeStats[]> {
    return this.http.get<SessionTimeStats[]>(`${this.apiUrl}/sessions/by-month`);
  }

  getSessionsByAnimator(): Observable<AnimatorSessionStats[]> {
    return this.http.get<AnimatorSessionStats[]>(`${this.apiUrl}/sessions/by-animator`);
  }

  getActivitiesByCategory(): Observable<ActivityCategoryStats[]> {
    return this.http.get<ActivityCategoryStats[]>(`${this.apiUrl}/activities/by-category`);
  }

  getAveragePriceByCategory(): Observable<ActivityCategoryPrice[]> {
    return this.http.get<ActivityCategoryPrice[]>(`${this.apiUrl}/activities/average-price-by-category`);
  }

  getActiveActivitiesCount(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/activities/active/count`);
  }

  getInactiveActivitiesCount(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/activities/inactive/count`);
  }
}