import { Component } from '@angular/core';
import { FileUpload } from 'src/app/model/file-upload';
import { FileUploadService } from 'src/app/service/file-upload.service';

@Component({
  selector: 'app-upload-form',
  templateUrl: './upload-form.component.html',
  styleUrls: ['./upload-form.component.scss']
})
export class UploadFormComponent {
  selectedFiles?: FileList;
  currentFileUpload?: FileUpload;
  percentage = 0;

  constructor(private uploadService: FileUploadService) {}

  selectFile(event: any) : void {
    this.selectedFiles = event.target.files;
  }

  // upload() :void {
  //   if (this.selectedFiles) {
  //     const file: File | null = this.selectedFiles.item(0)
  //     this.selectedFiles = undefined;

  //     if (file) {
  //       this.currentFileUpload = new FileUpload(file);
  //       this.uploadService.pushFileToStorage(this.currentFileUpload).subscribe(percent => {
  //         this.percentage = Math.round(percent ? percent : 0);
  //       },
  //       error => {
  //         console.error(error);
  //       }
  //     )
  //     }
  //   }
  // }
}