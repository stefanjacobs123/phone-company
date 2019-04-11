package com.phone

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.phone.solutions.AkkaStreamSolutions
import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

object Main extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val context: ExecutionContext = system.dispatcher

  import com.phone.solutions.ScalaCollectionSolution._

  val akkaStreamSolutions = new AkkaStreamSolutions()

  /**
    * Execute the three different Solutions.
    */
  def executeSolutions(): Unit = {
    println("Now running 'Scala Collection Solution':")
    iteratorLines2DailyBills.foreach(println)
    println()

    println("Now running 'Akka Stream Solution (promotion not implemented)':")
    Await.result(akkaStreamSolutions.srcDailyBills.runForeach(finalBill => println(finalBill.toString)), 5.seconds)
    println()


    println("Now running 'Akka Stream Solution (promotion implemented)':")
    Await.result(akkaStreamSolutions.srcDailyBill.runForeach(println), 5.seconds)
    println()
  }

  executeSolutions()

}
