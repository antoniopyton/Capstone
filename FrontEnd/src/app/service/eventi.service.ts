import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Eventi } from '../interface/eventi.interface';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment.development';
import { Utente } from '../interface/utente.interface';

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

  getEventiPrenotatiByUtente(utenteId: number): Observable<Eventi[]> {
    return this.http.get<Eventi[]>(`${this.apiUrl}utenti/${utenteId}/prenotazioni`);
  }

  getArtistiCandidati(eventoId: number): Observable<Utente[]> {
    return this.http.get<Utente[]>(`${this.apiUrl}eventi/${eventoId}/artisti-candidati`);
  }

  getEventiByArtista(id: number): Observable<Eventi[]> {
    return this.http.get<Eventi[]>(`${this.apiUrl}artista/${id}`);
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

  nuovaPrenotazione(eventoId: number, utenteId: number, quantita: number): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}prenotazioni/${eventoId}/${utenteId}`, { quantita });
  }

  nuovaCandidatura(eventoId: number, artistaId: number): Observable<string> {
    return this.http.post(`${this.apiUrl}candidature/${eventoId}/${artistaId}`, {}, { responseType: 'text' });
  }
}
