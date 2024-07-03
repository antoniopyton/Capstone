import { Component } from '@angular/core';
import { Eventi } from 'src/app/interface/eventi.interface';
import { EventiService } from 'src/app/service/eventi.service';

@Component({
  selector: 'app-prossimi-eventi',
  templateUrl: './prossimi-eventi.component.html',
  styleUrls: ['./prossimi-eventi.component.scss']
})
export class ProssimiEventiComponent {

  prossimiEventi: Eventi[] = [];

  constructor(private eventoSrv: EventiService) { }

  ngOnInit(): void {
    this.eventoSrv.getProssimiEventi().subscribe(
      data => this.prossimiEventi = data,
      error => console.error('Error: ', error)
    );
  }
  

}
