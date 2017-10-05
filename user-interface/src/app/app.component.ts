import {Component, OnInit} from '@angular/core';
import {UserService} from './user.service';
import {User} from './user'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  constructor(private userService: UserService) {

  }

  private value: string = null;

  currentUser: User = null;

  ngOnInit(): void {
    console.log(this.userService.user);
    this.currentUser = this.userService.user;

    this.userService.ping();

  }
}
