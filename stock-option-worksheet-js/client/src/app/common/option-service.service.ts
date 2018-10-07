import { Injectable, Inject } from '@angular/core';
import {
  HttpClient,
  HttpRequest,
  HttpHeaders
} from '@angular/common/http';
import { Observable } from 'rxjs/Rx';

import {
  Stock, StockOption, StockOptionHistory,
  StockOptionUnderlyingAsset,
  IVSeries, IVSeriesTimePoint,
  StockOptionUnderlyingAsset,
} from  './option-result.model';

export const WEBSERVICE_ROOT = 'http://localhost:8080'
export const SEARCH_STOCK_BY_SEHK_CODE_URL = WEBSERVICE_ROOT + '/stock/{0}';
export const SEARCH_STOCK_BY_SEHK_CODE_WITH_PARAM_URL = WEBSERVICE_ROOT + '/stock/{0}?startDate={1}&endDate={2}';
export const SEARCH_STOCK_OPTION_BY_SEHK_CODE_URL = WEBSERVICE_ROOT + '/stockOption/sehk/{0}';
export const SEARCH_STOCK_OPTION_BY_SEHK_CODE_WITH_PARAM_URL = WEBSERVICE_ROOT + '/stockOption/sehk/{0}?startDate={1}&endDate={2}';
export const SEARCH_STOCK_OPTION_BY_TICKER_URL = WEBSERVICE_ROOT + '/stockOption/code/{0}';
export const SEARCH_STOCK_OPTION_BY_TICKER_WITH_PARAM_URL = WEBSERVICE_ROOT + '/stockOption/code/{0}?startDate={1}&endDate={2}';
export const GET_UNDERLYING_ASSET_LIST_URL = WEBSERVICE_ROOT + '/stockOption/underlyingAsset';
export const SEARCH_IV_SERIES_BY_SEHK_CODE_URL = WEBSERVICE_ROOT + '/ivseries/{0}';
export const SEARCH_IV_SERIES_BY_SEHK_CODE_WITH_PARAM_URL = WEBSERVICE_ROOT + '/ivseries/{0}?startDate={1}&endDate={2}';

@Injectable({
  providedIn: 'root'
})
export class OptionServiceService {

  constructor(
    private http: HttpClient,
    @Inject(WEBSERVICE_ROOT) private webServiceRoot: string,
    @Inject(SEARCH_STOCK_BY_SEHK_CODE_URL) private searchStockBySehkCodeUrl: string,
    @Inject(SEARCH_STOCK_BY_SEHK_CODE_WITH_PARAM_URL) private searchStockBySehkCodeWithParamUrl: string,
    @Inject(SEARCH_STOCK_OPTION_BY_SEHK_CODE_URL) private searchStockOptionBySehkCodeUrl: string,
    @Inject(SEARCH_STOCK_OPTION_BY_SEHK_CODE_WITH_PARAM_URL) private searchStockOptionBySehkCodeWithParamUrl: string,
    @Inject(SEARCH_STOCK_OPTION_BY_TICKER_URL) private searchStockOptionByTickerUrl: string,
    @Inject(SEARCH_STOCK_OPTION_BY_TICKER_WITH_PARAM_URL) private searchStockOptionByTickerWithParamUrl: string,
    @Inject(GET_UNDERLYING_ASSET_LIST_URL) private getUnderlyingAssetListUrl: string,
    @Inject(SEARCH_IV_SERIES_BY_SEHK_CODE_URL) private searchIvSeriesBySehkCodeUrl: string,
    @Inject(SEARCH_IV_SERIES_BY_SEHK_CODE_WITH_PARAM_URL) private searchIvSeriesBySehkCodeWithParamUrl: string,
  ) { }

  searchStockBySehkCode(sehkCode: string, startDate?: Date, endDate?: Date): Observable<Stock> {
    let queryUrl: string;

    if (startDate != null)
      queryUrl = String.Format(searchStockBySehkCodeWithParamUrl, sehkCode, startDate, endDate);
    else
      queryUrl = String.Format(searchStockBySehkCode, sehkCode);

    return this.http.get(queryUrl).map(response => {
      return <any>response.map(item => {
        // console.log("raw item", item); // uncomment if you want to debug
        if (item.historyList)
          item.historyList = item.historyList.map(history => new StockHistory(history));
        return new Stock(item);
      });
    });
  }

  searchStockOptionBySehkCode(sehkCode: string, startDate?: Date, endDate?: Date): Observable<StockOption> {
    let queryUrl: string;

    if (startDate != null)
      queryUrl = String.Format(searchStockOptionBySehkCodeWithParamUrl, sehkCode, startDate, endDate);
    else
      queryUrl = String.Format(searchStockOptionBySehkCodeUrl, sehkCode);

    return this.http.get(queryUrl).map(response => {
      return <any>response.map(item => {
        // console.log("raw item", item); // uncomment if you want to debug
        if (item.historyList)
          item.historyList = item.historyList.map(history => new StockOptionHistory(history));
        return new StockOption(item);
      });
    });
  }

  searchStockOptionByTicker(ticker: string, startDate?: Date, endDate?: Date): Observable<StockOption> {
    let queryUrl: string;

    if (startDate != null)
      queryUrl = String.Format(searchStockOptionByTickerWithParamUrl, sehkCode, startDate, endDate);
    else
      queryUrl = String.Format(searchStockOptionByTickerUrl, sehkCode);

    return this.http.get(queryUrl).map(response => {
      return <any>response.map(item => {
        // console.log("raw item", item); // uncomment if you want to debug
        if (item.historyList)
          item.historyList = item.historyList.map(history => new StockOptionHistory(history));
        return new StockOption(item);
      });
    });
  }

  getUnderlyingAssetList(): Observable<>{
    
    return this.http.get(getUnderlyingAssetListUrl).map(response => {
      return <any>response.map(item => {
        // console.log("raw item", item); // uncomment if you want to debug
        return new StockOptionUnderlyingAsset(item);
      });
    });
  }

  searchIvSeriesBySehkCode(sehkCode: number, startDate?: Date, endDate?: Date): Observable<IVSeries> {
    let queryUrl: string;

    if (startDate != null)
      queryUrl = String.Format(searchIvSeriesBySehkCodeWithParamUrl, sehkCode, startDate, endDate);
    else
      queryUrl = String.Format(searchIvSeriesBySehkCodeUrl, sehkCode);

    return this.http.get(queryUrl).map(response => {
      return <any>response.map(item => {
        // console.log("raw item", item); // uncomment if you want to debug
        if (item.historyList)
          item.historyList = item.historyList.map(history => new IVSeriesTimePoint(history));
        return new IVSeries(item);
      });
    });
  }
}
