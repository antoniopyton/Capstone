import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable, map, take } from 'rxjs';
import { AuthService } from '../service/auth.service';


@Injectable({
  providedIn: 'root'
})
export class ArtistaGuard implements CanActivate{
  
  constructor(private authSrv: AuthService, private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.authSrv.user$.pipe(
      take(1),
      map((user) => {
        if (user && user.user.tipoUtente === 'ARTISTA') {
          return true;
        }
        return this.router.createUrlTree(['/']);
      })
    );
  }
}


