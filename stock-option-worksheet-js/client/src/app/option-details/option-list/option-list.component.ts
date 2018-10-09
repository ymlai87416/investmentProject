import { Component, OnInit, Input } from '@angular/core';
import { StockOption } from '../../option-result.model';
import * as moment from 'moment';

@Component({
  selector: 'app-option-list',
  templateUrl: './option-list.component.html',
  styleUrls: ['./option-list.component.css']
})
export class OptionListComponent implements OnInit {

  @Input() 
  set stockOptionList(stockOptionList: StockOption[]){
    if(stockOptionList != null){
      let callStockOptionList = stockOptionList.filter(x => x.optionType = 'C');
      let putStockOptionList = stockOptionList.filter(x => x.optionType = 'P');
      this.priceList = stockOptionList.map(x => x.dateTime).filter((v, i, a) => a.indexOf(v) === i); 
      this.callMonthList = callStockOptionList.map(x => this.formatOptionDate(x.dateTime)).filter((v, i, a) => a.indexOf(v) === i); 
      this.putMonthList = putStockOptionList.map(x => this.formatOptionDate(x.dateTime)).filter((v, i, a) => a.indexOf(v) === i);
    }
  }

  priceList: Date[];
  callMonthList: string[];
  putMonthList: string[];
  callPrice: number[][];
  
  constructor() { }

  ngOnInit() {
  }

  formatOptionDate(date: Date){
    return moment(date).format('MMMYY');
  }

}
