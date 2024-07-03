import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { loadStripe } from '@stripe/stripe-js';
import { Eventi } from 'src/app/interface/eventi.interface';
import { EventiService } from 'src/app/service/eventi.service';
import { environment } from 'src/environments/environment.development';

@Component({
  selector: 'app-dettagli-evento',
  templateUrl: './dettagli-evento.component.html',
  styleUrls: ['./dettagli-evento.component.scss']
})
export class DettagliEventoComponent implements OnInit {
 
  evento: Eventi | undefined;
  quantity: number = 1;
  eventiSimili: Eventi[] | undefined;

  private apiUrl=environment.apiUrl;
  stripePromise = loadStripe(environment.stripe);

  constructor(
    private route: ActivatedRoute,
    private eventoSrv: EventiService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.eventoSrv.getEventoById(+id).subscribe(
        data => {
          this.evento = data;
          this.loadEventiSimili(data.id);
        },
        error => console.error('Error: ', error)
      );
    }
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

  calculateTotal(): number {
    return this.quantity * 10 + 2.90;
  }

  async pay(): Promise<void> {
    const amount = this.calculateTotal();

    const payment = {
      name: 'Carrello',
      currency: 'eur',
      amount: amount * 100,
      quantity: '1',
      cancelUrl: 'http://localhost:4200/cancel',
      successUrl: 'http://localhost:4200/success',
    };

    const stripe = await this.stripePromise;

    if (!stripe) {
      console.error('Stripe non è stato caricato correttamente.');
      return;
    }

    this.http.post(`${this.apiUrl}api/payment`, payment).subscribe((data: any) => {
      stripe.redirectToCheckout({
        sessionId: data.id,
      }).then((result) => {
        if (result.error) {
          console.error('Errore durante il redirect a Stripe:', result.error.message);
        }
      });
    });
  }
}