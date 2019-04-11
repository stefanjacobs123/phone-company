package com.phone.util

import org.scalatest.{Matchers, WordSpec}

class UtilSpec extends WordSpec with Matchers {

  "A Util object" should {
    val util = Util

    "drop the largest bill from a Map[NumberCalled, BillForNumberTotal]" in {

      val mapToDropFrom = Map(
        "number1" -> 999.0,
        "number2" -> Double.MaxValue,
        "number3" -> Double.MinValue
      )

      val dropped = util.dropLargestBill(mapToDropFrom)

      val expectedOutput = Map(
        "number3" -> Double.MinValue,
        "number1" -> 999.0
      )

      dropped should be ( expectedOutput )

    }

    "drop the only bill from a Map[NumberCalled, BillForNumberTotal]" in {

      val mapToDropFrom = Map("number1" -> 999.0)

      val dropped = util.dropLargestBill(mapToDropFrom)

      val expectedOutput = Map.empty

      dropped should be ( expectedOutput )

    }

    "if the two largest bills in Map[NumberCalled, BillForNumberTotal] are equal, drop only one" in {

      val mapToDropFrom = Map(
        "number1" -> 999.0,
        "number2" -> Double.MaxValue,
        "number3" -> Double.MaxValue,
        "number4" -> Double.MinValue
      )

      val dropped = util.dropLargestBill(mapToDropFrom)

      val expectedOutput = Map(
        "number4" -> Double.MinValue,
        "number1" -> 999.0,
        "number2" -> Double.MaxValue
      )

      dropped should be ( expectedOutput )

    }

  }

}
