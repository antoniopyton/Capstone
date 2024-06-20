import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { HomePageComponent } from './components/landing-page/home-page/home-page.component';
import { JumbotronComponent } from './components/landing-page/jumbotron/jumbotron.component';
import { FooterComponent } from './components/footer/footer.component';
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
import { RegisterArtistaComponent } from './components/clienti/register-artista/register-artista.component';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    HomePageComponent,
    JumbotronComponent,
    FooterComponent,
    DiscoveryComponent,
    PrimoPianoComponent,
    NomeProgettoComponent,
    ProssimiEventiComponent,
    StayTunedComponent,
    LoginComponent,
    RegisterComponent,
    RegisterArtistaComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule, FormsModule, HttpClientModule
  ],
  providers: [{
    provide: HTTP_INTERCEPTORS,
    useClass: TokenInterceptor,
    multi: true
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
