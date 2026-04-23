import { Component, inject, signal } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink, FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  email = '';
  password = '';
  
  errorMsg = signal('');
  loading = signal(false);

  private apiService = inject(ApiService);
  private router = inject(Router);

  onSubmit(event: Event) {
    event.preventDefault();
    if (!this.email || !this.password) return;

    this.loading.set(true);
    this.errorMsg.set('');

    this.apiService.login({ email: this.email, password: this.password }).subscribe({
      next: (res: any) => {
        this.loading.set(false);
        // Guardar token (en el futuro esto será un JWT real)
        localStorage.setItem('token', res.token);
        
        alert('¡Bienvenido ' + res.email + '!');
        this.router.navigate(['/']); // Redirigir a la landing page o dashboard
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMsg.set('Correo o contraseña incorrectos.');
        console.error('Error de login:', err);
      }
    });
  }
}
