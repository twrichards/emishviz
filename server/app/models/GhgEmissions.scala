package models

import java.io.File

import shared._

class GhgEmissions(csvFile: File) extends AbstractCaitCsvRepresentation(csvFile) {

  override def extractSpecifics(cells: Array[String]): CaitYearCountryDetail = {

    val energy =
      safeDouble(cells, 11) // energy total
    -safeDouble(cells, 19) // transport raw


    val transport =
      safeDouble(cells, 19) // transport raw
    +safeDouble(cells, 16) // bunker fuels

    Map(
      "gases" -> Map(
        "co2" -> safeDouble(cells, 8),
        "ch4" -> safeDouble(cells, 9),
        "n2o" -> safeDouble(cells, 10)
      ),
      "source" -> Map(
        "energy" -> energy,
        "transport" -> transport,
        "agriculture" -> safeDouble(cells, 13),
        "industrial" -> safeDouble(cells, 12),
        "waste" -> safeDouble(cells, 14),
        "land_use_change" -> safeDouble(cells, 15)
      )
    )

  }


}
