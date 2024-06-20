import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-register-artista',
  templateUrl: './register-artista.component.html',
  styleUrls: ['./register-artista.component.scss']
})
export class RegisterArtistaComponent implements OnInit{
  ngOnInit(): void {
    
  }

  constructor(private authSrv: AuthService, private router: Router) {}

  onSubmitRegister(form:NgForm) {
    try {
      let value = {
        password: form.value.passwordRegister,
        email: form.value.emailRegister,
        nome: form.value.nomeRegister,
        cognome: form.value.cognomeRegister
      }
      this.authSrv.register(value).subscribe((data) => {
        window.alert("Registrazione effettuata. Effettua il login")
        this.router.navigate(["/login"]);})
    } catch (error) {
      console.error(error)
    }
  }
}