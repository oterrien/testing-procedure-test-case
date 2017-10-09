import {Component, OnInit, Input} from '@angular/core';
import {UserService} from "./user.service";
import {Router, ActivatedRoute} from "@angular/router";
import {IUser} from "./user";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  errorMessage: string;
  user: IUser;

  constructor(private _route: ActivatedRoute,
              private _userService: UserService) {
  }

  ngOnInit(): void {
    const id = +this._route.snapshot.paramMap.get('id');
    this._userService.getUser(id)
      .subscribe(
        user=> this.user = user,
        error => this.errorMessage = <any>error);
  }

}
