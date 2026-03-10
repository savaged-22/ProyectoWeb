import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./features/auth/login.page').then(m => m.LoginPage) },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./core/layout/shell.component').then(m => m.ShellComponent),
    children: [
      { path: 'processes', loadComponent: () => import('./features/processes/process-list.page').then(m => m.ProcessListPage) },
      { path: 'processes/:id', loadComponent: () => import('./features/processes/process-detail.page').then(m => m.ProcessDetailPage) },
      { path: 'editor/:id', loadComponent: () => import('./features/editor/editor.page').then(m => m.EditorPage) },
      { path: 'roles', loadComponent: () => import('./features/roles/roles.page').then(m => m.RolesPage) },
      { path: 'pool', loadComponent: () => import('./features/pools/pool-settings.page').then(m => m.PoolSettingsPage) },
    ]
  },
  { path: '**', redirectTo: 'processes' }
];
