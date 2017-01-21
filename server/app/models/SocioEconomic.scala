package models

import java.io.InputStream

import shared._

class SocioEconomic(csvStream: InputStream) extends AbstractCaitCsvRepresentation(csvStream) {

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
