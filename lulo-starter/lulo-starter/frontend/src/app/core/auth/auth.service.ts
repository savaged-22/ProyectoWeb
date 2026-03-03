import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { TokenService } from './token.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  constructor(private http: HttpClient, private tokens: TokenService) {}

  login(email: string, password: string): Observable<void> {
    return this.http.post<{token: string}>('/api/auth/login', { email, password })
      .pipe(map(res => { this.tokens.set(res.token); }));
  }

  logout(): void { this.tokens.clear(); }
  isLoggedIn(): boolean { return !!this.tokens.get(); }
}
