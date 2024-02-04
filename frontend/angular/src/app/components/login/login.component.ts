import { Component } from '@angular/core';
import {AuthenticationRequest} from "../../models/authentication-request";
import {AuthenticationService} from "../../services/authentication/authentication.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  authenticationRequest: AuthenticationRequest = {};
  errorMsg = '';

  constructor(
    private readonly authenticationService: AuthenticationService,
    private readonly router: Router
  ) {
  }

  login() {
    this.errorMsg = '';
    this.authenticationService
      .login(this.authenticationRequest)
      .subscribe({
        next: (authenticationResponse) => {
          localStorage.setItem('user', JSON.stringify(authenticationResponse));
          this.router.navigate(['customers']);
        },
        error: (error) => {
          if(error.error.statusCode === 401){
            this.errorMsg = 'Login and / or password is incorrect'
          }
        }
      })
  }
}
