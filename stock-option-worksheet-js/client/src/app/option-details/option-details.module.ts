import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StockSummaryComponent } from './stock-summary/stock-summary.component';
import { OptionListComponent } from './option-list/option-list.component';
import { OptionDetailsScreenComponent } from './option-details-screen/option-details-screen.component';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [StockSummaryComponent, OptionListComponent, OptionDetailsScreenComponent]
})
export class OptionDetailsModule { }
