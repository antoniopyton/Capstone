import { Component } from '@angular/core';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-nome-progetto',
  templateUrl: './nome-progetto.component.html',
  styleUrls: ['./nome-progetto.component.scss']
})
export class NomeProgettoComponent {

  constructor(private authSrv: AuthService) {
    this.authSrv.user$.subscribe(user => {
      if(user) {
        this.isLoggedIn = true
      } else {
        this.isLoggedIn = false
      }
    })
  }

  isLoggedIn = false; 

}
