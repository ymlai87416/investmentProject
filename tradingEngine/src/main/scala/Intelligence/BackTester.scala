package Intelligence

import akka.actor.{ Actor, Props, Terminated }

/**
  * Created by ymlai on 1/5/2017.
  */
class BackTester extends Actor{
  def receive = {
    case _ =>
      print("Hello world!");
  }
}
