import { Component, OnInit, Input } from '@angular/core';
import { IVSeries, Stock } from '../../option-result.model';
import { sprintf } from 'sprintf-js';
import { toPublicName } from '@angular/compiler/src/i18n/serializers/xmb';

@Component({
  selector: 'app-stock-summary',
  templateUrl: './stock-summary.component.html',
  styleUrls: ['./stock-summary.component.css']
})
export class StockSummaryComponent implements OnInit {
  
  @Input() stock: Stock;
  @Input() ivSeries: IVSeries;
  @Input() selectedDate: Date;

  constructor() { }

  ngOnInit() {
  }

  getStockName(stock: Stock): string{
    if(stock != null)
      return stock.name;
    else return 'N/A';
  }

  getFormattedDate(selectedDate: Date): string{
    if(selectedDate != null)
      return selectedDate.toLocaleDateString();
    else return "N/A";
  }

  formatNumber(num: number): string{
    if(num == null) return 'N/A';
    else return  sprintf("%.5f", num);
  }

  getCurrentPrice(stock: Stock, selectedDate: Date): number{
    if(stock != null && stock.historyList != null){
      let stockhist = stock.historyList.find(x => x.priceDate.getTime() == selectedDate.getTime());
      if(stockhist != null) return stockhist.adjClosePrice;
      else return null;
    }
    else return null;
  }

  getCurrentIV(ivseries: IVSeries, selectedDate: Date): number{
    //console.log(ivseries);
    if(ivseries != null){
      let tp = ivseries.timePointList.find(x => x.date.getTime() == selectedDate.getTime());
      if(tp != null) return tp.value;
      else return null;
    }
    else return null;
  }

  getStatsCount(ivseries: IVSeries): number{
    if(ivseries != null){
      return ivseries.timePointList.length;
    }
    else return null;
  }

  getStatsMax(ivseries: IVSeries): number{
    if(ivseries != null){
      let result= Math.max(...ivseries.timePointList.map(x=> x.value));
      return result;
    }
    else return null;
  }

  getStatsMin(ivseries: IVSeries): number{
    if(ivseries != null){
      let result= Math.min(...ivseries.timePointList.map(x=> x.value));
      return result;
    }
    else return null;
  }

  getStatsAvg(ivseries: IVSeries): number{
    if(ivseries != null){
      let avg = ivseries.timePointList.map(x=>x.value).reduce((prev, cur) => prev+cur) / ivseries.timePointList.length;
      return avg;
    }
    else return null;
  }

  getStatsStd(ivseries: IVSeries): number{
    if(ivseries != null){
      let avgInt = this.getStatsAvg(ivseries);
      let squareDiff = ivseries.timePointList.map(x => (x.value-avgInt) * (x.value-avgInt))
      let avgSquareDiff = squareDiff.reduce((prev, cur) => prev+cur) / squareDiff.length;
      let result = Math.sqrt(avgSquareDiff);

      return result;
    }
    else return null;
  }

}
