import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Child } from '../models/child.model';

@Injectable({
  providedIn: 'root'
})
export class ChildService {

  private apiUrl = 'http://localhost:8080/api/children';

  constructor(private http: HttpClient) { }

  getChildById(id: number): Observable<Child> {
    return this.http.get<Child>(`${this.apiUrl}/${id}`);
  }

  getChildrenByParentId(parentId: number): Observable<Child[]> {
    return this.http.get<Child[]>(`${this.apiUrl}/parent/${parentId}`);
  }

  countChildrenByParentId(parentId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count/parent/${parentId}`);
  }

  createChild(child: Child): Observable<Child> {
    return this.http.post<Child>(this.apiUrl, child);
  }

  updateChild(id: number, child: Child): Observable<Child> {
    return this.http.put<Child>(`${this.apiUrl}/${id}`, child);
  }

  deleteChild(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  belongsToParent(childId: number, parentId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/${childId}/belongs-to-parent/${parentId}`);
  }
}