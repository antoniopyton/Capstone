import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Eventi } from '../interface/eventi.interface';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class EventiService {

  private apiUrl = environment.apiUrl;
  constructor(private http: HttpClient) { }

  getEventi(): Observable<Eventi[]> {
    return this.http.get<Eventi[]>(`${this.apiUrl}eventi`);
  }

  getProssimiEventi(): Observable<Eventi[]> {
    return this.http.get<Eventi[]>(`${this.apiUrl}prossimiEventi`);
  }

  getEventoById(id: number): Observable<Eventi> {
    return this.http.get<Eventi>(`${this.apiUrl}eventi/${id}`);
  }

  getEventiSimili(id: number): Observable<Eventi[]> {
    return this.http.get<Eventi[]>(`${this.apiUrl}eventi/${id}/simili`);
  }

  salvaEvento(evento: Eventi): Observable<Eventi> {
    return this.http.post<Eventi>(this.apiUrl, evento);
  }

  updateEvento(id: number, evento: Eventi): Observable<Eventi> {
    return this.http.put<Eventi>(`${this.apiUrl}${id}`, evento);
  }

  deleteEvento(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}${id}`);
  }

  patchImmagineEvento(id: number, file: File): Observable<Eventi> {
    const formData: FormData = new FormData();
    formData.append('file', file, file.name);
    return this.http.patch<Eventi>(`${this.apiUrl}${id}/logo`, formData);
  }
}
