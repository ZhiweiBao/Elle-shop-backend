import {Component, Inject, OnInit} from '@angular/core';
import {OKTA_AUTH} from "@okta/okta-angular";
import {OktaAuth} from '@okta/okta-auth-js';

@Component({
  selector: 'app-login-status',
  templateUrl: './login-status.component.html',
  styleUrls: ['./login-status.component.css']
})
export class LoginStatusComponent implements OnInit {
  isAuthenticated: boolean= false;
  userFullName: string | undefined = '';

  constructor(@Inject(OKTA_AUTH) private oktaAuthService: OktaAuth) {
  }

  ngOnInit(): void {
    this.oktaAuthService.authStateManager.subscribe(
      (result: boolean) => {
        this.isAuthenticated = result;
        this.getUserDetails();
      }
    );
  }

  private getUserDetails() {
    if (this.isAuthenticated) {
      this.oktaAuthService.getUser().then(
        res => {
          this.userFullName = res.name;
        }
      );
    }
  }

  logout() {
    this.oktaAuthService.signOut();
  }
}
