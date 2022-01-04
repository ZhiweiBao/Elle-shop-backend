import {Inject, Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {from, Observable} from "rxjs";
import {OktaAuth} from "@okta/okta-auth-js";
import {OKTA_AUTH} from "@okta/okta-angular";

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptorService implements HttpInterceptor {

  constructor(@Inject(OKTA_AUTH) private oktaAuth: OktaAuth) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // @ts-ignore
    return from(this.handleAccess(req, next));
  }

  private async handleAccess(req: HttpRequest<any>, next: HttpHandler): Promise<HttpEvent<any>> {
    // Add an access token for secured endpoints
    const securedEndpoints = ['http://localhost:8080/api/orders']

    if (securedEndpoints.some(url => req.urlWithParams.includes(url))) {
      // get the access token
      const accessToken = await this.oktaAuth.getAccessToken();

      // clone the request and add new header with access token
      req = req.clone({
        setHeaders: {
          Authorization: 'Bearer ' + accessToken
        }
      });
    }

    // @ts-ignore
    return next.handle(req).toPromise();
  }
}
