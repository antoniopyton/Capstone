import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { loadStripe } from '@stripe/stripe-js';
import { environment } from 'src/environments/environment.development';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss']
})
export class CheckoutComponent {

  private apiUrl=environment.apiUrl;

  stripePromise = loadStripe(environment.stripe);
  constructor(private http: HttpClient) {}

money = 100
  async pay(amount: number): Promise<void> {
    
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
   this.http.post(`${environment.apiUrl}api/payment`, payment).subscribe((data: any) => {
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