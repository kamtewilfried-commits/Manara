import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ChatResponse {
  reply: string;
}

@Injectable({
  providedIn: 'root'
})
export class ChatbotService {

  private apiUrl = 'http://localhost:8080/api/chat2';

  constructor(private http: HttpClient) {}

  sendMessage(message: string): Observable<ChatResponse> {

    return this.http.post<ChatResponse>(
      this.apiUrl,
      {
        message: message
      },
      {
        withCredentials: true
      }
    );
  }
}