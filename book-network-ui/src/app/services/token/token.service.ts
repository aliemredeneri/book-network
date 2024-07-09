import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TokenService {


  get token(): string {
    return localStorage.getItem('token') as string;
  }

  set token(token: string) {
    localStorage.setItem('token', token);
  }
}
