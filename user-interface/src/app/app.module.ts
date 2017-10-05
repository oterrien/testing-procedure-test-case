import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {HttpClientModule} from '@angular/common/http';
import {HttpModule}    from '@angular/http';
import {UserService} from './user.service';

@NgModule({
  declarations: [AppComponent],
  imports: [BrowserModule, HttpClientModule, HttpModule],
  providers: [UserService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
