import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomePageComponent } from './components/landing-page/home-page/home-page.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { RegisterArtistaComponent } from './components/flusso-artista/register-artista/register-artista.component';
import { UploadFormComponent } from './components/dashboard-artista/save-brano/upload-form/upload-form.component';
import { DashboardArtistaComponent } from './components/dashboard-artista/dashboard-artista.component';
import { AuthGuard } from './guard/auth.guard';
import { SaveBranoComponent } from './components/dashboard-artista/save-brano/save-brano.component';
import { RegisterArtista2Component } from './components/flusso-artista/register-artista2/register-artista2.component';
import { RegisterArtista3Component } from './components/flusso-artista/register-artista3/register-artista3.component';
import { ArtistiComponent } from './components/artisti/artisti.component';
import { ArtistaGuard } from './guard/artista.guard';
import { DettagliEventoComponent } from './components/dettagli-evento/dettagli-evento.component';
import { CheckoutComponent } from './components/stripe/checkout/checkout.component';
import { SuccessComponent } from './components/stripe/success/success.component';
import { CancelComponent } from './components/stripe/cancel/cancel.component';
import { ProfiloUtenteComponent } from './components/profilo-utente/profilo-utente.component';

const routes: Routes = [
  {
    path:"",
    component: HomePageComponent
  },
  {
    path:"login",
    component: LoginComponent
  },
  {
    path:"register",
    component: RegisterComponent
  },
  {
    path:"artisti",
    component: ArtistiComponent
  },
  {
    path:"checkout",
    component: CheckoutComponent
  },
  {
    path:"success",
    component: SuccessComponent
  },
  {
    path:"cancel",
    component: CancelComponent
  },
  {
    path:"eventi/:id",
    component: DettagliEventoComponent
  },
  {
    path:"registerr",
    component: RegisterArtistaComponent, 
    children: [
      {
        path: '',  
        component: RegisterArtista2Component
      },
      {
        path: 'registerr2',  
        component: RegisterArtista3Component
      } ]
  },
  {
    path:"upload",
    component: UploadFormComponent
  },
  {
    path:"dashboardArtista",
    component: DashboardArtistaComponent,
    canActivate: [AuthGuard, ArtistaGuard] 
  },
  {
    path:"caricaBrano",
    component: SaveBranoComponent,
    canActivate: [AuthGuard, ArtistaGuard]
  },
  {
    path:"profilo",
    component: ProfiloUtenteComponent,
    canActivate: [AuthGuard] 
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
