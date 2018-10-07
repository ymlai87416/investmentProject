import { Component, OnInit } from '@angular/core';
import { StockOptionHistory, Stock, StockStatistics} from '../../option-result.model';

@Component({
  selector: 'app-option-details-screen',
  templateUrl: './option-details-screen.component.html',
  styleUrls: ['./option-details-screen.component.css']
})
export class OptionDetailsScreenComponent implements OnInit {
  stock: Stock;
  stockStats: StockStatistics;
  relatedOptionList: StockOptionHistory[];
  searchDate: Date;
  error: boolean;

  constructor() {
    this.error = false;
    this.stockStats = null;
    this.stock = null;
    this.relatedOptionList = null;
    this.searchDate = null;
  }

  ngOnInit() {
  }

}
