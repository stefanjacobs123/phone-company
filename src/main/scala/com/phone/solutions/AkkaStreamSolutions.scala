package com.phone.solutions

import akka.NotUsed
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.phone.util.FileParser.akkaSourceFileLines
import com.phone.util.LineParser.{call2bill, logLine2call}
import com.phone.model.DailyBill

import scala.concurrent.{ExecutionContext, Future}

class AkkaStreamSolutions(implicit context:ExecutionContext, materializer: ActorMaterializer) {
  import com.phone.util.Util._

  lazy val srcBillTotalForNumberCalled: Source[(CustomerId, NumberCalled, BillForNumberTotal), NotUsed] =
    akkaSourceFileLines
      .map(logLine2call)
      .map(call2bill)
      //create subStream for each unique customerId encountered
      .groupBy(Int.MaxValue, bill => bill.customerId)
      //on each subStream created, create a subStream for numbersCalled
      .groupBy(Int.MaxValue, bill => bill.numberCalled)
      //fold over Bills, and accumulate to get total bill (for each number called, for each customer)
      .fold(("","", 0.0))((accum,bill) => (bill.customerId, bill.numberCalled, accum._3 + bill.price))
      //merge both subStreams into main stream
      .mergeSubstreams.mergeSubstreams

  /**
    * Akka Stream Solution (promotion not implemented)
    */
  lazy val srcDailyBills: Source[DailyBill, NotUsed] =
    srcBillTotalForNumberCalled
      //create subStream for each customerId for incoming tuple: (CustomerId, NumberCalled, BillForNumberTotal)
      .groupBy(Int.MaxValue, { case (customerId,_,_) => customerId } )
      //accumulate all the totals for each number
      .fold("",0.0)((accum,b) => (b._1, b._3 + accum._2))
      //merge subStream into main stream
      .mergeSubstreams
      //finally create DailyBill objects
      .map{ case (customerId, billTotal) => DailyBill(customerId, billTotal) }

  /**
    * Akka Stream Solution (promotion implemented)
    */

  lazy val seqBillTotalForNumberCalledFut: Future[Seq[(CustomerId, NumberCalled, BillForNumberTotal)]] =
    //from Source, create FutureSeq of tuples: (CustomerId, NumberCalled, BillForNumberTotal)
    srcBillTotalForNumberCalled.runWith(Sink.seq)

  lazy val dailyBillIterFut: Future[Iterable[DailyBill]] =
    seqBillTotalForNumberCalledFut.map {
      //group by CustomerId
      _.groupBy[CustomerId](_._1)
        //for each customerId, create a map ( customerId -> (a Map from numberCalled -> totalBillForNumber) )
        //dropLargestBill requires Map[NumberCalled,BillForNumberTotal] to drop the largestBill
        .map { case (id, seq: Seq[(CustomerId, NumberCalled, BillForNumberTotal)]) =>
          id -> seq.map { case (_, numberCalled, billAmount) => numberCalled -> billAmount }.toMap
        }
        //drop largest bill
        .map { case (customerId, numberToBillAmount) => customerId -> dropLargestBill(numberToBillAmount) }
        //accumulate all bills for all numbers for each customer
        .map { case (customerId, numberToBillAmount) =>
          customerId -> numberToBillAmount.foldLeft(0.0:BillForNumberTotal)((sum, bill) => sum + bill._2)
        }
        //finally create DailyBill objects
        .map{ case (customerId, billTotal) => DailyBill(customerId, billTotal) }
    }

  lazy val srcDailyBill: Source[DailyBill, NotUsed] = Source
    .fromFuture(dailyBillIterFut)
    .flatMapConcat(iter => Source.fromIterator[DailyBill](() => iter.toIterator))

}
