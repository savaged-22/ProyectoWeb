import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiClient } from '../../core/api/api-client';
import { AsyncPipe, NgFor } from '@angular/common';
import { Observable } from 'rxjs';
import { Process } from '../../shared/models/process.model';

@Component({
  standalone: true,
  imports: [RouterLink, NgFor, AsyncPipe],
  template: `
    <h2>Procesos</h2>
    <ul>
      <li *ngFor="let p of (processes$ | async)">
        <a [routerLink]="['/processes', p.id]">{{p.name}}</a>
        <span style="opacity:.7;"> — {{p.status}}</span>
        <a [routerLink]="['/editor', p.id]" style="margin-left:8px;">Editar diagrama</a>
      </li>
    </ul>
  `
})
export class ProcessListPage {
  processes$: Observable<Process[]> = this.api.get<Process[]>('/api/processes');
  constructor(private api: ApiClient) {}
}
