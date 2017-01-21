package models

import java.io.InputStream

import shared._

import scala.io.Source
import scala.util.{Failure, Success, Try}

abstract class AbstractCaitCsvRepresentation(csvStream: InputStream) {

  protected def extractSpecifics(cells: Array[String]): CaitYearCountryDetail

  def asJson() = stringifyCait(toMap)

  def replaceQuotedCommas: (String) => String =
    _.replaceAll("""\"(.*?)\,(.*?)\"""", """$1\|$2""") // replace quoted strings containing comma with pipes with no quotes (non-greedy)

  def groupByYear: (Array[String]) => String = _ (1) //2nd column

  def groupByCountry: (Array[String]) => String = _ (0).replaceAll("""\|""", ",") //1st column (with commas restored)

  protected def toMap: CaitMap = {

    val bufferedReader = Source.fromInputStream(csvStream)

    val result = bufferedReader
      .getLines
      .toArray
      .map(_.trim)
      .drop(3) // skip headings
      .map(replaceQuotedCommas)
      .map(_.split(",").map(_.trim))
      .groupBy(groupByYear) //year
      .mapValues(
      _.groupBy(groupByCountry) // country
        .filterKeys(_ != "World")
        .filterKeys(!_.startsWith("European Union"))
        .mapValues(_.map(extractSpecifics)) // extract detail
        .mapValues(_ (0)) // simple year arrays to first item
    )

    bufferedReader.close()

    result

  }

  protected def safeDouble(cells: Array[String], index: Int, default: Double): Double = {
    Try {
      cells(index).toDouble
    } match {
      case Success(value) => value
      case Failure(_) => default
    }
  }

  protected def safeDouble(cells: Array[String], index: Int): Double = safeDouble(cells, index, 0.0)

}


