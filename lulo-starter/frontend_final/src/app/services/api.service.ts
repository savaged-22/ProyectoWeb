import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private http = inject(HttpClient);
  private baseUrl = environment.apiUrl;

  // Método de ejemplo para verificar la conectividad
  testConnection() {
    return this.http.get(this.baseUrl, { responseType: 'text' });
  }

  // Método para autenticar usuarios contra el backend
  login(credentials: {email: string, password: string}) {
    return this.http.post(`${this.baseUrl}/auth/login`, credentials);
  }
}
