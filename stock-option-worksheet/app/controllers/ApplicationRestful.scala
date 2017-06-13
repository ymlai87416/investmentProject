package controllers

import model.Database._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import util.Joda._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import scala.util.Try

/**
  * Created by Tom on 4/3/2017.
  *
  * Resource entity: stock option and stock
  */
object ApplicationRestful extends Controller{

  case class StockTimeSeries(stock: Stock, history: List[StockHistory])
  case class StockOptionTimeSeries(stockOption: StockOption, history: List[StockOptionHistory])
  case class StockOptionDetail(stockOption: StockOption, history: StockOptionHistory)
  case class IVTimeSeries(series: IVSeries, timepoint: List[IVSeriesTimePoint])

  implicit val stockHistoryFormat: Format[StockHistory] = (
    (JsPath \ "id").format[Long] and
      (JsPath \ "stockId").format[Long] and
      (JsPath \ "priceDate").format[DateTime] and
      (JsPath \ "openPrice").format[Float] and
      (JsPath \ "dailyHigh").format[Float] and
      (JsPath \ "dailyLow").format[Float] and
      (JsPath \ "closePrice").format[Float] and
      (JsPath \ "adjClosePrice").format[Float] and
      (JsPath \ "volume").format[Long])(StockHistory.apply, unlift(StockHistory.unapply))

  implicit val stockOptionHistoryFormat: Format[StockOptionHistory] = (
    (JsPath \ "id").format[Long] and
      (JsPath \ "stockOptionId").format[Long] and
      (JsPath \ "priceDate").format[DateTime] and
      (JsPath \ "openPrice").format[Float] and
      (JsPath \ "dailyHigh").format[Float] and
      (JsPath \ "dailyLow").format[Float] and
      (JsPath \ "settlePrice").format[Float] and
      (JsPath \ "openInterest").format[Long] and
      (JsPath \ "iv").format[Float])(StockOptionHistory.apply, unlift(StockOptionHistory.unapply))

  implicit val stockFormat: Format[Stock] = (
    (JsPath \ "id").format[Long] and
      (JsPath \ "ticker").format[String] and
      (JsPath \ "name").format[String])(Stock.apply, unlift(Stock.unapply))

  implicit val stockOptionFormat: Format[StockOption] = (
    (JsPath \ "id").format[Long] and
      (JsPath \ "ticker").format[String] and
      (JsPath \ "name").format[String])(StockOption.apply, unlift(StockOption.unapply))

  implicit val ivSeriesFormat: Format[IVSeries] = (
    (JsPath \ "id").format[Long] and
      (JsPath \ "seriesName").format[String])(IVSeries.apply, unlift(IVSeries.unapply)
  )

  implicit val ivSeriesTimePointFormat: Format[IVSeriesTimePoint] = (
    (JsPath \ "id").format[Long] and
      (JsPath \ "date").format[DateTime] and
      (JsPath \ "value").format[Float])(IVSeriesTimePoint.apply, unlift(IVSeriesTimePoint.unapply))

  implicit val stockStatisticFormat: Format[StockStatistic] = (
    (JsPath \ "stockId").format[Long] and
      (JsPath \ "startDate").format[DateTime] and
      (JsPath \ "endDate").format[DateTime] and
      (JsPath \ "minPice").format[Float] and
      (JsPath \ "maxPrice").format[Float] and
      (JsPath \ "meanPrice").format[Float] and
      (JsPath \ "stdPrice").format[Float])(StockStatistic.apply, unlift(StockStatistic.unapply))

  implicit val stockStatisticOptionFormat: Format[StockOptionIVStatistic] = (
    (JsPath \ "stockId").format[Long] and
      (JsPath \ "startDate").format[DateTime] and
      (JsPath \ "endDate").format[DateTime] and
      (JsPath \ "maxIV").format[Float] and
      (JsPath \ "minIV").format[Float] and
      (JsPath \ "meanIV").format[Float] and
      (JsPath \ "stdIV").format[Float])(StockOptionIVStatistic.apply, unlift(StockOptionIVStatistic.unapply))

  implicit val stockTimeSeriesFormat: Format[StockTimeSeries] = (
    (JsPath \ "stock").format[Stock] and
      (JsPath \ "history").format[List[StockHistory]])(StockTimeSeries.apply, unlift(StockTimeSeries.unapply)
  )

  implicit val stockOptionTimeSeriesFormat: Format[StockOptionTimeSeries] = (
    (JsPath \ "stockOption").format[StockOption] and
      (JsPath \ "history").format[List[StockOptionHistory]])(StockOptionTimeSeries.apply, unlift(StockOptionTimeSeries.unapply)
  )

