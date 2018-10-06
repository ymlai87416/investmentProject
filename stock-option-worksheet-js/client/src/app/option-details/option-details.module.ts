import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StockSummaryComponent } from './stock-summary/stock-summary.component';
import { OptionListComponent } from './option-list/option-list.component';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [StockSummaryComponent, OptionListComponent]
})
export class OptionDetailsModule { }
