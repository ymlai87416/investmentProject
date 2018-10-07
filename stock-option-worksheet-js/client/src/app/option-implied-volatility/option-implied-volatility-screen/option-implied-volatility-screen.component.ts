

import { Component, OnInit } from '@angular/core';
import { IVSeries, Stock } from '../../option-result.model';

@Component({
  selector: 'app-option-implied-volatility-screen',
  templateUrl: './option-implied-volatility-screen.component.html',
  styleUrls: ['./option-implied-volatility-screen.component.css']
})
export class OptionImpliedVolatilityScreenComponent implements OnInit {

  stock: Stock;
  relatedIVSeriesList: IVSeries[];
  searchDate: Date;
  error: boolean;

  constructor() {
    this.error = false;
    this.stock = null;
    this.relatedIVSeriesList = null;
    this.searchDate = null;
  }

  ngOnInit() {
  }

}
