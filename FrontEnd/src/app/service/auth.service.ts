import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { BehaviorSubject, Observable, catchError, tap, throwError } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http'
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment.development';
import { AuthData } from '../interface/auth-data.interface';
import { Utente } from '../interface/utente.interface';



@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private jwtHelper = new JwtHelperService();

  private authSub = new BehaviorSubject<AuthData | null>(null);
  user$ = this.authSub.asObservable();
  private timeout!:any;

  constructor(private http: HttpClient, private router:Router) {}

  register(data: {password: string, email: string, nome: string, cognome: string}) {
    return this.http.post(`${environment.apiUrl}auth/register`, data).pipe(catchError(this.errors));
  }

  registerArtist(data: {password: string, email: string, nome: string, cognome: string, nomeArtista: string, tipoArtista: string}) {
    return this.http.post(`${environment.apiUrl}auth/register/artista`, data).pipe(catchError(this.errors));
  }
  
  registerArtist2(id: number, data: { descrizioneArtista?: string }): Observable<any> {
    const url = `${environment.apiUrl}utenti/2/${id}`;
    return this.http.patch(url, data).pipe(
      catchError(this.handleError)
    );
  }

  updateAvatar(id: number, formData: FormData) : Observable<Utente> {
    return this.http.patch<Utente>(`${environment.apiUrl}utenti/${id}/avatar`, formData)
  }

  uploadFile(file: File): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', file, file.name);

    const url = `${environment.apiUrl}upload`; 
    return this.http.post(url, formData)
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(error: any): Observable<never> {
    console.error('An error occurred', error); // Per scopi di debug
    // Puoi anche gestire l'errore in modo più sofisticato qui
    throw error;
  }

  login(data: { email: string, password: string }): Observable<AuthData> {
    const url = `${environment.apiUrl}auth/login`;    return this.http.post<AuthData>(url, data).pipe(
      tap(user => {
        localStorage.setItem('user', JSON.stringify(user));
        this.authSub.next(user); 
      }),
      catchError(this.handleError)
    );
  }

  loginGoogle(token:any){

    return this.http.post<AuthData>(`${environment.apiUrl}auth/login/oauth2/code/google`,token).pipe(
      tap(async (user) => {
        this.authSub.next(user);
        localStorage.setItem('user', JSON.stringify(user));
        this.autoLogout(user);
      })
    )
  }
  
  private initializeGoogleLogin() {
    
    window.location.reload(); 
  }
   
  updateUser(data: Utente) {
    const datas = this.authSub.getValue();
    if (datas) {
      datas.user = data;
    }
    this.authSub.next(datas);
    localStorage.setItem('user', JSON.stringify(datas))
  }

  logout() {
    this.authSub.next(null);
    localStorage.removeItem('user');
    this.router.navigate(['/'])
    this.initializeGoogleLogin();
  }

  private autoLogout(data: AuthData) {
    const dataExp = this.jwtHelper.getTokenExpirationDate(data.accessToken) as Date;
    const msExp = dataExp.getTime() - new Date().getTime();
    this.timeout = setTimeout(() => {
      this.logout();
    }, msExp)
  }

  async restore() {
    const userJson = localStorage.getItem('user');
    if (!userJson) {
      return
    }
    const user:AuthData = JSON.parse(userJson);
    this.authSub.next(user);
    // this.router.navigate(['/'])
    this.autoLogout(user);
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
