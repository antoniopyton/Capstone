import { Component, OnInit } from '@angular/core';
import { AuthService } from './service/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit{
  title = 'FrontEnd';

  constructor(private authSrv: AuthService) {

  }
  ngOnInit(): void {
    this.authSrv.restore();
  }
}
