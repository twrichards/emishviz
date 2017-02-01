package controllers

import javax.inject.Inject

import models.{GhgEmissions, SocioEconomic}
import play.Environment
import play.api.mvc._

class DataController @Inject()(env: Environment, emissionsMap: GhgEmissions, socioMap: SocioEconomic) extends Controller {

  def emissions = Action {

    Ok(emissionsMap.json)

  }

  def socio = Action {

    Ok(socioMap.json)

  }

}
