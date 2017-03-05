package controllers

import controllers.Application.Ok
import play.api.mvc._
/**
  * Created by Tom on 4/3/2017.
  */
object ApplicationRestful extends Controller{
  def listStockWithStockOption() = Action{
    Ok(views.html.index("Your new application is ready."))
  }

  def listStockOptionsBySEHKCodeAndDate(sehkCode: Int, priceDate: String) = Action{
    Ok(views.html.index("Your new application is ready."))
  }

  def getStockHistory(sehkCode: Int, priceDate: String) = Action{
    Ok(views.html.index("Your new application is ready."))
  }

  def getStockHistoryStatistic(sehkCode: Int, startDate: String, endDate: String) = Action{
    Ok(views.html.index("Your new application is ready."))
  }

  def getStockOptionTradingDays(sehkCode: Int) = Action{
    Ok(views.html.index("Your new application is ready."))
  }

  def getStockOptionIV(sehkCode: Int, startDate: String, endDate: String) = Action{
    Ok(views.html.index("Your new application is ready."))
  }
}
