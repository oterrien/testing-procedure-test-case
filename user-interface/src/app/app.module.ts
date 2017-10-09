import {BrowserModule} from "@angular/platform-browser";
import {NgModule} from "@angular/core";
import {AppComponent} from "./app.component";
import {FormsModule} from "@angular/forms";
import {RouterModule} from "@angular/router";
import {AdminComponent} from "./admin/admin.component";
import {ProfileComponent} from "./profile/profile.component";
import {UserComponent} from './user/user.component';
import {UserService} from "./user/user.service";
import {HttpClientModule} from "@angular/common/http";

@NgModule({
  declarations: [
    AppComponent,
    AdminComponent,
    ProfileComponent,
    UserComponent],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    RouterModule.forRoot([
      {path: 'profile', component: ProfileComponent},
      {path: 'admin', component: AdminComponent},
      {path: 'user/:id', component: UserComponent},
      {path: '', redirectTo: 'profile', pathMatch: 'full'},
      {path: '**', redirectTo: 'profile', pathMatch: 'full'}
    ])
  ],
  providers: [UserService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
