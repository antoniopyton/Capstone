import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/service/auth.service';

declare global {
  interface Window {
      google: any;
  }
}

@Component({
  selector: 'app-google-login',
  templateUrl: './google-login.component.html',
  styleUrls: ['./google-login.component.scss']
})
export class GoogleLoginComponent implements OnInit{

   
constructor(private authSrv:AuthService ) { }

ngOnInit(): void {
  window.onload = () => {
    google.accounts.id.initialize({
      client_id: '287317140125-d3lkd2jkkspvhue01mq5g72cadktrpe2.apps.googleusercontent.com',
      callback: this.handleCredentialResponse.bind(this)
    });
    const buttonDiv = document.getElementById('buttonDiv');
    if (buttonDiv) {
      google.accounts.id.renderButton(
        buttonDiv,
        { theme: 'outline', size: 'large' }  // personalizza il pulsante come desideri
      );
      google.accounts.id.prompt(); // mostra il dialogo One Tap
    } else {
      console.error('Elemento buttonDiv non trovato');
    }
  }
}


handleCredentialResponse(response: any) {
  if (response && response.credential) {
    console.log('Encoded JWT ID token: ' + response.credential);
    this.sendTokenToBackend(response.credential);
  } else {
    console.error('Credenziali non valide', response);
  }
}

sendTokenToBackend(token: string): void {
  this.authSrv.loginGoogle({token}).subscribe();
}

}
