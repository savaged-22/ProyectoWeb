import { Component } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

@Component({
  standalone: true,
  imports: [RouterLink],
  template: `
    <a routerLink="/processes">← Volver</a>
    <h2>Editor (placeholder)</h2>
    <p>Aquí va el canvas + panels (HU-08..16, HU-22).</p>
    <p>Proceso: {{processId}}</p>
    <div style="border:1px dashed #aaa; height:360px; display:grid; place-items:center;">
      Canvas
    </div>
  `
})
export class EditorPage {
  processId = this.route.snapshot.paramMap.get('id');
  constructor(private readonly route: ActivatedRoute) {}
}
