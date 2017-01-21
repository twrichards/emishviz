package controllers

import java.io.InputStream
import javax.inject.Inject

import models.{GhgEmissions, SocioEconomic}
import play.Environment
import play.api.mvc._

class DataController @Inject()(env: Environment) extends Controller {

  def emissions = Action {

    val stream: InputStream = env.resourceAsStream("data/cait/csv/CAIT Country GHG Emissions.csv")

    Ok(new GhgEmissions(stream).asJson())

  }

  def socio = Action {

    val stream: InputStream = env.resourceAsStream("data/cait/csv/CAIT Country Socio-Economic Data.csv")

    Ok(new SocioEconomic(stream).asJson())

  }

}
