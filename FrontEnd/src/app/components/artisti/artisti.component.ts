import { Component, OnInit } from '@angular/core';
import { Utente } from 'src/app/interface/utente.interface';
import { ArtistiService } from 'src/app/service/artisti.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ModaleArtistaComponent } from '../modale-artista/modale-artista.component';


@Component({
  selector: 'app-artisti',
  templateUrl: './artisti.component.html',
  styleUrls: ['./artisti.component.scss']
})
export class ArtistiComponent implements OnInit {
  artisti: Utente[] = [];
  avatar: string = "assets/omino.png"; 
  
  constructor(private artistiSrv: ArtistiService, private modalService: NgbModal) {}

  ngOnInit(): void {
    this.artistiSrv.getArtisti().subscribe((artists: Utente[]) => {
      this.artisti = artists;
    });
  }

  openModal(artistId: number): void {
    const modalRef = this.modalService.open(ModaleArtistaComponent);
    modalRef.componentInstance.artistaId = artistId;  
  }
}

