import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

@Component({
  standalone: true,
  imports: [RouterLink, RouterOutlet],
  template: `
    <div style="display:flex; gap:12px; padding:12px; border-bottom:1px solid #ddd;">
      <a routerLink="/processes">Procesos</a>
      <a routerLink="/roles">Roles</a>
      <a routerLink="/pool">Pool</a>
    </div>
    <div style="padding:12px;">
      <router-outlet />
    </div>
  `
})
export class ShellComponent {}
