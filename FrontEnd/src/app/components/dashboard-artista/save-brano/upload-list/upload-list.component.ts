import { Component } from '@angular/core';
import { FileUploadService } from 'src/app/service/file-upload.service';

@Component({
  selector: 'app-upload-list',
  templateUrl: './upload-list.component.html',
  styleUrls: ['./upload-list.component.scss']
})
export class UploadListComponent {
  link: any = "";
  constructor(private service: FileUploadService) {
    this.link = service.getFileFromStorage("Un altro sogno MST V3.mp3")
  }
}