  implicit val stockOptionDetailFormat: Format[StockOptionDetail] = (
    (JsPath \ "stockOption").format[StockOption] and
      (JsPath \ "history").format[StockOptionHistory])(StockOptionDetail.apply, unlift(StockOptionDetail.unapply)
  )

  implicit val iVTimeSeriesFormat: Format[IVTimeSeries] = (
    (JsPath \ "series").format[IVSeries] and
      (JsPath \ "timepoint").format[List[IVSeriesTimePoint]])(IVTimeSeries.apply, unlift(IVTimeSeries.unapply)
  )

  /*
  implicit def tuple2Reads[A, B](implicit aReads: Reads[A], bReads: Reads[B]): Reads[Tuple2[A, B]] = Reads[Tuple2[A, B]] {
    case JsArray(arr) if arr.size == 3 => for {
      a <- aReads.reads(arr(0))
      b <- bReads.reads(arr(1))
    } yield (a, b)
    case _ => JsError(Seq(JsPath() -> Seq(ValidationError("Expected array of two elements"))))
  }

  implicit def tuple2Writes[A, B](implicit aWrites: Writes[A], bWrites: Writes[B]): Writes[Tuple2[A, B]] = new Writes[Tuple2[A, B]] {
    def writes(tuple: Tuple2[A, B]) = JsArray(Seq(aWrites.writes(tuple._1), bWrites.writes(tuple._2)))
  }

  implicit def stockOptionDetailByDateFormat:
  Format[Tuple2[StockOption, StockOptionHistory]] = Format(tuple2Reads[StockOption,StockOptionHistory], tuple2Writes[StockOption,StockOptionHistory])
  */


  val format = DateTimeFormat.forPattern("yyyy-MM-dd")

  def parseDate(param: Option[String]): Option[DateTime] ={
    param match {
      case None => None
      case Some(s : String) => Try(format.parseDateTime(s)).toOption
    }
  }

  /**
    * Return all stock which have stock option available
    * @return a list of stock (List[Stock])
    */
  def getStockHavingStockOptionAvaliable() = Action{
    val stockIdList = StockOption.stockAbbrList.keys.toList;

    val result= Stock.findBySEHKCodeList(stockIdList)
    val json = Json.obj("stocks" -> result)
    Ok(json)
  }

  /**
    * Return a list of stock option of a securities
    *   query param
    *     date1: the date specified to return a list of stock option, default latest available data
    * @param sehkCode the stock securities code
    * @return a list of stock option with price and oi (List[(StockOption, StockOptionHistory)])
    */
  def getStockOptionHistoryByStockCode(sehkCode: Int) = Action{ implicit request =>
    val date1Opt: Option[DateTime] = parseDate(request.getQueryString("date1"))
    val d: Option[DateTime] = date1Opt match{
      case Some(x) => Some(x)
      case None => StockOption.findLatestAvailableDateBySEHKCode(sehkCode)
    }

    d match {
      case Some(x) => {
        val result = StockOption.findBySEHKCodeWithHistory(sehkCode, x)
        result match {
          case Some(y) => Ok(Json.obj("stockOptionDetails" -> y.map(z => new StockOptionDetail(z._1, z._2))))
          case None => BadRequest("No data available")
        }
      }
      case None => BadRequest("No data available")
    }
  }

  /**
    * Return stock option history for a given stock option code
    * query param
    *     date1: the start of the date range (default: earliest possible, max: 1 year)
    *     date2: the end of the date range (default: latest)
    * @param optionCode
    * @return stock option description together with a list of option history (StockOption, List[StockOptionHistory])
    */
  def getStockOptionHistoryByOptionCode(optionCode: String) = Action{ implicit request =>

    val date1Opt: Option[DateTime] = parseDate(request.getQueryString("date1"))
    val date2Opt: Option[DateTime] = parseDate(request.getQueryString("date2"))

    val start: DateTime = date1Opt.getOrElse(DateTime.now.minusYears(1))
    val end: DateTime = date2Opt.getOrElse(DateTime.now)

    val result = StockOption.findByOptionCodeWithHistory(optionCode, start, end)
    result match{
      case Some(x) => Ok(Json.toJson(new StockOptionTimeSeries(x._1, x._2)))
      case None => BadRequest("No data available")
    }
  }

