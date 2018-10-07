import { 
  Component,
  OnInit,
  Output,
  EventEmitter,
  ElementRef
} from '@angular/core';
import { DatepickerMode} from 'ng2-semantic-ui';

import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/switch';

import { StockOption } from '../../option-result.model';

@Component({
  selector: 'app-option-details-query-form',
  templateUrl: './option-details-query-form.component.html',
  styleUrls: ['./option-details-query-form.component.css']
})
export class OptionDetailsQueryFormComponent implements OnInit {
  @Output() loading: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() results: EventEmitter<StockOption[]> = new EventEmitter<StockOption[]>();

  mode: DatepickerMode;
  date: Date;

  constructor() {
    this.mode = DatepickerMode.Date;
   }

  ngOnInit() {
  }

}
