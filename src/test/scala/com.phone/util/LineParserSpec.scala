package com.phone.util

import java.time.LocalTime

import com.phone.model.{Bill, Call}
import org.scalatest.{Matchers, PrivateMethodTester, WordSpec}

class LineParserSpec extends WordSpec with PrivateMethodTester with Matchers {

  "A LineParser" should {

    val lineParser = LineParser

    "transform a string callDuration (HH:mm:ss) into a java.time.LocalTime" in {

      val callDurationToTime = PrivateMethod[LocalTime]('callDuration2Time)

      lineParser invokePrivate callDurationToTime("00:00:00") should be ( LocalTime.MIDNIGHT )
      lineParser invokePrivate callDurationToTime("23:59:00") should be ( LocalTime.parse("23:59:00") )

    }

    "transform a LocalTime into seconds since midnight" in {

      val time2SecondsSinceMidnight = PrivateMethod[Long]('time2SecondsSinceMidnight)

      lineParser invokePrivate time2SecondsSinceMidnight(LocalTime.MIDNIGHT) should be ( 0L )
      lineParser invokePrivate time2SecondsSinceMidnight(LocalTime.parse("00:00:22")) should be ( 22L  )
      lineParser invokePrivate time2SecondsSinceMidnight(LocalTime.parse("00:01:00")) should be ( 60L  )
      lineParser invokePrivate time2SecondsSinceMidnight(LocalTime.parse("00:03:00")) should be ( 180L )

    }

    "calculate price on call duration in seconds" in {

      val calculateCallPrice = PrivateMethod[Double]('calculateCallPrice)

      lineParser invokePrivate calculateCallPrice(0L)   should be ( 0.0  )
      lineParser invokePrivate calculateCallPrice(180L) should be ( 9.0  )
      lineParser invokePrivate calculateCallPrice(181L) should be ( 9.03 )
      lineParser invokePrivate calculateCallPrice(182L) should be ( 9.06 )

    }

    "parse a log line from calls.log to a Call case class" in {

      val logLines: Seq[String] = Seq(
          "A 333-555-111 00:00:09",
          "A 333-666-111 00:00:09",
          "B 333-666-111 00:02:09"
        )

      val expectedCalls: Seq[Call] = Seq(
          Call("A", "333-555-111", 9L),
          Call("A", "333-666-111", 9L),
          Call("B", "333-666-111", 129L)
        )

      val actualCalls: Seq[Call] = logLines.map(lineParser.logLine2call)

      expectedCalls should be ( actualCalls )

    }

    "create a Bill from a Call" in {

      val calls: Seq[Call] = Seq(
          Call("A", "333-555-111", 9L),
          Call("A", "333-666-111", 9L),
          Call("B", "333-666-111", 181L)
        )

      val expectedBills: Seq[Bill] = Seq(
          Bill("A", "333-555-111", 0.45),
          Bill("A", "333-666-111", 0.45),
          Bill("B", "333-666-111", 9.03)
        )

      val actualBills: Seq[Bill] = calls.map(lineParser.call2bill)

      expectedBills should be ( actualBills )

    }

  }

}
