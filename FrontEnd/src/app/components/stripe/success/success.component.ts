import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Utente } from 'src/app/interface/utente.interface';
import { AuthService } from 'src/app/service/auth.service';
import { EventiService } from 'src/app/service/eventi.service';

@Component({
  selector: 'app-success',
  templateUrl: './success.component.html',
  styleUrls: ['./success.component.scss']
})
export class SuccessComponent implements OnInit {

  user: Utente | undefined;
  quantity: number = 1;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private eventiSrv: EventiService
  ) {}

  ngOnInit(): void {
    this.authService.user$.subscribe(data => {
      this.user = data?.user;
    });

    const eventoId = parseInt(this.route.snapshot.queryParamMap.get('eventoId') || '0');
    this.quantity = parseInt(this.route.snapshot.queryParamMap.get('quantita') || '1');

    if (eventoId && this.user) {
      const utenteId = this.user.id;

      this.eventiSrv.nuovaPrenotazione(eventoId, utenteId, this.quantity).subscribe(
        response => {
          console.log('Prenotazione effettuata con successo:', response);
        },
        error => {
          console.error('Errore durante la prenotazione:', error);
        }
      );

      this.authService.restore();
      setTimeout(() => {
        this.router.navigate(['/']);
      }, 5000);
    } else {
      console.error('ID evento o utente non trovati');
    }
  }
}

