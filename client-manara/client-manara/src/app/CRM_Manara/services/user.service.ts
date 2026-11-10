import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { PasswordForm } from '../models/password-form.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  afficherUtilisateurs(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/all`);
  }

  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  getUserByEmail(email: string): Observable<User> {
    const params = new HttpParams().set('email', email);
    return this.http.get<User>(`${this.apiUrl}/email`, { params });
  }

  getUsersByRole(role: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/role/${role}`);
  }

  getAllParents(): Observable<User[]> {
    return this.getUsersByRole('PARENT');
  }
  getAllAnimateurs(): Observable<User[]> {
  return this.getUsersByRole('ANIMATEUR');
}

  countByRole(role: string): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count/role/${role}`);
  }

  countNewThisMonth(role: string): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count/new-this-month/${role}`);
  }

  registerParent(user: User): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/register-parent`, user);
  }

  createUser(user: User): Observable<User> {
    return this.http.post<User>(this.apiUrl, user);
  }

  createSimpleUser(user: User): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/create-simple`, user);
  }

  updateProfile(id: number, user: User): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}/profile`, user);
  }

  changePassword(id: number, form: PasswordForm): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/change-password`, form);
  }

  toggleActive(id: number): Observable<User> {
    return this.http.patch<User>(`${this.apiUrl}/${id}/toggle-active`, {});
  }
  deleteUser(id: number): Observable<void> {
  return this.http.delete<void>(`${this.apiUrl}/${id}`);
}
}