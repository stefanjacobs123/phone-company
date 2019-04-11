package com.phone.util

import java.time.format.DateTimeFormatter
import java.time.{Duration, LocalTime}

import com.phone.model.{Call, Bill}

object LineParser {

  private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

  /**
    * Parse string representation of call duration found in calls.log (HH:mm:ss) to a [[LocalTime]].
    *
    * The call duration is then in reference to 00:00:00.
    *
    * @param string HH:mm:ss string for call duration found in calls.log
    * @return [[LocalTime]] presentation
    */
  private def callDuration2Time(callDuration: String): LocalTime = {
    LocalTime.parse(callDuration, dateTimeFormatter)
  }

  /**
    * Given a [[LocalTime]] (format of call duration in calls.log file), calculate the duration in seconds the call lasted.
    *
    * We can get a [[java.time.Duration]] presentation of the call length by calculating the duration between two times:
    * midnight (00:00:00) and call duration (HH:mm:ss). From there we can easily go to seconds.
    *
    * @param time [[LocalTime]] presentation of 'call duration' (HH:mm:ss)
    * @return call duration in seconds
    */
  private def time2SecondsSinceMidnight(time: LocalTime): Long = {
    val baselineTime = LocalTime.MIDNIGHT
    Duration.between(baselineTime, time).getSeconds
  }

  /**
    * As per spec:
    *   - first 3 minutes (180 seconds)   => 0.05p/sec (includes second 180)    => 180s = 9p
    *   - over 3 minutes (> 180 seconds)  => 0.03p/sec (start from second 181)  => 9p + (callDuration - 180) * 0.03
    *
    * @param callDuration duration of call in seconds
    * @return price in p for the call
    */

  private def calculateCallPrice(callDuration: Long): Double = {
    if (callDuration < 180) callDuration * 0.05
    else 9 + (callDuration - 180) * 0.03
  }

  /**
    * Very rudimentary function for creating a [[Call]] case class from a CSV line.
    *
    * Not safe to use in production. No error handling for instance. Investigate Shapeless implementation for a more
    * general purpose CSV -> Case Class implementation. Or make use of external library.
    *
    * CSV Definition
    *
    *   `'customer id','phone number called','call duration'`
    *
    * Extract from calls.log
    *
    *   A 555-433-242 00:01:03
    *   B 555-333-212 00:01:20
    *
    * @param line a line from a calls.log file
    * @return [[Call]] case class
    */
  def logLine2call(line: String): Call = {

    // array of values in line
    val parsedLine = line.split(" ")
    val callDurationInSeconds = time2SecondsSinceMidnight(callDuration2Time(parsedLine(2)))

    Call(
      customerId = parsedLine(0),
      numberCalled = parsedLine(1),
      duration = callDurationInSeconds
    )

  }

  /**
    * Transform a [[Call]] case class into a [[Bill]] case class.
    *
    * In essence, take call information and create a bill from it.
    *
    * @param call [[Call]] case class
    * @return [[Bill]] case class
    */
  def call2bill(call: Call): Bill = {
    Bill(call.customerId, call.numberCalled, calculateCallPrice(call.duration))
  }

}
