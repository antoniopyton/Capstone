import { Injectable } from '@angular/core';
import { AngularFireStorage, AngularFireUploadTask } from '@angular/fire/compat/storage';
import { FileUpload } from '../model/file-upload';
import { Observable, finalize } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FileUploadService {
  private basePath = "/uploads";

  constructor(private storage: AngularFireStorage) { }

  pushFileToStorage(fileUpload: FileUpload) : AngularFireUploadTask {
    const filePath = `${this.basePath}/${fileUpload.file.name}`;
    const storageRef = this.storage.ref(filePath);
    const uploadTask = this.storage.upload(filePath, fileUpload.file);

    uploadTask.snapshotChanges().pipe(
      finalize(() => {
        storageRef.getDownloadURL().subscribe(downloadURL => {
          fileUpload.url = downloadURL;
          fileUpload.name = fileUpload.file.name;
          console.log(fileUpload)
        })
      })
    ).subscribe();

    return uploadTask;
  }

  getFileFromStorage(nomeFile: String): {name?: String, videolink?: string} {
    const filePath = `${this.basePath}/${nomeFile}`;
    const storageRef = this.storage.ref(filePath);
    let fileList: {name?: String, videolink?: string} = {};
    let url = storageRef.getDownloadURL().subscribe((data2) => {
      fileList.name = nomeFile,
      fileList.videolink = data2
    })
    return fileList;
  }
}