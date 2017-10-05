import 'rxjs/add/operator/map'

import {Injectable} from '@angular/core';
//import {HttpClient} from '@angular/common/http';

import {Http, Response, Headers} from '@angular/http';
import {User} from './user'

@Injectable()
export class UserService {

  constructor(private http: Http) {
  }

  private actionUrl: string = 'http://localhost:8080/api/v1/ping/';

  public data: any = null;

  public ping() {
    return this.http.get(this.actionUrl).map((res: Response) => res.text()).subscribe(data => {
      this.data = data;
      console.log("Ping result = " + this.data);
    });
  }

  user: User = {
    "id": 1,
    "login": "root",
    "password": {
      "value": "XohImNooBHFR0OVvjcYpJ3NgPQ1qq73WKhHvch0VQtg=",
      "encoded": true
    },
    "roles": [
      "ADMIN"
    ]
  };

}
