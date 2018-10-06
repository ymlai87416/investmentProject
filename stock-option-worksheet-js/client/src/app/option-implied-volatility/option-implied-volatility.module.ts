import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ImpliedVolatilitySummaryComponent } from './implied-volatility-summary/implied-volatility-summary.component';
import { ImpliedVolatilityGraphComponent } from './implied-volatility-graph/implied-volatility-graph.component';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [ImpliedVolatilitySummaryComponent, ImpliedVolatilityGraphComponent]
})
export class OptionImpliedVolatilityModule { }
