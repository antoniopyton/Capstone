import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  [x: string]: any;
    register = false;
    constructor(private authSrv: AuthService, private router: Router) {}
    
    ngOnInit(): void {
     this.authSrv.user$.subscribe(user => {
      if (user){
        this.router.navigate(['/'])
      }
     })
       
    }
    onSubmit(form:NgForm) {
      try {
        this.authSrv.login(form.value).subscribe((data) => {
          this.router.navigate(['/'])
        });
      } catch (error) {
        console.log(error)
      }
    }
  
  
    @ViewChild('container') container!: ElementRef;
  
  
  signIn() {
    this.container.nativeElement.classList.remove('right-panel-active');
  }
  
  signUp() {
    this.container.nativeElement.classList.add('right-panel-active');
  }}
