import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ImpliedVolatilitySummaryComponent } from './implied-volatility-summary/implied-volatility-summary.component';
import { ImpliedVolatilityGraphComponent } from './implied-volatility-graph/implied-volatility-graph.component';
import { OptionImpliedVolatilityScreenComponent } from './option-implied-volatility-screen/option-implied-volatility-screen.component';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [ImpliedVolatilitySummaryComponent, ImpliedVolatilityGraphComponent, OptionImpliedVolatilityScreenComponent]
})
export class OptionImpliedVolatilityModule { }
