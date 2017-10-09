import {Component, OnInit} from '@angular/core';
import {UserService} from "../user/user.service";
import {IUser} from "../user/user";

@Component({
  selector: 'app-admin',
  templateUrl: 'admin.component.html',
  styleUrls: ['admin.component.css']
})
export class AdminComponent implements OnInit {

  users: IUser[] = [];
  errorMessage: string;

  constructor(private _userService: UserService) {
  }

  ngOnInit(): void {
    this._userService
      .getUsers()
      .subscribe(
        users => this.users = users,
        error => this.errorMessage = <any>error);
  }

}
