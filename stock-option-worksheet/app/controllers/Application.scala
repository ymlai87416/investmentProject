package controllers

import java.text.SimpleDateFormat

import model.Database._
import org.joda.time.DateTime
import play.api._
import play.api.mvc._
import util.Joda._

object Application extends Controller {

  val sqlDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd")

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def list = Action {
    val stockOptions: List[StockOption] = StockOption.getAllStockOption;

    Ok(views.html.index("Your new application is ready."))
  }

  def listByCode(sehkCode: Int) = Action {
    val stockNameOpt: Option[String] = StockOption.stockFullList.get(sehkCode)
    val stockOptionsOpt = StockOption.findBySEHKCode(sehkCode);

    (stockNameOpt, stockOptionsOpt) match{

      case (Some(stockName), Some(stockOptions)) =>
        Ok(views.html.optionlist(sehkCode, stockName, stockOptions))
      case _ => Ok(views.html.invalidpage("No such stock code: " + sehkCode))
    }
  }

  def detailSettlePrice(sehkCode: Int, priceDate: String) = detail("settlePrice", sehkCode, priceDate)
  def detailOpenInterest(sehkCode: Int, priceDate: String) = detail("openInterest", sehkCode, priceDate)

  def detail(dataType: String, sehkCode: Int, priceDate: String) = Action{
    val date = new DateTime(sqlDateTimeFormat.parse(priceDate).getTime())
    val stockOptionHistoryOpt = StockOption.findBySEHKCodeWithHistory(sehkCode, date)
    val stockOpt= Stock.findBySEHKCodeWithHistory(sehkCode, date)

    (stockOptionHistoryOpt, stockOpt) match {
      case (Some(stockOptionHistory), Some((stock, stockHistory))) => {

        val expriyDays: List[DateTime] = stockOptionHistory.map(x => x._1.expiryDate).toSet.toList.sorted
        val strikePrices: List[Int] = stockOptionHistory.map(x => (x._1.strikePrice * 1000) toInt).toSet.toList.sorted

        val infoMap: Map[(DateTime, Int, Character), AnyVal] = stockOptionHistory.groupBy(stockOption => (stockOption._1.expiryDate, (stockOption._1.strikePrice * 1000) toInt, stockOption._1.optionType))
          .mapValues(_.map {
            if (dataType == "openInterest") _._2.openInterest else _._2.settlePrice
          }.head)

        Ok(views.html.optionview(stock, stockHistory, date, expriyDays, strikePrices, infoMap))
      }
      case _ => Ok(views.html.invalidpage("No such stock code: " + sehkCode))
    }
  }

  def ivDetail(sehkCode: Int, priceDate: String) = Action{
    val date = new DateTime(sqlDateTimeFormat.parse(priceDate).getTime())
    val stockOpt: Option[(Stock, StockHistory)] = Stock.findBySEHKCodeWithHistory(sehkCode, date)
    val ivSeriesOpt: Option[List[IVSeriesTimePoint]] = IVSeries.findBySEHKCodeWithTimePoint(sehkCode, date).map{
      x => x.filter(x => x._1.seriesType=="Implied volatility").values.head  //todo: how to improve this, head may throw exception
    }

    (stockOpt, ivSeriesOpt) match{
      case(Some(stock), Some(ivSeries)) =>
        if(ivSeries.isEmpty){
          Ok(views.html.invalidpage("No IV information available for " + sehkCode + " on " + priceDate))
        }
        else {
          val ivSeriesSort = ivSeries.sortBy(_.date)
          val minIV = ivSeriesSort.reduceLeft((l, r) => if (r.value < l.value) r else l).value
          val maxIV = ivSeriesSort.reduceLeft((l, r) => if (r.value > l.value) r else l).value
          val currentIV = ivSeriesSort.last.value

          val ivValueList = ivSeriesSort.map(x => x.value)
          val ivValueSize = ivSeriesSort.length

          val meanIV = ivValueList.sum / ivValueSize
          val devs = ivValueList.map(v => (v - meanIV) * (v - meanIV))
          val stddevIV = Math.sqrt(devs.sum / (ivValueSize - 1))

          Ok(views.html.optioniv(stock._1, date, minIV, maxIV, meanIV, stddevIV.toFloat, currentIV, ivSeriesSort))
        }
      case _ => Ok(views.html.invalidpage("No information available for " + sehkCode))
    }
  }


}