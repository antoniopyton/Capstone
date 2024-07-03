import { Component, OnInit, QueryList, Renderer2, ViewChildren } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { Brani } from 'src/app/interface/brani.interface';
import { Utente } from 'src/app/interface/utente.interface';
import { FileUpload } from 'src/app/model/file-upload';
import { ArtistiService } from 'src/app/service/artisti.service';
import { AuthService } from 'src/app/service/auth.service';
import { BraniService } from 'src/app/service/brani.service';
import { FileUploadService } from 'src/app/service/file-upload.service';

@Component({
  selector: 'app-dashboard-artista',
  templateUrl: './dashboard-artista.component.html',
  styleUrls: ['./dashboard-artista.component.scss']
})
export class DashboardArtistaComponent implements OnInit {

  user: Utente | undefined = undefined;
  previewUrl: string | ArrayBuffer | null = "assets/omino.png";
  descrizione: string | undefined = "Inserisci subito una descrizione";
  coverFile?: File;
  coverUrl: string = '';
  selectedFiles?: FileList;
  currentFileUpload?: FileUpload;
  percentage = 0;
  fileUrl: string = '';
  artista: Utente | undefined;
  ascoltiSettimanali: number = 0;
  ascoltiMensili: number = 0;
  ascoltiGiornalieri: number = 0;

  constructor(
    private authSrv: AuthService,
    private artistiSrv: ArtistiService,
    private braniSrv: BraniService,
    private router: Router,
    private uploadSrv: FileUploadService,
    private renderer: Renderer2
  ) {
    this.authSrv.user$.subscribe(data => {
      this.artista = data?.user;
    });
  }

  ngOnInit(): void {
    this.authSrv.user$.subscribe((data) => {
      this.user = data?.user;
      this.previewUrl = this.user?.avatar || "assets/img/ominoverde.png";
      if (this.user) {
        this.getBrani(this.user.id);
        this.getAscolti(this.user.id)
      }
    });
  }

  getBrani(userId: number): void {
    this.artistiSrv.getBrani(userId).subscribe((brani: Brani[]) => {
      if (this.user) {
        this.user.brani = brani;
      }
    });
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

            this.artistiSrv.updateAvatar(this.user.id, formData).subscribe((data) => {
              this.authSrv.updateUser(data);
            });
          }
        }
      };

      reader.readAsDataURL(file);
    }
  }

  selectFile(event: any, type: 'brano' | 'cover'): void {
    if (type === 'brano') {
      this.selectedFiles = event.target.files;
    } else if (type === 'cover') {
      const input = event.target as HTMLInputElement;
      if (input.files && input.files[0]) {
        this.coverFile = input.files[0];
        this.uploadCover();
      }
    }
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
            test.then(async snapshot => {
              this.fileUrl = await snapshot.ref.getDownloadURL();
            });
          }
        );
      }
    }
  }

  uploadCover(): void {
    if (this.coverFile) {
      const fileUpload = new FileUpload(this.coverFile);
      let test = this.uploadSrv.pushFileToStorage(fileUpload);

      test.percentageChanges().subscribe(
        percent => {
          this.percentage = Math.round(percent ? percent : 0);
        },
        error => {
          console.error(error);
        },
        () => {
          test.then(async snapshot => {
            this.coverUrl = await snapshot.ref.getDownloadURL();
          });
        }
      );
    }
  }

  onSubmitBrano(registerForm: NgForm): void {
    if (registerForm.valid) {
      const branoData = {
        titolo: registerForm.value.titoloRegister,
        genere: registerForm.value.genereRegister,
        fileUrl: this.fileUrl,
        durata: registerForm.value.durataRegister,
        artista: this.artista?.id,
        copertina: this.coverUrl
      };

      this.braniSrv.saveBrano(branoData).subscribe(
        response => {
          console.log('Brano caricato con successo:', response);
          alert('Brano caricato con successo!');
          document.getElementById('closeModalButton')?.click();  
          this.router.navigate(['/dashboardArtista']).then(() => {
            window.location.reload();  
          });
        },
        error => {
          console.error('Errore nel caricamento del brano:', error);
        }
      );
    }
  }

  getAscolti(artistaId: number): void {
   
    this.artistiSrv.getAscoltiSettimana(artistaId).subscribe((ascolti) => {
      console.log(ascolti)
      this.ascoltiSettimanali = ascolti;
    });
    this.artistiSrv.getAscoltiMese(artistaId).subscribe((ascolti) => {
      this.ascoltiMensili = ascolti;
    });
    this.artistiSrv.getAscoltiGiorno(artistaId).subscribe((ascolti) => {
      this.ascoltiGiornalieri = ascolti;
    });
  }
  


  triggerFileInput(): void {
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    fileInput.click();
  }

  updatePassword(): void {
    try {
      const newPassword = (document.getElementById('newPassword') as HTMLInputElement).value;
      if (this.user && typeof this.user.id === 'number' && newPassword) {
        this.artistiSrv.updateUser(this.user.id, { password: newPassword }).subscribe(
          (response) => {
            window.alert('Password aggiornata con successo');
            this.authSrv.logout();
          },
          (error) => {
            console.error('Errore durante l\'aggiornamento della password', error);
          }
        );
      }
    } catch (error) {
      console.error(error);
    }
  }

  playPause(id: number) {
    const song = document.getElementById('song' + id) as HTMLAudioElement | null;
    
    if (song) {
      if (song.paused) {
        song.play();
        console.log(`Playing song with id: ${id}`);
        this.braniSrv.incrementaAscolti(id).subscribe({
          next: () => {
            console.log(`Incremented ascolti for song id: ${id}`);
            if (this.user?.id !== undefined) {
              this.getAscolti(this.user.id);
            }
          },
          error: (err) => {
            console.error(`Error incrementing ascolti for song id: ${id}`, err);
          }
        });
      } else {
        song.pause();
        console.log(`Paused song with id: ${id}`);
      }
    } else {
      console.error(`Song element with id 'song${id}' not found`);
    }
  }
}
