import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { loadStripe } from '@stripe/stripe-js';
import { Subscription } from 'rxjs';
import { Eventi } from 'src/app/interface/eventi.interface';
import { Utente } from 'src/app/interface/utente.interface';
import { AuthService } from 'src/app/service/auth.service';
import { EventiService } from 'src/app/service/eventi.service';
import { environment } from 'src/environments/environment.development';
import { ModaleArtistaComponent } from '../modale-artista/modale-artista.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-dettagli-evento',
  templateUrl: './dettagli-evento.component.html',
  styleUrls: ['./dettagli-evento.component.scss']
})
export class DettagliEventoComponent implements OnInit {
 
  evento: Eventi | undefined;
  quantity: number = 1;
  eventiSimili: Eventi[] | undefined;
  user: Utente | undefined;
  artistiOspiti: Utente [] = [];
  isArtista: boolean = false;
  isCandidato: boolean = false;

  private apiUrl = environment.apiUrl;
  stripePromise = loadStripe(environment.stripe);
  private routeSub: Subscription | undefined;

  constructor(
    private route: ActivatedRoute,
    private eventoSrv: EventiService,
    private http: HttpClient,
    private authSrv: AuthService,
    private router: Router,
    private modalService: NgbModal 
  ) {
    this.authSrv.user$.subscribe(data => {
      this.user = data?.user;
      this.checkIfArtista();
    });
  }

  ngOnInit(): void {
    this.routeSub = this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.loadEvento(+id);
      }
    });
  }

  ngOnDestroy(): void {
    if (this.routeSub) {
      this.routeSub.unsubscribe();
    }
  }

  openModal(artistId: number): void {
    const modalRef = this.modalService.open(ModaleArtistaComponent);
    modalRef.componentInstance.artistaId = artistId;  
  }

  loadEvento(id: number): void {
    this.eventoSrv.getEventoById(id).subscribe(
      data => {
        this.evento = data;
        this.loadEventiSimili(data.id);
        this.loadArtistiCandidati(data.id);
      },
      error => console.error('Error: ', error)
    );
  }

  navigateToEvento(eventoId: number): void {
    this.router.navigate(['/eventi', eventoId]).then(() => {
      this.loadEvento(eventoId);
    });
  }

  checkIfArtista(): void {
    this.isArtista = this.user?.tipoUtente === 'ARTISTA';
  }

  increaseQuantity() {
    this.quantity++;
  }

  decreaseQuantity() {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }

  loadEventiSimili(eventoId: number) {
    this.eventoSrv.getEventiSimili(eventoId).subscribe(
      data => this.eventiSimili = data,
      error => console.error('Error: ', error)
    );
  }

  loadArtistiCandidati(eventoId: number) {
    this.eventoSrv.getArtistiCandidati(eventoId).subscribe(
      data => {
        this.artistiOspiti = data;
        if (this.user) {
          this.isCandidato = data.some(artist => artist.id === this.user?.id);
        }
      },
      error => console.error('Error: ', error)
    );
  }

  candidatiArtista(): void {
    if (!this.evento || !this.user) {
      console.error('Evento o utente non trovato.');
      return;
    }

    const eventoId = this.evento.id;
    const artistaId = this.user.id;

    this.eventoSrv.nuovaCandidatura(eventoId, artistaId).subscribe(
      response => {
        console.log('Candidatura avvenuta con successo', response);
       alert("Candidatura inviata con successo.")
      },
      error => {
        console.error('Errore durante la candidatura', error);
      }
    );
  }

  calculateTotal(): number {
    return this.quantity * 10 + 2.90;
  }

  async pay(): Promise<void> {
    const amount = this.calculateTotal();
    const eventoId = this.evento?.id; // Assumi che evento non sia undefined
    if (!eventoId) {
      console.error('Evento non trovato.');
      return;
    }

    const userId = this.user?.id;
    if (userId === undefined) {
      console.error('Utente non trovato.');
      return;
    }

    const payment = {
      name: 'Carrello',
      currency: 'eur',
      amount: amount * 100,
      quantity: this.quantity,
      cancelUrl: 'http://localhost:4200/cancel',
      successUrl: `http://localhost:4200/success?eventoId=${eventoId}&quantita=${this.quantity}`,
    };

    const stripe = await this.stripePromise;

    if (!stripe) {
      console.error('Stripe non è stato caricato correttamente.');
      return;
    }

    this.http.post(`${this.apiUrl}api/payment`, payment).subscribe(async (data: any) => {
      const result = await stripe.redirectToCheckout({
        sessionId: data.id,
      });

      if (result.error) {
        console.error('Errore durante il redirect a Stripe:', result.error.message);
      }
    });
  }

}