package models

import javax.inject.Inject

import play.Environment
import shared._

class GhgEmissions @Inject()(env: Environment) extends AbstractCaitCsvRepresentation(env, PATH_EMISSIONS_CSV) {

  override def extractSpecifics(cells: Array[String]): CaitYearCountryDetail = {

    val energy =
      safeDouble(cells, 11) // energy total
    -safeDouble(cells, 19) // transport raw


    val transport =
      safeDouble(cells, 19) // transport raw
    +safeDouble(cells, 16) // bunker fuels

    Map(
      GASES -> Map(
        CO2 -> safeDouble(cells, 8),
        CH4 -> safeDouble(cells, 9),
        N2O -> safeDouble(cells, 10)
      ),
      SOURCE -> Map(
        ENERGY -> energy,
        TRANSPORT -> transport,
        AGRICULTURE -> safeDouble(cells, 13),
        INDUSTRIAL -> safeDouble(cells, 12),
        WASTE -> safeDouble(cells, 14),
        LAND_USE_CHANGE -> safeDouble(cells, 15)
      )
    )

  }

}
