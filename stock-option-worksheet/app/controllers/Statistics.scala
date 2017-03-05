package controllers

import org.joda.time.{DateTime, Days}

/**
  * Created by Tom on 6/3/2017.
  */

class Statistic(val startDate: DateTime, val endDate: DateTime, val min: Float, val max: Float, val mean: Float, val stddev: Float){
  def numberOfDays: Int = Days.daysBetween(startDate.toLocalDate(), endDate.toLocalDate()).getDays()

  def deviateInStddev(value: Float): Float = (value-mean)/stddev
}
