import { Component, HostListener, OnDestroy, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { Observable, filter, map, take } from 'rxjs';
import { AuthData } from 'src/app/interface/auth-data.interface';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit, OnDestroy   {

  [x: string]: any;
  nascondi = false;  
  user!: AuthData | null
  isScrolled = false;
    constructor(private authSrv: AuthService, private router: Router) {}
    ngOnInit(): void {
      window.addEventListener('scroll', this.onWindowScroll);
      this.authSrv.user$.subscribe((data) => {
        this.user = data
      })
      this.router.events.pipe(
        filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
        console.log(event.url)
        if (event.url === '/registerr' || event.url === '/register' || event.url === '/login') {
          this.nascondi = true;
        } else {
          this.nascondi = false;
        }
    });
    }

    @HostListener('window:scroll', [])
    onWindowScroll() {
    const offset = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0;
    this.isScrolled = offset > 2200 && offset < 3100; 
  }

  ngOnDestroy() {
    window.removeEventListener('scroll', this.onWindowScroll);
  }
  
    logout() {
      this.authSrv.logout();
    }

    isArtist(): boolean {
      return this.user?.user.tipoUtente === "ARTISTA";
    }

  }
  