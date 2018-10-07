import { Injectable, Inject } from '@angular/core';
import {
  HttpClient,
  HttpRequest,
  HttpHeaders
} from '@angular/common/http';
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/operator/map'

import {
  Stock, StockHistory, StockOption, StockOptionHistory,
  StockOptionUnderlyingAsset,
  IVSeries, IVSeriesTimePoint,
} from  './option-result.model';
import { sprintf } from 'sprintf-js';

export const WEBSERVICE_ROOT = 'http://localhost:8080'
export const SEARCH_STOCK_BY_SEHK_CODE_URL = WEBSERVICE_ROOT + '/stock/%s';
export const SEARCH_STOCK_BY_SEHK_CODE_WITH_PARAM_URL = WEBSERVICE_ROOT + '/stock/%s?startDate=%s&endDate=%s';
export const SEARCH_STOCK_OPTION_BY_SEHK_CODE_URL = WEBSERVICE_ROOT + '/stockOption/sehk/%s';
export const SEARCH_STOCK_OPTION_BY_SEHK_CODE_WITH_PARAM_URL = WEBSERVICE_ROOT + '/stockOption/sehk/%s?startDate=%s&endDate=%s';
export const SEARCH_STOCK_OPTION_BY_TICKER_URL = WEBSERVICE_ROOT + '/stockOption/code/%s';
export const SEARCH_STOCK_OPTION_BY_TICKER_WITH_PARAM_URL = WEBSERVICE_ROOT + '/stockOption/code/%s?startDate=%s&endDate=%s';
export const GET_UNDERLYING_ASSET_LIST_URL = WEBSERVICE_ROOT + '/stockOption/underlyingAsset';
export const SEARCH_IV_SERIES_BY_SEHK_CODE_URL = WEBSERVICE_ROOT + '/ivseries/%s';
export const SEARCH_IV_SERIES_BY_SEHK_CODE_WITH_PARAM_URL = WEBSERVICE_ROOT + '/ivseries/%s?startDate=%s&endDate=%s';

@Injectable({
  providedIn: 'root'
})
export class OptionService {

  constructor(
    private http: HttpClient,
    @Inject(SEARCH_STOCK_BY_SEHK_CODE_URL) private searchStockBySehkCodeUrl: string,
    @Inject(SEARCH_STOCK_BY_SEHK_CODE_WITH_PARAM_URL) private searchStockBySehkCodeWithParamUrl: string,
    @Inject(SEARCH_STOCK_OPTION_BY_SEHK_CODE_URL) private searchStockOptionBySehkCodeUrl: string,
    @Inject(SEARCH_STOCK_OPTION_BY_SEHK_CODE_WITH_PARAM_URL) private searchStockOptionBySehkCodeWithParamUrl: string,
    @Inject(SEARCH_STOCK_OPTION_BY_TICKER_URL) private searchStockOptionByTickerUrl: string,
    @Inject(SEARCH_STOCK_OPTION_BY_TICKER_WITH_PARAM_URL) private searchStockOptionByTickerWithParamUrl: string,
    @Inject(GET_UNDERLYING_ASSET_LIST_URL) private getUnderlyingAssetListUrl: string,
    @Inject(SEARCH_IV_SERIES_BY_SEHK_CODE_URL) private searchIvSeriesBySehkCodeUrl: string,
    @Inject(SEARCH_IV_SERIES_BY_SEHK_CODE_WITH_PARAM_URL) private searchIvSeriesBySehkCodeWithParamUrl: string,
  ) { 
    console.log("Option service created.");
  }

  searchStockBySehkCode(sehkCode: string, startDate?: Date, endDate?: Date): Observable<Stock[]> {
    let queryUrl: string;

    if (startDate != null)
      queryUrl = sprintf(this.searchStockBySehkCodeWithParamUrl, sehkCode, startDate, endDate);
    else
      queryUrl = sprintf(this.searchStockBySehkCodeUrl, sehkCode);

    return this.http.get(queryUrl).map(response => {
      return <any>response['test'].map(item => {
        // console.log("raw item", item); // uncomment if you want to debug
        if (item.historyList)
          item.historyList = item.historyList.map(history => new StockHistory(history));
        return new Stock(item);
      });
    });
  }

  searchStockOptionBySehkCode(sehkCode: string, startDate?: Date, endDate?: Date): Observable<StockOption[]> {
    let queryUrl: string;

    if (startDate != null)
      queryUrl = sprintf(this.searchStockOptionBySehkCodeWithParamUrl, sehkCode, startDate, endDate);
    else
      queryUrl = sprintf(this.searchStockOptionBySehkCodeUrl, sehkCode);

    return this.http.get(queryUrl).map(response => {
      return <any>response['test'].map(item => {
        // console.log("raw item", item); // uncomment if you want to debug
        if (item.historyList)
          item.historyList = item.historyList.map(history => new StockOptionHistory(history));
        return new StockOption(item);
      });
    });
  }

  searchStockOptionByTicker(ticker: string, startDate?: Date, endDate?: Date): Observable<StockOption[]> {
    let queryUrl: string;

    if (startDate != null)
      queryUrl = sprintf(this.searchStockOptionByTickerWithParamUrl, ticker, startDate, endDate);
    else
      queryUrl = sprintf(this.searchStockOptionByTickerUrl, ticker);

    return this.http.get(queryUrl).map(response => 
      {
      return <any>response['test'].map(item => {
        // console.log("raw item", item); // uncomment if you want to debug
        if (item.historyList)
          item.historyList = item.historyList.map(history => new StockOptionHistory(history));
        return new StockOption(item);
      });
    });
  }

  getUnderlyingAssetList(): Observable<StockOptionUnderlyingAsset[]>{
    return this.http.get(this.getUnderlyingAssetListUrl).map(response => {
      let responseArr = response as StockOptionUnderlyingAsset[];
      return responseArr.map(item => {
        return new StockOptionUnderlyingAsset(item);
      });
    });
  }

  searchIvSeriesBySehkCode(sehkCode: number, startDate?: Date, endDate?: Date): Observable<IVSeries[]> {
    let queryUrl: string;

    if (startDate != null)
      queryUrl = sprintf(this.searchIvSeriesBySehkCodeWithParamUrl, sehkCode, startDate, endDate);
    else
      queryUrl = sprintf(this.searchIvSeriesBySehkCodeUrl, sehkCode);

    return this.http.get(queryUrl).map(response => {
      return <any>response['test'].map(item => {
        // console.log("raw item", item); // uncomment if you want to debug
        if (item.historyList)
          item.historyList = item.historyList.map(history => new IVSeriesTimePoint(history));
        return new IVSeries(item);
      });
    });
  }
}
