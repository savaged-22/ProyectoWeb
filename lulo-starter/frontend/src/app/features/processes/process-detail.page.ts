import { Component } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiClient } from '../../core/api/api-client';
import { AsyncPipe } from '@angular/common';
import { Observable, switchMap } from 'rxjs';
import { Process } from '../../shared/models/process.model';

@Component({
  standalone: true,
  imports: [RouterLink, AsyncPipe],
  template: `
    <a routerLink="/processes">← Volver</a>
    <h2>Detalle proceso</h2>
    <pre *ngIf="process$ | async as p">{{ p | json }}</pre>
  `
})
export class ProcessDetailPage {
  process$: Observable<Process> = this.route.paramMap.pipe(
    switchMap(pm => this.api.get<Process>(`/api/processes/${pm.get('id')}`))
  );

  constructor(private route: ActivatedRoute, private api: ApiClient) {}
}
