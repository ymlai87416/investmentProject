import * as moment from 'moment';

function _convertStringToDate(input: string): Date {
    let result = moment(input).toDate();
    return result;
}

export class StockOption {
    id: number;
    ticker: string;
    name: string;
    historyList: StockOptionHistory[];
    optionType: string;
    strikePrice: number;
    dateTime: Date;

    constructor(obj? : any){
        this.id = obj && obj.id || null;
        this.ticker = obj && obj.ticker || null;
        this.name = obj && obj.name || null;
        this.historyList = obj && obj.historyList || null;
        this.optionType = obj && obj.optionType || null;
        this.strikePrice = obj && obj.strikePrice || null;
        this.dateTime = obj && _convertStringToDate(obj.dateTime) || null;
    }
}

export class StockOptionHistory {
    id: number;
    stockOptionId: number;
    priceDate: Date;
    openPrice: number;
    dailyHigh: number;
    dailyLow: number;
    settlePrice: number;
    openInterest: number;
    iv: number;

    constructor(obj?: any){
        this.id = obj && obj.id || null;
        this.stockOptionId = obj && obj.stockOptionId || null;
        this.priceDate = obj && _convertStringToDate(obj.priceDate) || null;
        this.openPrice = obj && obj.openPrice || null;
        this.dailyHigh = obj && obj.dailyHigh || null;
        this.dailyLow = obj && obj.dailyLow || null;
        this.settlePrice = obj && obj.settlePrice || null;
        this.openInterest = obj && obj.openInterest || null;
        this.iv = obj && obj.iv || null;
    }
}

export class Stock {
    id: number;
    ticker: string;
    name: string;
    historyList: StockHistory[];
    sehkCode: number;

    constructor(obj? : any){
        this.id = obj && obj.id || null;
        this.ticker = obj && obj.ticker || null;
        this.name = obj && obj.name || null;
        this.sehkCode = obj && obj.sehkCode || null;
        this.historyList = obj && obj.historyList || null;
    }
}

export class StockHistory{
    id: number;
    stockId: number;
    priceDate: Date;
    openPrice: number;
    dailyHigh: number;
    dailyLow: number;
    closePrice: number;
    adjClosePrice: number;
    volume: number;

    constructor(obj?: any){
        this.id = obj && obj.id || null;
        this.stockId = obj && obj.stockId || null;
        this.priceDate = obj && _convertStringToDate(obj.priceDate) || null;
        this.openPrice = obj && obj.openPrice || null;
        this.dailyHigh = obj && obj.dailyHigh || null;
        this.dailyLow = obj && obj.dailyLow || null;
        this.closePrice = obj && obj.closePrice || null;
        this.adjClosePrice = obj && obj.adjClosePrice || null;
        this.volume = obj && obj.volume || null;
    }
}

export class StockStatistics{
    stockId: number;
    startDate: Date;
    endDate: Date;
    minPrice: number;
    maxPrice: number;
    meanPrice: number;
    stdPrice: number;

    constructor(obj?: any){
        this.stockId = obj && obj.stockId || null;
        this.startDate = obj && _convertStringToDate(obj.startDate) || null; 
        this.endDate = obj && _convertStringToDate(obj.endDate) || null;
        this.minPrice = obj && obj.minPrice || null;
        this.maxPrice = obj && obj.maxPrice || null;
        this.meanPrice = obj && obj.meanPrice || null;
        this.stdPrice = obj && obj.stdPrice || null;
    }
}

export class IVSeries{
    id: number;
    seriesName: string;
    timePointList: IVSeriesTimePoint[];

    constructor(obj?: any){
        this.id = obj && obj.id || null; 
        this.seriesName = obj && obj.seriesName || null;
        this.timePointList = obj && obj.timePointList || null;
    }
}

export class IVSeriesTimePoint{
    id: number;
    date: Date;
    value: number;

    constructor(obj?: any){
        this.id = obj && obj.id || null;
        this.date = obj && _convertStringToDate(obj.date) || null;
        this.value = obj && obj.value || null;
    }
}

export class StockOptionUnderlyingAsset{
    id: number;
    ticker: string;
    shortForm: string;
    fullName: string;

    constructor(obj?: any){
        this.id = obj && obj.id || null;
        this.ticker = obj && obj.ticker ||  null;
        this.shortForm = obj && obj.shortForm || null;
        this.fullName = obj && obj.fullName || null;
    }
}