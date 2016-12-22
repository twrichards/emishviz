package controllers

import javax.inject.Inject

import models.{SocioEconomic, GhgEmissions}
import play.Environment
import play.api.mvc._

class DataController @Inject()(env: Environment) extends Controller {

  def emissions = Action {

    val file = env.getFile("conf/data/cait/csv/CAIT Country GHG Emissions.csv")

    Ok(new GhgEmissions(file).asJson())

  }

  def socio = Action {

    val file = env.getFile("conf/data/cait/csv/CAIT Country Socio-Economic Data.csv")

    Ok(new SocioEconomic(file).asJson())

  }

}
