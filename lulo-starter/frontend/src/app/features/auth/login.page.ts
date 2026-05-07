import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  standalone: true,
  imports: [FormsModule],
  template: `
    <h2>Login — Lulo</h2>
    <form (ngSubmit)="submit()" style="display:grid; gap:8px; max-width:320px;">
      <input name="email" [(ngModel)]="email" placeholder="Email" />
      <input name="password" [(ngModel)]="password" placeholder="Password" type="password" />
      <button type="submit">Entrar</button>
    </form>
    <p *ngIf="error" style="color:#b00020;">{{error}}</p>
  `,
})
export class LoginPage {
  email = '';
  password = '';
  error = '';

  constructor(private readonly auth: AuthService, private readonly router: Router) {}

  submit() {
    this.error = '';
    this.auth.login(this.email, this.password).subscribe({
      next: () => this.router.navigateByUrl('/processes'),
      error: () => this.error = 'No se pudo iniciar sesión'
    });
  }
}
