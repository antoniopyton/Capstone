import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment.development';
import { Utente } from '../interface/utente.interface';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UtentiService {
  private apiUrl = environment.apiUrl;
  constructor(private http:HttpClient) { }

  updateUser(id: number, user: Utente): Observable<Utente> {
    const url = `${this.apiUrl}/utenti/${id}`;
    return this.http.put<Utente>(url, user);
  }

}
