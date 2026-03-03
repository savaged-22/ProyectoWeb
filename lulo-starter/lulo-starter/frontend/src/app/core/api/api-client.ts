import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class ApiClient {
  constructor(private http: HttpClient) {}
  get<T>(url: string, params?: any) { return this.http.get<T>(url, { params }); }
  post<T>(url: string, body: any) { return this.http.post<T>(url, body); }
  patch<T>(url: string, body: any) { return this.http.patch<T>(url, body); }
  delete<T>(url: string) { return this.http.delete<T>(url); }
}
