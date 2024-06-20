import { Component, OnInit } from '@angular/core';
import { AuthData } from 'src/app/interface/auth-data.interface';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit   {
  [x: string]: any;
  
  user!: AuthData | null
    constructor(private authSrv: AuthService) {}
    ngOnInit(): void {
      this.authSrv.user$.subscribe((data) => {
        this.user = data
      })
    }
  
    logout() {
      this.authSrv.logout();
    }
  }
  