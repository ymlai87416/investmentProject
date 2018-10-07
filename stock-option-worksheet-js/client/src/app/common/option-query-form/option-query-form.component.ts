import { Component, OnInit } from '@angular/core';
import { DatepickerMode} from 'ng2-semantic-ui'

@Component({
  selector: 'app-option-query-form',
  templateUrl: './option-query-form.component.html',
  styleUrls: ['./option-query-form.component.css']
})
export class OptionQueryFormComponent implements OnInit {
  mode: DatepickerMode;
  date: Date;

  constructor() {
    this.mode = DatepickerMode.Date;
   }

  ngOnInit() {
  }

}
