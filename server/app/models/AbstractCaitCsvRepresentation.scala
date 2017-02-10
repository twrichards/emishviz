package models

import play.Environment
import shared._

import scala.io.Source
import scala.util.{Failure, Success, Try}

import AbstractCaitCsvRepresentation._

abstract class AbstractCaitCsvRepresentation(env: Environment, csvPath: String) {

  private val bufferedReader = Source.fromInputStream(env.resourceAsStream(csvPath), "windows-1252")

  private val caitMap =
    bufferedReader
      .getLines
      .toArray
      .map(_.trim)
      .drop(3) // skip headings
      .map(replaceQuotedCommas)
      .map(_.split(",").map(_.trim))
      .groupBy(groupByYear) //year
      .mapValues(
      _.groupBy(groupByCountryRestoringCommas) // country
        .filterKeys(_ != "World")
        .filterKeys(!_.startsWith("European Union"))
        .mapValues(_.map(extractSpecifics)) // extract detail
        .mapValues(_ (0)) // simple year arrays to first item
    )

  bufferedReader.close()

  val json = stringifyCait(caitMap)

  protected[models] def extractSpecifics(cells: Array[String]): CaitYearCountryDetail

}

object AbstractCaitCsvRepresentation {

  protected[models] def replaceQuotedCommas: (String) => String =
    _.replaceAll("""\"(.*?)\,(.*?)\"""", """$1\|$2""") // replace quoted strings containing comma with pipes with no quotes (non-greedy)

  protected[models] def groupByYear: (Array[String]) => String =
    _ (1) //2nd column

  protected[models] def groupByCountryRestoringCommas: (Array[String]) => String =
    _ (0).replaceAll("""\|""", ",") //1st column (with commas restored)

  protected[models] def safeDouble(cells: Array[String], index: Int, default: Double): Double = {
    Try {
      cells(index).toDouble
    } match {
      case Success(value) => value
      case Failure(_) => default
    }
  }

  protected[models] def safeDouble(cells: Array[String], index: Int): Double =
    safeDouble(cells, index, 0.0)

}


