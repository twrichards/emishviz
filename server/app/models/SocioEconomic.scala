package models

import javax.inject.Inject

import play.Environment
import shared._

import AbstractCaitCsvRepresentation._

class SocioEconomic @Inject()(env: Environment) extends AbstractCaitCsvRepresentation(env, PATH_SOCIO_CSV) {

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
