import {AuthenticationResponse} from "../models/authentication-response";
import {JwtHelperService} from "@auth0/angular-jwt";
import {Router} from "@angular/router";
import {inject} from "@angular/core";

export const authGuard = () => {
  console.log('authGuard#canActivate called');
  const router = inject(Router);
  const storedUser = localStorage.getItem('user');
  if(storedUser) {
    const authResponse: AuthenticationResponse = JSON.parse(storedUser);
    const token = authResponse.jwtToken;
    if(token) {
      const jwtHelper = new JwtHelperService();
      const isTokenNonExpired = !jwtHelper.isTokenExpired(token);
      if(isTokenNonExpired) {
        return true;
      }
    }
  }
  return router.parseUrl('/login');
};
