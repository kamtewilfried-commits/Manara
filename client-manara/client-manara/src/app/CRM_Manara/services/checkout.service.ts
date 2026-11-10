import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CheckoutService {

  private apiUrl = 'http://localhost:8080/api/checkout';

  constructor(private http: HttpClient) {}

  checkout(request: any): Observable<{ url: string }> {
    return this.http.post<{ url: string }>(
      this.apiUrl,
      request,
      {
        withCredentials: true
      }
    );
  }
}