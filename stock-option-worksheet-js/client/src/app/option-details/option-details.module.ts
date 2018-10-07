import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StockSummaryComponent } from './stock-summary/stock-summary.component';
import { OptionListComponent } from './option-list/option-list.component';
import { OptionDetailsScreenComponent } from './option-details-screen/option-details-screen.component';
import { AppCommonModule} from '../common/common.module'
import { OptionQueryFormComponent } from '../common/option-query-form/option-query-form.component';

@NgModule({
  imports: [
    CommonModule,
    AppCommonModule,
  ],
  declarations: [
    StockSummaryComponent, 
    OptionListComponent, 
    OptionDetailsScreenComponent, 
    //OptionQueryFormComponent,
  ]
})
export class OptionDetailsModule { }
