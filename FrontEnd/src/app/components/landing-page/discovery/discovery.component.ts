import { Component, OnInit } from '@angular/core';
import { Brani } from 'src/app/interface/brani.interface';
import { AuthService } from 'src/app/service/auth.service';
import { BraniService } from 'src/app/service/brani.service';

@Component({
  selector: 'app-discovery',
  templateUrl: './discovery.component.html',
  styleUrls: ['./discovery.component.scss']
})
export class DiscoveryComponent implements OnInit {

  top5Brani: Brani[] = [];
  isLoggedIn = false; 

  constructor(private braniSrv: BraniService, private authSrv: AuthService) {}

  ngOnInit(): void {
    this.getTop5Brani();
    this.authSrv.user$.subscribe(data => {
      if (data?.user) {
        this.isLoggedIn = true
      } else {
        false
      }
    })
  }

  getTop5Brani(): void {
    this.braniSrv.getTop5BraniByAscolti().subscribe((brani: Brani[]) => {
      this.top5Brani = brani;
    });
  }

  playPause(id: number) {
    const audio = document.getElementById('top5song' + id) as HTMLAudioElement;

    if (audio.paused) {
      this.top5Brani.forEach(brano => {
        const otherAudio = document.getElementById('top5song' + brano.id) as HTMLAudioElement;
        if (otherAudio && otherAudio !== audio) {
          otherAudio.pause();
        }
      });

      audio.play();
      this.braniSrv.incrementaAscolti(id).subscribe({
        next: () => {
          console.log(`Incremented ascolti for song id: ${id}`);
        },
        error: (err) => {
          console.error(`Error incrementing ascolti for song id: ${id}`, err);
        }
      });
    } else {
      audio.pause();
    }
  }
}