  /**
    * Return stock option iv related to a stock
    * query param
    *     date1: the start of the date range (default: earliest record)
    *     date2: the end of the date range (default: latest)
    * @param sehkCode
    * @return the stock option iv according to the input parameters. List of (IVSeries, List(IVSeriesTimePoint)) - 4 series
    */
  def getStockOptionIVListByStockCode(sehkCode: Int) = Action{ implicit request =>

    val date1Opt: Option[DateTime] = parseDate(request.getQueryString("date1"))
    val date2Opt: Option[DateTime] = parseDate(request.getQueryString("date2"))

    val start: DateTime = date1Opt.getOrElse(DateTime.now.minusYears(1))
    val end: DateTime = date2Opt.getOrElse(DateTime.now)

    val result = IVSeries.findBySEHKCodeWithTimePoint(sehkCode, start, end)

    result match{
      case Some(x) => Ok(Json.obj("ivTimeSeries" -> x.map(y => new IVTimeSeries(y._1, y._2)).toList))
      case None => BadRequest("No data available")
    }
  }

  /**
    * Return stock price history of a stock
    * query param
    *     date1: the start of the date range which stock history price is returned. (default: one year before the current date)
    *     date2: the end of the date range (default: current date)
    * @param sehkCode the stock securities code
    * @return a list of stock price history according to input parameters. (Stock, List[StockHistory])
    */
  def getStockPriceHistoryByStockCode(sehkCode: Int) = Action{implicit request =>
    val date1Opt: Option[DateTime] = parseDate(request.getQueryString("date1"))
    val date2Opt: Option[DateTime] = parseDate(request.getQueryString("date2"))

    val start: DateTime = date1Opt.getOrElse(DateTime.now.minusYears(1))
    val end: DateTime = date2Opt.getOrElse(DateTime.now)

    val result = Stock.findBySEHKCodeWithHistory(sehkCode, start, end)

    result match{
      case Some(x) => Ok(Json.toJson(new StockTimeSeries(x._1, x._2)))
      case None => BadRequest("No data available")
    }
  }

  /**
    * Return stock price history statistic of a stock
    * query param
    *     date1: the start of the date range which stock history price is returned. (default: one year before the current date)
    *     date2: the end of the date range (default: current date)
    * @param sehkCode the stock securities code
    * @return the descriptive statistic of the time series given stock code, start and end date - StockStatistic
    */
  def getStockPriceHistoryStatisticByStockCode(sehkCode: Int) = Action{implicit request =>

    val date1Opt: Option[DateTime] = parseDate(request.getQueryString("date1"))
    val date2Opt: Option[DateTime] = parseDate(request.getQueryString("date2"))

    val start: DateTime = date1Opt.getOrElse(DateTime.now.minusYears(1))
    val end: DateTime = date2Opt.getOrElse(DateTime.now)

    val result = Stock.getStockStatistic(sehkCode, start, end)
    result match{
      case Some(x) => Ok(Json.toJson(x))
      case None => BadRequest("No data available")
    }
  }

  /**
    * Return iv history of a stock
    * query param
    *     date1: the start of the date range which stock history price is returned. (default: one year before the current date)
    *     date2: the end of the date range (default: current date)
    * @param sehkCode the stock securities code
    * @return the descriptive statistic of the time series given stock code, start and end date - StockStatistic
    */
  def getStockIVStatisticByStockCode(sehkCode: Int) = Action{implicit request =>

    val date1Opt: Option[DateTime] = parseDate(request.getQueryString("date1"))
    val date2Opt: Option[DateTime] = parseDate(request.getQueryString("date2"))

    val start: DateTime = date1Opt.getOrElse(DateTime.now.minusYears(1))
    val end: DateTime = date2Opt.getOrElse(DateTime.now)

    val ivSeriesOpt: Option[List[IVSeriesTimePoint]] = IVSeries.findBySEHKCodeWithTimePoint(sehkCode, start, end).map{
      x => x.filter(x => x._1.seriesType=="Implied volatility").values.headOption.getOrElse(List())
    }

    ivSeriesOpt match {
      case None => BadRequest("No data available")
      case Some((ivSeries)) => {
        val ivSeriesSort = ivSeries.sortBy(_.date)
        val minIV = ivSeriesSort.reduceLeft((l, r) => if (r.value < l.value) r else l).value
        val maxIV = ivSeriesSort.reduceLeft((l, r) => if (r.value > l.value) r else l).value
        val currentIV = ivSeriesSort.last.value

        val startDate = ivSeriesSort.head.date;
        val endDate = ivSeriesSort.last.date;

        val IVValueList = ivSeriesSort.map(x => x.value)
        val IVValueSize = ivSeriesSort.length

        val meanIV = IVValueList.sum / IVValueSize
        val devs = IVValueList.map(v => (v - meanIV) * (v - meanIV))
        val stddevPrice = Math.sqrt(devs.sum / (IVValueSize - 1))

        val result = new StockOptionIVStatistic(sehkCode, startDate, endDate, minIV, maxIV, meanIV, stddevPrice.floatValue)
        Ok(Json.toJson(result))
      }
    }
  }
}
