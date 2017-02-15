package models

import java.io.InputStream
import javax.inject.Inject

import models.AbstractCaitCsvRepresentation._
import play.Environment
import shared._

class GhgEmissions (inputStream: InputStream) extends AbstractCaitCsvRepresentation(inputStream) {

  @Inject
  def this(env: Environment) =
    this(env.resourceAsStream("data/cait/csv/CAIT Country GHG Emissions.csv"))

  override protected[models] def howManyHeadingLines: Int = 3

  override def extractSpecifics(cells: Array[String]): CaitYearCountryDetail = {

    val energy = // energy total MINUS transport raw
    safeDouble(cells, 11) - safeDouble(cells, 19)

    val transport = // transport raw PLUS bunker fuels
    safeDouble(cells, 19) + safeDouble(cells, 16)

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
