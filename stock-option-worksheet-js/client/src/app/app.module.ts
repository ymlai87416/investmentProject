import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { HeaderItemComponent } from './header/header-item.component';
import { HomeScreenComponent } from './home-screen/home-screen/home-screen.component';
import { OptionDetailsScreenComponent } from './option-details/option-details-screen/option-details-screen.component';
import { OptionImpliedVolatilityScreenComponent } from './option-implied-volatility/option-implied-volatility-screen/option-implied-volatility-screen.component';
import {
  RouterModule,
  Routes,
  Router
} from '@angular/router';
import {
  APP_BASE_HREF,
  LocationStrategy,
  HashLocationStrategy
} from '@angular/common';

import { ScreenDef } from './screen.model';
import { HomeScreenModule } from './home-screen/home-screen.module';
import { OptionDetailsModule } from './option-details/option-details.module';
import { OptionImpliedVolatilityModule } from './option-implied-volatility/option-implied-volatility.module';

const screens: ScreenDef[] = [
  {label: 'Home',                            name: 'Root',                       path: '',                       component: HomeScreenComponent},
  {label: 'Option details',                  name: 'Option details',             path: 'option-details',         component: OptionDetailsScreenComponent },
  {label: 'Option implied volatility',       name: 'Option IV',                  path: 'option-iv',              component: OptionImpliedVolatilityScreenComponent},
];

const routes: Routes = [
  { path: '', component: HomeScreenComponent, pathMatch: 'full' },
  { path: 'option-details', component: OptionDetailsScreenComponent, pathMatch: 'full' },
  { path: 'option-iv', component: OptionImpliedVolatilityScreenComponent, pathMatch: 'full' },
]

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    HeaderItemComponent,
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    HttpModule,
    FormsModule,
    HomeScreenModule,
    OptionDetailsModule,
    OptionImpliedVolatilityModule,
  ],
  providers: [
    { provide: APP_BASE_HREF, useValue: '/' },
    { provide: LocationStrategy, useClass: HashLocationStrategy },
    { provide: 'ScreenDefs',    useValue: screens }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
