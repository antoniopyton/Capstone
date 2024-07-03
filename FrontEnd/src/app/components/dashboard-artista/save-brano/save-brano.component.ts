import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { Utente } from 'src/app/interface/utente.interface';
import { FileUpload } from 'src/app/model/file-upload';
import { AuthService } from 'src/app/service/auth.service';
import { BraniService } from 'src/app/service/brani.service';
import { FileUploadService } from 'src/app/service/file-upload.service';

@Component({
  selector: 'app-save-brano',
  templateUrl: './save-brano.component.html',
  styleUrls: ['./save-brano.component.scss']
})
export class SaveBranoComponent {

  selectedFiles?: FileList;
  currentFileUpload?: FileUpload;
  percentage = 0;
  fileUrl: string = '';;
  artista: Utente | undefined;

  constructor(
    private authArtist: BraniService,
    private router: Router,
    private uploadSrv: FileUploadService,
    private authSrv: AuthService
  ) {
    this.authSrv.user$.subscribe(data => {
      this.artista = data?.user
    })
  }

  selectFile(event: any): void {
    this.selectedFiles = event.target.files;
  }

  upload(): void {
    if (this.selectedFiles) {
      const file: File | null = this.selectedFiles.item(0);
      this.selectedFiles = undefined;

      if (file) {
        this.currentFileUpload = new FileUpload(file);
        let test = this.uploadSrv.pushFileToStorage(this.currentFileUpload);
        
        test.percentageChanges().subscribe(
          percent => {
            this.percentage = Math.round(percent ? percent : 0);
          },
          error => {
            console.error(error);
          },
          () => {
            console.log(this.currentFileUpload?.url)
            test.then(async snapshot => {
              this.fileUrl = await snapshot.ref.getDownloadURL();
            })
          }
        );
      }
    }
  }

  onSubmitBrano(registerForm: NgForm): void {
    if (registerForm.valid) {
      console.log(this.fileUrl)
      const branoData = {
        titolo: registerForm.value.titoloRegister,
        genere: registerForm.value.genereRegister,
        fileUrl: this.fileUrl,
        durata: registerForm.value.durataRegister,
        artista: this.artista?.id
      };

      
      this.authArtist.saveBrano(branoData).subscribe(
        response => {
          console.log('Brano caricato con successo:', response);
          alert('Brano caricato con successo! Sarai reindirizzato alla homepage.');
          this.router.navigate(['/']);
        },
        error => {
          console.error('Errore nel caricamento del brano:', error);
        }
      );
    }
  }
}

