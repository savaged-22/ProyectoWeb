import { Component, inject, signal, afterNextRender } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css'
})
export class LandingComponent {
  public backendStatus = signal<string>('Comprobando conexión con los servidores principales...');
  public isConnected = signal<boolean | null>(null);
  private apiService = inject(ApiService);

  constructor() {
    afterNextRender(() => {
      this.apiService.testConnection().subscribe({
        next: () => {
          this.backendStatus.set('Sistemas en línea y operativos');
          this.isConnected.set(true);
        },
        error: () => {
          this.backendStatus.set('Sin conexión al servidor principal');
          this.isConnected.set(false);
        }
      });
    });
  }
}
