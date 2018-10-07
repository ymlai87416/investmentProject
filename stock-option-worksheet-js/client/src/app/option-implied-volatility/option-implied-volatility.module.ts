import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ImpliedVolatilitySummaryComponent } from './implied-volatility-summary/implied-volatility-summary.component';
import { ImpliedVolatilityGraphComponent } from './implied-volatility-graph/implied-volatility-graph.component';
import { OptionImpliedVolatilityScreenComponent } from './option-implied-volatility-screen/option-implied-volatility-screen.component';
import { AppCommonModule } from '../common/common.module';
import { OptionQueryFormComponent } from '../common/option-query-form/option-query-form.component';

@NgModule({
  imports: [
    CommonModule,
    AppCommonModule,
  ],
  declarations: [
    ImpliedVolatilitySummaryComponent, 
    ImpliedVolatilityGraphComponent, 
    OptionImpliedVolatilityScreenComponent,
    //OptionQueryFormComponent,
  ]
})
export class OptionImpliedVolatilityModule { }
