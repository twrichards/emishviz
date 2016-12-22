package models

import java.io.File

import play.api.libs.json.Json

import scala.io.Source
import scala.util.{Failure, Success, Try}

abstract class AbstractCaitCsvRepresentation(csvFile: File) {

  protected def extractSpecifics(cells: Array[String]): Map[String, Map[String, Double]]

  def asJson() = Json.toJson(toMap)

  protected def toMap = {

    val bufferedReader = Source.fromFile(csvFile)

    val result = bufferedReader
      .getLines
      .toArray
      .map(_.trim)
      .drop(3) // skip headings
      .map(_.split(",").map(_.trim))
      .groupBy(_ (1)) //year
      .mapValues(
      _.groupBy(_ (0)) // country
        .mapValues(_.map(extractSpecifics)) // extract detail
        .mapValues(_ (0)) // simple year arrays to first item
    )

    bufferedReader.close()

    result

  }

  protected def safeDouble(cells: Array[String], index: Int): Double = {
    Try {
      cells(index).toDouble
    } match {
      case Success(value) => value
      case Failure(_) => 0.0
    }
  }

}


