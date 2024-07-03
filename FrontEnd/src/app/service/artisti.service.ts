import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from 'src/environments/environment.development';
import { Utente } from '../interface/utente.interface';
import { Brani } from '../interface/brani.interface';

@Injectable({
  providedIn: 'root'
})
export class ArtistiService {
  
private apiUrl=environment.apiUrl;

  constructor(private http: HttpClient) { }

  getArtisti(): Observable<Utente[]> {
    return this.http.get<Utente[]>(`${this.apiUrl}artisti`);
  }

  getArtistaById(id: number): Observable<Utente> {
    return this.http.get<Utente>(`${this.apiUrl}utenti/${id}`)
  }

  updateUser(id: number, user: Partial<Utente>): Observable<Utente> {
    return this.http.patch<Utente>(`${this.apiUrl}utenti/${id}`, user);
  }

  updateAvatar(id: number, formData: FormData) : Observable<Utente> {
    return this.http.patch<Utente>(`${this.apiUrl}utenti/${id}/avatar`, formData)
  }

  getBrani(id: number): Observable<Brani[]> {
    return this.http.get<Brani[]>(`${this.apiUrl}brani/artista/${id}`);
  }

  getAscoltiSettimana(artistaId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}artisti/${artistaId}/ascolti/settimana`);
  }

  getAscoltiMese(artistaId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}artisti/${artistaId}/ascolti/mese`);
  }

  getAscoltiGiorno(artistaId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}artisti/${artistaId}/ascolti/giorno`);
  }

  private errors(err: any) {
    console.log(err)
      let error = "";
      switch (err.error) {
          case 'Email already exists':
              error = "Utente già presente"
              break;

          case 'Incorrect password':
              error = 'Password errata';
              break;

          case 'Cannot find user':
              error = 'Utente non trovato';
              break;
          case 'Password is too short':
            error = 'La password è troppo corta';
            break
          default:
              error = 'Errore nella chiamata';
              break;
      }
      return throwError(error)
  }
}
