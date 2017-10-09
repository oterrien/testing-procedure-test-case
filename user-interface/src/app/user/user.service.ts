import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {Observable} from "rxjs/Observable";
import 'rxjs/add/operator/map';
import 'rxjs/add/observable/throw';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/catch';

import {IUser} from './user'

@Injectable()
export class UserService {

  constructor(private http: HttpClient) {
  }

  public getUsers(): Observable<IUser[]> {
    return this.http.get<IUser[]>('http://localhost:8080/api/v1/test/users')
      .do(data => console.log('DATA: ' + JSON.stringify(data)))
      .catch(this.handleError);
  }

  public getUser(id: number): Observable<IUser> {
    return this.http.get<IUser[]>('http://localhost:8080/api/v1/test/users/' + id)
      .do(data => console.log('DATA: ' + JSON.stringify(data)))
      .catch(this.handleError);
  }

  private handleError(err: HttpErrorResponse) {
    let errorMessage = '';
    if (err.error instanceof Error) {
      errorMessage = 'An error occurred: ${err.error.message}';
    } else {
      errorMessage = 'Server returned code: ${err.status}, error message is: ${err.message}';
    }
    console.error(errorMessage);
    return Observable.throw(errorMessage);

  }
}
