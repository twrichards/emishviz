package models

import java.io.File

class SocioEconomic(csvFile: File) extends AbstractCaitCsvRepresentation(csvFile) {

  override def extractSpecifics(cells: Array[String]): Map[String, Map[String, Double]] = {

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
