import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OptionQueryFormComponent } from './option-query-form/option-query-form.component';
import {SuiModule} from 'ng2-semantic-ui';
import { FormsModule } from '@angular/forms';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    SuiModule,
  ],
  exports: [OptionQueryFormComponent],
  declarations: [OptionQueryFormComponent]
})
export class AppCommonModule { }
