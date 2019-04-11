package com.phone.util

import scala.collection.immutable.ListMap

object Util {

  /**
    * These types aliases eases reading the code.
    *
    * If we need to groupBy (for instance) we can use the type to indicate by which field we would be grouping by.
    */
  type CustomerId         = String
  type NumberCalled       = String
  type BillForNumberTotal = Double
  type BillForCall        = Double

  /**
    * Promotion!
    *
    * Given a Map from [[NumberCalled]] -> [[BillForNumberTotal]], drop the [[NumberCalled]] with the
    * largest [[BillForNumberTotal]].
    *
    * If two large bills are the same size, one will be dropped.
    * If there's only one bill, that will be dropped.
    *
    * @param numberToBill Map from NumberCalled -> [[BillForNumberTotal]]
    * @return input with largest bill dropped
    */
  def dropLargestBill(numberToBill: Map[NumberCalled, BillForNumberTotal]): Map[NumberCalled, BillForNumberTotal] = {
    ListMap(numberToBill.toSeq.sortBy(_._2):_*).dropRight(1)
  }


}
