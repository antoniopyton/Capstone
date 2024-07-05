import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Eventi } from 'src/app/interface/eventi.interface';
import { Utente } from 'src/app/interface/utente.interface';
import { AuthService } from 'src/app/service/auth.service';
import { EventiService } from 'src/app/service/eventi.service';

@Component({
  selector: 'app-profilo-utente',
  templateUrl: './profilo-utente.component.html',
  styleUrls: ['./profilo-utente.component.scss']
})
export class ProfiloUtenteComponent {

user: Utente | undefined;
previewUrl: string | ArrayBuffer | null = "assets/omino.png";
eventiPrenotati: Eventi[] = [];

constructor(
  private authSrv: AuthService,
  private eventiService: EventiService,
  private router: Router
) {}

ngOnInit(): void {
  this.authSrv.user$.subscribe(user => {
    this.user = user?.user;
    if (this.user) {
      this.getEventiPrenotati(this.user.id);
    }
  });
}

getEventiPrenotati(utenteId: number): void {
  this.eventiService.getEventiPrenotatiByUtente(utenteId).subscribe(eventi => {
    this.eventiPrenotati = eventi;
  });
}

goToEventDetails(eventoId: number): void {
  this.router.navigate(['/dettagliEvento', eventoId]);
}
onFileSelected(event: Event): void {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files[0]) {
    const file = input.files[0];
    const reader = new FileReader();

    reader.onloadend = e => {
      this.previewUrl = reader.result;
      if (e.target && e.target.readyState === FileReader.DONE && this.user !== null && this.user?.id) {
        if (this.previewUrl) {
          const formData = new FormData();
          formData.set('file', file, file.name);

          this.authSrv.updateAvatar(this.user.id, formData).subscribe((data) => {
            this.authSrv.updateUser(data);
          });
        }
      }
    };

    reader.readAsDataURL(file);
  }
}

triggerFileInput(): void {
  const fileInput = document.getElementById('fileInput') as HTMLInputElement;
  fileInput.click();
}

}
