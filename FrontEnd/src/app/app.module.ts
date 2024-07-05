import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import {AngularFireModule} from '@angular/fire/compat';
import { AngularFirestoreModule } from '@angular/fire/compat/firestore';
import { AngularFireStorageModule } from '@angular/fire/compat/storage';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { HomePageComponent } from './components/landing-page/home-page/home-page.component';
import { JumbotronComponent } from './components/landing-page/jumbotron/jumbotron.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { DiscoveryComponent } from './components/landing-page/discovery/discovery.component';
import { PrimoPianoComponent } from './components/landing-page/primo-piano/primo-piano.component';
import { NomeProgettoComponent } from './components/landing-page/nome-progetto/nome-progetto.component';
import { ProssimiEventiComponent } from './components/landing-page/prossimi-eventi/prossimi-eventi.component';
import { StayTunedComponent } from './components/landing-page/stay-tuned/stay-tuned.component';
import { FormsModule } from '@angular/forms';
import { LoginComponent } from './components/login/login.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { TokenInterceptor } from './interceptor/token.interceptor';
import { RegisterComponent } from './components/register/register.component';
import { RegisterArtistaComponent } from './components/flusso-artista/register-artista/register-artista.component';
import { GoogleLoginComponent } from './components/google-login/google-login.component';
import { SocialLoginModule, SocialAuthServiceConfig } from '@abacritt/angularx-social-login';
import {
  GoogleLoginProvider,
  FacebookLoginProvider
} from '@abacritt/angularx-social-login';
import { UploadFormComponent } from './components/dashboard-artista/save-brano/upload-form/upload-form.component';
import { UploadListComponent } from './components/dashboard-artista/save-brano/upload-list/upload-list.component';
import { environment } from 'src/environments/environment.development';
import { RegisterArtista2Component } from './components/flusso-artista/register-artista2/register-artista2.component';
import { DashboardArtistaComponent } from './components/dashboard-artista/dashboard-artista.component';
import { SaveBranoComponent } from './components/dashboard-artista/save-brano/save-brano.component';
import { RegisterArtista3Component } from './components/flusso-artista/register-artista3/register-artista3.component';
import { ArtistiComponent } from './components/artisti/artisti.component';
import { EventiComponent } from './components/eventi/eventi.component';
import { ModaleArtistaComponent } from './components/modale-artista/modale-artista.component';
import { DettagliEventoComponent } from './components/dettagli-evento/dettagli-evento.component';
import { SuccessComponent } from './components/stripe/success/success.component';
import { CancelComponent } from './components/stripe/cancel/cancel.component';
import { CheckoutComponent } from './components/stripe/checkout/checkout.component';
import { ProfiloUtenteComponent } from './components/profilo-utente/profilo-utente.component';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    HomePageComponent,
    JumbotronComponent,
    DiscoveryComponent,
    PrimoPianoComponent,
    NomeProgettoComponent,
    ProssimiEventiComponent,
    StayTunedComponent,
    LoginComponent,
    RegisterComponent,
    RegisterArtistaComponent,
    GoogleLoginComponent,
    UploadFormComponent,
    UploadListComponent,
    RegisterArtista2Component,
    DashboardArtistaComponent,
    SaveBranoComponent,
    RegisterArtista3Component,
    ArtistiComponent,
    EventiComponent,
    ModaleArtistaComponent,
    DettagliEventoComponent,
    SuccessComponent,
    CancelComponent,
    CheckoutComponent,
    ProfiloUtenteComponent,
    
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule, FormsModule, HttpClientModule,
    SocialLoginModule,  AngularFireModule.initializeApp(environment.firebase),
    AngularFirestoreModule,
    AngularFireStorageModule
  ],
  providers: [{
    provide: HTTP_INTERCEPTORS,
    useClass: TokenInterceptor,
    multi: true
  }, {
    provide: 'SocialAuthServiceConfig',
    useValue: {
      autoLogin: false,
      providers: [
        {
          id: GoogleLoginProvider.PROVIDER_ID,
          provider: new GoogleLoginProvider(
            '287317140125-d3lkd2jkkspvhue01mq5g72cadktrpe2.apps.googleusercontent.com', {
            scopes: 'openid profile email',
          }
          )
        },
        {
          id: FacebookLoginProvider.PROVIDER_ID,
          provider: new FacebookLoginProvider('clientId')
        }
      ],
      onError: (err) => {
        console.error(err);
      }
    } as SocialAuthServiceConfig,
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
