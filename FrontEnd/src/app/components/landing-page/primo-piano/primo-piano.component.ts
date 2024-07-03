import { Component, OnInit } from '@angular/core';
import { Utente } from 'src/app/interface/utente.interface';
import { ArtistiService } from 'src/app/service/artisti.service';

@Component({
  selector: 'app-primo-piano',
  templateUrl: './primo-piano.component.html',
  styleUrls: ['./primo-piano.component.scss']
})
export class PrimoPianoComponent implements OnInit {
artisti: Utente[] = [];
  constructor(private artistiSrv: ArtistiService) {}

  ngOnInit(): void {
    this.artistiSrv.getArtisti().subscribe((artists: Utente[]) => {
      this.artisti = artists.filter(artists => artists.avatar);
    });
  }

  
  

}
