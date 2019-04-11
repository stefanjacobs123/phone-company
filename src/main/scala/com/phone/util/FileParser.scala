package com.phone.util

import akka.NotUsed
import akka.stream.scaladsl.{Source => akkaStreamSource}
import scala.io.{Source => scalaIOSource}

object FileParser {

  /**
    *
    * @return
    */
  def linesInFile: Iterator[String] = {
    scalaIOSource.fromResource("calls.log").getLines.filter(!_.isEmpty)
  }

  /**
    *
    * @return
    */
  def akkaSourceFileLines: akkaStreamSource[String, NotUsed] = {
    akkaStreamSource.fromIterator(() => linesInFile)
  }

}
