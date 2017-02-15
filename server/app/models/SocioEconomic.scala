package models

import java.io.InputStream
import javax.inject.Inject

import models.AbstractCaitCsvRepresentation._
import play.Environment
import shared._

class SocioEconomic (inputStream: InputStream) extends AbstractCaitCsvRepresentation(inputStream) {

  @Inject
  def this(env: Environment) =
    this(env.resourceAsStream("data/cait/csv/CAIT Country Socio-Economic Data.csv"))

  override protected[models] def howManyHeadingLines: Int = 2

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
