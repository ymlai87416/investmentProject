import { 
  Component,
  OnInit,
  Output,
  EventEmitter,
  ElementRef,
  Inject,
  ViewChild
} from '@angular/core';
import { DatepickerMode} from 'ng2-semantic-ui';

import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/switch';
import { Observable } from 'rxjs/Rx';

import { IVSeries, StockOptionUnderlyingAsset, Stock, StockStatistics } from '../../option-result.model';
import { OptionService } from '../../option-service.service';

@Component({
  selector: 'app-option-iv-query-form',
  templateUrl: './option-iv-query-form.component.html',
  styleUrls: ['./option-iv-query-form.component.css']
})
export class OptionIvQueryFormComponent implements OnInit {
  @Output() loading: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() ivseriesResult: EventEmitter<IVSeries[]> = new EventEmitter<IVSeries[]>();
  @Output() priceResult: EventEmitter<Stock> = new EventEmitter<Stock>();
  @Output() statsResult: EventEmitter<StockStatistics> = new EventEmitter<StockStatistics>();
 
  mode: DatepickerMode;
  date: Date;
  underlyingAssetList: StockOptionUnderlyingAsset[];
  selectedAsset: StockOptionUnderlyingAsset;

  @ViewChild('button') button;

  constructor(
    private optionService: OptionService,) {
    this.mode = DatepickerMode.Date;
    this.underlyingAssetList = null;
    this.selectedAsset = null;
  }

  ngOnInit() {
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
      .map((e: any) => { let obj = {asset: this.selectedAsset, date: this.date}; return obj;}) // extract the value of the input
      .filter(query => query["asset"] != null && query["date"] != null ) // filter out if empty
      .debounceTime(250)                         // only once every 250ms
      .do(() => this.loading.emit(true))         // enable loading
      // search, discarding old events if new input comes in
      .map((query: string) => {
      
        this.optionService.search(query)
      })
      .switch()
      // act on the return of the search
      .subscribe(
        (results: SearchResult[]) => { // on sucesss
          this.loading.emit(false);
          this.results.emit(results);
        },
        (err: any) => { // on error
          console.log(err);
          this.loading.emit(false);
        },
        () => { // on completion
          this.loading.emit(false);
        }
      );
  }



  

}
