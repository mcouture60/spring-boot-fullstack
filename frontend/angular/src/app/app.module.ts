import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {InputTextModule} from "primeng/inputtext";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {CustomerComponent} from "./components/customer/customer.component";
import {HeaderBarComponent} from "./components/header-bar/header-bar.component";
import {LoginComponent} from "./components/login/login.component";
import {ManageCustomerComponent} from "./components/manage-customer/manage-customer.component";
import {MenuBarComponent} from "./components/menu-bar/menu-bar.component";
import {MenuItemComponent} from "./components/menu-item/menu-item.component";
import {FormsModule} from "@angular/forms";
import {AvatarModule} from "primeng/avatar";
import {ButtonModule} from "primeng/button";
import {RippleModule} from "primeng/ripple";
import {MenuModule} from "primeng/menu";
import {SidebarModule} from "primeng/sidebar";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {MessageModule} from "primeng/message";
import {HttpInterceptorService} from "./services/interceptor/http-interceptor.service";
import {CustomerCardComponent} from "./components/customer-card/customer-card.component";
import {ToastModule} from "primeng/toast";
import {ConfirmationService, MessageService} from "primeng/api";
import {ConfirmDialogModule} from "primeng/confirmdialog";

@NgModule({
  declarations: [
    AppComponent,
    CustomerComponent,
    HeaderBarComponent,
    LoginComponent,
    ManageCustomerComponent,
    MenuBarComponent,
    MenuItemComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    FormsModule,
    InputTextModule,
    AvatarModule,
    ButtonModule,
    RippleModule,
    MenuModule,
    SidebarModule,
    HttpClientModule,
    MessageModule,
    CustomerCardComponent,
    ToastModule,
    ConfirmDialogModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpInterceptorService,
      multi: true
    },
    MessageService,
    ConfirmationService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
