package DataSource
import akka.actor.{ Actor, Props, Identify, ActorIdentity, Terminated }

/**
  * Created by ymlai on 1/5/2017.
  */
class HistoryPriceFetcher extends Actor{
  //this actor acts like query-reply
  def receive = {
    case MarketEventQuery =>
      print("Hello world");
  }
}
