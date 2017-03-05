package util

import org.joda.time.DateTime

/**
  * Created by Tom on 4/3/2017.
  */
object Joda {
  implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)
}
