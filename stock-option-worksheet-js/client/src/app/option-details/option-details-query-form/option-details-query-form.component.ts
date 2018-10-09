import {
  Component,
  OnInit,
  Output,
  EventEmitter,
  ElementRef,
  ViewChild
} from '@angular/core';
import { DatepickerMode } from 'ng2-semantic-ui';

import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/switch';
import { Observable } from 'rxjs/Rx';

import { StockOption, StockOptionUnderlyingAsset, Stock, StockStatistics, IVSeries } from '../../option-result.model';
import { OptionService } from '../../option-service.service';

@Component({
  selector: 'app-option-details-query-form',
  templateUrl: './option-details-query-form.component.html',
  styleUrls: ['./option-details-query-form.component.css']
})
export class OptionDetailsQueryFormComponent implements OnInit {
  @Output() loading: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() stockOptionList: EventEmitter<StockOption[]> = new EventEmitter<StockOption[]>();
  @Output() ivSeries: EventEmitter<IVSeries> = new EventEmitter<IVSeries>();
  @Output() stock: EventEmitter<Stock> = new EventEmitter<Stock>();
  @Output() selectedDate: EventEmitter<Date> = new EventEmitter<Date>();
  @Output() error: EventEmitter<boolean> = new EventEmitter<boolean>();

  mode: DatepickerMode;
  date: Date;
  underlyingAssetList: StockOptionUnderlyingAsset[];
  selectedAsset: StockOptionUnderlyingAsset;

  @ViewChild('search') button;

  constructor(
    private optionService: OptionService, ) {
    this.mode = DatepickerMode.Date;
    this.underlyingAssetList = null;
    this.selectedAsset = null;
  }

  ngOnInit() {
    console.log(this.button);

    this.optionService.getUnderlyingAssetList().subscribe(
      (results: StockOptionUnderlyingAsset[]) => { // on sucesss
        this.loading.emit(false);
        this.underlyingAssetList = results;
        this.selectedAsset = this.underlyingAssetList[0];
      },
      (err: any) => { // on error
        console.log(err);
        this.loading.emit(false);
      },
      () => { // on completion
        this.loading.emit(false);
      }

    )

    Observable.fromEvent(this.button.nativeElement, 'click')
      .map((e: any) => { let obj = { asset: this.selectedAsset, date: this.date }; return obj; }) // extract the value of the input
      .filter(query => query["asset"] != null && query["date"] != null) // filter out if empty
      .debounceTime(250)                         // only once every 250ms
      .do(() => this.loading.emit(true))         // enable loading
      // search, discarding old events if new input comes in
      .map((query) => {
        console.log("Button clicked!");
        let asset = query["asset"] as StockOptionUnderlyingAsset;
        let date = query["date"] as Date
        var sDate = new Date(date);
        sDate.setMonth(sDate.getMonth() - 3)
        let stockListOb = this.optionService.searchStockBySehkCode(asset.ticker, date, date);
        let stockOptionListOb = this.optionService.searchStockOptionBySehkCode(asset.ticker, date, date);
        let ivSeriesListOb = this.optionService.searchIvSeriesBySehkCode(asset.ticker, sDate, date);
        let dateOb = Observable.of(date);
        let result = Observable.zip(stockOptionListOb, ivSeriesListOb, dateOb, stockListOb,
          (a, b, c, d) => {
            let combine = { stockOptionList: a, ivSeriesList: b, queryDate: c, stockList: d };
            return combine;
          }
        );

        return result;
      })
      .switch()
      // act on the return of the search
      .subscribe(
        (results) => { // on sucesss
          this.loading.emit(false);
          let stockList = results["stockList"] as Stock[];
          let stockOptionList = results["stockOptionList"] as StockOption[];
          let ivSeriesList = results["ivSeriesList"] as IVSeries[];
          let queryDate = results["queryDate"] as Date;
          let stock;
          let ivSeries;

          if (stockList != null && stockList.length > 0) stock = stockList[0];
          else stock = null;

          if (ivSeriesList != null && ivSeriesList.length > 0) ivSeries = ivSeriesList.find(x => x.seriesName.includes("IV"));
          else ivSeries = null;

          if (stockOptionList != null && queryDate != null
            && ivSeries != null && stock != null) {
            this.stock.emit(stock);
            this.stockOptionList.emit(stockOptionList);
            this.ivSeries.emit(ivSeries);
            this.selectedDate.emit(queryDate);
            this.error.emit(false);
          }
          else {
            this.stock.emit(null);
            this.stockOptionList.emit(null);
            this.ivSeries.emit(null);
            this.selectedDate.emit(null);
            this.error.emit(true);
          }

          console.log(results);
        },
        (err: any) => { // on error
          console.log(err);
          this.loading.emit(false);
          this.loading.emit(true);
        },
        () => { // on completion
          this.loading.emit(false);
        }
      );
  }

}