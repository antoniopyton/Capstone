import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Brani } from 'src/app/interface/brani.interface';
import { Eventi } from 'src/app/interface/eventi.interface';
import { Utente } from 'src/app/interface/utente.interface';
import { ArtistiService } from 'src/app/service/artisti.service';
import { BraniService } from 'src/app/service/brani.service';
import { EventiService } from 'src/app/service/eventi.service';

@Component({
  selector: 'app-modale-artista',
  templateUrl: './modale-artista.component.html',
  styleUrls: ['./modale-artista.component.scss']
})
export class ModaleArtistaComponent implements OnInit {
  @Input() artistaId: number | null = null;
  artista: Utente | null = null;
  brani: Brani[] = [];
  eventi: Eventi[] = [];

  constructor(
    private artistiService: ArtistiService,
    public activeModal: NgbActiveModal,
    private braniSrv: BraniService,
    private eventiSrv: EventiService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.artistaId !== null) {
      this.artistiService.getArtistaById(this.artistaId).subscribe((artist: Utente) => {
        this.artista = artist;
      });

      this.braniSrv.getBraniByArtistaId(this.artistaId).subscribe((brani: Brani[]) => {
        this.brani = brani;
      });

      this.eventiSrv.getEventiByArtista(this.artistaId).subscribe((eventi: Eventi[]) => {
        this.eventi = eventi;
      });
    }
  }

  goToEventDetails(eventoId: number): void {
    this.activeModal.close();
    this.router.navigate(['/eventi', eventoId]);
  }

  closeModal(): void {
    this.activeModal.close();
  }
}
