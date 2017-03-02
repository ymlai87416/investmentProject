package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def list = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def listByCode(sehkcode: Long) = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def detail(sehkcode: Long, pricedate: String) = Action{
    Ok(views.html.index("Your new application is ready."))
  }

}