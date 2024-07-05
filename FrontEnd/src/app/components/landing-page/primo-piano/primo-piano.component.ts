import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Utente } from 'src/app/interface/utente.interface';
import { ArtistiService } from 'src/app/service/artisti.service';
import { ModaleArtistaComponent } from '../../modale-artista/modale-artista.component';

@Component({
  selector: 'app-primo-piano',
  templateUrl: './primo-piano.component.html',
  styleUrls: ['./primo-piano.component.scss']
})
export class PrimoPianoComponent implements OnInit {
artisti: Utente[] = [];
  constructor(private artistiSrv: ArtistiService,  private modalService: NgbModal) {}

  ngOnInit(): void {
    this.artistiSrv.getArtisti().subscribe((artists: Utente[]) => {
      this.artisti = artists.filter(artists => artists.avatar);
    });
  }

  openModal(artistId: number): void {
    const modalRef = this.modalService.open(ModaleArtistaComponent);
    modalRef.componentInstance.artistaId = artistId;  
  }
  
}
