package models

import java.io.File

import shared._

class SocioEconomic(csvFile: File) extends AbstractCaitCsvRepresentation(csvFile) {

  override def extractSpecifics(cells: Array[String]): CaitYearCountryDetail = {

    Map(
      "population" -> Map(
        "population" -> safeDouble(cells, 2)
      ),
      "gdp" -> Map(
        "ppp" -> safeDouble(cells, 3),
        "usd" -> safeDouble(cells, 4)
      )
    )

  }


}
