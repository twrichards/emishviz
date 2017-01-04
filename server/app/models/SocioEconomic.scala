package models

import java.io.File

import shared._

class SocioEconomic(csvFile: File) extends AbstractCaitCsvRepresentation(csvFile) {

  override def extractSpecifics(cells: Array[String]): CaitYearCountryDetail = {

    Map(
      POPULATION -> Map(
        POPULATION -> safeDouble(cells, 2)
      ),
      GDP -> Map(
        PPP -> safeDouble(cells, 3),
        USD -> safeDouble(cells, 4)
      )
    )

  }


}
