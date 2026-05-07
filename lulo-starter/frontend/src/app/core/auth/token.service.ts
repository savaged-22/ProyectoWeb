import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class TokenService {
  private readonly key = 'auth_token';
  set(token: string) { localStorage.setItem(this.key, token); }
  get(): string | null { return localStorage.getItem(this.key); }
  clear() { localStorage.removeItem(this.key); }
}
