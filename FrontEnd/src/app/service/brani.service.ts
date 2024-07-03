import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from 'src/environments/environment.development';
import { Utente } from '../interface/utente.interface';
import { Brani } from '../interface/brani.interface';

@Injectable({
  providedIn: 'root'
})
export class BraniService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  saveBrano(data: {titolo: string, genere: string, durata: number, fileUrl: string}) {
    return this.http.post(`${environment.apiUrl}brani`, data).pipe(catchError(this.errors));
  }

  getBraniByArtistaId(id: number): Observable<Brani[]> {
    return this.http.get<Brani[]>(`${this.apiUrl}brani/artista/${id}`);
  }

  updateCopertina(id: number, formData: FormData) : Observable<Brani> {
    return this.http.patch<Brani>(`${this.apiUrl}brani/${id}/brano`, formData)
  }

  incrementaAscolti(branoId: number): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}brani/${branoId}/play`, {});
  }

  getTop5BraniByAscolti(): Observable<Brani[]> {
    return this.http.get<Brani[]>(`${this.apiUrl}brani/top5`);
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
