package com.phone.solutions

import com.phone.util.FileParser.linesInFile
import com.phone.util.LineParser.{call2bill, logLine2call}
import com.phone.model.DailyBill

object ScalaCollectionSolution {
  import com.phone.util.Util._

  /**
    * Scala Collection Solution
    */

  val iteratorLines2DailyBills: Iterable[DailyBill] =
    linesInFile.toList.map(logLine2call).map(call2bill)
      .groupBy[(CustomerId, NumberCalled)](bill => (bill.customerId, bill.numberCalled))
      .map{ case ((customerId, numberCalled), bills) => (customerId, numberCalled) -> bills.map(_.price).sum }
      .groupBy[CustomerId]{ case ((customerId, numberCalled), billTotal) => customerId }
      .mapValues(_.map { case ((customerId, numberCalled), billTotal) => numberCalled -> billTotal } )
      .map { case (customerId, numberCalledToBillTotal) => customerId -> dropLargestBill(numberCalledToBillTotal)}
      .map{ case (customerId, bills) => DailyBill(customerId, bills.foldLeft(0.0)((sum, bill) => sum + bill._2))}

}
