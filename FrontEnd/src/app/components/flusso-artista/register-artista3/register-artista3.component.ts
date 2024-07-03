import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-register-artista3',
  templateUrl: './register-artista3.component.html',
  styleUrls: ['./register-artista3.component.scss']
})
export class RegisterArtista3Component implements OnInit, OnDestroy {
  selectedFiles?: FileList;
  userId: number | null = null; 
  private userSubscription: Subscription | null = null;

  constructor(private authSrv: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.userSubscription = this.authSrv.user$.subscribe(user => {
      if (user) {
        this.userId = user.user.id;
      }
    });
  }

  ngOnDestroy(): void {
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  onSubmitDati(form: NgForm) {
    if (this.userId === null) {
      console.error("User ID non trovato");
      return;
    }

    if (this.selectedFiles) {
      const file: File | null = this.selectedFiles.item(0);
      if (file) {
        this.authSrv.uploadFile(file).subscribe(response => {
          console.log("File uploaded successfully!");
          this.registerDescription(form);
        }, error => {
          console.error("Error uploading file", error);
        });
      }
    } else {
      this.registerDescription(form);
    }
  }

  registerDescription(form: NgForm) {
  if (this.userId === null) {
    console.error("User ID non trovato");
    return;
  }
  
  const value = { descrizioneArtista: form.value.descrizioneRegister };
  this.authSrv.registerArtist2(this.userId, value).subscribe(data => {
    window.alert("Descrizione registrata con successo");
    this.router.navigate(["/dashboardArtista"]);
  }, error => {
    console.error("Error registering description", error);
  });
}

  selectFile(event: any): void {
    this.selectedFiles = event.target.files;
  }

  skipToDashboard(): void {
    this.router.navigate(['/']);
  }
}