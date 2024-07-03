import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-register-artista2',
  templateUrl: './register-artista2.component.html',
  styleUrls: ['./register-artista2.component.scss']
})
export class RegisterArtista2Component {

  constructor(private authSrv: AuthService, private router: Router) {}

  selectedFiles?: FileList;

  onSubmitRegisterArtista(form: NgForm) {
    const validArtistTypes = ['ARTISTA', 'BAND']; 
    try {
      let value = {
        password: form.value.passwordRegister,
        email: form.value.emailRegister,
        nome: form.value.nomeRegister,
        cognome: form.value.cognomeRegister,
        nomeArtista: form.value.nomeArtistaRegister,
        tipoArtista: form.value.tipoArtistaRegister
      };

      if (!validArtistTypes.includes(value.tipoArtista)) {
        console.error('Tipo Artista non valido:', value.tipoArtista);
        return;
      }

      this.authSrv.registerArtist(value).subscribe((data) => {
        window.alert("Registrazione effettuata.");
        
        this.authSrv.login({ email: value.email, password: value.password }).subscribe(loginResponse => {
          
          this.router.navigate(["/registerr/registerr2"]);
        }, error => {
          console.error("Login fallito dopo la registrazione", error);
          window.alert("Login fallito dopo la registrazione. Per favore, effettua il login manualmente.");
        });
      }, error => {
        console.error("Registrazione fallita", error);
      });
    } catch (error) {
      console.error(error);
    }
  }
}
